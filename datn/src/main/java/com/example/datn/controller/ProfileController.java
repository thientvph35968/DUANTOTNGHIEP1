package com.example.datn.controller;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Lấy username hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Tìm nhân viên
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByTaiKhoan(username);

        if (nhanVienOpt.isEmpty()) {
            return "redirect:/login";
        }

        NhanVien nhanVien = nhanVienOpt.get();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("username", username);

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute NhanVien updatedNhanVien,
            RedirectAttributes redirectAttributes) {

        // Lấy username hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Tìm nhân viên hiện tại
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByTaiKhoan(username);

        if (nhanVienOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng");
            return "redirect:/profile";
        }

        NhanVien nhanVien = nhanVienOpt.get();

        // Cập nhật thông tin (không cho phép đổi tài khoản, mật khẩu, vai trò)
        nhanVien.setTenNhanVien(updatedNhanVien.getTenNhanVien());
        nhanVien.setGioiTinh(updatedNhanVien.getGioiTinh());
        nhanVien.setSdt(updatedNhanVien.getSdt());
        nhanVien.setEmail(updatedNhanVien.getEmail());
        nhanVien.setDiaChi(updatedNhanVien.getDiaChi());

        // Lưu vào DB
        nhanVienRepository.save(nhanVien);

        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @ModelAttribute("matKhauCu") String matKhauCu,
            @ModelAttribute("matKhauMoi") String matKhauMoi,
            @ModelAttribute("xacNhanMatKhau") String xacNhanMatKhau,
            RedirectAttributes redirectAttributes) {

        // Lấy username hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Tìm nhân viên
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByTaiKhoan(username);

        if (nhanVienOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng");
            return "redirect:/profile";
        }

        NhanVien nhanVien = nhanVienOpt.get();

        // Validate
        if (!nhanVien.getMatKhau().equals(matKhauCu)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ không đúng");
            return "redirect:/profile";
        }

        if (matKhauMoi == null || matKhauMoi.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự");
            return "redirect:/profile";
        }

        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            redirectAttributes.addFlashAttribute("error", "Xác nhận mật khẩu không khớp");
            return "redirect:/profile";
        }

        // Cập nhật mật khẩu
        nhanVien.setMatKhau(matKhauMoi);
        nhanVienRepository.save(nhanVien);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}