package com.example.datn.repository;

import com.example.datn.entity.DiaChiGiaoHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ===== DiaChiGiaoHangRepository =====
@Repository
public interface DiaChiGiaoHangRepository extends JpaRepository<DiaChiGiaoHang, Integer> {
    List<DiaChiGiaoHang> findByKhachHangIdAndTrangThai(Integer khachHangId, Boolean trangThai);
}
