package com.example.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ===== CheckoutRequest =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    private Integer diaChiGiaoHangId;
    private Integer phuongThucThanhToanId;
    private Integer phuongThucGiaoHangId;
    private String maVoucher;
    private String ghiChu;

    // Thông tin địa chỉ mới (nếu không chọn địa chỉ có sẵn)
    private String tenNguoiNhan;
    private String soDienThoai;
    private String diaChi;
    private String phuong;
    private String quan;
    private String tinh;
}
