package com.example.datn.repository;

import com.example.datn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    boolean existsByMaKhachHangAndIdNot(String maKhachHang, Integer id);
    boolean existsByEmailAndIdNot(String email, Integer id);
    boolean existsBySdtAndIdNot(String sdt, Integer id);

    boolean existsByMaKhachHang(String maKhachHang);
    boolean existsByEmail(String email);
    boolean existsBySdt(String sdt);
    Optional<KhachHang> findByTaiKhoan(String taiKhoan);
}