package com.example.datn.repository;

import com.example.datn.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    // ✅ Tìm sản phẩm còn hàng và đang hoạt động với đầy đủ quan hệ
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "LEFT JOIN FETCH sp.thuongHieu " +
            "LEFT JOIN FETCH sp.chiTietList ct " +
            "LEFT JOIN FETCH ct.mauSac " +
            "LEFT JOIN FETCH ct.kichThuoc " +
            "WHERE sp.trangThai = true")
    List<SanPham> findActiveProducts();

    // Tìm theo mã sản phẩm
    SanPham findByMaSanPham(String maSanPham);

    // ✅ Tìm theo thương hiệu với đầy đủ quan hệ
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "LEFT JOIN FETCH sp.thuongHieu th " +
            "LEFT JOIN FETCH sp.chiTietList ct " +
            "LEFT JOIN FETCH ct.mauSac " +
            "LEFT JOIN FETCH ct.kichThuoc " +
            "WHERE th.idThuongHieu = :idThuongHieu AND sp.trangThai = true")
    List<SanPham> findByThuongHieu(@Param("idThuongHieu") Integer idThuongHieu);

    // ✅ Tìm sản phẩm SALE với đầy đủ quan hệ
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "LEFT JOIN FETCH sp.thuongHieu " +
            "LEFT JOIN FETCH sp.chiTietList ct " +
            "LEFT JOIN FETCH ct.mauSac " +
            "LEFT JOIN FETCH ct.kichThuoc " +
            "WHERE sp.trangThai = true " +
            "ORDER BY sp.id DESC")
    List<SanPham> findSaleProducts();

    // ✅ Tìm sản phẩm theo ID với ĐẦY ĐỦ thông tin chi tiết
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
            "LEFT JOIN FETCH sp.thuongHieu " +
            "LEFT JOIN FETCH sp.chiTietList ct " +
            "LEFT JOIN FETCH ct.mauSac " +
            "LEFT JOIN FETCH ct.kichThuoc " +
            "LEFT JOIN FETCH ct.chatLieu " +
            "LEFT JOIN FETCH ct.khoaAo " +
            "WHERE sp.id = :id")
    SanPham findByIdWithDetails(@Param("id") Integer id);
}