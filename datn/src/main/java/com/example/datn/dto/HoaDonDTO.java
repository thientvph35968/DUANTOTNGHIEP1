package com.example.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// ===== HoaDonDTO =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDTO {
    private Integer id;
    private String maHoaDon;
    private String tenKhachHang;
    private String sdt;
    private LocalDate ngayTao;
    private BigDecimal tongTien;
    private BigDecimal soTienGiam;
    private BigDecimal tongThanhToan;
    private String trangThai;
    private String diaChiGiaoHang;
    private List<HoaDonChiTietDTO> chiTiet;
}
