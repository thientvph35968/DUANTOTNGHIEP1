package com.example.datn.controller;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.VaiTroRepository;
import com.example.datn.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/nhan-vien")
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("ListNhanVien", nhanVienService.getAll());
        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("dsVaiTro", vaiTroRepository.findAll());
        return "quanlynhanvien";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("ListNhanVien", nhanVienService.getAll());
        model.addAttribute("nhanVien", nhanVienService.getById(id));
        model.addAttribute("dsVaiTro", vaiTroRepository.findAll());
        return "quanlynhanvien";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("nhanVien") NhanVien nhanVien,
                       RedirectAttributes redirectAttributes) {
        try {
            if (nhanVien.getId() == null) {
                nhanVienService.create(nhanVien);
                redirectAttributes.addFlashAttribute("success", "Thêm nhân viên thành công");
            } else {
                nhanVienService.update(nhanVien.getId(), nhanVien);
                redirectAttributes.addFlashAttribute("success", "Cập nhật nhân viên thành công");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/nhan-vien";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id,
                         RedirectAttributes redirectAttributes) {
        try {
            nhanVienService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa nhân viên thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/nhan-vien";
    }
}
