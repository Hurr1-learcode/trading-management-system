// Controller xử lý logic export dữ liệu
package ui.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import dto.ThongKeDTO;
import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;
import ui.Panels.StatisticsPanel;
import ui.Utils.UIUtils;

public class ExportController {
    
    private StatisticsPanel statisticsPanel;
    private TransactionController transactionController;
    private StatisticsController statisticsController;
    
    public ExportController(StatisticsPanel statisticsPanel, 
                           TransactionController transactionController,
                           StatisticsController statisticsController) {
        this.statisticsPanel = statisticsPanel;
        this.transactionController = transactionController;
        this.statisticsController = statisticsController;
        
        setupEventHandlers();
    }
    
    /**
     * Thiết lập event handlers
     */
    private void setupEventHandlers() {
        statisticsPanel.getBtnInDanhSach().addActionListener(e -> showExportDialog());
    }
    
    /**
     * Hiển thị dialog chọn loại export
     */
    private void showExportDialog() {
        String[] options = {
            "Tất cả giao dịch", 
            "Giao dịch hiển thị trong bảng", 
            "Giao dịch + Thống kê tổng hợp"
        };
        
        int choice = JOptionPane.showOptionDialog(
            statisticsPanel,
            "Chọn nội dung muốn xuất:",
            "In danh sách giao dịch",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice != JOptionPane.CLOSED_OPTION) {
            selectFileAndExport(choice);
        }
    }
    
