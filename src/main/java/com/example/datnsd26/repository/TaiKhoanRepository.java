package com.example.datnsd26.repository;

import com.example.datnsd26.models.TaiKhoan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findByEmail(String email);

//    @Modifying
//    @Transactional
//    @Query("UPDATE TaiKhoan t SET t.trangThai = false WHERE t.nhanVien.id = :id")
//    void deactivateTaiKhoanByNhanVienId(@Param("id") Integer id);
}
