package com.example.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ===== UpdateCartRequest =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartRequest {
    private Integer gioHangChiTietId;
    private Integer soLuong;
}
