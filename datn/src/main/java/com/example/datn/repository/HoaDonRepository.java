package com.example.datn.repository;

import com.example.datn.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ===== HoaDonRepository =====
@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    Optional<HoaDon> findByMaHoaDon(String maHoaDon);

    @Query("SELECT hd FROM HoaDon hd LEFT JOIN FETCH hd.chiTiet WHERE hd.id = :id")
    Optional<HoaDon> findByIdWithDetails(@Param("id") Integer id);

    List<HoaDon> findByKhachHangIdOrderByNgayTaoDesc(Integer khachHangId);

    List<HoaDon> findAllByOrderByNgayTaoDesc();

    @Query("SELECT COUNT(hd) FROM HoaDon hd")
    Long countAll();
}
