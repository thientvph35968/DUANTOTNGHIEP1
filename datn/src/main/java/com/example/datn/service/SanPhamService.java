package com.example.datn.service;

import com.example.datn.dto.ProductDTO;
import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    // ✅ Lấy chi tiết sản phẩm THỰC TỪ DB
    public ProductDTO getProductDetail(Integer id) {
        // Lấy sản phẩm từ DB với đầy đủ quan hệ
        SanPham sanPham = sanPhamRepository.findByIdWithDetails(id);

        if (sanPham == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }

        // Convert sang DTO với đầy đủ thông tin
        ProductDTO dto = convertToDetailDTO(sanPham);

        return dto;
    }

    // Convert List Entity sang List DTO
    private List<ProductDTO> convertToDTO(List<SanPham> sanPhams) {
        return sanPhams.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert Entity sang DTO (cho danh sách)
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
            dto.setHinhAnh("/default.jpg");
        }

        // Lấy tên thương hiệu
        dto.setThuongHieu(sp.getThuongHieu() != null ?
                sp.getThuongHieu().getTenThuongHieu() : "Chưa có");
        dto.setTrangThai(sp.getTrangThai());

        // Lấy giá và số lượng từ chi tiết sản phẩm
        if (sp.getChiTietList() != null && !sp.getChiTietList().isEmpty()) {
            SanPhamChiTiet firstDetail = sp.getChiTietList().get(0);
            dto.setDonGia(firstDetail.getDonGia());

            int tongSoLuong = sp.getChiTietList().stream()
                    .filter(ct -> ct.getSoLuong() != null)
                    .mapToInt(SanPhamChiTiet::getSoLuong)
                    .sum();
            dto.setSoLuongTon(tongSoLuong);
        } else {
            dto.setDonGia(BigDecimal.ZERO);
            dto.setSoLuongTon(0);
        }

        return dto;
    }

    // ✅ Convert Entity sang DTO CHI TIẾT (với đầy đủ thông tin)
    private ProductDTO convertToDetailDTO(SanPham sp) {
        ProductDTO dto = new ProductDTO();

        // Thông tin cơ bản
        dto.setId(sp.getId());
        dto.setMaSanPham(sp.getMaSanPham());
        dto.setTenSanPham(sp.getTenSanPham());
        dto.setMoTa(sp.getMoTa());
        dto.setTrangThai(sp.getTrangThai());

        // Xử lý hình ảnh
        if (sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty()) {
            // Kiểm tra xem đã có "/" chưa
            if (sp.getHinhAnh().startsWith("/")) {
                dto.setHinhAnh(sp.getHinhAnh());
            } else {
                dto.setHinhAnh("/" + sp.getHinhAnh());
            }
        } else {
            dto.setHinhAnh("/default.jpg");
        }

        // Thương hiệu
        if (sp.getThuongHieu() != null) {
            dto.setThuongHieu(sp.getThuongHieu().getTenThuongHieu());
        } else {
            dto.setThuongHieu("Chưa xác định");
        }

        // Lấy thông tin giá và số lượng từ chi tiết sản phẩm
        if (sp.getChiTietList() != null && !sp.getChiTietList().isEmpty()) {
            // Lọc các chi tiết còn hoạt động
            List<SanPhamChiTiet> activeDetails = sp.getChiTietList().stream()
                    .filter(ct -> ct.getTrangThai() != null && ct.getTrangThai())
                    .collect(Collectors.toList());

            if (!activeDetails.isEmpty()) {
                // Lấy giá từ chi tiết đầu tiên (hoặc giá trung bình)
                SanPhamChiTiet firstDetail = activeDetails.get(0);
                dto.setDonGia(firstDetail.getDonGia() != null ?
                        firstDetail.getDonGia() : BigDecimal.ZERO);

                // Tính tổng số lượng tồn kho
                int tongSoLuong = activeDetails.stream()
                        .filter(ct -> ct.getSoLuong() != null)
                        .mapToInt(SanPhamChiTiet::getSoLuong)
                        .sum();
                dto.setSoLuongTon(tongSoLuong);

                // Log để debug
                System.out.println("=== CHI TIẾT SẢN PHẨM ID: " + sp.getId() + " ===");
                System.out.println("Tên: " + sp.getTenSanPham());
                System.out.println("Giá: " + dto.getDonGia());
                System.out.println("Số lượng tồn: " + tongSoLuong);
                System.out.println("Số biến thể: " + activeDetails.size());
            } else {
                dto.setDonGia(BigDecimal.ZERO);
                dto.setSoLuongTon(0);
            }
        } else {
            dto.setDonGia(BigDecimal.ZERO);
            dto.setSoLuongTon(0);
            System.out.println("⚠️ Sản phẩm " + sp.getId() + " không có chi tiết!");
        }

        return dto;
    }

    // Lấy sản phẩm theo thương hiệu
    public List<ProductDTO> getProductsByBrand(Integer brandId) {
        List<SanPham> sanPhams = sanPhamRepository.findByThuongHieu(brandId);
        return convertToDTO(sanPhams);
    }
}