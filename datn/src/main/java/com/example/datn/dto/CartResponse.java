package com.example.datn.dto;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private boolean success;
    private String message;
    private Integer cartItemCount; // Số lượng sản phẩm trong giỏ
}