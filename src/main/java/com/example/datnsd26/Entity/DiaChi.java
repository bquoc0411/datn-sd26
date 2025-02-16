package com.example.datnsd26.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dia_chi")
@Entity
public class DiaChi {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UniqueID")
    private UUID id;

    @Column(name = "dia_chi_cu_the")
    private String diaChiCuThe;

    @Column(name = "tinh_thanh_pho")
    private String tinh;

    @Column(name = "quan_huyen")
    private String huyen;

    @Column(name = "xa_phuong")
    private String xa;
}
