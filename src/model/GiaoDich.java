// Abstract base class for all transactions
package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import exception.ValidationException;
import utils.ValidationUtil;

public abstract class GiaoDich {
    protected String maGiaoDich;
    protected LocalDate ngayGiaoDich;
    protected BigDecimal donGia;
    protected int soLuong;
    
    // Constructor with validation
    public GiaoDich(String maGiaoDich, LocalDate ngayGiaoDich, BigDecimal donGia, int soLuong) 
            throws ValidationException {
        setMaGiaoDich(maGiaoDich);
        setNgayGiaoDich(ngayGiaoDich);
        setDonGia(donGia);
        setSoLuong(soLuong);
    }
    
    // Default constructor for database operations
    protected GiaoDich() {}
    
    // Abstract method to be implemented by subclasses
    public abstract BigDecimal tinhThanhTien();
    
    // Abstract method to get transaction type
    public abstract String getLoaiGiaoDich();
    
    // Getters with validation
    public String getMaGiaoDich() {
        return maGiaoDich;
    }
    
    public void setMaGiaoDich(String maGiaoDich) throws ValidationException {
        ValidationUtil.validateStringLength(maGiaoDich, 20, "Mã giao dịch");
        this.maGiaoDich = maGiaoDich.trim().toUpperCase();
    }
    
    public LocalDate getNgayGiaoDich() {
        return ngayGiaoDich;
    }
    
    public void setNgayGiaoDich(LocalDate ngayGiaoDich) throws ValidationException {
        ValidationUtil.validateNotNull(ngayGiaoDich, "Ngày giao dịch");
        if (ngayGiaoDich.isAfter(LocalDate.now())) {
            throw new ValidationException("Ngày giao dịch không được lớn hơn ngày hiện tại", "ngayGiaoDich");
        }
        this.ngayGiaoDich = ngayGiaoDich;
    }
    
    public BigDecimal getDonGia() {
        return donGia;
    }
    
    public void setDonGia(BigDecimal donGia) throws ValidationException {
        ValidationUtil.validatePositive(donGia, "Đơn giá");
        this.donGia = donGia;
    }
    
    public int getSoLuong() {
        return soLuong;
    }
    
    public void setSoLuong(int soLuong) throws ValidationException {
        ValidationUtil.validatePositive(soLuong, "Số lượng");
        this.soLuong = soLuong;
    }
    
    // Business logic methods
    public boolean isDonGiaLonHon1Ty() {
        return donGia.compareTo(new BigDecimal("1000000000")) > 0;
    }
    
    // Equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GiaoDich giaoDich = (GiaoDich) obj;
        return Objects.equals(maGiaoDich, giaoDich.maGiaoDich);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(maGiaoDich);
    }
    
    // toString for debugging
    @Override
    public String toString() {
        return String.format("GiaoDich{maGiaoDich='%s', ngayGiaoDich=%s, donGia=%s, soLuong=%d, thanhTien=%s}",
                maGiaoDich, ngayGiaoDich, donGia, soLuong, tinhThanhTien());
    }
}