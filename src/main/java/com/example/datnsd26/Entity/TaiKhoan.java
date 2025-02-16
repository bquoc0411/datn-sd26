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
@Table(name = "tai_khoan")
@Entity
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UniqueID")
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "mat_khau")
    private String matKhau;
    @Column(name = "vai_tro")
    private Integer vaiTro;

    @Column(name = "trang_thai")
    private Integer trangThai;
}
