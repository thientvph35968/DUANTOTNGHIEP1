package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Voucher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Voucher")
    private Integer id;

    @Column(name = "MaVoucher", length = 20)
    private String maVoucher;

    @Column(name = "TenVoucher", length = 100)
    private String tenVoucher;

    @Column(name = "GiamGia", precision = 5, scale = 2)
    private BigDecimal giamGia;

    @Column(name = "HinhThuc", length = 50)
    private String hinhThuc;

    @Column(name = "DieuKien", length = 255)
    private String dieuKien;

    @Column(name = "GiamGiaToiDa", precision = 18, scale = 2)
    private BigDecimal giamGiaToiDa;

    @Column(name = "NgayBatDau")
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDate ngayKetThuc;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
