package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "GioHang")
@Data
public class GioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GioHang")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_KhachHang")
    private KhachHang khachHang;

    @Column(name = "NgayTao")
    private LocalDate ngayTao;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "gioHang", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<GioHangChiTiet> chiTiet;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDate.now();
        }
        if (trangThai == null) {
            trangThai = true;
        }
    }
}
