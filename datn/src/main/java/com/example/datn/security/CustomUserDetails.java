package com.example.datn.security;

import com.example.datn.entity.KhachHang; // Thay đổi import sang KhachHang
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    // 1. Thay thế NhanVien bằng KhachHang
    private final KhachHang khachHang;

    // Constructor mới nhận vào KhachHang
    public CustomUserDetails(KhachHang khachHang) {
        this.khachHang = khachHang;
    }
    public KhachHang getKhachHang() {
        return khachHang;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 2. Lấy VaiTro từ KhachHang
        if (khachHang.getVaiTro() == null) {
            System.out.println("❌ VAI TRÒ NULL!");
            return List.of();
        }

        String roleName = khachHang.getVaiTro().getTenVaiTro();
        String authority = "ROLE_" + roleName;
        System.out.println("✅ AUTHORITY: " + authority);

        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        // 3. Lấy MatKhau từ KhachHang
        String password = khachHang.getMatKhau();
        System.out.println("   - Password trả về: " + password);
        return password;
    }

    @Override
    public String getUsername() {
        // 4. Lấy TaiKhoan từ KhachHang
        return khachHang.getTaiKhoan();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Giả định KhachHang.getTrangThai() trả về kiểu Boolean (Ánh xạ từ cột BIT trong SQL)

        boolean enabled = false; // Mặc định là false

        if (khachHang.getTrangThai() != null) {
            // Nếu TrangThai là Boolean, chỉ cần kiểm tra xem nó có phải là true không
            // (true tương ứng với 1 trong SQL BIT)
            enabled = khachHang.getTrangThai().booleanValue();
        }

        System.out.println("   - isEnabled: " + enabled);
        return enabled;
    }
}