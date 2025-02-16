package com.example.datnsd26.Service;

import com.example.datnsd26.Entity.DiaChi;
import com.example.datnsd26.Entity.KhachHang;
import com.example.datnsd26.Entity.NhanVien;
import com.example.datnsd26.Respository.NhanVienRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NhanVienService {
    @Autowired
    NhanVienRepository nhanVienRepository;

    public List<NhanVien> getAll() {
        return nhanVienRepository.findAll();
    }
    public NhanVien Add(NhanVien nv) {
        return nhanVienRepository.save(nv);
    }
    public NhanVien deleteById(Integer id) {
        Optional<NhanVien> optional = nhanVienRepository.findById(id);
        return optional.map(o -> {
            nhanVienRepository.delete(o);
            return o;
        }).orElse(null);
    }

    @Transactional
    public NhanVien updateNhanVien(Integer id, NhanVien nvUpdate) {

        NhanVien existingNhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + id));


        existingNhanVien.setMaNhanvien(nvUpdate.getMaNhanvien());
        existingNhanVien.setTenNhanVien(nvUpdate.getTenNhanVien());
        existingNhanVien.setGioiTinh(nvUpdate.getGioiTinh());
        existingNhanVien.setHinhAnh(nvUpdate.getHinhAnh());
        existingNhanVien.setIdTaiKhoan(nvUpdate.getIdTaiKhoan());
        existingNhanVien.setTrangThai(nvUpdate.getTrangThai());
        existingNhanVien.setNgayCapNhat(nvUpdate.getNgayCapNhat());
        existingNhanVien.setNgaySinh(nvUpdate.getNgaySinh());
        existingNhanVien.setNgayTao(nvUpdate.getNgayTao());
        existingNhanVien.setDiaChi(nvUpdate.getDiaChi());

        // Lưu lại thay đổi
        return nhanVienRepository.save(existingNhanVien);
    }
}
