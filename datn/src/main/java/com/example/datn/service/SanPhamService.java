package com.example.datn.service;

import com.example.datn.dto.ProductDTO;
import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    public List<ProductDTO> getAllProducts() {
        List<SanPham> sanPhams = sanPhamRepository.findActiveProducts();
        return convertToDTOList(sanPhams);
    }

    public List<ProductDTO> getSaleProducts() {
        List<SanPham> sanPhams = sanPhamRepository.findSaleProducts();
        List<ProductDTO> dtos = convertToDTOList(sanPhams);
        dtos.forEach(dto -> dto.setPhanTramGiam(20)); // Ví dụ giảm giá 20%
        return dtos;
    }

    public ProductDTO getProductDetail(Integer id) {
        SanPham sanPham = sanPhamRepository.findByIdWithDetails(id);
        if (sanPham == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }
        return convertToDetailDTO(sanPham);
    }

    private List<ProductDTO> convertToDTOList(List<SanPham> sanPhams) {
        if (sanPhams == null || sanPhams.isEmpty()) {
            return Collections.emptyList();
        }
        return sanPhams.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Phương thức chuyển đổi an toàn, chống NullPointerException.
     * Sẽ gán giá trị mặc định nếu các trường liên quan là null.
     */
    private ProductDTO convertToDTO(SanPham sp) {
        ProductDTO dto = new ProductDTO();
        if (sp == null) {
            return dto; // Trả về DTO trống nếu sản phẩm đầu vào là null
        }

        dto.setId(sp.getId());
        dto.setMaSanPham(sp.getMaSanPham());
        dto.setTenSanPham(sp.getTenSanPham());
        dto.setMoTa(sp.getMoTa());
        dto.setTrangThai(sp.getTrangThai());

        // Xử lý hình ảnh an toàn
        if (sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty()) {
            dto.setHinhAnh("/" + sp.getHinhAnh());
        } else {
            dto.setHinhAnh("/default.jpg"); // Ảnh mặc định
        }

        // Xử lý thương hiệu an toàn
        dto.setThuongHieu(sp.getThuongHieu() != null ? sp.getThuongHieu().getTenThuongHieu() : "N/A");

        // Xử lý giá và số lượng từ chi tiết sản phẩm một cách an toàn
        if (sp.getChiTietList() != null && !sp.getChiTietList().isEmpty()) {
            SanPhamChiTiet firstDetail = sp.getChiTietList().get(0);
            
            // Gán ID chi tiết sản phẩm để thêm vào giỏ hàng
            dto.setSanPhamChiTietId(firstDetail.getId());

            // Gán đơn giá, nếu null thì mặc định là 0
            dto.setDonGia(firstDetail.getDonGia() != null ? firstDetail.getDonGia() : BigDecimal.ZERO);

            // Tính tổng số lượng tồn kho, bỏ qua các chi tiết có số lượng null
            int tongSoLuong = sp.getChiTietList().stream()
                    .filter(ct -> ct.getSoLuong() != null)
                    .mapToInt(SanPhamChiTiet::getSoLuong)
                    .sum();
            dto.setSoLuongTon(tongSoLuong);
        } else {
            // Nếu không có chi tiết nào, gán giá trị mặc định
            dto.setSanPhamChiTietId(null);
            dto.setDonGia(BigDecimal.ZERO);
            dto.setSoLuongTon(0);
        }

        return dto;
    }

    private ProductDTO convertToDetailDTO(SanPham sp) {
        // Tạm thời có thể dùng chung logic chuyển đổi an toàn
        return convertToDTO(sp);
    }

    public List<ProductDTO> getProductsByBrand(Integer brandId) {
        List<SanPham> sanPhams = sanPhamRepository.findByThuongHieu(brandId);
        return convertToDTOList(sanPhams);
    }
}
