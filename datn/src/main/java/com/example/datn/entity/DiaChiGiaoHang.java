package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DiaChiGiaoHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChiGiaoHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DiaChi")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_KhachHang")
    private KhachHang khachHang;

    @Column(name = "TenNguoiNhan", length = 100)
    private String tenNguoiNhan;

    @Column(name = "SoDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "Phuong", length = 100)
    private String phuong;

    @Column(name = "Quan", length = 100)
    private String quan;

    @Column(name = "Tinh", length = 100)
    private String tinh;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
