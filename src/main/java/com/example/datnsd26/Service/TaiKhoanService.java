package com.example.datnsd26.Service;

import com.example.datnsd26.Api.TaiKhoanApi;
import com.example.datnsd26.Entity.NhanVien;
import com.example.datnsd26.Entity.TaiKhoan;
import com.example.datnsd26.Respository.TaiKhoanRespository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaiKhoanService {
    @Autowired
    TaiKhoanRespository taiKhoanRespository;

    public List<TaiKhoan> getAll() {
        return taiKhoanRespository.findAll();
    }
    public TaiKhoan Add(TaiKhoan tk) {
        return taiKhoanRespository.save(tk);
    }
    public TaiKhoan deleteById(UUID id) {
        Optional<TaiKhoan> optional = taiKhoanRespository.findById(id);
        return optional.map(o -> {
            taiKhoanRespository.delete(o);
            return o;
        }).orElse(null);
    }
    @Transactional
    public TaiKhoan updateTaiKhoan(UUID id, TaiKhoan taiKhoanUpdate) {

        TaiKhoan existingTaiKhoan = taiKhoanRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với ID: " + id));


        existingTaiKhoan.setEmail(existingTaiKhoan.getEmail());
        existingTaiKhoan.setSdt(existingTaiKhoan.getSdt());
        existingTaiKhoan.setMatKhau(existingTaiKhoan.getMatKhau());
        existingTaiKhoan.setVaiTro(existingTaiKhoan.getVaiTro());
        existingTaiKhoan.setTrangThai(existingTaiKhoan.getTrangThai());
        // Lưu lại thay đổi
        return taiKhoanRespository.save(existingTaiKhoan);
    }
}
