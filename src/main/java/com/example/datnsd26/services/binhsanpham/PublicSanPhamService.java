package com.example.datnsd26.services.binhsanpham;

import com.example.datnsd26.controller.response.PublicSanPhamResponse;
import com.example.datnsd26.models.HinhAnh;
import com.example.datnsd26.models.MauSac;
import com.example.datnsd26.models.SanPham;
import com.example.datnsd26.models.SanPhamChiTiet;
import com.example.datnsd26.repository.SanPhamChiTietRepository;
import com.example.datnsd26.repository.SanPhamRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicSanPhamService {
    private final SanPhamRepositoty sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    public List<PublicSanPhamResponse> getAllProducts() {
        List<SanPham> sanPhamList = sanPhamRepository.findAll();
        return sanPhamList.stream().map(sanPham -> {
            // Lấy danh sách tất cả biến thể của sản phẩm A
            List<SanPhamChiTiet> danhSachBienThe = sanPhamChiTietRepository.findBySanPham(sanPham);

            if (danhSachBienThe.isEmpty()) {
                return null; // Nếu không có biến thể, bỏ qua sản phẩm này
            }

            // Chọn biến thể đầu tiên (hoặc random)
            SanPhamChiTiet bienTheDauTien = danhSachBienThe.get(0);

            return PublicSanPhamResponse.builder()
                    .id(sanPham.getId())
                    .tenSanPham(sanPham.getTenSanPham())
                    .hinhAnh(bienTheDauTien.getHinhAnh().stream().findFirst()
                            .map(HinhAnh::getTenAnh)
                            .orElse("default.jpg")) // Lấy ảnh đầu tiên
                    .giaBan(bienTheDauTien.getGiaBan()) // Lấy giá từ biến thể
                    .giaBanSauGiam(bienTheDauTien.getGiaBanSauGiam())
                    .idSanPhamChiTiet(bienTheDauTien.getId()) // ✅ Lưu ID biến thể sản phẩm
                    .build();
        }).filter(Objects::nonNull).toList();
    }


    public List<PublicSanPhamResponse> filterProducts(List<Long> filterBrand, List<Long> filterSole,
                                               List<Long> filterMaterial, List<Long> filterColor,
                                               List<Long> filterSize) {
        return sanPhamChiTietRepository.filterProducts(filterBrand, filterSole, filterMaterial, filterColor, filterSize);
    }


    // Phương thức lấy tất cả sản phẩm sắp xếp theo tên A-Z
    public List<PublicSanPhamResponse> getAllProductsSortedByNameAsc() {
        return sanPhamChiTietRepository.findAllSortedByNameAsc();
    }

    // Phương thức lấy tất cả sản phẩm sắp xếp theo tên Z-A
    public List<PublicSanPhamResponse> getAllProductsSortedByNameDesc() {
        return sanPhamChiTietRepository.findAllSortedByNameDesc();
    }

    // Phương thức lấy tất cả sản phẩm sắp xếp theo giá từ thấp đến cao
    public List<PublicSanPhamResponse> getAllProductsSortedByPriceAsc() {
        return sanPhamChiTietRepository.findAllSortedByPriceAsc();
    }

    // Phương thức lấy tất cả sản phẩm sắp xếp theo giá từ cao đến thấp
    public List<PublicSanPhamResponse> getAllProductsSortedByPriceDesc() {
        return sanPhamChiTietRepository.findAllSortedByPriceDesc();
    }
    // Phương thức chung xử lý theo yêu cầu sắp xếp từ frontend

}
