package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "SanPhamChiTiet")
public class SanPhamChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SanPhamChiTiet")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_SanPham")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "ID_MauSac")
    private MauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "ID_KichThuoc")
    private KichThuoc kichThuoc;

    @ManyToOne
    @JoinColumn(name = "ID_ChatLieu")
    private ChatLieu chatLieu;

    @ManyToOne
    @JoinColumn(name = "ID_KhoaAo")
    private KhoaAo khoaAo;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "DonGia")
    private java.math.BigDecimal donGia;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
