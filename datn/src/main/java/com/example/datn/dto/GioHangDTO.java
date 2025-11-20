package com.example.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// ===== GioHangDTO =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangDTO {
    private Integer id;
    private Integer khachHangId;
    private List<GioHangItemDTO> items;
    private BigDecimal tongTien;
    private Integer tongSoLuong;
}

