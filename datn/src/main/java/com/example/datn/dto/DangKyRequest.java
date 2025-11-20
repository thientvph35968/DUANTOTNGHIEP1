package com.example.datn.dto;

import lombok.Data;

@Data
public class DangKyRequest {
    // Đã sửa từ tenNhanVien thành tenKhachHang
    private String tenKhachHang;

    private String taiKhoan;
    private String matKhau;
    private String xacNhanMatKhau;
    private String email;
    private String sdt;
    private String gioiTinh;
    private String diaChi;
    private Boolean trangThai;
}