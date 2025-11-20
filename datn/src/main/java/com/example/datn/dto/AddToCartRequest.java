package com.example.datn.dto;

public class AddToCartRequest {

    // Tên biến này phải là sanPhamChiTietId (để khớp với Controller/Service)
    private Integer sanPhamChiTietId;

    // Tên biến này phải là soLuong (để khớp với Controller/Service)
    private Integer soLuong;

    // PHẢI CÓ Getter và Setter cho cả hai trường

    // Ví dụ:
    public Integer getSanPhamChiTietId() {
        return sanPhamChiTietId;
    }

    public void setSanPhamChiTietId(Integer sanPhamChiTietId) {
        this.sanPhamChiTietId = sanPhamChiTietId;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }
}