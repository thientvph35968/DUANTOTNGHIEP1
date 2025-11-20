package com.example.datn.controller;

import com.example.datn.dto.*;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.*;
import com.example.datn.service.DatHangService;
import com.example.datn.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class DatHangController {

    @Autowired
    private DatHangService datHangService;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private DiaChiGiaoHangRepository diaChiGiaoHangRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private PhuongThucGiaoHangRepository phuongThucGiaoHangRepository;

    /**
     * Lấy ID khách hàng từ user đang đăng nhập
     */
    private Integer getCurrentKhachHangId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByTaiKhoan(username);
        if (nhanVienOpt.isEmpty()) {
            throw new RuntimeException("Vui lòng đăng nhập");
        }

        return nhanVienOpt.get().getId();
    }

    /**
     * Hiển thị trang thanh toán
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model) {
        try {
            Integer khachHangId = getCurrentKhachHangId();

            // Lấy giỏ hàng
            GioHangDTO cart = gioHangService.getCart(khachHangId);
            if (cart.getItems().isEmpty()) {
                return "redirect:/giohang";
            }

            // Lấy danh sách địa chỉ
            model.addAttribute("diaChiList",
                    diaChiGiaoHangRepository.findByKhachHangIdAndTrangThai(khachHangId, true));

            // Lấy phương thức thanh toán
            model.addAttribute("phuongThucThanhToanList",
                    phuongThucThanhToanRepository.findByTrangThai(true));

            // Lấy phương thức giao hàng
            model.addAttribute("phuongThucGiaoHangList",
                    phuongThucGiaoHangRepository.findByTrangThai(true));

            model.addAttribute("cart", cart);

            return "checkout";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    /**
     * API: Xử lý đặt hàng
     */
    @PostMapping("/api/checkout")
    @ResponseBody
    public ResponseEntity<ApiResponse<HoaDonDTO>> checkout(@RequestBody CheckoutRequest request) {
        try {
            Integer khachHangId = getCurrentKhachHangId();
            ApiResponse<HoaDonDTO> response = datHangService.checkout(khachHangId, request);

            if (response.getSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        }
    }

    /**
     * Trang xác nhận đơn hàng thành công
     */
    @GetMapping("/order/success/{maHoaDon}")
    public String orderSuccess(@PathVariable String maHoaDon, Model model) {
        // Lấy thông tin đơn hàng và hiển thị
        model.addAttribute("maHoaDon", maHoaDon);
        return "order-success";
    }
}