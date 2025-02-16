//package com.example.datnsd26.Api;
//import com.example.datnsd26.Entity.KhachHang;
//
//import com.example.datnsd26.Entity.TaiKhoan;
//import jakarta.validation.Valid;
//import com.example.datnsd26.Service.NhanVienService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
//@RestController
//public class KhachHangApi {
//    @Autowired
//    KhachHangService khachHangService;
//
//    @GetMapping("/hien-thi/khach-hang")
//    public ResponseEntity<?> hienThiKhachHang() {
//        return ResponseEntity.ok(khachHangService.getAll());
//    }
//
//    @PostMapping("/them/khach-hang")
//    public ResponseEntity<?> themKhachHang(@RequestBody @Valid KhachHang khachHang) {
//        return ResponseEntity.ok(khachHangService.Add(khachHang));
//    }
//
//    @GetMapping("/xoa/khach-hang")
//    public ResponseEntity<?> xoaKhachHang(@RequestParam("id") UUID id) {
//            return ResponseEntity.ok(khachHangService.deleteById(id));
//    }
//
//    @PutMapping("/sua/khach-hang/{id}")
//    public ResponseEntity<KhachHang> updateKhachHang(@PathVariable UUID id, @RequestBody KhachHang khachHang) {
//        try {
//            KhachHang updatedKhachHang = khachHang.updateKhachHang(id, khachHang);
//            return ResponseEntity.ok(updatedKhachHang);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//}
