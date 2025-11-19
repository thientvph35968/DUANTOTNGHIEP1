package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "KichThuoc")
public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_KichThuoc")
    private Integer id;

    @Column(name = "MaKichThuoc")
    private String maKichThuoc;

    @Column(name = "TenKichThuoc")
    private String tenKichThuoc;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
