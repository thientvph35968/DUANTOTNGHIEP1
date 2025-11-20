package com.example.datn.controller;

import com.example.datn.entity.KhachHang; // Thay đổi từ NhanVien sang KhachHang
import com.example.datn.repository.KhachHangRepository; // Thay đổi từ NhanVienRepository
import com.example.datn.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.Random;

@Controller
public class QuenMatKhauController {

    @Autowired
    private KhachHangRepository khachHangRepository; // Đổi tên Repository

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

        // Tìm tài khoản Khách hàng theo email
        Optional<KhachHang> khachHangOpt = khachHangRepository.findAll().stream()
                .filter(kh -> email.equalsIgnoreCase(kh.getEmail())) // Sử dụng equalsIgnoreCase cho an toàn
                .findFirst();

        if (khachHangOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại trong hệ thống");
            return "redirect:/quenmatkhau";
        }

        KhachHang khachHang = khachHangOpt.get();
        String newPassword = generateRandomPassword();
        khachHang.setMatKhau(newPassword);
        khachHangRepository.save(khachHang);

        // Gửi email
        try {
            emailService.sendNewPassword(email, newPassword);
            redirectAttributes.addFlashAttribute("success",
                    "Mật khẩu mới đã được gửi về email: " + maskEmail(email));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Đã xảy ra lỗi khi gửi email. Vui lòng thử lại sau.");
            // Log lỗi chi tiết
            e.printStackTrace();
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