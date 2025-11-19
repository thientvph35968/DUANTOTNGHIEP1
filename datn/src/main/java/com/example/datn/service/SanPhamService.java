package com.example.datn.service;

import com.example.datn.dto.ProductDTO;
import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    // Lấy tất cả sản phẩm
    public List<ProductDTO> getAllProducts() {
        List<SanPham> sanPhams = sanPhamRepository.findActiveProducts();
        return convertToDTO(sanPhams);
    }

    // Lấy sản phẩm SALE
    public List<ProductDTO> getSaleProducts() {
        List<SanPham> sanPhams = sanPhamRepository.findSaleProducts();
        List<ProductDTO> dtos = convertToDTO(sanPhams);

        // Thêm % giảm giá cho sản phẩm SALE (ví dụ: 20%)
        dtos.forEach(dto -> {
            dto.setPhanTramGiam(20);
        });

        return dtos;
    }

    // Lấy chi tiết sản phẩm
    public ProductDTO getProductDetail(Integer id) {
        SanPham sanPham = sanPhamRepository.findByIdWithDetails(id);
        if (sanPham == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }
        return convertToDTO(sanPham);
    }

    // Convert List Entity sang List DTO
    private List<ProductDTO> convertToDTO(List<SanPham> sanPhams) {
        return sanPhams.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert Entity sang DTO
    private ProductDTO convertToDTO(SanPham sp) {
        ProductDTO dto = new ProductDTO();
        dto.setId(sp.getId());
        dto.setMaSanPham(sp.getMaSanPham());
        dto.setTenSanPham(sp.getTenSanPham());
        dto.setMoTa(sp.getMoTa());

        // Xử lý đường dẫn ảnh
        if (sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty()) {
            dto.setHinhAnh("/" + sp.getHinhAnh());
        } else {
            dto.setHinhAnh("/default.jpg"); // Ảnh mặc định
        }

        // Lấy tên thương hiệu
        dto.setThuongHieu(sp.getThuongHieu() != null ?
                sp.getThuongHieu().getTenThuongHieu() : "Chưa có");
        dto.setTrangThai(sp.getTrangThai());

        // Lấy giá và số lượng từ chi tiết sản phẩm
        if (sp.getChiTietList() != null && !sp.getChiTietList().isEmpty()) {
            // Lấy giá từ chi tiết sản phẩm đầu tiên
            SanPhamChiTiet firstDetail = sp.getChiTietList().get(0);
            dto.setDonGia(firstDetail.getDonGia());

            // Tính tổng số lượng tồn từ tất cả các biến thể
            int tongSoLuong = sp.getChiTietList().stream()
                    .filter(ct -> ct.getSoLuong() != null)
                    .mapToInt(SanPhamChiTiet::getSoLuong)
                    .sum();
            dto.setSoLuongTon(tongSoLuong);
        } else {
            dto.setDonGia(java.math.BigDecimal.ZERO);
            dto.setSoLuongTon(0);
        }

        return dto;
    }

    // Lấy sản phẩm theo thương hiệu
    public List<ProductDTO> getProductsByBrand(Integer brandId) {
        List<SanPham> sanPhams = sanPhamRepository.findByThuongHieu(brandId);
        return convertToDTO(sanPhams);
    }
}