package com.example.datn.repository;

import com.example.datn.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ===== GioHangRepository =====
@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    Optional<GioHang> findByKhachHangIdAndTrangThai(Integer khachHangId, Boolean trangThai);

    @Query("SELECT gh FROM GioHang gh LEFT JOIN FETCH gh.chiTiet WHERE gh.khachHang.id = :khachHangId AND gh.trangThai = true")
    Optional<GioHang> findByKhachHangIdWithDetails(@Param("khachHangId") Integer khachHangId);
}

