package com.example.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer id;
    private String maSanPham;
    private String tenSanPham;
    private String moTa;
    private String hinhAnh;
    private BigDecimal donGia;
    private BigDecimal giaGiam;
    private Integer phanTramGiam;
    private String thuongHieu;
    private Integer soLuongTon;
    private Boolean trangThai;

    // Constructor để map từ Entity
    public ProductDTO(Integer id, String maSanPham, String tenSanPham,
                      String hinhAnh, BigDecimal donGia, String thuongHieu) {
        this.id = id;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.hinhAnh = hinhAnh;
        this.donGia = donGia;
        this.thuongHieu = thuongHieu;
    }

    // Tính giá giảm
    public BigDecimal getGiaGiam() {
        if (phanTramGiam != null && phanTramGiam > 0) {
            BigDecimal discount = donGia.multiply(BigDecimal.valueOf(phanTramGiam))
                    .divide(BigDecimal.valueOf(100));
            return donGia.subtract(discount);
        }
        return donGia;
    }

    // Format giá tiền kiểu VN
    public String getFormattedPrice() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(donGia) + "₫";
    }

    public String getFormattedSalePrice() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(getGiaGiam()) + "₫";
    }

    // Getter cho imageUrl để tương thích với template hiện tại
    public String getImageUrl() {
        return hinhAnh;
    }

    // Getter cho name để tương thích
    public String getName() {
        return tenSanPham;
    }

    // Getter cho salePrice và originalPrice (tương thích với model cũ)
    public String getSalePrice() {
        return getFormattedSalePrice();
    }

    public String getOriginalPrice() {
        return getFormattedPrice();
    }
}