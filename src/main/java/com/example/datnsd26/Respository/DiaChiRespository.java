package com.example.datnsd26.Respository;

import com.example.datnsd26.Entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DiaChiRespository extends JpaRepository<DiaChi, UUID> {
}
