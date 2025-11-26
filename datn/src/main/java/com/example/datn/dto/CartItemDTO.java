package com.example.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Integer idGioHangChiTiet;
    private Integer idSanPhamChiTiet;
    private String tenSanPham;
    private String hinhAnh;
    private String mauSac;
    private String kichThuoc;
    private Integer soLuong;
    private Integer soLuongTon; // Số lượng tồn kho
    private String giaSanPham; // Đã format
    private String tongTien; // Đã format
}