package com.example.datnsd26.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "khach_hang")
@Entity

public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UniqueID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan idTaiKhoan;

    @ManyToOne
    @JoinColumn(name = "id_dia_chi")
    private DiaChi idDiaChi;

    @Column(name = "ho_ten")
    private String tenKhachHang;

    @Column(name = "ma_khach_hang")
    private String maKhachHang;

    @Column(name = "gioi_tinh")
    private Integer gioiTinh;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "ngay_sinh")
    private Date ngaySinh;

    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Column(name = "ngay_cap_nhat")
    private Date ngayCapNhat;

    @Column(name = "trang_thai")
    private Integer trangThai;
}
