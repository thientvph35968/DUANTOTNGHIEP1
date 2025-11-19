package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "MauSac")
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MauSac")
    private Integer id;

    @Column(name = "MaMauSac")
    private String maMauSac;

    @Column(name = "TenMauSac")
    private String tenMauSac;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
