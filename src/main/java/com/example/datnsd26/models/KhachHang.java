package com.example.datnsd26.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "khach_hang")
@Entity
@Builder
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan taiKhoan;
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaChi> diaChi = new ArrayList<>();
    @Column(name = "ho_ten", columnDefinition = "NVARCHAR(255)")
    private String tenKhachHang;

    @Column(name = "ma_khach_hang")
    private String maKhachHang;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "ngay_sinh")
    private Date ngaySinh;
    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "ngay_tao")
    private Timestamp ngayTao;

    @Column(name = "ngay_cap_nhat")
    private Timestamp ngayCapNhat;


}
