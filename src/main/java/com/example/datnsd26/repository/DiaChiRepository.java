package com.example.datnsd26.repository;

import com.example.datnsd26.models.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi,Integer> {
}
