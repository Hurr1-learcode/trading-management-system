package usecase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import dto.ThongKeDTO;
import exception.BusinessException;
import exception.ValidationException;
import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;
import service.QuanLyGiaoDich;
import ui.Utils.UIUtils;

/**
 * Use Case: Export Transaction List
 * Theo Clean Architecture pattern
 */
public class ExportTransactionUseCase {
    
    private final QuanLyGiaoDich quanLyGiaoDich;
    
    public ExportTransactionUseCase(QuanLyGiaoDich quanLyGiaoDich) {
        this.quanLyGiaoDich = quanLyGiaoDich;
    }
    
    /**
     * Execute export transaction use case
     * @param file File to export to
     * @param exportType Type of export (0: All, 1: Display, 2: With Statistics)
     * @param transactionsToExport List of transactions to export (can be null for all)
     * @throws ValidationException Nếu validation fails
     * @throws BusinessException Nếu business rules violation
     */
    public void execute(File file, int exportType, List<GiaoDich> transactionsToExport) 
            throws ValidationException, BusinessException {
        
        // Step 1: Validate input parameters
        validateInputParameters(file, exportType);
        
        // Step 2: Validate business rules
        validateExportBusinessRules(file, exportType);
        
        // Step 3: Get data for export
        List<GiaoDich> dataToExport = getDataForExport(exportType, transactionsToExport);
        
        // Step 4: Execute export operation
        executeExport(file, exportType, dataToExport);
    }
    
    /**
     * Validate input parameters
     */
    private void validateInputParameters(File file, int exportType) throws ValidationException {
        if (file == null) {
            throw new ValidationException("File không được null", "NULL_FILE");
        }
        
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            throw new ValidationException("Thư mục đích không tồn tại", "DIRECTORY_NOT_EXISTS");
        }
        
        if (exportType < 0 || exportType > 2) {
            throw new ValidationException("Loại export không hợp lệ (0-2)", "INVALID_EXPORT_TYPE");
        }
        
