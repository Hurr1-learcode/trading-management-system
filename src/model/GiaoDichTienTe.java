// Currency transaction implementation
package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

import exception.ValidationException;
import utils.ValidationUtil;

public class GiaoDichTienTe extends GiaoDich {
    private String loaiTien;
    private BigDecimal tiGia;
    
    // Constructor with validation
    public GiaoDichTienTe(String maGiaoDich, LocalDate ngayGiaoDich, BigDecimal donGia,
                         int soLuong, String loaiTien, BigDecimal tiGia) throws ValidationException {
        super(maGiaoDich, ngayGiaoDich, donGia, soLuong);
        setLoaiTien(loaiTien);
        setTiGia(tiGia);
    }
    
    // Default constructor for database operations
    public GiaoDichTienTe() {
        super();
    }
    
    @Override
    public BigDecimal tinhThanhTien() {
        // Special case for VND: only multiply donGia * soLuong (no exchange rate)
        if ("VND".equals(loaiTien)) {
            return donGia.multiply(BigDecimal.valueOf(soLuong))
                         .setScale(2, RoundingMode.HALF_UP);
        }
        
        // For other currencies: donGia * soLuong * tiGia (rounded to 2 decimal places)
        return donGia.multiply(BigDecimal.valueOf(soLuong))
                     .multiply(tiGia)
                     .setScale(2, RoundingMode.HALF_UP);
    }
       //     ↑ CÔNG THỨC TIỀN TỆ: 
    //       VND: Thành tiền = Đơn giá × Số lượng
    //       Ngoại tệ khác: Thành tiền = Đơn giá × Số lượng × Tỉ giá
    //       Làm tròn 2 chữ số thập phân
    
    @Override
    public String getLoaiGiaoDich() {
        return "TIEN_TE";
    }
    
    public String getLoaiTien() {
        return loaiTien;
    }
    
    public void setLoaiTien(String loaiTien) throws ValidationException {
        ValidationUtil.validateStringLength(loaiTien, 10, "Loại tiền");
        // Validate currency code format (3 characters)
        String cleanCode = loaiTien.trim().toUpperCase();
        if (!cleanCode.matches("^[A-Z]{3}$")) {
            throw new ValidationException("Loại tiền phải là mã 3 ký tự (VD: USD, EUR)", "loaiTien");
        }
        this.loaiTien = cleanCode;
    }
    
    public BigDecimal getTiGia() {
        return tiGia;
    }
    
    public void setTiGia(BigDecimal tiGia) throws ValidationException {
        ValidationUtil.validatePositive(tiGia, "Tỉ giá");
        // Validate reasonable exchange rate (between 0.01 and 100000)
        if (tiGia.compareTo(new BigDecimal("0.01")) < 0 || 
            tiGia.compareTo(new BigDecimal("100000")) > 0) {
            throw new ValidationException("Tỉ giá phải trong khoảng 0.01 - 100,000", "tiGia");
            //                              ↑ QUY TẮC: Tỉ giá phải hợp lý (0.01 - 100,000)
        }
        this.tiGia = tiGia.setScale(4, RoundingMode.HALF_UP);// Làm tròn 4 chữ số thập phân

    }
    
    // Business methods specific to currency transactions
    public BigDecimal tinhThanhTienVND() {
        return tinhThanhTien();
    }
    
    public BigDecimal tinhGiaTriNgoaiTe() {
        // Original value in foreign currency
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }
    
    public boolean isMainCurrency() {
        return "USD".equals(loaiTien) || "EUR".equals(loaiTien) || "VND".equals(loaiTien);
    }
    //     ↑ QUY TẮC NGHIỆP VỤ: USD, EUR, VND là tiền tệ chính
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof GiaoDichTienTe)) return false;
        GiaoDichTienTe that = (GiaoDichTienTe) obj;
        return Objects.equals(loaiTien, that.loaiTien) &&
               Objects.equals(tiGia, that.tiGia);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), loaiTien, tiGia);
    }
    
    @Override
    public String toString() {
        return String.format("GiaoDichTienTe{maGiaoDich='%s', ngayGiaoDich=%s, donGia=%s, soLuong=%d, loaiTien='%s', tiGia=%s, thanhTien=%s}",
                maGiaoDich, ngayGiaoDich, donGia, soLuong, loaiTien, tiGia, tinhThanhTien());
    }
}