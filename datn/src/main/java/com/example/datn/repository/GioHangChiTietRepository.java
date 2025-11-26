package com.example.datn.repository;

import com.example.datn.entity.GioHang;
import com.example.datn.entity.GioHangChiTiet;
import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {

    // Lấy danh sách chi tiết giỏ hàng theo giỏ hàng
    List<GioHangChiTiet> findByGioHang(GioHang gioHang);

    /**
     * Lấy tất cả chi tiết giỏ hàng của một giỏ hàng, đồng thời tải ngay lập tức (EAGER)
     * các đối tượng SanPhamChiTiet và SanPham liên quan để tránh lỗi LazyInitializationException.
     */
    @Query("SELECT gct FROM GioHangChiTiet gct " +
           "JOIN FETCH gct.sanPhamChiTiet spct " +
           "JOIN FETCH spct.sanPham " +
           "WHERE gct.gioHang = :gioHang")
    List<GioHangChiTiet> findByGioHangAndFetchDetails(@Param("gioHang") GioHang gioHang);


    // Lấy chi tiết giỏ hàng theo giỏ hàng và sản phẩm chi tiết
    Optional<GioHangChiTiet> findByGioHangAndSanPhamChiTiet(GioHang gioHang, SanPhamChiTiet sanPhamChiTiet);

    // Đếm số lượng loại sản phẩm trong giỏ hàng
    @Query("SELECT COUNT(gct) FROM GioHangChiTiet gct WHERE gct.gioHang.idGioHang = :idGioHang")
    Long countByGioHangId(@Param("idGioHang") Integer idGioHang);

    // Tính tổng số lượng của tất cả sản phẩm trong giỏ hàng
    @Query("SELECT SUM(gct.soLuong) FROM GioHangChiTiet gct WHERE gct.gioHang.idGioHang = :gioHangId")
    Integer countTotalQuantityByGioHangId(@Param("gioHangId") Integer gioHangId);
}
