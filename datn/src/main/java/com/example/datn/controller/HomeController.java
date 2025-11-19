package com.example.datn.controller;

import com.example.datn.dto.ProductDTO;
import com.example.datn.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private SanPhamService sanPhamService;

    // Trang chủ
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Lấy sản phẩm SALE từ DB
        List<ProductDTO> saleProducts = sanPhamService.getSaleProducts();
        model.addAttribute("saleProducts", saleProducts);
        return "home";
    }

    // Chi tiết sản phẩm
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        try {
            ProductDTO product = sanPhamService.getProductDetail(id);
            model.addAttribute("product", product);
            return "product-detail";
        } catch (Exception e) {
            model.addAttribute("error", "Không tìm thấy sản phẩm");
            return "redirect:/";
        }
    }

    // Danh sách sản phẩm mới
    @GetMapping("/collections/new")
    public String newProducts(Model model) {
        List<ProductDTO> products = sanPhamService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Sản phẩm mới");
        return "product-list";
    }

    // Trang sản phẩm giảm giá
    @GetMapping("/collections/giamgia")
    public String saleProducts(Model model) {
        List<ProductDTO> products = sanPhamService.getSaleProducts();
        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Sản phẩm SALE");
        return "product-list";
    }

    @GetMapping("/giohang")
    public String giohang() {
        return "giohang";
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