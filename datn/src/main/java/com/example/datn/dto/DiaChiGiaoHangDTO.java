package com.example.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ===== DiaChiGiaoHangDTO =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiGiaoHangDTO {
    private Integer id;
    private String tenNguoiNhan;
    private String soDienThoai;
    private String diaChi;
    private String phuong;
    private String quan;
    private String tinh;
    private String ghiChu;
    private Boolean macDinh;
}
