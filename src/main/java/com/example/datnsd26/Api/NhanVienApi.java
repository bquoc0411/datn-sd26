package com.example.datnsd26.Api;

import com.example.datnsd26.Entity.DiaChi;
import com.example.datnsd26.Entity.NhanVien;
import com.example.datnsd26.Respository.NhanVienRepository;
import com.example.datnsd26.Service.NhanVienService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class NhanVienApi {
    @Autowired
    NhanVienService nhanVienService;


    @GetMapping("/hien-thi/nhan-vien")
    public ResponseEntity<?> hienThiNhanVien() {

        return ResponseEntity.ok(nhanVienService.getAll());
    }

    @PostMapping("/them/nhan-vien")
    public ResponseEntity<?> themNhanVien(@RequestBody @Valid NhanVien nhanVien) {
        return ResponseEntity.ok(nhanVienService.Add(nhanVien));
    }

    @GetMapping("/xoa/nhan-vien")
    public ResponseEntity<?> xoaNhanVien(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(nhanVienService.deleteById(id));
    }
    @PutMapping("/sua/nhan-vien/{id}")
    public ResponseEntity<NhanVien> updateNhanVien(@PathVariable Integer id, @RequestBody NhanVien nhanVien) {
        try {
            NhanVien updatedNhanVien = nhanVienService.updateNhanVien(id, nhanVien);
            return ResponseEntity.ok(updatedNhanVien);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
