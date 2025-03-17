package com.example.datnsd26.repository;

import com.example.datnsd26.models.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SanPhamRepositoty extends JpaRepository<SanPham, Integer> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM san_pham WHERE trang_thai =1
               ORDER BY ngay_tao DESC
            """)
    List<SanPham> getAllByNgayTao();

    // tìm id lớn nhất bên sp
    @Query(value = "SELECT MAX(s.id) FROM SanPham s")
    Integer findMaxIdSP();

    SanPham findFirstByOrderByNgayTaoDesc();

    boolean existsByTenSanPham(String tensanpham);

    // search bên sp
    @Query("SELECT " +
            "sp.id, " +
            "sp.tenSanPham, " +
            "sp.ngayTao," +
            "SUM(spct.soLuong) as tongSoLuong ," +
            "sp.trangThai," +
            "sp.maSanPham " +
            "FROM SanPham sp " +
            "JOIN sp.spct spct " +
            "WHERE (sp.maSanPham LIKE?1 OR sp.tenSanPham LIKE ?2)AND(?3 IS NULL OR sp.trangThai=?3) " +
            "GROUP BY sp.id, sp.tenSanPham, sp.ngayTao, sp.trangThai, sp.maSanPham " +
            "ORDER BY sp.ngayTao DESC, tongSoLuong DESC")
    List<Object[]> findByMaSanPhamAndTenSanPhamAndTrangThai(String masanpham, String key, Boolean trangthai);

    // các sp mới nhất bên sp
    @Query("SELECT " +
            "sp.id, " +
            "sp.tenSanPham, " +
            "sp.ngayTao, " +
            "SUM(spct.soLuong) AS tongSoLuong," +
            "sp.trangThai, " +
            "sp.maSanPham " +
            "FROM SanPham sp JOIN sp.spct spct " +
            "GROUP BY sp.id, sp.tenSanPham, sp.ngayTao, sp.trangThai, sp.maSanPham " +
            "ORDER BY sp.ngayTao DESC, tongSoLuong DESC")
    List<Object[]> findProductsWithTotalQuantityOrderByDateDesc();

    //lấy id và tên sản phẩm
    @Query("SELECT sp.id, sp.tenSanPham FROM SanPham sp WHERE sp.id = :id")
    List<Object[]> findByIdUpdatTenSP(@Param("id") Integer id);

}