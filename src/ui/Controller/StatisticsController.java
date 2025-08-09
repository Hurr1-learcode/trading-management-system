// Controller xử lý logic thống kê
package ui.Controller;

import java.time.LocalDate;
import java.util.List;

import dto.ThongKeDTO;
import model.GiaoDich;
import service.QuanLyGiaoDich;
import ui.Panels.StatisticsPanel;
import ui.Utils.UIUtils;

public class StatisticsController {
    
    private QuanLyGiaoDich quanLyGiaoDich;
    private StatisticsPanel statisticsPanel;
    
    public StatisticsController(StatisticsPanel statisticsPanel, QuanLyGiaoDich quanLyGiaoDich) {
        this.statisticsPanel = statisticsPanel;
        this.quanLyGiaoDich = quanLyGiaoDich;
        
        setupEventHandlers();
        loadInitialStatistics();
    }
    
    /**
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
     * Load thống kê ban đầu
     */
    private void loadInitialStatistics() {
        handleUpdateGeneralStatistics();
    }
    
    /**
     * Xử lý cập nhật thống kê tổng quan
     */
    private void handleUpdateGeneralStatistics() {
        try {
            // Lấy thống kê tổng quan
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoai();
            statisticsPanel.displayStatistics(thongKe);
            
            // Load giao dịch đơn giá lớn
            loadHighValueTransactions();
            
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi tải thống kê: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý thống kê hôm nay
     */
    private void handleTodayStatistics() {
        try {
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiHomNay();
            statisticsPanel.displayTodayStatistics(thongKe);
            
            // Vẫn hiển thị tất cả giao dịch đơn giá lớn
            loadHighValueTransactions();
            
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi tải thống kê hôm nay: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý thống kê tháng này
     */
    private void handleMonthStatistics() {
        try {
            LocalDate now = LocalDate.now();
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiTheoThang(
                now.getYear(), 
                now.getMonthValue()
            );
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
     */
    private void handleDateStatistics() {
        try {
            String ngayStr = statisticsPanel.getSelectedDate();
            LocalDate ngayChon = UIUtils.parseDate(ngayStr);
            
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiTheoKhoangNgay(
                ngayChon, 
                ngayChon
            );
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
     */
    private void loadHighValueTransactions() {
        try {
            List<GiaoDich> donGiaLon = quanLyGiaoDich.getDonGiaLonHon1Ty();
            statisticsPanel.loadHighValueTransactions(donGiaLon);
            
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi tải giao dịch đơn giá lớn: " + ex.getMessage());
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
     */
    public ThongKeDTO getGeneralStatistics() {
        try {
            return quanLyGiaoDich.getTongSoLuongTheoLoai();
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi lấy thống kê: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy danh sách giao dịch đơn giá lớn
     */
    public List<GiaoDich> getHighValueTransactions() {
        try {
            return quanLyGiaoDich.getDonGiaLonHon1Ty();
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi lấy giao dịch đơn giá lớn: " + ex.getMessage());
            return List.of();
        }
    }
    
    /**
     * Lấy thống kê theo khoảng ngày
     */
    public ThongKeDTO getStatisticsByDateRange(LocalDate fromDate, LocalDate toDate) {
        try {
            return quanLyGiaoDich.getTongSoLuongTheoLoaiTheoKhoangNgay(fromDate, toDate);
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi lấy thống kê theo ngày: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy thống kê hôm nay
     */
    public ThongKeDTO getTodayStatistics() {
        try {
            return quanLyGiaoDich.getTongSoLuongTheoLoaiHomNay();
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi lấy thống kê hôm nay: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy thống kê theo tháng
     */
    public ThongKeDTO getMonthStatistics(int year, int month) {
        try {
            return quanLyGiaoDich.getTongSoLuongTheoLoaiTheoThang(year, month);
        } catch (Exception ex) {
            UIUtils.showError(statisticsPanel, "Lỗi khi lấy thống kê theo tháng: " + ex.getMessage());
            return null;
        }
    }
}
