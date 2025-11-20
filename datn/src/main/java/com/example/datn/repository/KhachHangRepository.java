package com.example.datn.repository;

import com.example.datn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {
    boolean existsByMaKhachHangAndIdNot(String maKhachHang, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsBySdtAndIdNot(String sdt, Long id);

    boolean existsByMaKhachHang(String maKhachHang);
    boolean existsByEmail(String email);
    boolean existsBySdt(String sdt);
    Optional<KhachHang> findByTaiKhoan(String taiKhoan);
}
