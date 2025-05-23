package com.example.datnsd26.controller;

import com.example.datnsd26.models.*;
import com.example.datnsd26.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/shop/order-tracking")
public class OrderTrackingController {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @GetMapping
    public String showTrackingPage() {
        return "/shop/order-tracking";
    }

    @PostMapping
    public String searchOrder(
            @RequestParam(value = "maHoaDon", required = false) String maHoaDon,
            @RequestParam(value = "sdtNguoiNhan", required = false) String sdtNguoiNhan,
            Model model) {

        if (maHoaDon == null || maHoaDon.trim().isEmpty() ||
                sdtNguoiNhan == null || sdtNguoiNhan.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Vui lòng nhập cả Mã hóa đơn và Số điện thoại.");
            return "shop/order-tracking";
        }

        Optional<HoaDon> optionalHoaDon = hoaDonRepository.findByMaHoaDonAndSdtNguoiNhan(maHoaDon, sdtNguoiNhan);
        if (optionalHoaDon.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy đơn hàng với thông tin đã nhập.");
            return "shop/order-tracking";
        }

        HoaDon hoaDon = optionalHoaDon.get();
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDon(hoaDon);
        List<LichSuHoaDon> lichSuList = lichSuHoaDonRepository.findByHoaDonOrderByThoiGianAsc(hoaDon);


//        List<String> allTrangThai = List.of(
//                "Đặt hàng",
//                "Chờ xác nhận",
//                "Đã xác nhận",
//                "Đã giao cho đơn vị vận chuyển",
//                "Hoàn thành"
//
//        );
//        model.addAttribute("allTrangThai", allTrangThai);
//        String currentStatus = hoaDon.getTrangThai();
//        model.addAttribute("currentStatus", currentStatus);
//
//        // Thời gian của các trạng thái đã qua
//        Map<String, String> thoiGianFormattedMap = new LinkedHashMap<>();
//        for (LichSuHoaDon lichSu : lichSuList) {
//            String key = lichSu.getTrangThai().trim().toLowerCase();
//            thoiGianFormattedMap.putIfAbsent(key, new SimpleDateFormat("dd/MM/yyyy HH:mm").format(lichSu.getThoiGian()));
//        }
//
//        model.addAttribute("thoiGianFormattedMap", thoiGianFormattedMap);
//        model.addAttribute("hoaDon", hoaDon);
//        model.addAttribute("allTrangThai", allTrangThai);
//        model.addAttribute("lichSuList", lichSuList);
//        model.addAttribute("chiTietList", chiTietList);


        // Lấy tất cả trạng thái, bao gồm cả trùng lặp
        List<String> allTrangThai = lichSuList.stream()
                .map(LichSuHoaDon::getTrangThai)
                .collect(Collectors.toList());
        model.addAttribute("allTrangThai", allTrangThai);
        String currentStatus = hoaDon.getTrangThai();
        model.addAttribute("currentStatus", currentStatus);

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("lichSuList", lichSuList);
        model.addAttribute("chiTietList", chiTietList);

        return "shop/order-tracking";
    }

