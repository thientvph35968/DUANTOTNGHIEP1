package com.example.datn.controller;

import com.example.datn.dto.*;
import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepository;
import com.example.datn.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class GioHangController {

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private KhachHangRepository khachHangRepository;

    /**
     * Lấy ID khách hàng từ user đang đăng nhập
     */
    private Optional<Integer> getCurrentKhachHangId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();
        }

        String username = auth.getName();
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(username);

        return khachHangOpt.map(KhachHang::getId);
    }

    /**
     * API: Lấy giỏ hàng hiện tại
     */
    @GetMapping
    public ResponseEntity<ApiResponse<GioHangDTO>> getCart() {
        try {
            Optional<Integer> khachHangIdOpt = getCurrentKhachHangId();
            if (khachHangIdOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Vui lòng đăng nhập"));
            }

            GioHangDTO cart = gioHangService.getCart(khachHangIdOpt.get());
            return ResponseEntity.ok(ApiResponse.success("Lấy giỏ hàng thành công", cart));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * API: Thêm sản phẩm vào giỏ
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<GioHangDTO>> addToCart(@RequestBody AddToCartRequest request) {
        try {
            Optional<Integer> khachHangIdOpt = getCurrentKhachHangId();
            if (khachHangIdOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng."));
            }

            ApiResponse<GioHangDTO> response = gioHangService.addToCart(khachHangIdOpt.get(), request);

            if (response.getSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * API: Cập nhật số lượng
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<GioHangDTO>> updateCart(@RequestBody UpdateCartRequest request) {
        try {
            Optional<Integer> khachHangIdOpt = getCurrentKhachHangId();
            if (khachHangIdOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Vui lòng đăng nhập"));
            }

            ApiResponse<GioHangDTO> response = gioHangService.updateCart(khachHangIdOpt.get(), request);

            if (response.getSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * API: Xóa sản phẩm khỏi giỏ
     */
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<ApiResponse<GioHangDTO>> removeFromCart(@PathVariable Integer id) {
        try {
            Optional<Integer> khachHangIdOpt = getCurrentKhachHangId();
            if (khachHangIdOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Vui lòng đăng nhập"));
            }

            ApiResponse<GioHangDTO> response = gioHangService.removeFromCart(khachHangIdOpt.get(), id);

            if (response.getSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * API: Xóa toàn bộ giỏ hàng
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        try {
            Optional<Integer> khachHangIdOpt = getCurrentKhachHangId();
            if (khachHangIdOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Vui lòng đăng nhập"));
            }

            ApiResponse<Void> response = gioHangService.clearCart(khachHangIdOpt.get());

            if (response.getSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }
}
