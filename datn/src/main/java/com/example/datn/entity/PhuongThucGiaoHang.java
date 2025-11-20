package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "PhuongThucGiaoHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhuongThucGiaoHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PhuongThucGiaoHang")
    private Integer id;

    @Column(name = "MaPhuongThuc", length = 50)
    private String maPhuongThuc;

    @Column(name = "TenPhuongThuc", length = 255)
    private String tenPhuongThuc;

    @Column(name = "PhiGiaoHang")
    private BigDecimal phiGiaoHang;

    @Column(name = "MoTa")
    private String moTa;

    @Column(name = "TrangThai")
    private Boolean trangThai;   // 1 = đang sử dụng , 0 = ngừng
}
