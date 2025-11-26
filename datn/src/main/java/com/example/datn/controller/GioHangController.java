package com.example.datn.controller;

import com.example.datn.dto.AddToCartRequest;
import com.example.datn.dto.CartItemDTO;
import com.example.datn.dto.CartResponse;
import com.example.datn.dto.CheckoutFormDTO;
import com.example.datn.dto.UpdateCartRequest;
import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepository;
import com.example.datn.service.GioHangService;
import com.example.datn.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class GioHangController {

    private final GioHangService gioHangService;
    private final KhachHangRepository khachHangRepository;
    private static final Logger logger = LoggerFactory.getLogger(GioHangController.class);

    @ModelAttribute
    public void addUserToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            KhachHang khachHang = userDetails.getKhachHang();
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", khachHang.getTaiKhoan());
            model.addAttribute("loggedInUser", khachHang.getTenKhachHang());
        } else {
            model.addAttribute("isAuthenticated", false);
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0₫";
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "₫";
    }

    private KhachHang getCurrentKhachHang() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getKhachHang();
        }
        return null;
    }

    @GetMapping("/api/cart/count")
    @ResponseBody
    public ResponseEntity<Integer> getCartItemCount() {
        KhachHang khachHang = getCurrentKhachHang();
        if (khachHang == null) {
            return ResponseEntity.ok(0);
        }
        Integer count = gioHangService.getCartItemCount(khachHang.getId());
        return ResponseEntity.ok(count);
    }

    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request) {
        KhachHang khachHang = getCurrentKhachHang();
        if (khachHang == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CartResponse.builder().success(false).message("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng").build());
        }
        CartResponse response = gioHangService.addToCart(khachHang.getId(), request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/giohang")
    public String showGioHang(Model model) {
        KhachHang khachHang = getCurrentKhachHang();
        if (khachHang == null) {
            return "redirect:/login";
        }

        List<CartItemDTO> cartItems = gioHangService.getCartItems(khachHang.getId());
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> new BigDecimal(item.getTongTien().replaceAll("[^\\d]", "")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("userPhone", khachHang.getSdt());
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", formatCurrency(totalAmount));

        return "giohang";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        KhachHang khachHang = getCurrentKhachHang();
        if (khachHang == null) {
            return "redirect:/login";
        }

        List<CartItemDTO> cartItems = gioHangService.getCartItems(khachHang.getId());
        if (cartItems.isEmpty()) {
            return "redirect:/giohang";
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> new BigDecimal(item.getTongTien().replaceAll("[^\\d]", "")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Tạo và điền sẵn DTO cho form
        CheckoutFormDTO checkoutForm = new CheckoutFormDTO();
        checkoutForm.setTenNguoiNhan(khachHang.getTenKhachHang());
        checkoutForm.setSdtNhanHang(khachHang.getSdt());
        checkoutForm.setEmailNhanHang(khachHang.getEmail());
        model.addAttribute("checkoutForm", checkoutForm);
        
        model.addAttribute("khachHang", khachHang);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subTotal", totalAmount);
        model.addAttribute("formattedSubTotal", formatCurrency(totalAmount));

        return "checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(@ModelAttribute("checkoutForm") CheckoutFormDTO checkoutForm,
                             RedirectAttributes redirectAttributes) {
        KhachHang khachHang = getCurrentKhachHang();
        if (khachHang == null) {
            return "redirect:/login";
        }

        try {
            gioHangService.placeOrder(khachHang, checkoutForm);
            redirectAttributes.addFlashAttribute("orderSuccess", "Đặt hàng thành công! Cảm ơn bạn đã mua sắm.");
            return "redirect:/";
        } catch (IllegalStateException e) {
            logger.error("Lỗi khi đặt hàng (dữ liệu không hợp lệ): {}", e.getMessage());
            redirectAttributes.addFlashAttribute("orderError", e.getMessage());
            return "redirect:/checkout";
        } catch (Exception e) {
            logger.error("Lỗi không mong muốn khi đặt hàng: ", e);
            redirectAttributes.addFlashAttribute("orderError", "Đã có lỗi hệ thống xảy ra. Vui lòng thử lại sau.");
            return "redirect:/checkout";
        }
    }

    @PutMapping("/api/cart/update")
    @ResponseBody
    public ResponseEntity<CartResponse> updateQuantity(@RequestBody UpdateCartRequest request) {
        KhachHang khachHang = getCurrentKhachHang();
        if (khachHang == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CartResponse.builder().success(false).message("Vui lòng đăng nhập").build());
        }
        CartResponse response = gioHangService.updateQuantity(request.getGioHangChiTietId(), request.getSoLuong());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/cart/remove/{id}")
    @ResponseBody
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Integer id) {
        KhachHang khachHang = getCurrentKhachHang();
        if (khachHang == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CartResponse.builder().success(false).message("Vui lòng đăng nhập").build());
        }
        CartResponse response = gioHangService.removeFromCart(id);
        return ResponseEntity.ok(response);
    }
}
