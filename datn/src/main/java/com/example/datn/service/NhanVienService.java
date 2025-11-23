package com.example.datn.service;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    // Lấy tất cả nhân viên
    public List<NhanVien> getAll() {
        return nhanVienRepository.findAll();
    }

    // Lấy 1 nhân viên theo ID
    public NhanVien getById(Integer id) {
        return nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID = " + id));
    }

    // Thêm mới nhân viên
    public NhanVien create(NhanVien nhanVien) {
        // Check trùng mã NV
        if (nhanVien.getMaNhanVien() != null
                && nhanVienRepository.existsByMaNhanVien(nhanVien.getMaNhanVien())) {
            throw new RuntimeException("Mã nhân viên đã tồn tại");
        }

        if (nhanVien.getTrangThai() == null) {
            nhanVien.setTrangThai(true);
        }

        return nhanVienRepository.save(nhanVien);
    }

    // Cập nhật nhân viên
    public NhanVien update(Integer id, NhanVien nhanVien) {
        NhanVien old = getById(id);

        old.setVaiTro(nhanVien.getVaiTro());
        old.setMaNhanVien(nhanVien.getMaNhanVien());
        old.setTenNhanVien(nhanVien.getTenNhanVien());
        old.setGioiTinh(nhanVien.getGioiTinh());
        old.setTaiKhoan(nhanVien.getTaiKhoan());
        old.setMatKhau(nhanVien.getMatKhau());
        old.setSdt(nhanVien.getSdt());
        old.setEmail(nhanVien.getEmail());
        old.setDiaChi(nhanVien.getDiaChi());
        old.setTrangThai(nhanVien.getTrangThai());

        return nhanVienRepository.save(old);
    }

    // Xóa nhân viên
    public void delete(Integer id) {
        if (!nhanVienRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nhân viên với ID = " + id);
        }
        nhanVienRepository.deleteById(id);
    }
}
