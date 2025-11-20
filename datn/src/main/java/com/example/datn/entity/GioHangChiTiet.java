package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "GioHangChiTiet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GioHangChiTiet")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_GioHang")
    @JsonBackReference
    private GioHang gioHang;

    @ManyToOne
    @JoinColumn(name = "ID_SanPhamChiTiet")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "GiaSanPham")
    private BigDecimal giaSanPham;

    @Column(name = "TenSanPham")
    private String tenSanPham;
}