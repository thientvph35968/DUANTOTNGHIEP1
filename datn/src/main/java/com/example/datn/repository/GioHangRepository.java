package com.example.datn.repository;

import com.example.datn.entity.GioHang;
import com.example.datn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    // Tìm giỏ hàng theo khách hàng và trạng thái (true = đang hoạt động)
    Optional<GioHang> findByKhachHangAndTrangThai(KhachHang khachHang, Boolean trangThai);

    // Tìm giỏ hàng theo ID khách hàng
    Optional<GioHang> findByKhachHangId(Integer idKhachHang);
}
