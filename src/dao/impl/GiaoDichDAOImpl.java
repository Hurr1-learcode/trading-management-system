// Implementation of GiaoDichDAO with MySQL
package dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import dao.GiaoDichDAO;
import exception.DataAccessException;
import exception.ValidationException;
import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;
import utils.DatabaseUtil;

public class GiaoDichDAOImpl implements GiaoDichDAO {
    private static final Logger LOGGER = Logger.getLogger(GiaoDichDAOImpl.class.getName());
    private final DatabaseUtil databaseUtil = DatabaseUtil.getInstance();
    
    private static final String INSERT_GIAO_DICH = 
        "INSERT INTO giao_dich (ma_giao_dich, ngay_giao_dich, don_gia, so_luong, loai_giao_dich, loai_vang, loai_tien, ti_gia) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_GIAO_DICH = 
        "UPDATE giao_dich SET ngay_giao_dich = ?, don_gia = ?, so_luong = ?, loai_vang = ?, loai_tien = ?, ti_gia = ? " +
        "WHERE ma_giao_dich = ?";
    
    private static final String DELETE_GIAO_DICH = "DELETE FROM giao_dich WHERE ma_giao_dich = ?";
    
    private static final String SELECT_BY_ID = "SELECT * FROM giao_dich WHERE ma_giao_dich = ?";
    
    private static final String SELECT_ALL = "SELECT * FROM giao_dich ORDER BY ngay_giao_dich DESC";
    
    private static final String SELECT_BY_LOAI = "SELECT * FROM giao_dich WHERE loai_giao_dich = ? ORDER BY ngay_giao_dich DESC";
    
    private static final String SELECT_BY_DATE_RANGE = 
        "SELECT * FROM giao_dich WHERE ngay_giao_dich BETWEEN ? AND ? ORDER BY ngay_giao_dich DESC";
    
    private static final String SELECT_BY_DON_GIA_GREATER = 
        "SELECT * FROM giao_dich WHERE don_gia > ? ORDER BY don_gia DESC";
    
    private static final String COUNT_BY_LOAI = "SELECT COUNT(*) FROM giao_dich WHERE loai_giao_dich = ?";
    
    private static final String COUNT_BY_LOAI_AND_DATE = 
        "SELECT COUNT(*) FROM giao_dich WHERE loai_giao_dich = ? AND ngay_giao_dich BETWEEN ? AND ?";
    
    private static final String SUM_THANH_TIEN = 
        "SELECT SUM(CASE WHEN loai_giao_dich = 'VANG' THEN don_gia * so_luong " +
        "WHEN loai_giao_dich = 'TIEN_TE' THEN don_gia * so_luong * ti_gia END) " +
        "FROM giao_dich WHERE loai_giao_dich = ?";
    
    private static final String SUM_THANH_TIEN_BY_DATE = 
        "SELECT SUM(CASE WHEN loai_giao_dich = 'VANG' THEN don_gia * so_luong " +
        "WHEN loai_giao_dich = 'TIEN_TE' THEN don_gia * so_luong * ti_gia END) " +
        "FROM giao_dich WHERE loai_giao_dich = ? AND ngay_giao_dich BETWEEN ? AND ?";
    
    private static final String AVG_THANH_TIEN = 
        "SELECT AVG(CASE WHEN loai_giao_dich = 'VANG' THEN don_gia * so_luong " +
        "WHEN loai_giao_dich = 'TIEN_TE' THEN don_gia * so_luong * ti_gia END) " +
        "FROM giao_dich WHERE loai_giao_dich = ?";
    
    private static final String AVG_THANH_TIEN_BY_DATE = 
        "SELECT AVG(CASE WHEN loai_giao_dich = 'VANG' THEN don_gia * so_luong " +
        "WHEN loai_giao_dich = 'TIEN_TE' THEN don_gia * so_luong * ti_gia END) " +
        "FROM giao_dich WHERE loai_giao_dich = ? AND ngay_giao_dich BETWEEN ? AND ?";
    
    private static final String EXISTS_BY_ID = "SELECT 1 FROM giao_dich WHERE ma_giao_dich = ?";

    @Override
    public GiaoDich save(GiaoDich entity) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(INSERT_GIAO_DICH);
            
            setCommonParameters(stmt, entity);
            setSpecificParameters(stmt, entity);
            
            int result = stmt.executeUpdate();
            if (result == 0) {
                throw new DataAccessException("Không thể lưu giao dịch");
            }
            
