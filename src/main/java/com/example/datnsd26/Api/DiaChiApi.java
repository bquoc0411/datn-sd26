package com.example.datnsd26.Api;

import com.example.datnsd26.Entity.DiaChi;
import com.example.datnsd26.Respository.DiaChiRespository;
import com.example.datnsd26.Service.DiaChiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class DiaChiApi {
    @Autowired
    DiaChiService diaChiService;

    @GetMapping("/hien-thi/dia-chi")
    public ResponseEntity<?> hienThiDiaChi() {
        return ResponseEntity.ok(diaChiService.getAll());
    }

    @PostMapping("/them/dia-chi")
    public ResponseEntity<?> themDiaChi(@RequestBody DiaChi diaChi) {
        return ResponseEntity.ok(diaChiService.Add(diaChi));
    }

    @GetMapping("/xoa/dia-chi")
    public ResponseEntity<?> xoaDiaChi(@RequestParam("id") UUID id) {
        return ResponseEntity.ok(diaChiService.deleteById(id));
    }
    @PutMapping("/sua/dia-chi/{id}")
    public ResponseEntity<DiaChi> updateDiaChi(@PathVariable UUID id, @RequestBody DiaChi diaChi) {
        try {
            DiaChi updatedDiaChi = diaChiService.updateDiaChi(id, diaChi);
            return ResponseEntity.ok(updatedDiaChi);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
