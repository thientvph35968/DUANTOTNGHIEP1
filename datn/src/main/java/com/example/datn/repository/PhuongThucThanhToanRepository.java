package com.example.datn.repository;

import com.example.datn.entity.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, Integer> {
    // Phương thức này hữu ích để tìm phương thức thanh toán bằng tên (ví dụ: "COD")
    Optional<PhuongThucThanhToan> findByTenPhuongThuc(String tenPhuongThuc);
}
