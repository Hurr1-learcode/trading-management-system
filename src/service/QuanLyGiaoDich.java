// Service layer for business logic
package service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dao.GiaoDichDAO;
import dao.impl.GiaoDichDAOImpl;
import dto.GiaoDichFormDTO;
import dto.ThongKeDTO;
import exception.BusinessException;
import exception.DataAccessException;
import exception.ValidationException;
import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;


public class QuanLyGiaoDich {
    private static final Logger LOGGER = Logger.getLogger(QuanLyGiaoDich.class.getName());
    private final GiaoDichDAO giaoDichDAO;
    
    public QuanLyGiaoDich() {
        this.giaoDichDAO = new GiaoDichDAOImpl();
    }
    
    public QuanLyGiaoDich(GiaoDichDAO giaoDichDAO) {
        this.giaoDichDAO = giaoDichDAO;
    }
    
    // CRUD Operations
    public GiaoDich add(GiaoDichFormDTO formDTO) throws BusinessException {
        try {
            // Check if transaction already exists
            if (giaoDichDAO.exists(formDTO.getMaGiaoDich())) {
                throw new BusinessException("Mã giao dịch đã tồn tại: " + formDTO.getMaGiaoDich(), "DUPLICATE_ID");
            }
            
            GiaoDich giaoDich = createGiaoDichFromDTO(formDTO);
            GiaoDich saved = giaoDichDAO.save(giaoDich);
            
            LOGGER.info("Đã thêm giao dịch mới: " + saved.getMaGiaoDich());
            return saved;
            
        } catch (DataAccessException | ValidationException e) {
            throw new BusinessException("Không thể thêm giao dịch: " + e.getMessage(), e);
        }
    }
    
    public GiaoDich edit(String maGiaoDich, GiaoDichFormDTO formDTO) throws BusinessException {
        try {
            // Check if transaction exists
            Optional<GiaoDich> existing = giaoDichDAO.findById(maGiaoDich);
            if (!existing.isPresent()) {
                throw new BusinessException("Không tìm thấy giao dịch: " + maGiaoDich, "NOT_FOUND");
            }
            
            // Create updated transaction
            GiaoDich giaoDich = createGiaoDichFromDTO(formDTO);
            GiaoDich updated = giaoDichDAO.update(giaoDich);
            
            LOGGER.info("Đã cập nhật giao dịch: " + updated.getMaGiaoDich());
            return updated;
            
        } catch (DataAccessException | ValidationException e) {
            throw new BusinessException("Không thể cập nhật giao dịch: " + e.getMessage(), e);
        }
    }
    
    public boolean remove(String maGiaoDich) throws BusinessException {
        try {
            // Check if transaction exists
            if (!giaoDichDAO.exists(maGiaoDich)) {
                throw new BusinessException("Không tìm thấy giao dịch: " + maGiaoDich, "NOT_FOUND");
            }
            
            boolean deleted = giaoDichDAO.delete(maGiaoDich);
            if (deleted) {
                LOGGER.info("Đã xóa giao dịch: " + maGiaoDich);
            }
            return deleted;
            
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể xóa giao dịch: " + e.getMessage(), e);
        }
    }
    
    public List<GiaoDich> getAll() throws BusinessException {
        try {
            return giaoDichDAO.findAll();
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể lấy danh sách giao dịch: " + e.getMessage(), e);
        }
    }
    
    public Optional<GiaoDich> findById(String maGiaoDich) throws BusinessException {
        try {
            return giaoDichDAO.findById(maGiaoDich);
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể tìm giao dịch: " + e.getMessage(), e);
        }
    }
    
    // Statistics and Filtering
    public ThongKeDTO getTongSoLuongTheoLoai() throws BusinessException {
        try {
            long tongSoLuongVang = giaoDichDAO.countByLoaiGiaoDich("VANG");
            long tongSoLuongTienTe = giaoDichDAO.countByLoaiGiaoDich("TIEN_TE");
            BigDecimal trungBinhThanhTienTienTe = giaoDichDAO.getAverageThanhTienByLoaiGiaoDich("TIEN_TE");
            
            // Count transactions with don gia > 1 billion
            List<GiaoDich> giaoDichLon = getDonGiaLonHon1Ty();
            long soGiaoDichDonGiaLonHon1Ty = giaoDichLon.size();
            
            BigDecimal tongThanhTienVang = giaoDichDAO.sumThanhTienByLoaiGiaoDich("VANG");
            BigDecimal tongThanhTienTienTe = giaoDichDAO.sumThanhTienByLoaiGiaoDich("TIEN_TE");
            
            return new ThongKeDTO(tongSoLuongVang, tongSoLuongTienTe, trungBinhThanhTienTienTe,
                                 soGiaoDichDonGiaLonHon1Ty, tongThanhTienVang, tongThanhTienTienTe);
                                 
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể tính thống kê: " + e.getMessage(), e);
        }
    }
    
