package com.example.datnsd26.Entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nhan_vien")
@Entity
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan idTaiKhoan;

    @Column(name = "ma_nhan_vien")
    private String maNhanvien;

    @Column(name = "ho_ten")
    private String tenNhanVien;


    @Column(name = "dia_chi")
    private String diaChi;

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
