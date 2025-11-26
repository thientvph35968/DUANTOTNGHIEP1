package com.example.datn.controller;

import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepository;
import com.example.datn.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/khachhang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @GetMapping
    public String listKhachHang(Model model) {
        List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
        model.addAttribute("khachHangs", khachHangs);
        model.addAttribute("khachHang", new KhachHang());
        return "quanlykhachhang";
    }

    @PostMapping("/add")
    public String addKhachHang(@ModelAttribute KhachHang khachHang, RedirectAttributes redirectAttributes) {
        try {
            khachHangService.saveKhachHang(khachHang);
            redirectAttributes.addFlashAttribute("success", "Thêm khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/khachhang";
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public KhachHang getKhachHang(@PathVariable Integer id) {
        return khachHangService.getKhachHangById(id).orElse(null);
    }

    @PostMapping("/update")
    public String updateKhachHang(@ModelAttribute KhachHang khachHang, RedirectAttributes redirectAttributes) {
        try {
            khachHangService.saveKhachHang(khachHang);
            redirectAttributes.addFlashAttribute("success", "Cập nhật khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/khachhang";
    }

    @GetMapping("/delete/{id}")
    public String deleteKhachHang(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            khachHangService.deleteKhachHang(id);
            redirectAttributes.addFlashAttribute("success", "Xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/khachhang";
    }

    @GetMapping("/khachhang/check-duplicate")
    @ResponseBody
    public Map<String, Boolean> checkDuplicate(
            @RequestParam String maKhachHang,
            @RequestParam String email,
            @RequestParam String sdt,
            @RequestParam(required = false) Integer excludeId) {

        boolean duplicateMa;
        boolean duplicateEmail;
        boolean duplicateSdt;

        if (excludeId != null) {
            duplicateMa = khachHangRepository.existsByMaKhachHangAndIdNot(maKhachHang, excludeId);
            duplicateEmail = khachHangRepository.existsByEmailAndIdNot(email, excludeId);
            duplicateSdt = khachHangRepository.existsBySdtAndIdNot(sdt, excludeId);
        } else {
            duplicateMa = khachHangRepository.existsByMaKhachHang(maKhachHang);
            duplicateEmail = khachHangRepository.existsByEmail(email);
            duplicateSdt = khachHangRepository.existsBySdt(sdt);
        }

        Map<String, Boolean> result = new HashMap<>();
        result.put("duplicateMa", duplicateMa);
        result.put("duplicateEmail", duplicateEmail);
        result.put("duplicateSdt", duplicateSdt);

        return result;
    }
}