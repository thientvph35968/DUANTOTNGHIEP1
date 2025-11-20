package com.example.datn.repository;

import com.example.datn.entity.PhuongThucGiaoHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ===== PhuongThucGiaoHangRepository =====
@Repository
public interface PhuongThucGiaoHangRepository extends JpaRepository<PhuongThucGiaoHang, Integer> {
    List<PhuongThucGiaoHang> findByTrangThai(Boolean trangThai);
}
