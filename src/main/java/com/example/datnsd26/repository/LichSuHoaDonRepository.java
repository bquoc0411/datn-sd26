package com.example.datnsd26.repository;

import com.example.datnsd26.models.HoaDon;
import com.example.datnsd26.models.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {
    @Query("FROM LichSuHoaDon ls WHERE ls.trangThai like :status and ls.hoaDon.id = :invoice")
    Optional<LichSuHoaDon> findByStatusAndInvoice(String status, int invoice);

    List<LichSuHoaDon> findByHoaDonOrderByThoiGianAsc(HoaDon hoaDon);
}
