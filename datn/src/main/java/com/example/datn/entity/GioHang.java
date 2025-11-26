package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "GioHang")
public class GioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GioHang")
    private Integer idGioHang;

    @ManyToOne
    @JoinColumn(name = "ID_KhachHang", nullable = false)
    private KhachHang khachHang;

    @Column(name = "NgayTao")
    private LocalDate ngayTao;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "gioHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GioHangChiTiet> gioHangChiTiets;
}
