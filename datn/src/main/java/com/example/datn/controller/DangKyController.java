package com.example.datn.controller;

import com.example.datn.dto.DangKyRequest;
import com.example.datn.service.DangKyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DangKyController {

    @Autowired
    private DangKyService dangKyService;

    // ✅ Hiển thị form đăng ký
    @GetMapping("/dangky")
    public String showDangKyForm(Model model) {
        if (!model.containsAttribute("dangKyRequest")) {
            model.addAttribute("dangKyRequest", new DangKyRequest());
        }
        return "dangky"; // tên file HTML: dangky.html
    }

    // ✅ Xử lý khi người dùng nhấn "Đăng ký"
    @PostMapping("/dangky")
    public String dangKy(
            @Valid @ModelAttribute("dangKyRequest") DangKyRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // ❌ Nếu có lỗi validate => quay lại form
        if (bindingResult.hasErrors()) {
            model.addAttribute("dangKyRequest", request);
            return "dangky";
        }

        // ✅ Gọi service xử lý đăng ký
        String result = dangKyService.dangKy(request);

        if ("SUCCESS".equals(result)) {
            // ✅ Gửi thông báo thành công qua redirect
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } else {
            // ⚠️ Nếu thất bại (VD: tài khoản đã tồn tại)
            redirectAttributes.addFlashAttribute("error", result);
            redirectAttributes.addFlashAttribute("dangKyRequest", request);
            return "redirect:/dangky";
        }
    }
}
