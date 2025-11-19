package com.example.datn.controller;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.Random;

@Controller
public class QuenMatKhauController {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/quenmatkhau")
    public String showQuenMatKhauForm() {
        return "quenmatkhau";
    }

    @PostMapping("/quenmatkhau")
    public String resetPassword(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập email");
            return "redirect:/quenmatkhau";
        }

        // Tìm tài khoản theo email
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findAll().stream()
                .filter(nv -> email.equals(nv.getEmail()))
                .findFirst();

        if (nhanVienOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại trong hệ thống");
            return "redirect:/quenmatkhau";
        }

        NhanVien nhanVien = nhanVienOpt.get();

        // Tạo mật khẩu ngẫu nhiên 6 số
        String newPassword = generateRandomPassword();

        // Cập nhật mật khẩu mới
        nhanVien.setMatKhau(newPassword);
        nhanVienRepository.save(nhanVien);

        // Gửi email
        try {
            emailService.sendNewPassword(email, newPassword);
            redirectAttributes.addFlashAttribute("success",
                    "Mật khẩu mới đã được gửi về email: " + maskEmail(email));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Đã xảy ra lỗi khi gửi email. Vui lòng thử lại sau.");
            return "redirect:/quenmatkhau";
        }

        return "redirect:/login";
    }


    private String generateRandomPassword() {
        Random random = new Random();
        int password = 100000 + random.nextInt(900000); // Số từ 100000 đến 999999
        return String.valueOf(password);
    }
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return username.charAt(0) + "***@" + domain;
        }

        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + "@" + domain;
    }
}