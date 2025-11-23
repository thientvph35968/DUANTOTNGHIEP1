package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVien implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NhanVien")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_VaiTro")
    private VaiTro vaiTro;

    @Column(name = "MaNhanVien")
    private String maNhanVien;

    @Column(name = "TaiKhoan")
    private String taiKhoan;

    @Column(name = "MatKhau")
    private String matKhau;

    @Column(name = "TenNhanVien")
    private String tenNhanVien;

    @Column(name = "GioiTinh")
    private String gioiTinh;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "Email")
    private String email;

    @Column(name = "DiaChi")
    private String diaChi;

    // BIT trong SQL → Boolean trong Java là đẹp nhất
    @Column(name = "TrangThai")
    private Boolean trangThai;
}
