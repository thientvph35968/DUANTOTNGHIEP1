package com.example.datn.controller;

import com.example.datn.dto.ProductDTO;
import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepository;
import com.example.datn.security.CustomUserDetails;
import com.example.datn.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @ModelAttribute
    public void addUserToModel(Model model, Authentication authentication) {
        // *** SỬA LỖI Ở ĐÂY: Thêm kiểm tra `instanceof` để tránh ClassCastException ***
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            KhachHang khachHang = userDetails.getKhachHang();
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", khachHang.getTaiKhoan());
            model.addAttribute("loggedInUser", khachHang.getTenKhachHang());
            if (khachHang.getVaiTro() != null) {
                model.addAttribute("userRole", khachHang.getVaiTro().getTenVaiTro());
            }
        } else {
            model.addAttribute("isAuthenticated", false);
        }
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<ProductDTO> saleProducts = sanPhamService.getSaleProducts();
        model.addAttribute("saleProducts", saleProducts);
        return "home";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        logger.info("Đang truy cập trang chi tiết sản phẩm với ID: {}", id);
        try {
            ProductDTO product = sanPhamService.getProductDetail(id);
            if (product == null) {
                logger.warn("Không tìm thấy sản phẩm với ID: {}", id);
                return "redirect:/";
            }
            model.addAttribute("product", product);
            return "SanPhamChiTiet";
        } catch (Exception e) {
            logger.error("Lỗi khi lấy chi tiết sản phẩm với ID {}: {}", id, e.getMessage());
            return "redirect:/";
        }
    }

    // ... các phương thức khác giữ nguyên ...
    @GetMapping("/collections/new")
    public String newProducts(Model model) {
        List<ProductDTO> products = sanPhamService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Sản phẩm mới");
        return "product-list";
    }

    @GetMapping("/collections/giamgia")
    public String saleProducts(Model model) {
        List<ProductDTO> products = sanPhamService.getSaleProducts();
        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Sản phẩm SALE");
        return "product-list";
    }


    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/quanlynguoidung")
    public String quanlynguoidung() {
        return "quanlynguoidung";
    }

    @GetMapping("/quanlysanpham")
    public String quanlysanpham() {
        return "quanlysanpham";
    }

    @GetMapping("/quanlykhachhang")
    public String quanLyKhachHang() {
        return "redirect:/khachhang";
    }

    @GetMapping("/quanlyhoadon")
    public String quanlyhoadon() {
        return "quanlyhoadon";
    }

    @GetMapping("/quanlydanhmuc")
    public String quanLyDanhMuc() {
        return "quanlydanhmuc";
    }

    @GetMapping("/quanlythuonghieu")
    public String quanLyThuongHieu() {
        return "quanlythuonghieu";
    }

    @GetMapping("/quanlybanhang")
    public String quanlybanhang() {
        return "quanlybanhang";
    }

    @GetMapping("/quanlymagiam")
    public String quanLyMaGiam() {
        return "quanlymagiam";
    }

    @GetMapping("/collections/aokhoacparka")
    public String showParkaProducts(Model model) {
        return "aokhoacparka";
    }

    @GetMapping("/collections/aokhoacsomi")
    public String showsomiProducts(Model model) {
        return "aokhoacsomi";
    }

    @GetMapping("/collections/aokhoacjean")
    public String showjeanProducts(Model model) {
        return "aokhoacjean";
    }

    @GetMapping("/collections/aokhoackaki")
    public String showkakiProducts(Model model) {
        return "aokhoackaki";
    }

    @GetMapping("/collections/aokhoacdu")
    public String showduProducts(Model model) {
        return "aokhoacdu";
    }

    @GetMapping("/collections/aokhoacbomber")
    public String showbomberProducts(Model model) {
        return "aokhoacbomber";
    }

    @GetMapping("/collections/aokhoachoodie")
    public String showhoodieProducts(Model model) {
        return "aokhoachoodie";
    }

    @GetMapping("/collections/aokhoacthethao")
    public String showttProducts(Model model) {
        return "aokhoacthethao";
    }

    @GetMapping("/collections/khampha")
    public String showxtProducts(Model model) {
        return "khampha";
    }

    @GetMapping("/collections/nike")
    public String showthProducts(Model model) {
        return "nike";
    }

    @GetMapping("/collections/adidas")
    public String showaProducts(Model model) {
        return "adidas";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
