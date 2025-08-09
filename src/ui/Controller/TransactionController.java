// Controller xử lý các thao tác CRUD cho giao dịch
package ui.Controller;

import java.util.List;
import java.util.Optional;

import dto.GiaoDichFormDTO;
import exception.ValidationException;
import model.GiaoDich;
import service.QuanLyGiaoDich;
import ui.Panels.TransactionFormPanel;
import ui.Panels.TransactionTablePanel;
import ui.Utils.UIUtils;

public class TransactionController {
    
    private QuanLyGiaoDich quanLyGiaoDich;
    private TransactionFormPanel formPanel;
    private TransactionTablePanel tablePanel;
    private StatisticsController statisticsController; // Để refresh stats khi cần
    
    public TransactionController(TransactionFormPanel formPanel, TransactionTablePanel tablePanel) {
        this.formPanel = formPanel;
        this.tablePanel = tablePanel;
        this.quanLyGiaoDich = new QuanLyGiaoDich();
        
        setupEventHandlers();
        loadAllTransactions();
    }
    
    /**
     * Đặt statistics controller để có thể refresh khi cần
     */
    public void setStatisticsController(StatisticsController statisticsController) {
        this.statisticsController = statisticsController;
    }
    
    /**
     * Thiết lập các event handler
     */
    private void setupEventHandlers() {
        // Xử lý sự kiện click nút Thêm
        formPanel.getBtnThem().addActionListener(e -> handleAdd());
        
        // Xử lý sự kiện click nút Sửa
        formPanel.getBtnSua().addActionListener(e -> handleEdit());
        
        // Xử lý sự kiện click nút Xóa
        formPanel.getBtnXoa().addActionListener(e -> handleDelete());
        
        // Xử lý sự kiện click nút Làm mới
        formPanel.getBtnLamMoi().addActionListener(e -> handleRefresh());
        
        // Xử lý sự kiện chọn giao dịch trong bảng
        tablePanel.setSelectionListener(this::handleTransactionSelection);
    }
    
    /**
     * Xử lý thêm giao dịch mới
     */
    private void handleAdd() {
        try {
            // Lấy dữ liệu từ form và validate
            GiaoDichFormDTO formDTO = formPanel.getFormData();
            
            // Thêm giao dịch
            quanLyGiaoDich.add(formDTO);
            
            // Hiển thị thông báo thành công
            UIUtils.showSuccess(formPanel, "Thêm giao dịch thành công!");
            
            // Làm mới form và bảng
            formPanel.clearForm();
            loadAllTransactions();
            
            // Refresh thống kê nếu có
            if (statisticsController != null) {
                statisticsController.refreshStatistics();
            }
            
        } catch (ValidationException ex) {
            UIUtils.showError(formPanel, "Lỗi validation: " + ex.getMessage());
        } catch (Exception ex) {
            UIUtils.showError(formPanel, "Lỗi: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý sửa giao dịch
     */
    private void handleEdit() {
        String selectedMaGiaoDich = formPanel.getSelectedMaGiaoDich();
        
        if (selectedMaGiaoDich == null) {
            UIUtils.showWarning(formPanel, "Vui lòng chọn giao dịch cần sửa!");
            return;
        }
        
        try {
            // Lấy dữ liệu từ form và validate
            GiaoDichFormDTO formDTO = formPanel.getFormData();
            
            // Cập nhật giao dịch
            quanLyGiaoDich.edit(selectedMaGiaoDich, formDTO);
            
            // Hiển thị thông báo thành công
            UIUtils.showSuccess(formPanel, "Cập nhật giao dịch thành công!");
            
            // Làm mới form và bảng
            formPanel.clearForm();
            loadAllTransactions();
            
            // Refresh thống kê nếu có
            if (statisticsController != null) {
                statisticsController.refreshStatistics();
            }
            
        } catch (ValidationException ex) {
            UIUtils.showError(formPanel, "Lỗi validation: " + ex.getMessage());
        } catch (Exception ex) {
            UIUtils.showError(formPanel, "Lỗi: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý xóa giao dịch
     */
    private void handleDelete() {
        String selectedMaGiaoDich = formPanel.getSelectedMaGiaoDich();
        
        if (selectedMaGiaoDich == null) {
            UIUtils.showWarning(formPanel, "Vui lòng chọn giao dịch cần xóa!");
            return;
        }
        
        // Xác nhận xóa
        boolean confirmed = UIUtils.showConfirm(
            formPanel, 
            "Bạn có chắc chắn muốn xóa giao dịch: " + selectedMaGiaoDich + "?",
            "Xác nhận xóa"
        );
        
        if (confirmed) {
            try {
                // Xóa giao dịch
                quanLyGiaoDich.remove(selectedMaGiaoDich);
                
                // Hiển thị thông báo thành công
                UIUtils.showSuccess(formPanel, "Xóa giao dịch thành công!");
                
                // Làm mới form và bảng
                formPanel.clearForm();
                loadAllTransactions();
                
                // Refresh thống kê nếu có
                if (statisticsController != null) {
                    statisticsController.refreshStatistics();
                }
                
            } catch (Exception ex) {
                UIUtils.showError(formPanel, "Lỗi: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Xử lý làm mới dữ liệu
     */
    private void handleRefresh() {
        formPanel.clearForm();
        loadAllTransactions();
        UIUtils.showSuccess(formPanel, "Dữ liệu đã được làm mới!");
    }
    
    /**
     * Xử lý khi chọn giao dịch trong bảng
     */
    private void handleTransactionSelection(String maGiaoDich) {
        try {
            Optional<GiaoDich> optional = quanLyGiaoDich.findById(maGiaoDich);
            if (optional.isPresent()) {
                // Load dữ liệu giao dịch vào form
                formPanel.loadTransactionData(optional.get());
            } else {
                UIUtils.showError(formPanel, "Không tìm thấy giao dịch với mã: " + maGiaoDich);
            }
        } catch (Exception ex) {
            UIUtils.showError(formPanel, "Lỗi khi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    /**
     * Load tất cả giao dịch vào bảng
     */
    public void loadAllTransactions() {
        try {
            List<GiaoDich> giaoDichs = quanLyGiaoDich.getAll();
            tablePanel.loadTransactions(giaoDichs);
        } catch (Exception ex) {
            UIUtils.showError(tablePanel, "Lỗi khi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    /**
     * Lấy tất cả giao dịch (để export)
     */
    public List<GiaoDich> getAllTransactions() {
        try {
            return quanLyGiaoDich.getAll();
        } catch (Exception ex) {
            UIUtils.showError(tablePanel, "Lỗi khi lấy dữ liệu: " + ex.getMessage());
            return List.of();
        }
    }
    
    /**
     * Tìm giao dịch theo mã
     */
    public Optional<GiaoDich> findTransactionById(String maGiaoDich) {
        try {
            return quanLyGiaoDich.findById(maGiaoDich);
        } catch (Exception ex) {
            UIUtils.showError(tablePanel, "Lỗi khi tìm giao dịch: " + ex.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Lấy service quản lý giao dịch
     */
    public QuanLyGiaoDich getQuanLyGiaoDich() {
        return quanLyGiaoDich;
    }
}
