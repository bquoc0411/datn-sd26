package com.example.datnsd26.controller.sanpham;

import com.example.datnsd26.models.*;
import com.example.datnsd26.info.*;
import com.example.datnsd26.repository.*;
import com.example.datnsd26.services.QRCodeGenerator;
import com.example.datnsd26.services.impl.*;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Controller
public class SanPhamController {
    @Autowired
    MauSacRepository mauSacRepository;
    @Autowired
    ChatLieuRepository chatLieuRepository;
    @Autowired
    ThuongHieuRepository thuongHieuRepository;
    @Autowired
    DeGiayRepository deGiayRepository;
    @Autowired
    KichCoRepository kichCoRepository;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    SanPhamRepositoty sanPhamRepositoty;

    @Autowired
    SanPhamImp sanPhamImp;

    @Autowired
    SanPhamChiTietImp sanPhamChiTietImp;

    @Autowired
    ThuongHieuImp thuongHieuImp;

    @Autowired
    MauSacImp mauSacImp;

    @Autowired
    KichCoImp kichCoImp;

    @Autowired
    DeGiayImp deGiayImp;

    @Autowired
    ChatLieuImp chatLieuImp;

    @Autowired
    HinhAnhImp anhImp;
    @Autowired
    HinhAnhRepository hinhAnhRepository;

    @Autowired
    private QRCodeGenerator qrCodeGenerator;


    private String taoChuoiNgauNhien(int doDaiChuoi, String kiTu) {
        Random random = new Random();
        StringBuilder chuoiNgauNhien = new StringBuilder(doDaiChuoi);
        for (int i = 0; i < doDaiChuoi; i++) {
            chuoiNgauNhien.append(kiTu.charAt(random.nextInt(kiTu.length())));
        }
        return chuoiNgauNhien.toString();
    }

