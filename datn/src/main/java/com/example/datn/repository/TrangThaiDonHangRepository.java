package com.example.datn.repository;

import com.example.datn.entity.TrangThaiDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// ===== TrangThaiDonHangRepository =====
@Repository
public interface TrangThaiDonHangRepository extends JpaRepository<TrangThaiDonHang, Integer> {
    Optional<TrangThaiDonHang> findByMaTrangThai(String maTrangThai);
}
