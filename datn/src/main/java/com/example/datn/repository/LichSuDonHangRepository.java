package com.example.datn.repository;

import com.example.datn.entity.LichSuDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ===== LichSuDonHangRepository =====
@Repository
public interface LichSuDonHangRepository extends JpaRepository<LichSuDonHang, Integer> {

    List<LichSuDonHang> findByHoaDon_IdOrderByThoiGianThayDoiDesc(Integer hoaDonId);


}

