// Data Transfer Object for GiaoDich form input
package dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GiaoDichFormDTO {
    private String maGiaoDich;
    private LocalDate ngayGiaoDich;
    private BigDecimal donGia;
    private int soLuong;
    private String loaiGiaoDich; // "VANG" or "TIEN_TE"
    
    // For GiaoDichVang
    private String loaiVang;
    
    // For GiaoDichTienTe
    private String loaiTien;
    private BigDecimal tiGia;
    
    public GiaoDichFormDTO() {}
    
    // Getters and Setters
    public String getMaGiaoDich() {
        return maGiaoDich;
    }
    
    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }
    
    public LocalDate getNgayGiaoDich() {
        return ngayGiaoDich;
    }
    
    public void setNgayGiaoDich(LocalDate ngayGiaoDich) {
        this.ngayGiaoDich = ngayGiaoDich;
    }
    
    public BigDecimal getDonGia() {
        return donGia;
    }
    
    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }
    
    public int getSoLuong() {
        return soLuong;
    }
    
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
    
    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }
    
    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }
    
    public String getLoaiVang() {
        return loaiVang;
    }
    
    public void setLoaiVang(String loaiVang) {
        this.loaiVang = loaiVang;
    }
    
    public String getLoaiTien() {
        return loaiTien;
    }
    
    public void setLoaiTien(String loaiTien) {
        this.loaiTien = loaiTien;
    }
    
    public BigDecimal getTiGia() {
        return tiGia;
    }
    
    public void setTiGia(BigDecimal tiGia) {
        this.tiGia = tiGia;
    }
    
    public boolean isGiaoDichVang() {
        return "VANG".equals(loaiGiaoDich);
    }
    
    public boolean isGiaoDichTienTe() {
        return "TIEN_TE".equals(loaiGiaoDich);
    }
}
