package com.example.datnsd26.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "de_giay")
public class DeGiay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten", columnDefinition = "NVARCHAR(255)")
    private String ten;

    @Column(name = "ngay_cap_nhat")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngayCapNhat;

    @Column(name = "ngay_tao")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngayTao;

    @Column(name = "trang_thai", columnDefinition = "BIT DEFAULT 1")
    private Boolean trangThai;
}
