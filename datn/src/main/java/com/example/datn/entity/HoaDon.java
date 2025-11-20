package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "HoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HoaDon")
    private Integer id;

    @Column(name = "MaHoaDon")
    private String maHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_NhanVien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_KhachHang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Voucher")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PhuongThucThanhToan")
    private PhuongThucThanhToan phuongThucThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PhuongThucGiaoHang")
    private PhuongThucGiaoHang phuongThucGiaoHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TrangThaiDonHang")
    private TrangThaiDonHang trangThaiDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DiaChi")
    private DiaChiGiaoHang diaChiGiaoHang;

    @Column(name = "NgayTao")
    private LocalDate ngayTao;

    @Column(name = "TongTien")
    private BigDecimal tongTien;

    @Column(name = "SoTienGiam")
    private BigDecimal soTienGiam;

    // TongThanhToan được tính tự động bởi DB (PERSISTED computed column)
    @Column(name = "TongThanhToan", insertable = false, updatable = false)
    private BigDecimal tongThanhToan;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> chiTiet;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDate.now();
        }
        if (trangThai == null) {
            trangThai = true;
        }
        if (soTienGiam == null) {
            soTienGiam = BigDecimal.ZERO;
        }
    }
}