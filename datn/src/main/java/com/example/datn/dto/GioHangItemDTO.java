package com.example.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// ===== GioHangItemDTO =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangItemDTO {
    private Integer id;
    private Integer sanPhamChiTietId;
    private Integer sanPhamId;
    private String tenSanPham;
    private String hinhAnh;
    private String mauSac;
    private String kichThuoc;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private Integer soLuongTon;
}
