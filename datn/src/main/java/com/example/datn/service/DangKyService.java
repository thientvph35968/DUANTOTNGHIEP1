package com.example.datn.service;

import com.example.datn.dto.DangKyRequest;
import com.example.datn.entity.NhanVien;
import com.example.datn.entity.VaiTro;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.repository.VaiTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DangKyService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    /**
     * Xử lý đăng ký tài khoản mới cho người dùng (vai trò USER mặc định)
     */
    public String dangKy(DangKyRequest request) {

        // ✅ Kiểm tra dữ liệu đầu vào
        if (request.getTaiKhoan() == null || request.getTaiKhoan().trim().isEmpty()) {
            return "Tài khoản không được để trống";
        }

        if (request.getMatKhau() == null || request.getMatKhau().length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }

        if (!request.getMatKhau().equals(request.getXacNhanMatKhau())) {
            return "Mật khẩu xác nhận không khớp";
        }

        // ✅ Kiểm tra tài khoản đã tồn tại chưa
        Optional<NhanVien> existingNV = nhanVienRepository.findByTaiKhoan(request.getTaiKhoan());
        if (existingNV.isPresent()) {
            return "Tài khoản đã tồn tại";
        }

        // ✅ Sinh mã nhân viên tự động
        String maNhanVien = generateMaNhanVien();

        // ✅ Lấy vai trò USER (mặc định cho tài khoản tự đăng ký)
        VaiTro vaiTro = vaiTroRepository.findByMaVaiTro("VT02")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò USER (VT02)"));

        // ✅ Tạo mới nhân viên
        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setTenNhanVien(request.getTenNhanVien());
        nhanVien.setTaiKhoan(request.getTaiKhoan());
        nhanVien.setMatKhau(request.getMatKhau()); // Dùng NoOpPasswordEncoder nên giữ nguyên plain text
        nhanVien.setEmail(request.getEmail());
        nhanVien.setSdt(request.getSdt());
        nhanVien.setGioiTinh(request.getGioiTinh());
        nhanVien.setDiaChi(request.getDiaChi());
        nhanVien.setVaiTro(vaiTro);
        nhanVien.setTrangThai(1); // 1 = Active

        // ✅ Lưu vào DB
        nhanVienRepository.save(nhanVien);

        return "SUCCESS";
    }

    /**
     * Sinh mã nhân viên tự động theo dạng NV001, NV002,...
     */
    private String generateMaNhanVien() {
        long count = nhanVienRepository.count();
        return String.format("NV%03d", count + 1);
    }
}
