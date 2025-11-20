package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuDonHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LichSu")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_HoaDon")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "ID_TrangThaiDonHang")
    private TrangThaiDonHang trangThaiDonHang;

    @Column(name = "GhiChu", columnDefinition = "NVARCHAR(MAX)")
    private String ghiChu;

    @Column(name = "ThoiGianThayDoi")
    private LocalDateTime thoiGianThayDoi;

    @Column(name = "NguoiThucHien", length = 255)
    private String nguoiThucHien; // có thể là nhân viên hoặc hệ thống
}
