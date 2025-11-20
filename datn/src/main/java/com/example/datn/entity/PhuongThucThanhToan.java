package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PhuongThucThanhToan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhuongThucThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PhuongThucThanhToan")
    private Integer id;

    @Column(name = "MaPhuongThuc", length = 20)
    private String maPhuongThuc;

    @Column(name = "TenPhuongThuc", length = 100)
    private String tenPhuongThuc;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
