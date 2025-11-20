package com.example.datn.controller;

import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepository;
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
    private KhachHangRepository khachHangRepository;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Lấy username hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // SỬA LỖI: Đổi tên biến từ nhanVienOpt sang khachHangOpt để phù hợp với kiểu KhachHang
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(username);

        if (khachHangOpt.isEmpty()) {
            return "redirect:/login";
        }

        KhachHang khachHang = khachHangOpt.get();
        model.addAttribute("khachHang", khachHang);
        model.addAttribute("username", username);

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            // 1. SỬA: Đổi tên biến local cho rõ ràng hơn (updatedKhachHang)
            @ModelAttribute KhachHang updatedKhachHang,
            RedirectAttributes redirectAttributes) {

        // Lấy username hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 2. SỬA: Đổi tên biến Optional (khachHangOpt)
        // 3. Đảm bảo KhachHangRepository được Autowired
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(username);

        if (khachHangOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng");
            return "redirect:/profile";
        }

        // 4. SỬA: Đổi tên biến local KhachHang
        KhachHang khachHang = khachHangOpt.get();

        // Cập nhật thông tin Khách hàng (không cho phép đổi tài khoản, mật khẩu, vai trò)
        // 5. SỬA: setTenNhanVien -> setTenKhachHang, dùng biến updatedKhachHang
        khachHang.setTenKhachHang(updatedKhachHang.getTenKhachHang());

        khachHang.setGioiTinh(updatedKhachHang.getGioiTinh());
        khachHang.setSdt(updatedKhachHang.getSdt());
        khachHang.setEmail(updatedKhachHang.getEmail());
        khachHang.setDiaChi(updatedKhachHang.getDiaChi());

        // 6. SỬA: Sử dụng khachHangRepository để lưu
        khachHangRepository.save(khachHang);

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

        // 🛑 SỬA LỖI: Tìm Khách hàng và sử dụng khachHangRepository
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(username);

        if (khachHangOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng");
            return "redirect:/profile";
        }

        // 🛑 SỬA LỖI: Gán vào đối tượng KhachHang
        KhachHang khachHang = khachHangOpt.get();

        // Validate
        // 🛑 SỬA LỖI: Kiểm tra mật khẩu của Khách hàng
        if (!khachHang.getMatKhau().equals(matKhauCu)) {
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
        khachHang.setMatKhau(matKhauMoi);
        // 🛑 SỬA LỖI: Lưu bằng khachHangRepository
        khachHangRepository.save(khachHang);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}