// Gold transaction implementation
package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import exception.ValidationException;
import utils.ValidationUtil;

public class GiaoDichVang extends GiaoDich {
    private String loaiVang;
    
    // Constructor with validation
    public GiaoDichVang(String maGiaoDich, LocalDate ngayGiaoDich, BigDecimal donGia, 
                        int soLuong, String loaiVang) throws ValidationException {
        super(maGiaoDich, ngayGiaoDich, donGia, soLuong);
        setLoaiVang(loaiVang);
    }
    
    // Default constructor for database operations
    public GiaoDichVang() {
        super();
    }
    
    @Override
    public BigDecimal tinhThanhTien() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }
     //      CÔNG THỨC VÀNG: Thành tiền = Đơn giá × Số lượng
    //       (Không có tỉ giá vì vàng tính bằng VND)

    @Override
    public String getLoaiGiaoDich() {
        return "VANG";
    }
    
    public String getLoaiVang() {
        return loaiVang;
    }
    
    public void setLoaiVang(String loaiVang) throws ValidationException {
        ValidationUtil.validateStringLength(loaiVang, 50, "Loại vàng");
        this.loaiVang = loaiVang.trim();
    }
    
    // Business methods specific to gold transactions
    public boolean isVangCaoKy() {
        return loaiVang.contains("24K") || loaiVang.contains("999");
    }
     //     ↑ QUY TẮC NGHIỆP VỤ: Vàng 24K hoặc 999 là cao cấp
    public BigDecimal tinhThueVAT() {
        // VAT 10% for gold transactions
        return tinhThanhTien().multiply(new BigDecimal("0.10"));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof GiaoDichVang)) return false;
        GiaoDichVang that = (GiaoDichVang) obj;
        return Objects.equals(loaiVang, that.loaiVang);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), loaiVang);
    }
    
    @Override
    public String toString() {
        return String.format("GiaoDichVang{maGiaoDich='%s', ngayGiaoDich=%s, donGia=%s, soLuong=%d, loaiVang='%s', thanhTien=%s}",
                maGiaoDich, ngayGiaoDich, donGia, soLuong, loaiVang, tinhThanhTien());
    }
}