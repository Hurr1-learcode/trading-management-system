// Controller xử lý logic thống kê
package ui.Controller;

import java.time.LocalDate;
import java.util.List;

import dto.ThongKeDTO;
import model.GiaoDich;
import service.QuanLyGiaoDich;
import ui.Panels.StatisticsPanel;
import ui.Utils.UIUtils;
import usecase.AverageValueOfCurrencyTransactionsUseCase;
import usecase.ViewStatisticsUseCase;

public class StatisticsController {
    
    private StatisticsPanel statisticsPanel;
    private ViewStatisticsUseCase viewStatisticsUseCase;
    private AverageValueOfCurrencyTransactionsUseCase avgCurrencyUseCase;
    
    public StatisticsController(QuanLyGiaoDich quanLyGiaoDich, StatisticsPanel statisticsPanel) {
        this.statisticsPanel = statisticsPanel;
        
        // Initialize Use Case theo Clean Architecture (UC9)
        this.viewStatisticsUseCase = new ViewStatisticsUseCase(quanLyGiaoDich);
        this.avgCurrencyUseCase = new AverageValueOfCurrencyTransactionsUseCase(quanLyGiaoDich);
        
        setupEventHandlers();
        handleUpdateGeneralStatistics();
    }    /**
     * Thiết lập các event handler
     */
    private void setupEventHandlers() {
        // Xử lý sự kiện cập nhật thống kê tổng quan
        statisticsPanel.getBtnCapNhatThongKe().addActionListener(e -> handleUpdateGeneralStatistics());
        
        // Xử lý sự kiện thống kê hôm nay
        statisticsPanel.getBtnThongKeHomNay().addActionListener(e -> handleTodayStatistics());
        
        // Xử lý sự kiện thống kê tháng này
        statisticsPanel.getBtnThongKeThangNay().addActionListener(e -> handleMonthStatistics());
        
        // Xử lý sự kiện thống kê tất cả
        statisticsPanel.getBtnThongKeTatCa().addActionListener(e -> handleAllStatistics());
        
        // Xử lý sự kiện thống kê theo ngày
        statisticsPanel.getBtnThongKeTheoNgay().addActionListener(e -> handleDateStatistics());
    }
    
    /**
     *  Xử lý cập nhật thống kê tổng quan
     * Sử dụng ViewStatisticsUseCase theo Clean Architecture (UC9)
     */
    private void handleUpdateGeneralStatistics() {
        try {
            // Delegate to View Statistics Use Case (UC9)
            ThongKeDTO thongKe = viewStatisticsUseCase.executeGeneralStatistics();
            // Lấy giá trị trung bình thành tiền tiền tệ (UC10)
            java.math.BigDecimal avg = avgCurrencyUseCase.execute();
            // Hiển thị thống kê tổng quan kèm giá trị trung bình
            statisticsPanel.displayStatistics(thongKe, "TỔNG QUAN\nTrung bình thành tiền tiền tệ: " + ui.Utils.UIUtils.formatCurrency(avg) + " VNĐ");
            // Load giao dịch đơn giá lớn (tích hợp UC8)
            loadHighValueTransactions();
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi tải thống kê: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý thống kê hôm nay
     * Sử dụng ViewStatisticsUseCase theo Clean Architecture (UC9)
     */
    private void handleTodayStatistics() {
        try {
            // Delegate to View Statistics Use Case (UC9)
            ThongKeDTO thongKe = viewStatisticsUseCase.executeTodayStatistics();
            statisticsPanel.displayTodayStatistics(thongKe);
            
            // Vẫn hiển thị tất cả giao dịch đơn giá lớn
            loadHighValueTransactions();
            
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi tải thống kê hôm nay: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý thống kê tháng này
     * Sử dụng ViewStatisticsUseCase theo Clean Architecture (UC9)
     */
    private void handleMonthStatistics() {
        try {
            // Delegate to View Statistics Use Case (UC9)
            ThongKeDTO thongKe = viewStatisticsUseCase.executeCurrentMonthStatistics();
            statisticsPanel.displayMonthStatistics(thongKe);
            
            // Vẫn hiển thị tất cả giao dịch đơn giá lớn
            loadHighValueTransactions();
            
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi tải thống kê tháng này: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý thống kê tất cả
     */
    private void handleAllStatistics() {
        handleUpdateGeneralStatistics();
    }
    
    /**
     * Xử lý thống kê theo ngày
     * Sử dụng ViewStatisticsUseCase theo Clean Architecture (UC9)
     */
    private void handleDateStatistics() {
        try {
            String ngayStr = statisticsPanel.getSelectedDate();
            LocalDate ngayChon = UIUtils.parseDate(ngayStr);
            
            // Delegate to View Statistics Use Case (UC9)
            ThongKeDTO thongKe = viewStatisticsUseCase.executeDateStatistics(ngayChon);
            statisticsPanel.displayDateStatistics(thongKe, ngayChon);
            
            // Vẫn hiển thị tất cả giao dịch đơn giá lớn
            loadHighValueTransactions();
            
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, 
                "Lỗi khi tải thống kê theo ngày: " + ex.getMessage() + 
                "\nVui lòng nhập đúng định dạng yyyy-MM-dd");
        }
    }
    
    /**
     * Load danh sách giao dịch đơn giá lớn
     * Sử dụng ViewStatisticsUseCase để tích hợp UC8 và UC9
     */
    private void loadHighValueTransactions() {
        try {
            // Delegate to View Statistics Use Case (UC9) which integrates with UC8
            List<GiaoDich> donGiaLon = viewStatisticsUseCase.executeHighValueTransactions();
            statisticsPanel.loadHighValueTransactions(donGiaLon);
            
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi: " + ex.getMessage());
        }
    }
    
    /**
     * Refresh thống kê (gọi từ bên ngoài khi có thay đổi dữ liệu)
     */
    public void refreshStatistics() {
        handleUpdateGeneralStatistics();
    }
    
    /**
     * Lấy thống kê tổng quan
     * Sử dụng ViewStatisticsUseCase theo Clean Architecture (UC9)
     */
    public ThongKeDTO getGeneralStatistics() {
        try {
            return viewStatisticsUseCase.executeGeneralStatistics();
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi lấy thống kê: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy danh sách giao dịch đơn giá lớn
     * Sử dụng ViewStatisticsUseCase để tích hợp UC8 và UC9
     */
    public List<GiaoDich> getHighValueTransactions() {
        try {
            return viewStatisticsUseCase.executeHighValueTransactions();
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi: " + ex.getMessage());
            return List.of();
        }
    }
    
    /**
     * Lấy thống kê theo khoảng ngày
     * Sử dụng ViewStatisticsUseCase theo Clean Architecture (UC9)
     */
    public ThongKeDTO getStatisticsByDateRange(LocalDate fromDate, LocalDate toDate) {
        try {
            return viewStatisticsUseCase.executeDateRangeStatistics(fromDate, toDate);
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi lấy thống kê theo ngày: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy thống kê hôm nay
     * Sử dụng ViewStatisticsUseCase theo Clean Architecture (UC9)
     */
    public ThongKeDTO getTodayStatistics() {
        try {
            return viewStatisticsUseCase.executeTodayStatistics();
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi lấy thống kê hôm nay: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy thống kê theo tháng
     * Sử dụng ViewStatisticsUseCase theo Clean Architecture (UC9)
     */
    public ThongKeDTO getMonthStatistics(int year, int month) {
        try {
            return viewStatisticsUseCase.executeMonthStatistics(year, month);
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi lấy thống kê theo tháng: " + ex.getMessage());
            return null;
        }
    }
}
