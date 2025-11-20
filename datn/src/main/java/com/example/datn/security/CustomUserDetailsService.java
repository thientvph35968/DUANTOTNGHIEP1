package com.example.datn.security;

import com.example.datn.entity.KhachHang; // Import Entity KhachHang
import com.example.datn.repository.KhachHangRepository; // Import Repository KhachHang
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private KhachHangRepository khachHangRepository; // 1. Thay thế NhanVienRepository bằng KhachHangRepository

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== BẮT ĐẦU TÌM USER KHACHHANG: " + username + " ===");

        // 2. Tìm kiếm KhachHang theo TaiKhoan
        KhachHang khachHang = khachHangRepository.findByTaiKhoan(username)
                .orElseThrow(() -> {
                    System.out.println("❌ KHÔNG TÌM THẤY KHACHHANG: " + username);
                    return new UsernameNotFoundException("Không tìm thấy user: " + username);
                });

        System.out.println("✅ ĐÃ TÌM THẤY KHACHHANG:");
        System.out.println("   - ID: " + khachHang.getId());
        System.out.println("   - TaiKhoan: " + khachHang.getTaiKhoan());
        System.out.println("   - MatKhau: " + khachHang.getMatKhau());
        System.out.println("   - TrangThai: " + khachHang.getTrangThai());

        if (khachHang.getVaiTro() != null) {
            System.out.println("   - VaiTro: " + khachHang.getVaiTro().getTenVaiTro());
            System.out.println("   - ID_VaiTro: " + khachHang.getVaiTro().getIdVaiTro());
        } else {
            System.out.println("   - ⚠️ VAI TRÒ LÀ NULL!");
        }

        // 3. Sử dụng CustomUserDetails với đối tượng KhachHang
        return new CustomUserDetails(khachHang);
    }
}