package com.example.datn.repository;

import com.example.datn.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ===== GioHangChiTietRepository =====
@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {
    Optional<GioHangChiTiet> findByGioHangIdAndSanPhamChiTietId(Integer gioHangId, Integer sanPhamChiTietId);

    List<GioHangChiTiet> findByGioHangId(Integer gioHangId);

    void deleteByGioHangId(Integer gioHangId);
}
