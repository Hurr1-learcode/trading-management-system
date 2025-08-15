package usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import dto.ThongKeDTO;
import exception.BusinessException;
import exception.ValidationException;
import model.GiaoDich;
import service.QuanLyGiaoDich;

/**
 * UC9: View Statistics Use Case
 * Xử lý logic nghiệp vụ cho việc xem thống kê
 * Tuân theo Clean Architecture pattern
 */
public class ViewStatisticsUseCase {
    
    private QuanLyGiaoDich quanLyGiaoDich;
    
    public ViewStatisticsUseCase(QuanLyGiaoDich quanLyGiaoDich) {
        if (quanLyGiaoDich == null) {
            throw new IllegalArgumentException("QuanLyGiaoDich service không được null");
        }
        this.quanLyGiaoDich = quanLyGiaoDich;
    }
    
    /**
     * Execute UC9: Load thống kê tổng quan khi mở tab thống kê
     * @return ThongKeDTO chứa thông tin thống kê tổng quan
     * @throws BusinessException nếu có lỗi nghiệp vụ
     * @throws ValidationException nếu dữ liệu không hợp lệ
     */
    public ThongKeDTO executeGeneralStatistics() throws BusinessException, ValidationException {
        try {
            // Lấy thống kê tổng quan
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoai();
            
            // Validate kết quả
            validateStatisticsResult(thongKe);
            
            return thongKe;
            
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi tải thống kê tổng quan: " + ex.getMessage());
        }
    }
    
    /**
     * Execute thống kê hôm nay
     * @return ThongKeDTO chứa thông tin thống kê hôm nay
     * @throws BusinessException nếu có lỗi nghiệp vụ
     */
    public ThongKeDTO executeTodayStatistics() throws BusinessException {
        try {
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiHomNay();
            validateStatisticsResult(thongKe);
            return thongKe;
            
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi tải thống kê hôm nay: " + ex.getMessage());
        }
    }
    
    /**
     * Execute thống kê tháng hiện tại
     * @return ThongKeDTO chứa thông tin thống kê tháng này
     * @throws BusinessException nếu có lỗi nghiệp vụ
     */
    public ThongKeDTO executeCurrentMonthStatistics() throws BusinessException {
        try {
            LocalDate now = LocalDate.now();
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiTheoThang(
                now.getYear(), 
                now.getMonthValue()
            );
            validateStatisticsResult(thongKe);
            return thongKe;
            
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi tải thống kê tháng này: " + ex.getMessage());
        }
    }
    
    /**
     * Execute thống kê theo tháng cụ thể
     * @param year năm
     * @param month tháng
     * @return ThongKeDTO chứa thông tin thống kê
     * @throws ValidationException nếu tham số không hợp lệ
     * @throws BusinessException nếu có lỗi nghiệp vụ
     */
    public ThongKeDTO executeMonthStatistics(int year, int month) 
            throws ValidationException, BusinessException {
        
        // Validate input
        validateMonthYearInput(year, month);
        
        try {
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiTheoThang(year, month);
            validateStatisticsResult(thongKe);
            return thongKe;
            
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi tải thống kê tháng " + month + "/" + year + ": " + ex.getMessage());
        }
    }
    
    /**
     * Execute thống kê theo ngày cụ thể
     * @param date ngày cần thống kê
     * @return ThongKeDTO chứa thông tin thống kê
     * @throws ValidationException nếu ngày không hợp lệ
     * @throws BusinessException nếu có lỗi nghiệp vụ
     */
    public ThongKeDTO executeDateStatistics(LocalDate date) 
            throws ValidationException, BusinessException {
        
        // Validate input
        validateDateInput(date);
        
        try {
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiTheoKhoangNgay(date, date);
            validateStatisticsResult(thongKe);
            return thongKe;
            
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi tải thống kê ngày " + date + ": " + ex.getMessage());
        }
    }
    