    // Thống kê theo thời gian
    public ThongKeDTO getTongSoLuongTheoLoaiTheoThang(int nam, int thang) throws BusinessException {
        LocalDate tuNgay = LocalDate.of(nam, thang, 1);
        LocalDate denNgay = tuNgay.withDayOfMonth(tuNgay.lengthOfMonth());
        
        return getTongSoLuongTheoLoaiTheoKhoangNgay(tuNgay, denNgay);
    }
    
    public ThongKeDTO getTongSoLuongTheoLoaiHomNay() throws BusinessException {
        LocalDate homNay = LocalDate.now();
        return getTongSoLuongTheoLoaiTheoKhoangNgay(homNay, homNay);
    }
    
    public ThongKeDTO getTongSoLuongTheoLoaiTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        try {
            long tongSoLuongVang = giaoDichDAO.countByLoaiGiaoDichAndDateRange("VANG", tuNgay, denNgay);
            long tongSoLuongTienTe = giaoDichDAO.countByLoaiGiaoDichAndDateRange("TIEN_TE", tuNgay, denNgay);
            BigDecimal trungBinhThanhTienTienTe = giaoDichDAO.getAverageThanhTienByLoaiGiaoDichAndDateRange("TIEN_TE", tuNgay, denNgay);
            
            // Count transactions with don gia > 1 billion in date range
            List<GiaoDich> giaoDichLon = findByDateRange(tuNgay, denNgay).stream()
                    .filter(gd -> gd.getDonGia().compareTo(new BigDecimal("1000000000")) > 0)
                    .collect(Collectors.toList());
            long soGiaoDichDonGiaLonHon1Ty = giaoDichLon.size();
            
            BigDecimal tongThanhTienVang = giaoDichDAO.sumThanhTienByLoaiGiaoDichAndDateRange("VANG", tuNgay, denNgay);
            BigDecimal tongThanhTienTienTe = giaoDichDAO.sumThanhTienByLoaiGiaoDichAndDateRange("TIEN_TE", tuNgay, denNgay);
            
            return new ThongKeDTO(tongSoLuongVang, tongSoLuongTienTe, trungBinhThanhTienTienTe,
                                 soGiaoDichDonGiaLonHon1Ty, tongThanhTienVang, tongThanhTienTienTe);
                                 
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể tính thống kê theo thời gian: " + e.getMessage(), e);
        }
    }

    public BigDecimal getTrungBinhThanhTienTienTe() throws BusinessException {
        try {
            return giaoDichDAO.getAverageThanhTienByLoaiGiaoDich("TIEN_TE");
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể tính trung bình thành tiền tiền tệ: " + e.getMessage(), e);
        }
    }
    
    public List<GiaoDich> getDonGiaLonHon1Ty() throws BusinessException {
        try {
            BigDecimal motTy = new BigDecimal("1000000000");
            System.out.println("DEBUG: Looking for transactions with don_gia > " + motTy);
            
            // Lấy tất cả giao dịch để debug
            List<GiaoDich> allTransactions = giaoDichDAO.findAll();
            System.out.println("DEBUG: Total transactions in database: " + allTransactions.size());
            
            for (GiaoDich gd : allTransactions) {
                BigDecimal donGia = gd.getDonGia();
                boolean isGreater = donGia.compareTo(motTy) > 0;
                System.out.println("DEBUG: Transaction ID=" + gd.getMaGiaoDich() + 
                                 ", don_gia=" + donGia + 
                                 ", > 1ty? " + isGreater);
                
                if (gd instanceof GiaoDichTienTe) {
                    GiaoDichTienTe gdtt = (GiaoDichTienTe) gd;
                    System.out.println("       Currency: " + gdtt.getLoaiTien() + 
                                     ", Exchange rate: " + gdtt.getTiGia() + 
                                     ", Total amount: " + gdtt.tinhThanhTien());
                }
            }
            
            List<GiaoDich> result = giaoDichDAO.findByDonGiaGreaterThan(motTy);
            System.out.println("DEBUG: Found " + result.size() + " transactions with don_gia > 1 billion");
            return result;
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể lọc giao dịch đơn giá lớn hơn 1 tỷ: " + e.getMessage(), e);
        }
    }
    
    // Filter methods
    public List<GiaoDich> findByLoaiGiaoDich(String loaiGiaoDich) throws BusinessException {
        try {
            return giaoDichDAO.findByLoaiGiaoDich(loaiGiaoDich);
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể tìm giao dịch theo loại: " + e.getMessage(), e);
        }
    }
    
    public List<GiaoDich> findByDateRange(LocalDate tuNgay, LocalDate denNgay) throws BusinessException {
        try {
            if (tuNgay.isAfter(denNgay)) {
                throw new BusinessException("Ngày bắt đầu không được lớn hơn ngày kết thúc", "INVALID_DATE_RANGE");
            }
            return giaoDichDAO.findByNgayGiaoDich(tuNgay, denNgay);
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể tìm giao dịch theo khoảng ngày: " + e.getMessage(), e);
        }
    }
    
    public List<GiaoDich> findByDonGiaGreaterThan(BigDecimal donGia) throws BusinessException {
        try {
            return giaoDichDAO.findByDonGiaGreaterThan(donGia);
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể tìm giao dịch theo đơn giá: " + e.getMessage(), e);
        }
    }
    
    // Business validation methods
    public void validateGiaoDichData(GiaoDichFormDTO formDTO) throws ValidationException {
        if (formDTO.getMaGiaoDich() == null || formDTO.getMaGiaoDich().trim().isEmpty()) {
            throw new ValidationException("Mã giao dịch không được để trống", "maGiaoDich");
        }
        
        if (formDTO.getNgayGiaoDich() == null) {
            throw new ValidationException("Ngày giao dịch không được để trống", "ngayGiaoDich");
        }
        
        if (formDTO.getNgayGiaoDich().isAfter(LocalDate.now())) {
            throw new ValidationException("Ngày giao dịch không được lớn hơn ngày hiện tại", "ngayGiaoDich");
        }
        
        if (formDTO.getDonGia() == null || formDTO.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Đơn giá phải lớn hơn 0", "donGia");
        }
        
        if (formDTO.getSoLuong() <= 0) {
            throw new ValidationException("Số lượng phải lớn hơn 0", "soLuong");
        }
        
        if (formDTO.getLoaiGiaoDich() == null || formDTO.getLoaiGiaoDich().trim().isEmpty()) {
            throw new ValidationException("Loại giao dịch không được để trống", "loaiGiaoDich");
        }
        
        if (formDTO.isGiaoDichVang()) {
            if (formDTO.getLoaiVang() == null || formDTO.getLoaiVang().trim().isEmpty()) {
                throw new ValidationException("Loại vàng không được để trống", "loaiVang");
            }
        } else if (formDTO.isGiaoDichTienTe()) {
            if (formDTO.getLoaiTien() == null || formDTO.getLoaiTien().trim().isEmpty()) {
                throw new ValidationException("Loại tiền không được để trống", "loaiTien");
            }
            if (formDTO.getTiGia() == null || formDTO.getTiGia().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Tỉ giá phải lớn hơn 0", "tiGia");
            }
        }
    }
    
    // Helper methods
    private GiaoDich createGiaoDichFromDTO(GiaoDichFormDTO formDTO) throws ValidationException {
        validateGiaoDichData(formDTO);
        
        if (formDTO.isGiaoDichVang()) {
            return new GiaoDichVang(
                formDTO.getMaGiaoDich(),
                formDTO.getNgayGiaoDich(),
                formDTO.getDonGia(),
                formDTO.getSoLuong(),
                formDTO.getLoaiVang()
            );
        } else if (formDTO.isGiaoDichTienTe()) {
            return new GiaoDichTienTe(
                formDTO.getMaGiaoDich(),
                formDTO.getNgayGiaoDich(),
                formDTO.getDonGia(),
                formDTO.getSoLuong(),
                formDTO.getLoaiTien(),
                formDTO.getTiGia()
            );
        } else {
            throw new ValidationException("Loại giao dịch không hợp lệ: " + formDTO.getLoaiGiaoDich(), "loaiGiaoDich");
        }
    }
    
    // Utility methods for business logic
    public BigDecimal calculateTotalRevenue() throws BusinessException {
        try {
            BigDecimal tongVang = giaoDichDAO.sumThanhTienByLoaiGiaoDich("VANG");
            BigDecimal tongTienTe = giaoDichDAO.sumThanhTienByLoaiGiaoDich("TIEN_TE");
            
            return (tongVang != null ? tongVang : BigDecimal.ZERO)
                    .add(tongTienTe != null ? tongTienTe : BigDecimal.ZERO);
                    
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể tính tổng doanh thu: " + e.getMessage(), e);
        }
    }
    
    public long getTotalTransactionCount() throws BusinessException {
        try {
            return giaoDichDAO.countByLoaiGiaoDich("VANG") + giaoDichDAO.countByLoaiGiaoDich("TIEN_TE");
        } catch (DataAccessException e) {
            throw new BusinessException("Không thể đếm tổng số giao dịch: " + e.getMessage(), e);
        }
    }
}