        // Validate file extension
        String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".csv") && !fileName.endsWith(".txt")) {
            throw new ValidationException("Chỉ hỗ trợ file .csv và .txt", "UNSUPPORTED_FILE_FORMAT");
        }
        
        // Validate file name
        if (fileName.length() < 5) { // minimum: x.csv or x.txt
            throw new ValidationException("Tên file quá ngắn", "FILENAME_TOO_SHORT");
        }
    }
    
    /**
     * Validate business rules for export operation
     */
    private void validateExportBusinessRules(File file, int exportType) throws BusinessException {
        try {
            // Business rule: Check if file already exists and can be overwritten
            if (file.exists() && !file.canWrite()) {
                throw new BusinessException("File đã tồn tại và không thể ghi đè", "FILE_NOT_WRITABLE");
            }
            
            // Business rule: Check available disk space (basic check)
            long freeSpace = file.getParentFile().getFreeSpace();
            if (freeSpace < 1024 * 1024) { // Less than 1MB
                throw new BusinessException("Không đủ dung lượng ổ đĩa", "INSUFFICIENT_DISK_SPACE");
            }
            
            // Business rule: Validate data availability for export type
            if (exportType == 2) { // Export with statistics
                try {
                    ThongKeDTO stats = quanLyGiaoDich.getTongSoLuongTheoLoai();
                    if (stats == null) {
                        throw new BusinessException("Không có dữ liệu thống kê để export", "NO_STATISTICS_DATA");
                    }
                } catch (Exception ex) {
                    throw new BusinessException("Lỗi khi lấy dữ liệu thống kê: " + ex.getMessage(), ex);
                }
            }
            
        } catch (Exception ex) {
            if (ex instanceof BusinessException) {
                throw ex;
            }
            throw new BusinessException("Lỗi khi kiểm tra business rules: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Get data for export based on export type
     */
    private List<GiaoDich> getDataForExport(int exportType, List<GiaoDich> providedTransactions) 
            throws BusinessException {
        try {
            switch (exportType) {
                case 0: // All transactions
                case 2: // All transactions with statistics
                    return quanLyGiaoDich.getAll();
                    
                case 1: // Provided transactions (displayed in table)
                    if (providedTransactions != null && !providedTransactions.isEmpty()) {
                        return providedTransactions;
                    } else {
                        // Fallback to all transactions if no specific list provided
                        return quanLyGiaoDich.getAll();
                    }
                    
                default:
                    return quanLyGiaoDich.getAll();
            }
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi lấy dữ liệu export: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Execute the actual export operation
     */
    private void executeExport(File file, int exportType, List<GiaoDich> dataToExport) 
            throws BusinessException {
        try {
            String fileName = file.getName().toLowerCase();
            
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8)) {
                
                // Add BOM for UTF-8 to help Excel read Vietnamese correctly
                if (fileName.endsWith(".csv")) {
                    writer.write('\ufeff'); // UTF-8 BOM
                }
                
                // Write header
                writeHeader(writer);
                
                // Export data based on file format
                if (fileName.endsWith(".csv")) {
                    exportToCSV(writer, dataToExport);
                } else {
                    exportToText(writer, dataToExport);
                }
                
                // Add statistics if requested
                if (exportType == 2) {
                    writeStatisticsSection(writer, fileName.endsWith(".csv"));
                }
            }
            
        } catch (IOException ex) {
            throw new BusinessException("Lỗi khi ghi file: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new BusinessException("Lỗi hệ thống khi export: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Write file header
     */
    private void writeHeader(OutputStreamWriter writer) throws IOException {
        writer.write("=".repeat(80) + "\n");
        writer.write("           DANH SÁCH GIAO DỊCH - " + 
            UIUtils.formatDate(LocalDate.now()) + "\n");
        writer.write("=".repeat(80) + "\n\n");
    }
    
    /**
     * Export data to CSV format
     */
    private void exportToCSV(OutputStreamWriter writer, List<GiaoDich> transactions) throws IOException {
        // CSV Header
        writer.write("Mã GD,Ngày GD,Đơn giá,Số lượng,Loại GD,Chi tiết,Thành tiền\n");
        
        for (GiaoDich gd : transactions) {
            writer.write(String.format("%s,%s,%s,%d,%s,\"%s\",%s\n",
                gd.getMaGiaoDich(),
                UIUtils.formatDate(gd.getNgayGiaoDich()),
                gd.getDonGia(),
                gd.getSoLuong(),
                gd.getLoaiGiaoDich(),
                getTransactionDetail(gd),
                gd.tinhThanhTien()
            ));
        }
    }
    
    /**
     * Export data to text format
     */
    private void exportToText(OutputStreamWriter writer, List<GiaoDich> transactions) throws IOException {
        String format = "%-12s %-12s %-15s %-8s %-8s %-20s %-15s\n";
        
        // Header
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
     * Get transaction detail based on type
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
     * Write statistics section
     */
    private void writeStatisticsSection(OutputStreamWriter writer, boolean isCSV) throws IOException {
        writer.write("\n" + "=".repeat(80) + "\n");
        writer.write("                    THỐNG KÊ TỔNG HỢP\n");
        writer.write("=".repeat(80) + "\n");
        
        try {
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoai();
            if (thongKe != null) {
                if (isCSV) {
                    writeStatisticsCSV(writer, thongKe);
                } else {
                    writeStatisticsText(writer, thongKe);
                }
            } else {
                writer.write("Không có dữ liệu thống kê\n");
            }
        } catch (Exception e) {
            writer.write("Lỗi khi lấy thống kê: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Write statistics in CSV format
     */
    private void writeStatisticsCSV(OutputStreamWriter writer, ThongKeDTO thongKe) throws IOException {
        writer.write("Loại thống kê,Giá trị\n");
        writer.write(String.format("Tổng giao dịch vàng,%d\n", thongKe.getTongSoLuongVang()));
        writer.write(String.format("Tổng giao dịch tiền tệ,%d\n", thongKe.getTongSoLuongTienTe()));
        writer.write(String.format("Tổng doanh thu vàng,%s\n", thongKe.getTongThanhTienVang()));
        writer.write(String.format("Tổng doanh thu tiền tệ,%s\n", thongKe.getTongThanhTienTienTe()));
        writer.write(String.format("Tổng doanh thu,%s\n", thongKe.getTongThanhTienTatCa()));
        writer.write(String.format("Giao dịch đơn giá > 1 tỷ,%d\n", thongKe.getSoGiaoDichDonGiaLonHon1Ty()));
    }
    
    /**
     * Write statistics in text format
     */
    private void writeStatisticsText(OutputStreamWriter writer, ThongKeDTO thongKe) throws IOException {
        writer.write(String.format("📊 Tổng số giao dịch vàng: %,d\n", thongKe.getTongSoLuongVang()));
        writer.write(String.format("💱 Tổng số giao dịch tiền tệ: %,d\n", thongKe.getTongSoLuongTienTe()));
        writer.write(String.format("🥇 Tổng doanh thu vàng: %s VND\n", 
            UIUtils.formatCurrency(thongKe.getTongThanhTienVang())));
        writer.write(String.format("💵 Tổng doanh thu tiền tệ: %s VND\n", 
            UIUtils.formatCurrency(thongKe.getTongThanhTienTienTe())));
        writer.write(String.format("💎 Tổng doanh thu: %s VND\n", 
            UIUtils.formatCurrency(thongKe.getTongThanhTienTatCa())));
        writer.write(String.format("💰 Số giao dịch đơn giá > 1 tỷ: %,d\n", 
            thongKe.getSoGiaoDichDonGiaLonHon1Ty()));
    }
    
    /**
     * Kiểm tra xem có thể export không
     */
    public boolean canExport(File file, int exportType) {
        try {
            validateInputParameters(file, exportType);
            validateExportBusinessRules(file, exportType);
            return true;
        } catch (ValidationException | BusinessException e) {
            return false;
        }
    }
    
    /**
     * Lấy thông tin lỗi validation nếu có
     */
    public String getValidationError(File file, int exportType) {
        try {
            validateInputParameters(file, exportType);
            validateExportBusinessRules(file, exportType);
            return null;
        } catch (ValidationException | BusinessException e) {
            return e.getMessage();
        }
    }
    
    /**
     * Estimate file size (for preview)
     */
    public long estimateFileSize(int exportType, List<GiaoDich> transactions) {
        try {
            List<GiaoDich> dataToExport = getDataForExport(exportType, transactions);
            // Rough estimate: 100 bytes per transaction + headers + statistics
            long baseSize = dataToExport.size() * 100 + 1000;
            if (exportType == 2) {
                baseSize += 2000; // Additional for statistics
            }
            return baseSize;
        } catch (Exception ex) {
            return 10000; // Default estimate
        }
    }
}