    @GetMapping("/get-order-tracking")
    public String getOrderTrackingPage(
            @RequestParam(value = "maHoaDon", required = false) String maHoaDon,
            @RequestParam(value = "sdtNguoiNhan", required = false) String sdtNguoiNhan,
            Model model) {

        if (maHoaDon == null || maHoaDon.trim().isEmpty() ||
                sdtNguoiNhan == null || sdtNguoiNhan.trim().isEmpty()) {
            return "shop/order-tracking"; // chỉ hiện form tìm kiếm
        }

        Optional<HoaDon> optionalHoaDon = hoaDonRepository.findByMaHoaDonAndSdtNguoiNhan(maHoaDon, sdtNguoiNhan);
        if (optionalHoaDon.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy đơn hàng với thông tin đã nhập.");
            return "shop/order-tracking";
        }

        HoaDon hoaDon = optionalHoaDon.get();
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDon(hoaDon);
        List<LichSuHoaDon> lichSuList = lichSuHoaDonRepository.findByHoaDonOrderByThoiGianAsc(hoaDon);


//        List<String> allTrangThai = List.of(
//                "Đặt hàng",
//                "Chờ xác nhận",
//                "Đã xác nhận",
//                "Đã giao cho đơn vị vận chuyển",
//                "Hoàn thành"
//
//        );
//        model.addAttribute("allTrangThai", allTrangThai);
//        String currentStatus = hoaDon.getTrangThai();
//        model.addAttribute("currentStatus", currentStatus);
//
//        // Thời gian của các trạng thái đã qua
//        Map<String, String> thoiGianFormattedMap = new LinkedHashMap<>();
//        for (LichSuHoaDon lichSu : lichSuList) {
//            String key = lichSu.getTrangThai().trim().toLowerCase();
//            thoiGianFormattedMap.putIfAbsent(key, new SimpleDateFormat("dd/MM/yyyy HH:mm").format(lichSu.getThoiGian()));
//        }
//
//        model.addAttribute("thoiGianFormattedMap", thoiGianFormattedMap);
//        model.addAttribute("hoaDon", hoaDon);
//        model.addAttribute("allTrangThai", allTrangThai);
//        model.addAttribute("lichSuList", lichSuList);
//        model.addAttribute("chiTietList", chiTietList);


        // Lấy tất cả trạng thái, bao gồm cả trùng lặp
        List<String> allTrangThai = lichSuList.stream()
                .map(LichSuHoaDon::getTrangThai)
                .collect(Collectors.toList());
        model.addAttribute("allTrangThai", allTrangThai);
        String currentStatus = hoaDon.getTrangThai();
        model.addAttribute("currentStatus", currentStatus);

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("lichSuList", lichSuList);
        model.addAttribute("chiTietList", chiTietList);

        return "shop/order-tracking";
    }

    @PostMapping("/huy-don")
    public String huyDon(@RequestParam("idHoaDon") Integer idHoaDon, @RequestParam("reason") String reason, RedirectAttributes redirectAttributes) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepository.findById(idHoaDon);

        if (optionalHoaDon.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hóa đơn để hủy.");
            return "redirect:/shop/order-tracking";
        }

        HoaDon hoaDon = optionalHoaDon.get();
        String trangThaiHienTai = hoaDon.getTrangThai();
        // Lấy lại thông tin mã hóa đơn và sdt người nhận
        String maHoaDon = hoaDon.getMaHoaDon();
        String sdtNguoiNhan = hoaDon.getSdtNguoiNhan();


        // Kiểm tra trạng thái hiện tại có cho phép hủy không
        if (!trangThaiHienTai.equals("Đặt hàng") &&
                !trangThaiHienTai.equals("Chờ xác nhận") &&
                !trangThaiHienTai.equals("Đã xác nhận")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy đơn ở trạng thái hiện tại.");
            return "redirect:/shop/order-tracking/get-order-tracking?maHoaDon=" + maHoaDon + "&sdtNguoiNhan=" + sdtNguoiNhan;
        }

        // 1. Cộng lại voucher (nếu có)
        if (hoaDon.getVoucher() != null) {
            Voucher voucher = hoaDon.getVoucher();
            voucher.setSoLuong(voucher.getSoLuong() + 1);
            if (voucher.getTrangThai() == 2) {
                voucher.setTrangThai(1); // Chuyển voucher về trạng thái hoạt động nếu trước đó đã hết lượt
            }
            voucherRepository.save(voucher);
        }

        // 2. Trả lại sản phẩm vào kho (tùy theo trạng thái đơn hàng)
        if (trangThaiHienTai.equals("Đã xác nhận")) {
            for (HoaDonChiTiet chiTiet : hoaDon.getDanhSachSanPham()) {
                SanPhamChiTiet sanPhamChiTiet = chiTiet.getSanPhamChiTiet();
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() + chiTiet.getSoLuong());
                sanPhamChiTietRepository.save(sanPhamChiTiet);
            }
        }

        // Hủy đơn
        hoaDon.setTrangThai("Đã hủy");
        hoaDon.setGhiChu("KH: " + reason);
        hoaDon.setNgayCapNhat(new Date());
        hoaDonRepository.save(hoaDon);

        // Ghi lịch sử hủy
        LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                .hoaDon(hoaDon)
                .trangThai("Đã hủy")
                .build();
        lichSuHoaDonRepository.save(lichSuHoaDon);

        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được hủy thành công.");
        return "redirect:/shop/order-tracking/get-order-tracking?maHoaDon=" + maHoaDon + "&sdtNguoiNhan=" + sdtNguoiNhan;
    }

}
