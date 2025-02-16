package com.example.datnsd26.Service;

import com.example.datnsd26.Entity.KhachHang;
import com.example.datnsd26.Respository.KhachHangRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KhachHangService {
    @Autowired
    KhachHangRepository khachHangRepository;

    public List<KhachHang> getAll() {
        return khachHangRepository.findAll();
    }
    public KhachHang Add(KhachHang kh) {
        return khachHangRepository.save(kh);
    }
    public KhachHang deleteById(UUID id) {
        Optional<KhachHang> optional = khachHangRepository.findById(id);
        return optional.map(o -> {
            khachHangRepository.delete(o);
            return o;
        }).orElse(null);
    }

    @Transactional
    public KhachHang updateKhachHang(UUID id, KhachHang khUpdate) {

        KhachHang existingKhachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));


        existingKhachHang.setMaKhachHang(khUpdate.getMaKhachHang());
        existingKhachHang.setTenKhachHang(khUpdate.getTenKhachHang());
        existingKhachHang.setGioiTinh(khUpdate.getGioiTinh());
        existingKhachHang.setHinhAnh(khUpdate.getHinhAnh());
        existingKhachHang.setIdDiaChi(khUpdate.getIdDiaChi());
        existingKhachHang.setIdTaiKhoan(khUpdate.getIdTaiKhoan());
        existingKhachHang.setTrangThai(khUpdate.getTrangThai());
        existingKhachHang.setNgayCapNhat(khUpdate.getNgayCapNhat());
        existingKhachHang.setNgaySinh(khUpdate.getNgaySinh());
        existingKhachHang.setNgayTao(khUpdate.getNgayTao());

        // Lưu lại thay đổi
        return khachHangRepository.save(existingKhachHang);
    }


}