    /**
     * Chọn file và thực hiện export
     */
    private void selectFileAndExport(int exportType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file");
        
        // Thêm các file filter
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
        
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.setFileFilter(txtFilter);
        
        // Đặt tên file mặc định với ngày hiện tại
        String defaultName = "DanhSachGiaoDich_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        fileChooser.setSelectedFile(new File(defaultName + ".txt"));
        
        int result = fileChooser.showSaveDialog(statisticsPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                exportData(selectedFile, exportType);
                UIUtils.showSuccess(statisticsPanel, 
                    "Xuất file thành công!\nĐường dẫn: " + selectedFile.getAbsolutePath());
            } catch (Exception e) {
                UIUtils.showError(statisticsPanel, "Lỗi khi xuất file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Thực hiện export dữ liệu với UTF-8 encoding
     */
    private void exportData(File file, int exportType) throws IOException {
        String fileName = file.getName().toLowerCase();
        
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            
            // Thêm BOM cho UTF-8 để Excel đọc đúng tiếng Việt
            if (fileName.endsWith(".csv")) {
                writer.write('\ufeff'); // UTF-8 BOM
            }
            
            // Viết header
            writer.write("=".repeat(80) + "\n");
            writer.write("           DANH SÁCH GIAO DỊCH - " + 
                UIUtils.formatDate(LocalDate.now()) + "\n");
            writer.write("=".repeat(80) + "\n\n");
            
            // Lấy dữ liệu để export
            List<GiaoDich> dataToExport = getDataForExport(exportType);
            
            // Export theo định dạng file
            if (fileName.endsWith(".csv")) {
                exportToCSV(writer, dataToExport, exportType);
            } else {
                exportToText(writer, dataToExport, exportType);
            }
            
            // Thêm thống kê nếu được yêu cầu
            if (exportType == 2) {
                writer.write("\n" + "=".repeat(80) + "\n");
                writer.write("                    THỐNG KÊ TỔNG HỢP\n");
                writer.write("=".repeat(80) + "\n");
                addStatistics(writer, fileName.endsWith(".csv"));
            }
        }
    }
    
    /**
     * Lấy dữ liệu để export theo loại
     */
    private List<GiaoDich> getDataForExport(int exportType) {
        switch (exportType) {
            case 0: // Tất cả giao dịch
                return transactionController.getAllTransactions();
            case 1: // Giao dịch hiển thị trong bảng
            case 2: // Giao dịch + thống kê
                // Hiện tại trả về tất cả, có thể mở rộng để lấy từ filter
                return transactionController.getAllTransactions();
            default:
                return transactionController.getAllTransactions();
        }
    }
    
    /**
     * Export dữ liệu dạng CSV với UTF-8 và BOM
     */
    private void exportToCSV(OutputStreamWriter writer, List<GiaoDich> transactions, int exportType) 
            throws IOException {
        // CSV Header với tiếng Việt
        writer.write("Mã GD,Ngày GD,Đơn giá,Số lượng,Loại GD,Chi tiết,Thành tiền\n");
        
        for (GiaoDich gd : transactions) {
            writer.write(String.format("%s,%s,%s,%d,%s,\"%s\",%s\n",
                gd.getMaGiaoDich(),
                UIUtils.formatDate(gd.getNgayGiaoDich()),
                gd.getDonGia(),
                gd.getSoLuong(),
                gd.getLoaiGiaoDich(),
                getTransactionDetail(gd), // Wrap trong quotes để tránh lỗi comma
                gd.tinhThanhTien()
            ));
        }
    }
    
    /**
     * Export dữ liệu dạng text có format với UTF-8
     */
    private void exportToText(OutputStreamWriter writer, List<GiaoDich> transactions, int exportType) 
            throws IOException {
        String format = "%-12s %-12s %-15s %-8s %-8s %-20s %-15s\n";
        
        // Header bảng
        writer.write(String.format(format, 
            "Mã GD", "Ngày GD", "Đơn giá", "SL", "Loại", "Chi tiết", "Thành tiền"));
        writer.write("-".repeat(100) + "\n");
        
        for (GiaoDich gd : transactions) {
            writer.write(String.format(format,
                gd.getMaGiaoDich(),
                UIUtils.formatDate(gd.getNgayGiaoDich()),
                UIUtils.formatCurrency(gd.getDonGia()),
                gd.getSoLuong(),
                gd.getLoaiGiaoDich(),
                getTransactionDetail(gd),
                UIUtils.formatCurrency(gd.tinhThanhTien())
            ));
        }
        
        writer.write("-".repeat(100) + "\n");
        writer.write(String.format("Tổng số giao dịch: %d\n", transactions.size()));
    }
    
    /**
     * Lấy chi tiết giao dịch
     */
    private String getTransactionDetail(GiaoDich gd) {
        if (gd instanceof GiaoDichVang) {
            return ((GiaoDichVang) gd).getLoaiVang();
        } else if (gd instanceof GiaoDichTienTe) {
            GiaoDichTienTe gdtt = (GiaoDichTienTe) gd;
            if ("VND".equals(gdtt.getLoaiTien())) {
                return gdtt.getLoaiTien();
            } else {
                return gdtt.getLoaiTien() + " (TG: " + UIUtils.formatCurrency(gdtt.getTiGia()) + ")";
            }
        }
        return "";
    }
    
    /**
     * Thêm thống kê vào file export
     */
    private void addStatistics(OutputStreamWriter writer, boolean isCSV) throws IOException {
        try {
            ThongKeDTO thongKe = statisticsController.getGeneralStatistics();
            if (thongKe == null) {
                writer.write("Không có dữ liệu thống kê\n");
                return;
            }
            
            if (isCSV) {
                addStatisticsCSV(writer, thongKe);
            } else {
                addStatisticsText(writer, thongKe);
            }
        } catch (Exception e) {
            writer.write("Lỗi khi lấy thống kê: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Thêm thống kê dạng CSV
     */
    private void addStatisticsCSV(OutputStreamWriter writer, ThongKeDTO thongKe) throws IOException {
        writer.write("Loại thống kê,Giá trị\n");
        writer.write(String.format("Tổng giao dịch vàng,%d\n", thongKe.getTongSoLuongVang()));
        writer.write(String.format("Tổng giao dịch tiền tệ,%d\n", thongKe.getTongSoLuongTienTe()));
        writer.write(String.format("Tổng doanh thu vàng,%s\n", thongKe.getTongThanhTienVang()));
        writer.write(String.format("Tổng doanh thu tiền tệ,%s\n", thongKe.getTongThanhTienTienTe()));
        writer.write(String.format("Tổng doanh thu,%s\n", thongKe.getTongThanhTienTatCa()));
        writer.write(String.format("Trung bình thành tiền tiền tệ,%s\n", thongKe.getTrungBinhThanhTienTienTe()));
        writer.write(String.format("Giao dịch đơn giá > 1 tỷ,%d\n", thongKe.getSoGiaoDichDonGiaLonHon1Ty()));
    }
    
    /**
     * Thêm thống kê dạng text
     */
    private void addStatisticsText(OutputStreamWriter writer, ThongKeDTO thongKe) throws IOException {
        writer.write(String.format("📊 Tổng số giao dịch vàng: %,d\n", thongKe.getTongSoLuongVang()));
        writer.write(String.format("💱 Tổng số giao dịch tiền tệ: %,d\n", thongKe.getTongSoLuongTienTe()));
        writer.write(String.format("🥇 Tổng doanh thu vàng: %s VND\n", 
            UIUtils.formatCurrency(thongKe.getTongThanhTienVang())));
        writer.write(String.format("💵 Tổng doanh thu tiền tệ: %s VND\n", 
            UIUtils.formatCurrency(thongKe.getTongThanhTienTienTe())));
        writer.write(String.format("💎 Tổng doanh thu: %s VND\n", 
            UIUtils.formatCurrency(thongKe.getTongThanhTienTatCa())));
        writer.write(String.format("📈 Trung bình thành tiền tiền tệ: %s VND\n", 
            UIUtils.formatCurrency(thongKe.getTrungBinhThanhTienTienTe())));
        writer.write(String.format("💰 Số giao dịch đơn giá > 1 tỷ: %,d\n", 
            thongKe.getSoGiaoDichDonGiaLonHon1Ty()));
    }
}
