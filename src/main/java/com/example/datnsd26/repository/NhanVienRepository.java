package com.example.datnsd26.repository;

import com.example.datnsd26.models.NhanVien;
import com.example.datnsd26.models.TaiKhoan;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Page<NhanVien> findAll(Pageable pageable);

    @Query(value = "SELECT nv FROM NhanVien nv WHERE " +
            "(nv.tenNhanVien LIKE %:search% " +
            "OR nv.idTaiKhoan.sdt LIKE %:search% " +
            "OR nv.maNhanvien LIKE %:search% " +
            "OR nv.tenNhanVien LIKE %:search% " +
            "OR nv.idTaiKhoan.email LIKE %:search%) " +
            "AND (:status IS NULL OR nv.trangThai = :status) " +
            "AND (:role IS NULL OR nv.idTaiKhoan.vaiTro = :role) " +
            "ORDER BY nv.id DESC")
    Page<NhanVien> searchByTenOrSdtOrTrangThai(@Param("search") String tenSdtMa,
                                               @Param("status") Boolean status,
                                               @Param("role") TaiKhoan.Role vaiTro,
                                               Pageable pageable);


    @Modifying
    @Transactional
    @Query("UPDATE NhanVien n SET n.trangThai = false WHERE n.id = :id")
    void deactivateNhanVien(@Param("id") Integer id);


}
