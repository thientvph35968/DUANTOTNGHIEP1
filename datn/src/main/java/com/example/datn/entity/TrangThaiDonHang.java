package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TrangThaiDonHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrangThaiDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TrangThaiDonHang")
    private Integer id;

    @Column(name = "MaTrangThai", length = 50)
    private String maTrangThai;

    @Column(name = "TenTrangThai", length = 255)
    private String tenTrangThai;

    @Column(name = "ThuTu")
    private Integer thuTu;

    @Column(name = "TrangThai")
    private Integer trangThai; // 1 = hoạt động, 0 = không dùng
}
