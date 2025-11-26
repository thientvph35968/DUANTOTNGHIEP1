package com.example.datn.repository;

import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    // Tìm tất cả sản phẩm chi tiết theo ID sản phẩm
    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :idSanPham AND spct.trangThai = true")
    List<SanPhamChiTiet> findBySanPham_Id(Integer sanPhamId);

    // Tìm sản phẩm chi tiết còn hàng
    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.soLuong > 0 AND spct.trangThai = true")
    List<SanPhamChiTiet> findAvailableProducts();
}