            LOGGER.info("Đã lưu giao dịch: " + entity.getMaGiaoDich());
            return entity;
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lưu giao dịch: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    @Override
    public GiaoDich update(GiaoDich entity) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(UPDATE_GIAO_DICH);
            
            stmt.setDate(1, Date.valueOf(entity.getNgayGiaoDich()));
            stmt.setBigDecimal(2, entity.getDonGia());
            stmt.setInt(3, entity.getSoLuong());
            
            if (entity instanceof GiaoDichVang) {
                GiaoDichVang giaoDichVang = (GiaoDichVang) entity;
                stmt.setString(4, giaoDichVang.getLoaiVang());
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.DECIMAL);
            } else if (entity instanceof GiaoDichTienTe) {
                GiaoDichTienTe giaoDichTienTe = (GiaoDichTienTe) entity;
                stmt.setNull(4, Types.VARCHAR);
                stmt.setString(5, giaoDichTienTe.getLoaiTien());
                stmt.setBigDecimal(6, giaoDichTienTe.getTiGia());
            }
            
            stmt.setString(7, entity.getMaGiaoDich());
            
            int result = stmt.executeUpdate();
            if (result == 0) {
                throw new DataAccessException("Không tìm thấy giao dịch để cập nhật");
            }
            
            LOGGER.info("Đã cập nhật giao dịch: " + entity.getMaGiaoDich());
            return entity;
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật giao dịch: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    @Override
    public boolean delete(String id) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(DELETE_GIAO_DICH);
            stmt.setString(1, id);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                LOGGER.info("Đã xóa giao dịch: " + id);
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa giao dịch: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    @Override
    public Optional<GiaoDich> findById(String id) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ID);
            stmt.setString(1, id);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToGiaoDich(rs));
            }
            return Optional.empty();
            
        } catch (SQLException | ValidationException e) {
            throw new DataAccessException("Lỗi khi tìm giao dịch: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<GiaoDich> findAll() throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL);
            rs = stmt.executeQuery();
            
            List<GiaoDich> giaoDichs = new ArrayList<>();
            while (rs.next()) {
                giaoDichs.add(mapResultSetToGiaoDich(rs));
            }
            return giaoDichs;
            
        } catch (SQLException | ValidationException e) {
            throw new DataAccessException("Lỗi khi lấy danh sách giao dịch: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public boolean exists(String id) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(EXISTS_BY_ID);
            stmt.setString(1, id);
            
            rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi kiểm tra tồn tại giao dịch: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<GiaoDich> findByLoaiGiaoDich(String loaiGiaoDich) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_LOAI);
            stmt.setString(1, loaiGiaoDich);
            rs = stmt.executeQuery();
            
            List<GiaoDich> giaoDichs = new ArrayList<>();
            while (rs.next()) {
                giaoDichs.add(mapResultSetToGiaoDich(rs));
            }
            return giaoDichs;
            
        } catch (SQLException | ValidationException e) {
            throw new DataAccessException("Lỗi khi tìm giao dịch theo loại: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<GiaoDich> findByNgayGiaoDich(LocalDate tuNgay, LocalDate denNgay) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_DATE_RANGE);
            stmt.setDate(1, Date.valueOf(tuNgay));
            stmt.setDate(2, Date.valueOf(denNgay));
            rs = stmt.executeQuery();
            
            List<GiaoDich> giaoDichs = new ArrayList<>();
            while (rs.next()) {
                giaoDichs.add(mapResultSetToGiaoDich(rs));
            }
            return giaoDichs;
            
        } catch (SQLException | ValidationException e) {
            throw new DataAccessException("Lỗi khi tìm giao dịch theo ngày: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<GiaoDich> findByDonGiaGreaterThan(BigDecimal donGia) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_DON_GIA_GREATER);
            stmt.setBigDecimal(1, donGia);
            rs = stmt.executeQuery();
            
            List<GiaoDich> giaoDichs = new ArrayList<>();
            while (rs.next()) {
                giaoDichs.add(mapResultSetToGiaoDich(rs));
            }
            return giaoDichs;
            
        } catch (SQLException | ValidationException e) {
            throw new DataAccessException("Lỗi khi tìm giao dịch theo đơn giá: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public long countByLoaiGiaoDich(String loaiGiaoDich) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(COUNT_BY_LOAI);
            stmt.setString(1, loaiGiaoDich);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi đếm giao dịch: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public BigDecimal sumThanhTienByLoaiGiaoDich(String loaiGiaoDich) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(SUM_THANH_TIEN);
            stmt.setString(1, loaiGiaoDich);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tính tổng thành tiền: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public BigDecimal getAverageThanhTienByLoaiGiaoDich(String loaiGiaoDich) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(AVG_THANH_TIEN);
            stmt.setString(1, loaiGiaoDich);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tính trung bình thành tiền: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    // Thống kê theo thời gian
    @Override
    public long countByLoaiGiaoDichAndDateRange(String loaiGiaoDich, LocalDate tuNgay, LocalDate denNgay) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(COUNT_BY_LOAI_AND_DATE);
            stmt.setString(1, loaiGiaoDich);
            stmt.setDate(2, Date.valueOf(tuNgay));
            stmt.setDate(3, Date.valueOf(denNgay));
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi đếm giao dịch theo thời gian: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public BigDecimal sumThanhTienByLoaiGiaoDichAndDateRange(String loaiGiaoDich, LocalDate tuNgay, LocalDate denNgay) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(SUM_THANH_TIEN_BY_DATE);
            stmt.setString(1, loaiGiaoDich);
            stmt.setDate(2, Date.valueOf(tuNgay));
            stmt.setDate(3, Date.valueOf(denNgay));
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tính tổng thành tiền theo thời gian: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public BigDecimal getAverageThanhTienByLoaiGiaoDichAndDateRange(String loaiGiaoDich, LocalDate tuNgay, LocalDate denNgay) throws DataAccessException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = databaseUtil.getConnection();
            stmt = conn.prepareStatement(AVG_THANH_TIEN_BY_DATE);
            stmt.setString(1, loaiGiaoDich);
            stmt.setDate(2, Date.valueOf(tuNgay));
            stmt.setDate(3, Date.valueOf(denNgay));
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tính trung bình thành tiền theo thời gian: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    private void setCommonParameters(PreparedStatement stmt, GiaoDich entity) throws SQLException {
        stmt.setString(1, entity.getMaGiaoDich());
        stmt.setDate(2, Date.valueOf(entity.getNgayGiaoDich()));
        stmt.setBigDecimal(3, entity.getDonGia());
        stmt.setInt(4, entity.getSoLuong());
        stmt.setString(5, entity.getLoaiGiaoDich());
    }
    
    private void setSpecificParameters(PreparedStatement stmt, GiaoDich entity) throws SQLException {
        if (entity instanceof GiaoDichVang) {
            GiaoDichVang giaoDichVang = (GiaoDichVang) entity;
            stmt.setString(6, giaoDichVang.getLoaiVang());
            stmt.setNull(7, Types.VARCHAR);
            stmt.setNull(8, Types.DECIMAL);
        } else if (entity instanceof GiaoDichTienTe) {
            GiaoDichTienTe giaoDichTienTe = (GiaoDichTienTe) entity;
            stmt.setNull(6, Types.VARCHAR);
            stmt.setString(7, giaoDichTienTe.getLoaiTien());
            stmt.setBigDecimal(8, giaoDichTienTe.getTiGia());
        }
    }
    
    private GiaoDich mapResultSetToGiaoDich(ResultSet rs) throws SQLException, ValidationException {
        String loaiGiaoDich = rs.getString("loai_giao_dich");
        
        if ("VANG".equals(loaiGiaoDich)) {
            GiaoDichVang giaoDichVang = new GiaoDichVang();
            giaoDichVang.setMaGiaoDich(rs.getString("ma_giao_dich"));
            giaoDichVang.setNgayGiaoDich(rs.getDate("ngay_giao_dich").toLocalDate());
            giaoDichVang.setDonGia(rs.getBigDecimal("don_gia"));
            giaoDichVang.setSoLuong(rs.getInt("so_luong"));
            giaoDichVang.setLoaiVang(rs.getString("loai_vang"));
            return giaoDichVang;
        } else if ("TIEN_TE".equals(loaiGiaoDich)) {
            GiaoDichTienTe giaoDichTienTe = new GiaoDichTienTe();
            giaoDichTienTe.setMaGiaoDich(rs.getString("ma_giao_dich"));
            giaoDichTienTe.setNgayGiaoDich(rs.getDate("ngay_giao_dich").toLocalDate());
            giaoDichTienTe.setDonGia(rs.getBigDecimal("don_gia"));
            giaoDichTienTe.setSoLuong(rs.getInt("so_luong"));
            giaoDichTienTe.setLoaiTien(rs.getString("loai_tien"));
            giaoDichTienTe.setTiGia(rs.getBigDecimal("ti_gia"));
            return giaoDichTienTe;
        }
        
        throw new SQLException("Loại giao dịch không hợp lệ: " + loaiGiaoDich);
    }
    
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) databaseUtil.releaseConnection(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing database resources", e);
        }
    }
}
