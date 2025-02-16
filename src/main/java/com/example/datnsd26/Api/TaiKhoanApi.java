package com.example.datnsd26.Api;


import com.example.datnsd26.Entity.TaiKhoan;
import com.example.datnsd26.Respository.TaiKhoanRespository;
import com.example.datnsd26.Service.TaiKhoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TaiKhoanApi {
    @Autowired
    TaiKhoanService taiKhoanService;

    @GetMapping("/hien-thi/tai-khoan")
    public ResponseEntity<?> hienThiTaiKhoan() {
        return ResponseEntity.ok(taiKhoanService.getAll());
    }

    @PostMapping("/them/tai-khoan")
    public ResponseEntity<?> themTaiKhoan(@RequestBody @Valid TaiKhoan taiKhoan) {
        return ResponseEntity.ok(taiKhoanService.Add(taiKhoan));
    }
    @GetMapping("/xoa/tai-khoan")
    public ResponseEntity<?> xoaTaiKhoan(@RequestParam("id") UUID id) {
        return ResponseEntity.ok(taiKhoanService.deleteById(id));
    }

    @PutMapping("/sua/tai-khoan/{id}")
    public ResponseEntity<TaiKhoan> updateDiaChi(@PathVariable UUID id, @RequestBody TaiKhoan taiKhoan) {
        try {
            TaiKhoan updatedTaiKhoan = taiKhoanService.updateTaiKhoan(id, taiKhoan);
            return ResponseEntity.ok(updatedTaiKhoan);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
