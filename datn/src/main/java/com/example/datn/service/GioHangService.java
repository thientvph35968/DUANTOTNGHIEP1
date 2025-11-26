package com.example.datn.service;

import com.example.datn.dto.AddToCartRequest;
import com.example.datn.dto.CartItemDTO;
import com.example.datn.dto.CartResponse;
import com.example.datn.dto.CheckoutFormDTO;
import com.example.datn.entity.*;
import com.example.datn.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final GioHangChiTietRepository gioHangChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final DiaChiGiaoHangRepository diaChiGiaoHangRepository;

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0₫";
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "₫";
    }

    @Transactional
    public void placeOrder(KhachHang khachHang, CheckoutFormDTO form) {
        GioHang gioHang = gioHangRepository.findByKhachHangAndTrangThai(khachHang, true)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy giỏ hàng để đặt hàng."));

        List<GioHangChiTiet> cartItems = gioHangChiTietRepository.findByGioHangAndFetchDetails(gioHang);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống.");
        }

        // 1. Tạo và lưu địa chỉ giao hàng
        DiaChiGiaoHang diaChi = new DiaChiGiaoHang();
        String diaChiDayDu = String.join(", ", form.getDiaChiChiTiet(), form.getPhuongXa(), form.getQuanHuyen(), form.getTinhThanh());
        diaChi.setDiaChi(diaChiDayDu);
        diaChi.setKhachHang(khachHang);
        diaChiGiaoHangRepository.save(diaChi);

        // 2. Tìm phương thức thanh toán (An toàn hơn bằng cách giả định ID)
        // Giả sử COD có ID=1, QR có ID=2. Thay đổi nếu cần.
        int ptttId = "COD".equals(form.getPaymentMethod()) ? 1 : 2; 
        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(ptttId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán với ID: " + ptttId));

        // 3. Tìm trạng thái đơn hàng
        TrangThaiDonHang trangThaiChoXacNhan = trangThaiDonHangRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái đơn hàng mặc định."));

        // 4. Tạo Hóa Đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon("HD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        hoaDon.setKhachHang(khachHang);
        hoaDon.setNgayTao(LocalDate.now());
        hoaDon.setPhuongThucThanhToan(pttt);
        hoaDon.setTrangThaiDonHang(trangThaiChoXacNhan);
        hoaDon.setDiaChiGiaoHang(diaChi);
        
        BigDecimal tongTien = BigDecimal.ZERO;
        
        // Lưu hóa đơn để lấy ID
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

        // 5. Tạo chi tiết hóa đơn và cập nhật kho
        for (GioHangChiTiet cartItem : cartItems) {
            SanPhamChiTiet spct = cartItem.getSanPhamChiTiet();
            int soLuongMua = cartItem.getSoLuong();

            if (spct.getSoLuong() < soLuongMua) {
                throw new IllegalStateException("Sản phẩm '" + spct.getSanPham().getTenSanPham() + "' không đủ số lượng trong kho.");
            }

            spct.setSoLuong(spct.getSoLuong() - soLuongMua);
            sanPhamChiTietRepository.save(spct);

            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct.setHoaDon(savedHoaDon);
            hdct.setSanPhamChiTiet(spct);
            hdct.setSoLuong(soLuongMua);
            hdct.setDonGia(cartItem.getGiaSanPham());
            hoaDonChiTietRepository.save(hdct);

            tongTien = tongTien.add(cartItem.getGiaSanPham().multiply(BigDecimal.valueOf(soLuongMua)));
        }
        
        // Cập nhật lại tổng tiền cho hóa đơn và lưu lại
        savedHoaDon.setTongTien(tongTien);
        hoaDonRepository.save(savedHoaDon);

        // 6. Xóa giỏ hàng
        gioHangChiTietRepository.deleteAll(cartItems);
    }
    
    // --- CÁC PHƯƠNG THỨC KHÁC ---

    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItems(Long idKhachHang) {
        List<CartItemDTO> cartItems = new ArrayList<>();
        try {
            KhachHang kh = khachHangRepository.findById(Math.toIntExact(idKhachHang))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

            Optional<GioHang> gioHangOpt = gioHangRepository.findByKhachHangAndTrangThai(kh, true);

            if (gioHangOpt.isPresent()) {
                List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangAndFetchDetails(gioHangOpt.get());
                for (GioHangChiTiet item : items) {
                    CartItemDTO dto = CartItemDTO.builder()
                            .idGioHangChiTiet(item.getIdGioHangChiTiet())
                            .idSanPhamChiTiet(item.getSanPhamChiTiet().getId())
                            .tenSanPham(item.getTenSanPham())
                            .hinhAnh(item.getSanPhamChiTiet().getSanPham().getHinhAnh())
                            .soLuong(item.getSoLuong())
                            .soLuongTon(item.getSanPhamChiTiet().getSoLuong())
                            .giaSanPham(formatCurrency(item.getGiaSanPham()))
                            .tongTien(formatCurrency(item.getGiaSanPham().multiply(BigDecimal.valueOf(item.getSoLuong()))))
                            .build();
                    cartItems.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartItems;
    }
    
    @Transactional(readOnly = true)
    public Integer getCartItemCount(Long idKhachHang) {
        try {
            return khachHangRepository.findById(Math.toIntExact(idKhachHang))
                    .flatMap(kh -> gioHangRepository.findByKhachHangAndTrangThai(kh, true))
                    .map(gioHang -> gioHangChiTietRepository.countTotalQuantityByGioHangId(gioHang.getIdGioHang()))
                    .orElse(0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public CartResponse addToCart(Long idKhachHang, AddToCartRequest request) {
        try {
            KhachHang khachHang = khachHangRepository.findById(Math.toIntExact(idKhachHang))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(request.getSanPhamChiTietId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            if (spct.getSoLuong() == null || spct.getSoLuong() < request.getSoLuong()) {
                return CartResponse.builder().success(false).message("Số lượng sản phẩm không đủ trong kho!").build();
            }

            GioHang gioHang = gioHangRepository.findByKhachHangAndTrangThai(khachHang, true)
                    .orElseGet(() -> {
                        GioHang newGioHang = new GioHang();
                        newGioHang.setKhachHang(khachHang);
                        newGioHang.setNgayTao(LocalDate.now());
                        newGioHang.setTrangThai(true);
                        return gioHangRepository.save(newGioHang);
                    });

            Optional<GioHangChiTiet> existingItem = gioHangChiTietRepository.findByGioHangAndSanPhamChiTiet(gioHang, spct);

            if (existingItem.isPresent()) {
                GioHangChiTiet item = existingItem.get();
                int newQuantity = item.getSoLuong() + request.getSoLuong();
                if (newQuantity > spct.getSoLuong()) {
                    return CartResponse.builder().success(false).message("Số lượng vượt quá tồn kho!").build();
                }
                item.setSoLuong(newQuantity);
                gioHangChiTietRepository.save(item);
            } else {
                GioHangChiTiet newItem = new GioHangChiTiet();
                newItem.setGioHang(gioHang);
                newItem.setSanPhamChiTiet(spct);
                newItem.setSoLuong(request.getSoLuong());
                newItem.setGiaSanPham(spct.getDonGia());
                newItem.setTenSanPham(spct.getSanPham().getTenSanPham());
                gioHangChiTietRepository.save(newItem);
            }

            Integer itemCount = gioHangChiTietRepository.countTotalQuantityByGioHangId(gioHang.getIdGioHang());
            return CartResponse.builder().success(true).message("Đã thêm sản phẩm vào giỏ hàng!").cartItemCount(itemCount != null ? itemCount : 0).build();

        } catch (Exception e) {
            return CartResponse.builder().success(false).message("Có lỗi: " + e.getMessage()).build();
        }
    }
    
    @Transactional
    public CartResponse updateQuantity(Integer idGioHangChiTiet, Integer soLuong) {
        try {
            GioHangChiTiet item = gioHangChiTietRepository.findById(idGioHangChiTiet)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

            if (soLuong <= 0) {
                gioHangChiTietRepository.delete(item);
                return CartResponse.builder().success(true).message("Đã xóa sản phẩm khỏi giỏ hàng").build();
            }

            if (soLuong > item.getSanPhamChiTiet().getSoLuong()) {
                return CartResponse.builder().success(false).message("Số lượng vượt quá tồn kho!").build();
            }

            item.setSoLuong(soLuong);
            gioHangChiTietRepository.save(item);
            return CartResponse.builder().success(true).message("Đã cập nhật số lượng").build();

        } catch (Exception e) {
            return CartResponse.builder().success(false).message("Có lỗi: " + e.getMessage()).build();
        }
    }

    @Transactional
    public CartResponse removeFromCart(Integer idGioHangChiTiet) {
        try {
            gioHangChiTietRepository.deleteById(idGioHangChiTiet);
            return CartResponse.builder().success(true).message("Đã xóa sản phẩm khỏi giỏ hàng").build();
        } catch (Exception e) {
            return CartResponse.builder().success(false).message("Có lỗi: " + e.getMessage()).build();
        }
    }
}