    /**
     * Execute thống kê theo khoảng ngày
     * @param fromDate ngày bắt đầu
     * @param toDate ngày kết thúc
     * @return ThongKeDTO chứa thông tin thống kê
     * @throws ValidationException nếu khoảng ngày không hợp lệ
     * @throws BusinessException nếu có lỗi nghiệp vụ
     */
    public ThongKeDTO executeDateRangeStatistics(LocalDate fromDate, LocalDate toDate) 
            throws ValidationException, BusinessException {
        
        // Validate input
        validateDateRangeInput(fromDate, toDate);
        
        try {
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiTheoKhoangNgay(fromDate, toDate);
            validateStatisticsResult(thongKe);
            return thongKe;
            
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi tải thống kê từ " + fromDate + " đến " + toDate + ": " + ex.getMessage());
        }
    }
    
    /**
     * Lấy danh sách giao dịch đơn giá lớn (integration với UC8)
     * @return List<GiaoDich> danh sách giao dịch có đơn giá > 1 tỷ
     * @throws BusinessException nếu có lỗi nghiệp vụ
     */
    public List<GiaoDich> executeHighValueTransactions() throws BusinessException {
        try {
            // Sử dụng TransactionsWithUnitPriceOver1BillionUseCase để tương thích với UC8
            TransactionsWithUnitPriceOver1BillionUseCase highValueUseCase = 
                new TransactionsWithUnitPriceOver1BillionUseCase(quanLyGiaoDich);
            
            return highValueUseCase.execute();
            
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi tải giao dịch đơn giá lớn: " + ex.getMessage());
        }
    }
    
    // ===== PRIVATE VALIDATION METHODS =====
    
    /**
     * Validate kết quả thống kê
     */
    private void validateStatisticsResult(ThongKeDTO thongKe) throws ValidationException {
        if (thongKe == null) {
            throw new ValidationException("Kết quả thống kê không được null", "thongKe");
        }
        
        // Validate số liệu âm
        if (thongKe.getTongSoLuongVang() < 0 || thongKe.getTongSoLuongTienTe() < 0) {
            throw new ValidationException("Số lượng giao dịch không được âm", "soLuong");
        }
        
        // Validate số tiền âm  
        if (thongKe.getTongThanhTienVang().compareTo(BigDecimal.ZERO) < 0 || 
            thongKe.getTongThanhTienTienTe().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Tổng thành tiền không được âm", "thanhTien");
        }
    }
    
    /**
     * Validate input tháng và năm
     */
    private void validateMonthYearInput(int year, int month) throws ValidationException {
        if (year < 1900 || year > 2100) {
            throw new ValidationException("Năm phải trong khoảng 1900-2100", "year");
        }
        
        if (month < 1 || month > 12) {
            throw new ValidationException("Tháng phải trong khoảng 1-12", "month");
        }
    }
    
    /**
     * Validate input ngày
     */
    private void validateDateInput(LocalDate date) throws ValidationException {
        if (date == null) {
            throw new ValidationException("Ngày không được null", "date");
        }
        
        // Validate năm hợp lý
        int year = date.getYear();
        if (year < 1900 || year > 2100) {
            throw new ValidationException("Năm phải trong khoảng 1900-2100", "year");
        }
        
        // Validate không được chọn ngày tương lai quá xa
        LocalDate maxDate = LocalDate.now().plusYears(1);
        if (date.isAfter(maxDate)) {
            throw new ValidationException("Không thể thống kê cho ngày quá xa trong tương lai", "date");
        }
    }
    
    /**
     * Validate khoảng ngày
     */
    private void validateDateRangeInput(LocalDate fromDate, LocalDate toDate) throws ValidationException {
        validateDateInput(fromDate);
        validateDateInput(toDate);
        
        if (fromDate.isAfter(toDate)) {
            throw new ValidationException("Ngày bắt đầu không được lớn hơn ngày kết thúc", "dateRange");
        }
        
        // Validate khoảng thời gian không quá dài (tối đa 1 năm)
        if (fromDate.plusYears(1).isBefore(toDate)) {
            throw new ValidationException("Khoảng thời gian thống kê không được vượt quá 1 năm", "dateRange");
        }
    }
}
