package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "KhoaAo")
public class KhoaAo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_KhoaAo")
    private Integer id;

    @Column(name = "MaKhoaAo")
    private String maKhoaAo;

    @Column(name = "TenKhoaAo")
    private String tenKhoaAo;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
