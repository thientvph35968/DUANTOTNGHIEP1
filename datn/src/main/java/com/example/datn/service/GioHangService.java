package com.example.datn.service;

import com.example.datn.dto.*;
import com.example.datn.entity.*;
import com.example.datn.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    /**
     * Lấy giỏ hàng của khách hàng
     */
    public GioHangDTO getCart(Integer khachHangId) {
        try {
            Optional<GioHang> gioHangOpt = gioHangRepository.findByKhachHangIdWithDetails(khachHangId);

            if (gioHangOpt.isEmpty()) {
                return new GioHangDTO(null, khachHangId, new ArrayList<>(), BigDecimal.ZERO, 0);
            }

            GioHang gioHang = gioHangOpt.get();
            List<GioHangItemDTO> items = new ArrayList<>();
            BigDecimal tongTien = BigDecimal.ZERO;
            int tongSoLuong = 0;

            for (GioHangChiTiet ct : gioHang.getChiTiet()) {

                // Lấy thông tin sản phẩm chi tiết flat, tránh lazy
                SanPhamChiTiet spct = ct.getSanPhamChiTiet();
                SanPham sp = spct.getSanPham();

                String tenSanPham = sp != null ? sp.getTenSanPham() : "Không xác định";
                String hinhAnh = sp != null ? sp.getHinhAnh() : null;
                String mauSac = spct.getMauSac() != null ? spct.getMauSac().getTenMauSac() : null;
                String kichThuoc = spct.getKichThuoc() != null ? spct.getKichThuoc().getTenKichThuoc() : null;

                BigDecimal thanhTien = ct.getGiaSanPham().multiply(BigDecimal.valueOf(ct.getSoLuong()));

                GioHangItemDTO item = new GioHangItemDTO(
                        ct.getId(),
                        spct.getId(),
                        sp != null ? sp.getId() : null,
                        tenSanPham,
                        hinhAnh,
                        mauSac,
                        kichThuoc,
                        ct.getSoLuong(),
                        ct.getGiaSanPham(),
                        thanhTien,
                        spct.getSoLuong()
                );

                items.add(item);
                tongTien = tongTien.add(thanhTien);
                tongSoLuong += ct.getSoLuong();
            }

            return new GioHangDTO(gioHang.getId(), khachHangId, items, tongTien, tongSoLuong);

        } catch (Exception e) {
            e.printStackTrace();
            return new GioHangDTO(null, khachHangId, new ArrayList<>(), BigDecimal.ZERO, 0);
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public ApiResponse<GioHangDTO> addToCart(Integer khachHangId, AddToCartRequest request) {
        try {
            if (khachHangId == null || request == null) {
                return ApiResponse.error("Dữ liệu không hợp lệ");
            }

            Optional<KhachHang> khachHangOpt = khachHangRepository.findById(Long.valueOf(khachHangId));
            if (khachHangOpt.isEmpty()) {
                return ApiResponse.error("Không tìm thấy khách hàng");
            }

            Optional<SanPhamChiTiet> spctOpt = sanPhamChiTietRepository.findById(request.getSanPhamChiTietId());
            if (spctOpt.isEmpty()) {
                return ApiResponse.error("Không tìm thấy sản phẩm");
            }

            SanPhamChiTiet spct = spctOpt.get();

            if (spct.getSoLuong() < request.getSoLuong()) {
                return ApiResponse.error("Sản phẩm chỉ còn " + spct.getSoLuong() + " sản phẩm");
            }

            // Lấy hoặc tạo giỏ hàng
            GioHang gioHang = gioHangRepository.findByKhachHangIdAndTrangThai(khachHangId, true)
                    .orElseGet(() -> {
                        GioHang gh = new GioHang();
                        gh.setKhachHang(khachHangOpt.get());
                        return gioHangRepository.save(gh);
                    });

            // Kiểm tra sản phẩm đã có trong giỏ chưa
            Optional<GioHangChiTiet> existingItemOpt = gioHangChiTietRepository
                    .findByGioHangIdAndSanPhamChiTietId(gioHang.getId(), request.getSanPhamChiTietId());

            if (existingItemOpt.isPresent()) {
                GioHangChiTiet existingItem = existingItemOpt.get();
                int newQuantity = existingItem.getSoLuong() + request.getSoLuong();

                if (spct.getSoLuong() < newQuantity) {
                    return ApiResponse.error("Vượt quá số lượng tồn kho");
                }

                existingItem.setSoLuong(newQuantity);
                gioHangChiTietRepository.save(existingItem);
            } else {
                GioHangChiTiet newItem = new GioHangChiTiet();
                newItem.setGioHang(gioHang);
                newItem.setSanPhamChiTiet(spct);
                newItem.setSoLuong(request.getSoLuong());
                newItem.setGiaSanPham(spct.getDonGia());
                newItem.setTenSanPham(spct.getSanPham().getTenSanPham());
                gioHangChiTietRepository.save(newItem);
            }

            GioHangDTO cartDTO = getCart(khachHangId);
            return ApiResponse.success("Đã thêm vào giỏ hàng", cartDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Cập nhật số lượng trong giỏ
     */
    public ApiResponse<GioHangDTO> updateCart(Integer khachHangId, UpdateCartRequest request) {
        try {
            if (khachHangId == null || request == null) {
                return ApiResponse.error("Dữ liệu không hợp lệ");
            }

            Optional<GioHangChiTiet> itemOpt = gioHangChiTietRepository.findById(request.getGioHangChiTietId());
            if (itemOpt.isEmpty()) {
                return ApiResponse.error("Không tìm thấy sản phẩm trong giỏ");
            }

            GioHangChiTiet item = itemOpt.get();
            if (item.getSanPhamChiTiet().getSoLuong() < request.getSoLuong()) {
                return ApiResponse.error("Sản phẩm chỉ còn " + item.getSanPhamChiTiet().getSoLuong());
            }

            item.setSoLuong(request.getSoLuong());
            gioHangChiTietRepository.save(item);

            GioHangDTO cartDTO = getCart(khachHangId);
            return ApiResponse.success("Đã cập nhật giỏ hàng", cartDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ
     */
    public ApiResponse<GioHangDTO> removeFromCart(Integer khachHangId, Integer gioHangChiTietId) {
        try {
            if (khachHangId == null || gioHangChiTietId == null) {
                return ApiResponse.error("Dữ liệu không hợp lệ");
            }

            gioHangChiTietRepository.deleteById(gioHangChiTietId);

            GioHangDTO cartDTO = getCart(khachHangId);
            return ApiResponse.success("Đã xóa sản phẩm", cartDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    public ApiResponse<Void> clearCart(Integer khachHangId) {
        try {
            if (khachHangId == null) {
                return ApiResponse.error("Dữ liệu không hợp lệ");
            }

            Optional<GioHang> gioHangOpt = gioHangRepository.findByKhachHangIdAndTrangThai(khachHangId, true);
            if (gioHangOpt.isPresent()) {
                gioHangChiTietRepository.deleteByGioHangId(gioHangOpt.get().getId());
            }

            return ApiResponse.success("Đã xóa giỏ hàng", null);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("Lỗi server: " + e.getMessage());
        }
    }
}
