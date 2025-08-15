// Controller xử lý logic export dữ liệu
package ui.Controller;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.GiaoDich;
import ui.Panels.StatisticsPanel;
import ui.Utils.UIUtils;
import usecase.ExportTransactionUseCase;

public class ExportController {
    
    private StatisticsPanel statisticsPanel;
    private TransactionController transactionController;
    private ExportTransactionUseCase exportTransactionUseCase;
    
    public ExportController(StatisticsPanel statisticsPanel, 
                           TransactionController transactionController,
                           StatisticsController statisticsController) {
        this.statisticsPanel = statisticsPanel;
        this.transactionController = transactionController;
        
        // Initialize Use Case
        this.exportTransactionUseCase = new ExportTransactionUseCase(
            transactionController.getQuanLyGiaoDich());
        
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
     * Sử dụng ExportTransactionUseCase theo Clean Architecture
     */
    private void selectFileAndExport(int exportType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file");
        
        // Thiết lập filter cho các loại file
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV files (*.csv)", "csv");
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.setFileFilter(csvFilter); // Default to CSV
        
        // Đặt tên file mặc định
        String defaultFileName = "giao_dich_" + UIUtils.formatDate(LocalDate.now());
        fileChooser.setSelectedFile(new File(defaultFileName + ".csv"));
        
        int result = fileChooser.showSaveDialog(statisticsPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Ensure file has proper extension
            String fileName = selectedFile.getName();
            if (!fileName.toLowerCase().endsWith(".csv") && !fileName.toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getParent(), fileName + ".csv");
            }
            
            try {
                // Get transactions to export based on type
                List<GiaoDich> transactionsToExport = getDataForExport(exportType);
                
                // Delegate to Export Use Case
                exportTransactionUseCase.execute(selectedFile, exportType, transactionsToExport);
                
                UIUtils.showSuccess(statisticsPanel, 
                    "Xuất file thành công!\nĐường dẫn: " + selectedFile.getAbsolutePath());
                    
            } catch (Exception e) {
                UIUtils.showError(statisticsPanel, "Lỗi: " + e.getMessage());
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
}
