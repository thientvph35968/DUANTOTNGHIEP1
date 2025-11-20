package com.example.datn.service;

import com.example.datn.dto.*;
import com.example.datn.entity.*;
import com.example.datn.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private LichSuDonHangRepository lichSuDonHangRepository;

    /**
     * Lấy tất cả đơn hàng (cho Admin)
     */
    public List<HoaDonDTO> getAllOrders() {
        List<HoaDon> hoaDons = hoaDonRepository.findAllByOrderByNgayTaoDesc();
        return convertToDTO(hoaDons);
    }


    public List<HoaDonDTO> getOrdersByKhachHang(Integer khachHangId) {
        List<HoaDon> hoaDons = hoaDonRepository.findByKhachHangIdOrderByNgayTaoDesc(khachHangId);
        return convertToDTO(hoaDons);
    }

    public Optional<HoaDonDTO> getOrderDetail(Integer hoaDonId) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findByIdWithDetails(hoaDonId);
        return hoaDonOpt.map(this::convertToDTO);
    }


    public ApiResponse<HoaDonDTO> updateOrderStatus(Integer hoaDonId, Integer trangThaiId, String ghiChu) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

            TrangThaiDonHang trangThai = trangThaiDonHangRepository.findById(trangThaiId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái"));

            // Cập nhật trạng thái
            hoaDon.setTrangThaiDonHang(trangThai);
            hoaDonRepository.save(hoaDon);

            // Lưu lịch sử
            LichSuDonHang lichSu = new LichSuDonHang();
            lichSu.setHoaDon(hoaDon);
            lichSu.setTrangThaiDonHang(trangThai);
            lichSu.setGhiChu(ghiChu);
            lichSuDonHangRepository.save(lichSu);

            return ApiResponse.success("Cập nhật trạng thái thành công", convertToDTO(hoaDon));
        } catch (Exception e) {
            return ApiResponse.error("Lỗi: " + e.getMessage());
        }
    }


    public ApiResponse<Void> cancelOrder(Integer hoaDonId, Integer khachHangId) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

            // Kiểm tra quyền hủy
            if (!hoaDon.getKhachHang().getId().equals(khachHangId)) {
                return ApiResponse.error("Bạn không có quyền hủy đơn hàng này");
            }

            // Chỉ hủy được nếu đang chờ xác nhận
            if (!"TT01".equals(hoaDon.getTrangThaiDonHang().getMaTrangThai())) {
                return ApiResponse.error("Chỉ có thể hủy đơn hàng đang chờ xác nhận");
            }

            // Hoàn lại tồn kho
            for (HoaDonChiTiet ct : hoaDon.getChiTiet()) {
                SanPhamChiTiet spct = ct.getSanPhamChiTiet();
                spct.setSoLuong(spct.getSoLuong() + ct.getSoLuong());
            }

            // Cập nhật trạng thái hủy (giả sử có TT04 là "Đã hủy")
            Optional<TrangThaiDonHang> trangThaiHuy = trangThaiDonHangRepository.findByMaTrangThai("TT04");
            if (trangThaiHuy.isPresent()) {
                hoaDon.setTrangThaiDonHang(trangThaiHuy.get());
                hoaDonRepository.save(hoaDon);

                // Lưu lịch sử
                LichSuDonHang lichSu = new LichSuDonHang();
                lichSu.setHoaDon(hoaDon);
                lichSu.setTrangThaiDonHang(trangThaiHuy.get());
                lichSu.setGhiChu("Khách hàng hủy đơn");
                lichSuDonHangRepository.save(lichSu);
            }

            return ApiResponse.success("Hủy đơn hàng thành công", null);
        } catch (Exception e) {
            return ApiResponse.error("Lỗi: " + e.getMessage());
        }
    }


    private List<HoaDonDTO> convertToDTO(List<HoaDon> hoaDons) {
        List<HoaDonDTO> dtos = new ArrayList<>();
        for (HoaDon hd : hoaDons) {
            dtos.add(convertToDTO(hd));
        }
        return dtos;
    }


    private HoaDonDTO convertToDTO(HoaDon hd) {
        List<HoaDonChiTietDTO> chiTietDTOs = new ArrayList<>();

        if (hd.getChiTiet() != null) {
            for (HoaDonChiTiet ct : hd.getChiTiet()) {
                SanPhamChiTiet spct = ct.getSanPhamChiTiet();
                HoaDonChiTietDTO dto = new HoaDonChiTietDTO(
                        ct.getId(),
                        spct.getSanPham().getTenSanPham(),
                        spct.getSanPham().getHinhAnh(),
                        spct.getMauSac() != null ? spct.getMauSac().getTenMauSac() : "",
                        spct.getKichThuoc() != null ? spct.getKichThuoc().getTenKichThuoc() : "",
                        ct.getSoLuong(),
                        ct.getDonGia(),
                        ct.getThanhTien()
                );
                chiTietDTOs.add(dto);
            }
        }

        String diaChiFull = "";
        if (hd.getDiaChiGiaoHang() != null) {
            diaChiFull = hd.getDiaChiGiaoHang().getDiaChi() + ", " +
                    hd.getDiaChiGiaoHang().getPhuong() + ", " +
                    hd.getDiaChiGiaoHang().getQuan() + ", " +
                    hd.getDiaChiGiaoHang().getTinh();
        }

        return new HoaDonDTO(
                hd.getId(),
                hd.getMaHoaDon(),
                hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "",
                hd.getDiaChiGiaoHang() != null ? hd.getDiaChiGiaoHang().getSoDienThoai() : "",
                hd.getNgayTao(),
                hd.getTongTien(),
                hd.getSoTienGiam(),
                hd.getTongThanhToan(),
                hd.getTrangThaiDonHang() != null ? hd.getTrangThaiDonHang().getTenTrangThai() : "",
                diaChiFull,
                chiTietDTOs
        );
    }
}