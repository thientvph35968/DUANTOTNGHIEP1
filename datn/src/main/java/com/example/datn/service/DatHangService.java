package com.example.datn.service;

import com.example.datn.dto.*;
import com.example.datn.entity.*;
import com.example.datn.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DatHangService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private DiaChiGiaoHangRepository diaChiGiaoHangRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private PhuongThucGiaoHangRepository phuongThucGiaoHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private LichSuDonHangRepository lichSuDonHangRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Đặt hàng
     */
    public ApiResponse<HoaDonDTO> checkout(Integer khachHangId, CheckoutRequest request) {
        try {
            // 1. Kiểm tra giỏ hàng
            Optional<GioHang> gioHangOpt = gioHangRepository.findByKhachHangIdWithDetails(khachHangId);
            if (gioHangOpt.isEmpty() || gioHangOpt.get().getChiTiet().isEmpty()) {
                return ApiResponse.error("Giỏ hàng trống");
            }

            GioHang gioHang = gioHangOpt.get();
            KhachHang khachHang = gioHang.getKhachHang();

            // 2. Kiểm tra số lượng tồn kho
            for (GioHangChiTiet ct : gioHang.getChiTiet()) {
                if (ct.getSanPhamChiTiet().getSoLuong() < ct.getSoLuong()) {
                    return ApiResponse.error("Sản phẩm " + ct.getTenSanPham() +
                            " chỉ còn " + ct.getSanPhamChiTiet().getSoLuong() + " sản phẩm");
                }
            }

            // 3. Xử lý địa chỉ giao hàng
            DiaChiGiaoHang diaChi;
            if (request.getDiaChiGiaoHangId() != null) {
                diaChi = diaChiGiaoHangRepository.findById(request.getDiaChiGiaoHangId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
            } else {
                // Tạo địa chỉ mới
                diaChi = new DiaChiGiaoHang();
                diaChi.setKhachHang(khachHang);
                diaChi.setTenNguoiNhan(request.getTenNguoiNhan());
                diaChi.setSoDienThoai(request.getSoDienThoai());
                diaChi.setDiaChi(request.getDiaChi());
                diaChi.setPhuong(request.getPhuong());
                diaChi.setQuan(request.getQuan());
                diaChi.setTinh(request.getTinh());
                diaChi.setGhiChu(request.getGhiChu());
                diaChi.setTrangThai(true);
                diaChi = diaChiGiaoHangRepository.save(diaChi);
            }

            // 4. Tính tổng tiền
            BigDecimal tongTien = BigDecimal.ZERO;
            for (GioHangChiTiet ct : gioHang.getChiTiet()) {
                BigDecimal thanhTien = ct.getGiaSanPham().multiply(new BigDecimal(ct.getSoLuong()));
                tongTien = tongTien.add(thanhTien);
            }

            // 5. Áp dụng voucher (nếu có)
            BigDecimal soTienGiam = BigDecimal.ZERO;
            Voucher voucher = null;
            if (request.getMaVoucher() != null && !request.getMaVoucher().isEmpty()) {
                Optional<Voucher> voucherOpt = voucherRepository.findByMaVoucherAndTrangThai(
                        request.getMaVoucher(), true);

                if (voucherOpt.isPresent()) {
                    voucher = voucherOpt.get();
                    // Tính số tiền giảm (ví dụ: giảm %)
                    soTienGiam = tongTien.multiply(voucher.getGiamGia())
                            .divide(new BigDecimal(100));

                    // Kiểm tra giảm giá tối đa
                    if (voucher.getGiamGiaToiDa() != null &&
                            soTienGiam.compareTo(voucher.getGiamGiaToiDa()) > 0) {
                        soTienGiam = voucher.getGiamGiaToiDa();
                    }
                }
            }

            // 6. Tạo hóa đơn
            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaHoaDon(generateMaHoaDon());
            hoaDon.setKhachHang(khachHang);
            hoaDon.setDiaChiGiaoHang(diaChi);
            hoaDon.setVoucher(voucher);

            // Lấy phương thức thanh toán
            PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(
                            request.getPhuongThucThanhToanId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));
            hoaDon.setPhuongThucThanhToan(pttt);

            // Lấy phương thức giao hàng
            PhuongThucGiaoHang ptgh = phuongThucGiaoHangRepository.findById(
                            request.getPhuongThucGiaoHangId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức giao hàng"));
            hoaDon.setPhuongThucGiaoHang(ptgh);

            // Lấy trạng thái "Chờ xác nhận"
            TrangThaiDonHang trangThai = trangThaiDonHangRepository.findByMaTrangThai("TT01")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái đơn hàng"));
            hoaDon.setTrangThaiDonHang(trangThai);

            hoaDon.setTongTien(tongTien);
            hoaDon.setSoTienGiam(soTienGiam);

            hoaDon = hoaDonRepository.save(hoaDon);

            // 7. Tạo chi tiết hóa đơn và trừ tồn kho
            for (GioHangChiTiet ct : gioHang.getChiTiet()) {
                HoaDonChiTiet hdct = new HoaDonChiTiet();
                hdct.setHoaDon(hoaDon);
                hdct.setSanPhamChiTiet(ct.getSanPhamChiTiet());
                hdct.setSoLuong(ct.getSoLuong());
                hdct.setDonGia(ct.getGiaSanPham());
                hoaDonChiTietRepository.save(hdct);

                // Trừ số lượng tồn
                SanPhamChiTiet spct = ct.getSanPhamChiTiet();
                spct.setSoLuong(spct.getSoLuong() - ct.getSoLuong());
                sanPhamChiTietRepository.save(spct);
            }

            // 8. Tạo lịch sử đơn hàng
            LichSuDonHang lichSu = new LichSuDonHang();
            lichSu.setHoaDon(hoaDon);
            lichSu.setTrangThaiDonHang(trangThai);
            lichSu.setGhiChu("Đơn hàng đã được đặt");
            lichSuDonHangRepository.save(lichSu);

            // 9. Xóa giỏ hàng
            gioHangChiTietRepository.deleteByGioHangId(gioHang.getId());

            // 10. Gửi email xác nhận (nếu có)
            try {
                sendOrderConfirmationEmail(khachHang, hoaDon);
            } catch (Exception e) {
                System.err.println("Lỗi gửi email: " + e.getMessage());
            }

            // 11. Trả về thông tin hóa đơn
            HoaDonDTO hoaDonDTO = convertToDTO(hoaDon);
            return ApiResponse.success("Đặt hàng thành công", hoaDonDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("Đặt hàng thất bại: " + e.getMessage());
        }
    }

    /**
     * Sinh mã hóa đơn tự động
     */
    private String generateMaHoaDon() {
        Long count = hoaDonRepository.countAll();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "HD" + timestamp + String.format("%04d", count + 1);
    }

    /**
     * Gửi email xác nhận đơn hàng
     */
    private void sendOrderConfirmationEmail(KhachHang khachHang, HoaDon hoaDon) {
        // Implement logic gửi email
        // emailService.sendOrderConfirmation(khachHang.getEmail(), hoaDon);
    }

    /**
     * Convert HoaDon sang DTO
     */
    private HoaDonDTO convertToDTO(HoaDon hd) {
        List<HoaDonChiTietDTO> chiTietDTOs = new ArrayList<>();

        for (HoaDonChiTiet ct : hd.getChiTiet()) {
            SanPhamChiTiet spct = ct.getSanPhamChiTiet();
            HoaDonChiTietDTO dto = new HoaDonChiTietDTO(
                    ct.getId(),
                    spct.getSanPham().getTenSanPham(),
                    spct.getSanPham().getHinhAnh(),
                    spct.getMauSac().getTenMauSac(),
                    spct.getKichThuoc().getTenKichThuoc(),
                    ct.getSoLuong(),
                    ct.getDonGia(),
                    ct.getThanhTien()
            );
            chiTietDTOs.add(dto);
        }

        String diaChiFull = hd.getDiaChiGiaoHang().getDiaChi() + ", " +
                hd.getDiaChiGiaoHang().getPhuong() + ", " +
                hd.getDiaChiGiaoHang().getQuan() + ", " +
                hd.getDiaChiGiaoHang().getTinh();

        return new HoaDonDTO(
                hd.getId(),
                hd.getMaHoaDon(),
                hd.getKhachHang().getTenKhachHang(),
                hd.getDiaChiGiaoHang().getSoDienThoai(),
                hd.getNgayTao(),
                hd.getTongTien(),
                hd.getSoTienGiam(),
                hd.getTongThanhToan(),
                hd.getTrangThaiDonHang().getTenTrangThai(),
                diaChiFull,
                chiTietDTOs
        );
    }
}