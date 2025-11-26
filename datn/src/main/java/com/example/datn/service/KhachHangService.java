package com.example.datn.service;

import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    public Optional<KhachHang> getKhachHangById(Integer id) {
        return khachHangRepository.findById(id);
    }

    public KhachHang saveKhachHang(KhachHang khachHang) {
        if (khachHang.getNgayTao() == null) {
            khachHang.setNgayTao(LocalDate.now());
        }
        if (khachHang.getTrangThai() == null) {
            khachHang.setTrangThai(true);
        }
        return khachHangRepository.save(khachHang);
    }

    public void deleteKhachHang(Integer id) {
        khachHangRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return khachHangRepository.existsByEmail(email);
    }

    public boolean existsBySdt(String sdt) {
        return khachHangRepository.existsBySdt(sdt);
    }
}