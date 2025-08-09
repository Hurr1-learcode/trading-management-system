// Data Transfer Object for statistics
package dto;

import java.math.BigDecimal;

public class ThongKeDTO {
    private long tongSoLuongVang;
    private long tongSoLuongTienTe;
    private BigDecimal trungBinhThanhTienTienTe;
    private long soGiaoDichDonGiaLonHon1Ty;
    private BigDecimal tongThanhTienVang;
    private BigDecimal tongThanhTienTienTe;
    
    public ThongKeDTO() {}
    
    public ThongKeDTO(long tongSoLuongVang, long tongSoLuongTienTe, 
                      BigDecimal trungBinhThanhTienTienTe, long soGiaoDichDonGiaLonHon1Ty,
                      BigDecimal tongThanhTienVang, BigDecimal tongThanhTienTienTe) {
        this.tongSoLuongVang = tongSoLuongVang;
        this.tongSoLuongTienTe = tongSoLuongTienTe;
        this.trungBinhThanhTienTienTe = trungBinhThanhTienTienTe;
        this.soGiaoDichDonGiaLonHon1Ty = soGiaoDichDonGiaLonHon1Ty;
        this.tongThanhTienVang = tongThanhTienVang;
        this.tongThanhTienTienTe = tongThanhTienTienTe;
    }
    
    // Getters and Setters
    public long getTongSoLuongVang() {
        return tongSoLuongVang;
    }
    
    public void setTongSoLuongVang(long tongSoLuongVang) {
        this.tongSoLuongVang = tongSoLuongVang;
    }
    
    public long getTongSoLuongTienTe() {
        return tongSoLuongTienTe;
    }
    
    public void setTongSoLuongTienTe(long tongSoLuongTienTe) {
        this.tongSoLuongTienTe = tongSoLuongTienTe;
    }
    
    public BigDecimal getTrungBinhThanhTienTienTe() {
        return trungBinhThanhTienTienTe != null ? trungBinhThanhTienTienTe : BigDecimal.ZERO;
    }
    
    public void setTrungBinhThanhTienTienTe(BigDecimal trungBinhThanhTienTienTe) {
        this.trungBinhThanhTienTienTe = trungBinhThanhTienTienTe;
    }
    
    public long getSoGiaoDichDonGiaLonHon1Ty() {
        return soGiaoDichDonGiaLonHon1Ty;
    }
    
    public void setSoGiaoDichDonGiaLonHon1Ty(long soGiaoDichDonGiaLonHon1Ty) {
        this.soGiaoDichDonGiaLonHon1Ty = soGiaoDichDonGiaLonHon1Ty;
    }
    
    public BigDecimal getTongThanhTienVang() {
        return tongThanhTienVang != null ? tongThanhTienVang : BigDecimal.ZERO;
    }
    
    public void setTongThanhTienVang(BigDecimal tongThanhTienVang) {
        this.tongThanhTienVang = tongThanhTienVang;
    }
    
    public BigDecimal getTongThanhTienTienTe() {
        return tongThanhTienTienTe != null ? tongThanhTienTienTe : BigDecimal.ZERO;
    }
    
    public void setTongThanhTienTienTe(BigDecimal tongThanhTienTienTe) {
        this.tongThanhTienTienTe = tongThanhTienTienTe;
    }
    
    public BigDecimal getTongThanhTienTatCa() {
        return getTongThanhTienVang().add(getTongThanhTienTienTe());
    }
    
    @Override
    public String toString() {
        return String.format(
            "ThongKeDTO{tongSoLuongVang=%d, tongSoLuongTienTe=%d, trungBinhThanhTienTienTe=%s, " +
            "soGiaoDichDonGiaLonHon1Ty=%d, tongThanhTienVang=%s, tongThanhTienTienTe=%s}",
            tongSoLuongVang, tongSoLuongTienTe, trungBinhThanhTienTienTe,
            soGiaoDichDonGiaLonHon1Ty, tongThanhTienVang, tongThanhTienTienTe
        );
    }
}
