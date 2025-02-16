package com.example.datnsd26.Service;

import com.example.datnsd26.Entity.DiaChi;
import com.example.datnsd26.Entity.KhachHang;
import com.example.datnsd26.Respository.DiaChiRespository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DiaChiService {
    @Autowired
    DiaChiRespository diaChiRespository;

    public List<DiaChi> getAll() {
        return diaChiRespository.findAll();
    }
    public DiaChi Add(DiaChi diaChi) {
        return diaChiRespository.save(diaChi);
    }
    public DiaChi deleteById(UUID id) {
        Optional<DiaChi> optional = diaChiRespository.findById(id);
        return optional.map(o -> {
            diaChiRespository.delete(o);
            return o;
        }).orElse(null);
    }
    @Transactional
    public DiaChi updateDiaChi(UUID id, DiaChi diaChiUpdate) {

        DiaChi existingDiaChi = diaChiRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ với ID: " + id));


        existingDiaChi.setDiaChiCuThe(diaChiUpdate.getDiaChiCuThe());
        existingDiaChi.setXa(diaChiUpdate.getXa());
        existingDiaChi.setTinh(diaChiUpdate.getTinh());
        existingDiaChi.setHuyen(diaChiUpdate.getHuyen());
        // Lưu lại thay đổi
        return diaChiRespository.save(existingDiaChi);
    }
}
