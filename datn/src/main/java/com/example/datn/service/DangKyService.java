package com.example.datn.service;

import com.example.datn.dto.DangKyRequest;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.VaiTro;
import com.example.datn.repository.KhachHangRepository;
import com.example.datn.repository.VaiTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DangKyService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    /**
     * Xử lý đăng ký tài khoản mới cho Khách hàng (vai trò USER mặc định)
     */
    public String dangKy(DangKyRequest request) {

        // ... (Kiểm tra dữ liệu đầu vào giữ nguyên) ...
        if (request.getTaiKhoan() == null || request.getTaiKhoan().trim().isEmpty()) {
            return "Tài khoản không được để trống";
        }
        if (request.getMatKhau() == null || request.getMatKhau().length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }
        if (!request.getMatKhau().equals(request.getXacNhanMatKhau())) {
            return "Mật khẩu xác nhận không khớp";
        }

        Optional<KhachHang> existingKH = khachHangRepository.findByTaiKhoan(request.getTaiKhoan());
        if (existingKH.isPresent()) {
            return "Tài khoản đã tồn tại";
        }

        String maKhachHang = generateMaKhachHang();

        VaiTro vaiTro = vaiTroRepository.findByMaVaiTro("VT02")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò USER (VT02)"));

        // ✅ Tạo mới Khách hàng
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(maKhachHang);
        khachHang.setTenKhachHang(request.getTenKhachHang());
        khachHang.setTaiKhoan(request.getTaiKhoan());
        khachHang.setMatKhau(request.getMatKhau());
        khachHang.setEmail(request.getEmail());
        khachHang.setSdt(request.getSdt());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setDiaChi(request.getDiaChi());
        khachHang.setVaiTro(vaiTro);

        // 🛑 SỬA LỖI TRANGTHAI: BIT (1) ánh xạ thành Boolean (true)
        khachHang.setTrangThai(true);

        // ✅ Lưu vào DB
        khachHangRepository.save(khachHang);

        return "SUCCESS";
    }

    /**
     * Sinh mã Khách hàng tự động theo dạng KH001, KH002,...
     */
    private String generateMaKhachHang() {
        long count = khachHangRepository.count();
        return String.format("KH%03d", count + 1);
    }
}