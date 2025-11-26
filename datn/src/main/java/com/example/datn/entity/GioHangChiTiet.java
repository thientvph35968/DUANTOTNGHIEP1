package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "GioHangChiTiet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GioHangChiTiet")
    private Integer idGioHangChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GioHang", nullable = false)
    private GioHang gioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SanPhamChiTiet", nullable = false)
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "GiaSanPham", precision = 18, scale = 2)
    private BigDecimal giaSanPham;

    @Column(name = "TenSanPham")
    private String tenSanPham;
}
