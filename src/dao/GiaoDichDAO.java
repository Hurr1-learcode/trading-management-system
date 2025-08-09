// DAO interface for GiaoDich operations
package dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import exception.DataAccessException;
import model.GiaoDich;

public interface GiaoDichDAO extends BaseDAO<GiaoDich, String> {
    List<GiaoDich> findByLoaiGiaoDich(String loaiGiaoDich) throws DataAccessException;
    List<GiaoDich> findByNgayGiaoDich(LocalDate tuNgay, LocalDate denNgay) throws DataAccessException;
    List<GiaoDich> findByDonGiaGreaterThan(BigDecimal donGia) throws DataAccessException;
    
    // Thống kê tổng (tất cả thời gian)
    long countByLoaiGiaoDich(String loaiGiaoDich) throws DataAccessException;
    BigDecimal sumThanhTienByLoaiGiaoDich(String loaiGiaoDich) throws DataAccessException;
    BigDecimal getAverageThanhTienByLoaiGiaoDich(String loaiGiaoDich) throws DataAccessException;
    
    // Thống kê theo thời gian
    long countByLoaiGiaoDichAndDateRange(String loaiGiaoDich, LocalDate tuNgay, LocalDate denNgay) throws DataAccessException;
    BigDecimal sumThanhTienByLoaiGiaoDichAndDateRange(String loaiGiaoDich, LocalDate tuNgay, LocalDate denNgay) throws DataAccessException;
    BigDecimal getAverageThanhTienByLoaiGiaoDichAndDateRange(String loaiGiaoDich, LocalDate tuNgay, LocalDate denNgay) throws DataAccessException;
}