    @PostMapping("/san-pham/updateTrangThai/{id}")
    public String updateTrangThaiCTSP(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        SanPham sp = sanPhamRepositoty.findById(id).orElse(null);
        if (sp != null) {
            sp.setTrangThai(!sp.getTrangThai());
            sanPhamRepositoty.save(sp);
            List<SanPhamChiTiet> lstSPCT = sanPhamChiTietRepository.findBySanPhamId(sp.getId());
            for (SanPhamChiTiet s : lstSPCT) {
                s.setTrangThai(!s.getTrangThai());
                sanPhamChiTietRepository.save(s);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        }
        return "redirect:/listsanpham";
    }

    @PostMapping("/addTenSPModal")
    public String addTenSPModel(Model model, @ModelAttribute("sanpham") SanPham sanPham, HttpSession session) {

        String chuoiNgauNhien = taoChuoiNgauNhien(7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        LocalDateTime currentTime = LocalDateTime.now();
//        String maSanPham = "SP" + chuoiNgauNhien;
        String maSanPham = "";
        if (sanPhamRepositoty.findAll().size() < 9) {
            int index = sanPhamRepositoty.findAll().size() + 1;
            maSanPham = "SP0" + index;
        } else {
            maSanPham = "SP" + sanPhamRepositoty.findAll().size();
        }
        // Trim tên sản phẩm và thay thế nhiều khoảng trắng liên tiếp bằng một khoảng trắng
        String trimmedTenSanPham = (sanPham.getTenSanPham() != null)
                ? sanPham.getTenSanPham().trim().replaceAll("\\s+", " ")
                : null;
        sanPham.setTenSanPham(trimmedTenSanPham);
        sanPham.setTrangThai(true);
        sanPham.setMaSanPham(maSanPham);
        sanPham.setNgayTao(currentTime);
        sanPham.setNgayCapNhat(currentTime);
        sanPhamRepositoty.save(sanPham);
        return "redirect:/viewaddSPGET";
    }


    @RequestMapping(value = {"/viewaddSPGET", "/viewaddSPPOST"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String viewaddSP(Model model, @RequestParam(defaultValue = "0") int p,
                            @ModelAttribute("thuonghieu") ThuongHieu thuongHieu,
                            @ModelAttribute("chatlieu") ChatLieu chatLieu,
                            @ModelAttribute("kichco") KichCo kichCo,
                            @ModelAttribute("degiay") DeGiay deGiay,
                            @ModelAttribute("mausac") MauSac mauSac,
                            @ModelAttribute("sanpham") SanPham sanpham,
                            @ModelAttribute("tim") ThuocTinhInfo info
    ) {
        List<SanPham> listSanPham = sanPhamRepositoty.getAllByNgayTao();
        List<SanPhamChiTiet> listSPCT = sanPhamChiTietRepository.findAll();
        List<ThuongHieu> listThuongHieu = thuongHieuRepository.getAll();
        List<MauSac> listMauSac = mauSacRepository.getAll();
        List<KichCo> listKichCo = kichCoRepository.getAll();
        List<DeGiay> listDeGiay = deGiayRepository.getAll();
        List<ChatLieu> listChatLieu = chatLieuRepository.getAll();
        List<HinhAnh> listHinhAnh = anhImp.findAll();
        model.addAttribute("sp", listSanPham);
        model.addAttribute("spct", listSPCT);
        model.addAttribute("th", listThuongHieu);
        model.addAttribute("ms", listMauSac);
        model.addAttribute("kc", listKichCo);
        model.addAttribute("dg", listDeGiay);
        model.addAttribute("cl", listChatLieu);
        model.addAttribute("a", listHinhAnh);
        return "admin/addsanpham";
    }

    List<SanPhamChiTiet> sanPhamChiTietList = new ArrayList<>();


    @GetMapping("/listsanpham")
    public String hienthi(Model model, @ModelAttribute("tim") SanPhamInfo info) {
        List<Object[]> list;
        String trimmedKey = (info.getKey() != null) ? info.getKey().trim().replaceAll("\\s+", " ") : null;
        boolean isKeyEmpty = (trimmedKey == null || trimmedKey.isEmpty());
        boolean isTrangthaiNull = (info.getTrangThai() == null);
        List<SanPham> listSanPham = sanPhamRepositoty.findAll();
        for (SanPham sp : listSanPham) {
            List<SanPhamChiTiet> listSPCT = sanPhamChiTietRepository.findBySanPhamId(sp.getId());

            if (listSPCT != null && !listSPCT.isEmpty()) {
                int soluong = 0;
                for (SanPhamChiTiet spct : listSPCT) {
                    soluong = soluong + spct.getSoLuong();
                }
                if (soluong <= 0) {
                    sp.setTrangThai(false);
                    sanPhamRepositoty.save(sp);
                }
            }
        }
        if (isKeyEmpty && isTrangthaiNull) {
            list = sanPhamRepositoty.findProductsWithTotalQuantityOrderByDateDesc();

        } else {
            list = sanPhamRepositoty.findByMaSanPhamAndTenSanPhamAndTrangThai("%" + trimmedKey + "%", "%" + trimmedKey + "%", info.getTrangThai());
        }
        model.addAttribute("list", list);
        model.addAttribute("fillSearch", trimmedKey);
        model.addAttribute("fillTrangThai", info.getTrangThai());
        sanPhamChiTietList.clear();
        return "admin/qlsanpham";
    }

    @PostMapping("san-pham/addProduct")
    public String addProduct(@RequestParam(defaultValue = "0") int p, Model model,
                             @RequestParam Integer tensp,
                             @RequestParam String mota,
                             @RequestParam ThuongHieu idThuongHieu,
                             @RequestParam ChatLieu idChatLieu,
                             @RequestParam Boolean gioitinh,
                             @RequestParam(name = "kichCoId") List<String> kichCoNames,
                             @RequestParam DeGiay idDeGiay,
                             @RequestParam List<MauSac> idMauSac, HttpSession session
    ) throws IOException, WriterException {
        LocalDateTime currentTime = LocalDateTime.now();

        String trimmedMota = (mota != null) ? mota.trim().replaceAll("\\s+", " ") : null;
        model.addAttribute("selectedTensp", tensp);
        model.addAttribute("motas", mota);
        model.addAttribute("gioitinh", gioitinh);
        model.addAttribute("selectedThuongHieu", idThuongHieu.getId());
        model.addAttribute("selectedDeGiay", idDeGiay.getId());
        model.addAttribute("selectedChatLieu", idChatLieu.getId());
//        model.addAttribute("gioitinh", gioitinh);
        model.addAttribute("kichCoNames", kichCoNames);

        //Hiển thị sau khi sửa đồng gía và số lượng
        session.setAttribute("selectedTensp", tensp);
        session.setAttribute("motas", mota);
        session.setAttribute("gioitinh", gioitinh);
        session.setAttribute("selectedThuongHieu", idThuongHieu.getId());
        session.setAttribute("selectedChatLieu", idChatLieu.getId());
        session.setAttribute("selectedDeGiay", idDeGiay.getId());


        SanPham sanPham = sanPhamRepositoty.findById(tensp).orElse(null);
        if (sanPham == null) {
            return "redirect:/error";
        }
        Integer nextId2 = sanPhamChiTietRepository.findMaxIdSPCT();
        if (nextId2 == null) {
            return "redirect:/error";
        }
        int i = 0;
        if (sanPhamChiTietList.size() <= 0) {

            for (MauSac colorId : idMauSac) {
                i++;
                for (String sizeName : kichCoNames) {
                    KichCo kichCo = kichCoRepository.findByTen(sizeName);
                    if (kichCo != null) {
                        String chuoiNgauNhien = taoChuoiNgauNhien(7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
//                        String maSanPhamCT = "SPCT" + chuoiNgauNhien;
                        String maSanPhamCT = sanPham.getMaSanPham() + "-" + "CT" + i + "-" + kichCo.getTen();
                        nextId2++;
                        SanPhamChiTiet spct = new SanPhamChiTiet();
                        spct.setId(nextId2);
                        spct.setMaSanPhamChiTiet(maSanPhamCT);
                        spct.setSanPham(sanPham);
                        spct.setSoLuong(1);
                        spct.setGiaBan(100000F);
                        spct.setGiaBanSauGiam(spct.getGiaBan());
                        spct.setMoTa(trimmedMota);
                        spct.setThuongHieu(idThuongHieu);
                        spct.setChatLieu(idChatLieu);
                        spct.setGioiTinh(gioitinh);
                        spct.setTrangThai(true);
                        spct.setKichCo(kichCo);
                        spct.setDeGiay(idDeGiay);
                        spct.setMauSac(colorId);
                        spct.setNgayTao(currentTime);
                        spct.setNgayCapNhat(currentTime);
                        // Generate and save QR Code
                        String qrCodeUrl = qrCodeGenerator.generateQRCodeImage(maSanPhamCT, maSanPhamCT);
                        spct.setQrCode(qrCodeUrl);
                        sanPhamChiTietList.add(spct);

                        for (SanPhamChiTiet spcts : sanPhamChiTietList) {
                            System.out.println("idspct:" + spcts.getId());
                            System.out.println("mausac:" + spcts.getKichCo().getTen());
                            System.out.println("kichco:" + spcts.getKichCo().getTen());
                        }
                    }
                }
            }

        } else {
            for (MauSac colorId : idMauSac) {
                i++;
                for (String sizeName : kichCoNames) {
                    KichCo kichCo = kichCoRepository.findByTen(sizeName);
                    if (kichCo != null) {
                        boolean found = false;
                        if (sanPhamChiTietList != null) {
                            for (SanPhamChiTiet spct2 : sanPhamChiTietList) {
                                if (spct2.getMauSac().getId() == colorId.getId() && spct2.getKichCo().getTen().equals(sizeName)) {
                                    spct2.setSoLuong(spct2.getSoLuong() + 1);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            String chuoiNgauNhien = taoChuoiNgauNhien(7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
//                            String maSanPhamCT = "SPCT" + chuoiNgauNhien;
                            String maSanPhamCT = sanPham.getMaSanPham() + "-" + "CT" + i + "-" + kichCo.getTen();
                            int lastIndex = sanPhamChiTietList.size() - 1;
                            SanPhamChiTiet lastItem = sanPhamChiTietList.get(lastIndex);
                            int count = lastItem.getId();
                            count++;
                            SanPhamChiTiet spct = new SanPhamChiTiet();
                            spct.setId(count);
                            spct.setSanPham(sanPham);
                            spct.setMaSanPhamChiTiet(maSanPhamCT);
                            spct.setSoLuong(1);
                            spct.setGiaBan(100000F);
                            spct.setGiaBanSauGiam(spct.getGiaBan());
                            spct.setMoTa(trimmedMota);
                            spct.setThuongHieu(idThuongHieu);
                            spct.setChatLieu(idChatLieu);
                            spct.setGioiTinh(gioitinh);
                            spct.setTrangThai(true);
                            spct.setKichCo(kichCo);
                            spct.setDeGiay(idDeGiay);
                            spct.setMauSac(colorId);
                            spct.setNgayTao(currentTime);
                            spct.setNgayCapNhat(currentTime);
                            // Generate and save QR Code
                            String qrCodeUrl = qrCodeGenerator.generateQRCodeImage(maSanPhamCT, maSanPhamCT);
                            spct.setQrCode(qrCodeUrl);
                            sanPhamChiTietList.add(spct);
                        }
                    }
                }
            }

        }
        model.addAttribute("sanphamchitiet", sanPhamChiTietList);
        return "forward:/viewaddSPPOST";
    }


    @GetMapping("/detailsanpham/{id}")
    public String detailsanpham(@PathVariable Integer id, Model model, @ModelAttribute("search") SanPhamChiTietInfo info) {
        List<SanPhamChiTiet> listSPCT = sanPhamChiTietRepository.findBySanPhamId(id);
        for (SanPhamChiTiet spct : listSPCT) {
            if (spct.getSoLuong() <= 0) {
                spct.setTrangThai(false);
                sanPhamChiTietRepository.save(spct);
            }
        }

        SanPham sanPham = sanPhamRepositoty.findById(id).orElse(null);
        if (sanPham == null) {
            // Xử lý trường hợp sản phẩm không tồn tại
            return "redirect:/error"; // Hoặc một trang thông báo lỗi nào đó
        }

        String trimmedKey = (info.getKey() != null) ? info.getKey().trim().replaceAll("\\s+", " ") : null;
        boolean isKeyEmpty = (trimmedKey == null || trimmedKey.isEmpty());
        boolean isAllFiltersNull = (
                isKeyEmpty &&
                        info.getIdChatLieu() == null &&
                        info.getIdDeGiay() == null &&
                        info.getIdKichCo() == null &&
                        info.getIdMauSac() == null &&
                        info.getIdThuongHieu() == null &&
                        info.getGioiTinh() == null &&
                        info.getTrangThai() == null
        );
        List<SanPhamChiTiet> listSanPhamChiTiet;
        if (isAllFiltersNull) {
            listSanPhamChiTiet = sanPham.getSpct();
        } else {
            listSanPhamChiTiet = sanPhamChiTietRepository.searchBySanPham(
                    sanPham,
                    "%" + trimmedKey + "%",
                    info.getIdThuongHieu(),
                    info.getIdDeGiay(),
                    info.getIdKichCo(),
                    info.getIdMauSac(),
                    info.getIdChatLieu(),
                    info.getGioiTinh(),
                    info.getTrangThai()
            );
        }

        List<SanPham> listSanPham = sanPhamImp.findAll();
        List<ThuongHieu> listThuongHieu = thuongHieuRepository.getAll();
        List<MauSac> listMauSac = mauSacRepository.getAll();
        List<KichCo> listKichCo = kichCoRepository.getAll();
        List<DeGiay> listDeGiay = deGiayRepository.getAll();
        List<ChatLieu> listChatLieu = chatLieuRepository.getAll();
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("sanPhamChiTiet", listSanPhamChiTiet);

//        for (SanPhamChiTiet chiTiet : listSanPhamChiTiet) {
//            chiTiet.setSanphamdotgiam(chiTiet.getSanphamdotgiam().stream()
//                    .filter(dotGiam -> dotGiam.getDotgiamgia().getTrangthai() == 1)
//                    .collect(Collectors.toList()));
//        }

        model.addAttribute("sp", listSanPham);
        model.addAttribute("th", listThuongHieu);
        model.addAttribute("ms", listMauSac);
        model.addAttribute("kc", listKichCo);
        model.addAttribute("dg", listDeGiay);
        model.addAttribute("cl", listChatLieu);
        model.addAttribute("fillSearch", trimmedKey);
        model.addAttribute("fillThuongHieu", info.getIdThuongHieu());
        model.addAttribute("fillDeGiay", info.getIdDeGiay());
        model.addAttribute("fillKichCo", info.getIdKichCo());
        model.addAttribute("fillMauSac", info.getIdMauSac());
        model.addAttribute("fillChatLieu", info.getIdChatLieu());
        model.addAttribute("fillGioiTinh", info.getGioiTinh());
        model.addAttribute("fillTrangThai", info.getTrangThai());
        return "admin/qlchitietsanpham";
    }


    @GetMapping("/deleteCTSP/{id}")
    public String deleteCTSP(@PathVariable Integer id, Model model) {
        for (Iterator<SanPhamChiTiet> iterator = sanPhamChiTietList.iterator(); iterator.hasNext(); ) {
            SanPhamChiTiet spct = iterator.next();
            if (spct.getId().equals(id)) {
                iterator.remove();
                break;
            }
        }
        model.addAttribute("sanphamchitiet", sanPhamChiTietList);
        return "forward:/viewaddSPPOST";
    }


    @PostMapping("/addImage")
    public String addImage(
            @RequestParam(name = "anh1") List<MultipartFile> anhFiles1,
            @RequestParam(name = "anh2") List<MultipartFile> anhFiles2,
            @RequestParam(name = "anh3") List<MultipartFile> anhFiles3,
            @RequestParam(name = "spctId") List<Integer> spctIds,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        SanPham sanPham = sanPhamChiTietList.get(0).getSanPham();
        List<SanPhamChiTiet> listsanPhamChiTietDB = sanPhamChiTietRepository.findBySanPham(sanPham);

        if (listsanPhamChiTietDB.isEmpty()) {
            for (SanPhamChiTiet spct : sanPhamChiTietList) {
                sanPhamChiTietRepository.save(spct);
            }
        } else {
            for (SanPhamChiTiet spctList : sanPhamChiTietList) {
                SanPhamChiTiet spctTim = sanPhamChiTietRepository.findSPCT(
                        spctList.getMauSac(), spctList.getKichCo(), spctList.getThuongHieu(),
                        spctList.getChatLieu(), spctList.getDeGiay(), spctList.getSanPham());
                if (spctTim != null) {
                    spctTim.setSoLuong(spctTim.getSoLuong() + spctList.getSoLuong());
                    if (spctList.getHinhAnh() != null) {
                        for (HinhAnh hinhAnh : spctList.getHinhAnh()) {
                            hinhAnh.setSanPhamChiTiet(spctTim);
                            hinhAnhRepository.save(hinhAnh);
                        }
                    }
                    sanPhamChiTietRepository.save(spctTim);
                } else {
                    sanPhamChiTietRepository.save(spctList);
                }
            }
        }
        sanPhamChiTietList.clear();
        for (int i = 0; i < spctIds.size(); i++) {
            Integer spctId = spctIds.get(i);
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(spctId).orElse(null);
            if (spct != null) {
                MultipartFile anhFile1 = anhFiles1.get(i);
                if (!anhFile1.isEmpty()) {
                    addAnh(spct, anhFile1);
                }
                MultipartFile anhFile2 = anhFiles2.get(i);
                if (!anhFile2.isEmpty()) {
                    addAnh(spct, anhFile2);
                }
                MultipartFile anhFile3 = anhFiles3.get(i);
                if (!anhFile3.isEmpty()) {
                    addAnh(spct, anhFile3);
                }
            }
        }
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/listsanpham";
    }

    @Autowired
    HttpSession session;

    private void addAnh(SanPhamChiTiet spct, MultipartFile anhFile) {

        if (!anhFile.isEmpty()) {
            String anhUrl = saveImage(anhFile);
            if (anhUrl != null) {
                HinhAnh hinhAnh = new HinhAnh();
                LocalDateTime currentTime = LocalDateTime.now();
                hinhAnh.setTenAnh(anhUrl);
                hinhAnh.setNgayTao(currentTime);
                hinhAnh.setSanPhamChiTiet(spct);
                hinhAnh.setTrangThai(true);
                hinhAnh.setNgayCapNhat(currentTime);
                hinhAnhRepository.save(hinhAnh);
            }
        }
    }

    private String saveImage(MultipartFile file) {
        String uploadDir = "D:\\DATN\\src\\main\\resources\\static\\upload";
        String dbUploadDir = "/upload";
        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String originalFileName = file.getOriginalFilename();
            String filePath = uploadDir + File.separator + originalFileName;
            File dest = new File(filePath);
            file.transferTo(dest);
            return dbUploadDir + "/" + originalFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @PostMapping("/updateGiaAndSoLuong")
    public String updateGiaAndSoLuong(
            Model model,
            @RequestParam("soluong") Integer soluong,
            @RequestParam("giatien") Float giatien,
            @RequestParam("choncheckbox") String[] choncheckbox,
            HttpSession session) {
        List<String> listString = Arrays.asList(choncheckbox);
        List<Integer> listInt = new ArrayList<>();
        for (String s : listString) {
            Integer i = Integer.parseInt(s);
            listInt.add(i);
        }
        listInt.remove(Integer.valueOf(-1));
        for (Integer id : listInt) {
            for (SanPhamChiTiet sanPhamChiTiet : sanPhamChiTietList) {
                if (sanPhamChiTiet.getId().equals(id)) {
                    sanPhamChiTiet.setGiaBan(giatien);
                    sanPhamChiTiet.setSoLuong(soluong);
                }
            }
        }
        Integer selectedTensp = (Integer) session.getAttribute("selectedTensp");
        String motas = (String) session.getAttribute("motas");
        Boolean gioitinh = (Boolean) session.getAttribute("gioitinh");
        Integer selectedThuongHieu = (Integer) session.getAttribute("selectedThuongHieu");
        Integer selectedChatLieu = (Integer) session.getAttribute("selectedChatLieu");
        Integer selectedDeGiay = (Integer) session.getAttribute("selectedDeGiay");

        model.addAttribute("selectedTensp", selectedTensp);
        model.addAttribute("motas", motas);
        model.addAttribute("gioitinh", gioitinh);
        model.addAttribute("selectedThuongHieu", selectedThuongHieu);
        model.addAttribute("selectedChatLieu", selectedChatLieu);
        model.addAttribute("selectedDeGiay", selectedDeGiay);


        model.addAttribute("sanphamchitiet", sanPhamChiTietList);
        return "forward:/viewaddSPPOST";
    }


    @GetMapping("timaddImage")
    @ResponseBody
    public ResponseEntity<?> timaddImage(@RequestParam("image") List<String> listData
    ) {
        HinhAnh hinhAnh = new HinhAnh();
        String url = "D:\\DATN\\src\\main\\resources\\static\\upload\\" + listData.get(1);
        hinhAnh.setTenAnh(url);
        List<HinhAnh> list = sanPhamChiTietList.get(Integer.valueOf(listData.get(0)) - 1).getHinhAnh() == null ? new ArrayList<>() : sanPhamChiTietList.get(Integer.valueOf(listData.get(0)) - 1).getHinhAnh();
        list.add(hinhAnh);
        System.out.println("KKKKKKKKKKKKKK" + list.size());
        sanPhamChiTietList.get(Integer.valueOf(listData.get(0)) - 1).setHinhAnh(list);
        return ResponseEntity.ok(true);
    }
}