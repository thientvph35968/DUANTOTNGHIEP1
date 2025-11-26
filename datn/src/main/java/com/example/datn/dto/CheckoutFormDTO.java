package com.example.datn.dto;

import lombok.Data;

@Data
public class CheckoutFormDTO {
    private String tenNguoiNhan;
    private String sdtNhanHang;
    private String emailNhanHang;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiChiTiet;
    private String paymentMethod;
}
