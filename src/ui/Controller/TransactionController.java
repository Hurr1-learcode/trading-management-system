// Controller xử lý các thao tác CRUD cho giao dịch với thiết kế module
package ui.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import dto.GiaoDichFormDTO;
import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;
import service.QuanLyGiaoDich;
import ui.Panels.CurrencyTransactionFormPanel;
import ui.Panels.GoldTransactionFormPanel;
import ui.Panels.TransactionTabPanel;
import ui.Panels.TransactionTablePanel;
import ui.Utils.UIUtils;

public class TransactionController {
    
    private QuanLyGiaoDich quanLyGiaoDich;
    private TransactionTabPanel tabPanel;
    private GoldTransactionFormPanel goldFormPanel;
    private CurrencyTransactionFormPanel currencyFormPanel;
    private TransactionTablePanel tablePanel;
    private StatisticsController statisticsController; // Để refresh stats khi cần
    
    // Danh sách giao dịch hiện tại (để filter theo loại)
    private List<GiaoDich> allTransactions;
    private String currentFilterType = "VANG"; // Mặc định hiển thị giao dịch vàng
    
    public TransactionController(TransactionTabPanel tabPanel, 
                               GoldTransactionFormPanel goldFormPanel,
                               CurrencyTransactionFormPanel currencyFormPanel,
                               TransactionTablePanel tablePanel,
                               QuanLyGiaoDich quanLyGiaoDich) {
        this.tabPanel = tabPanel;
        this.goldFormPanel = goldFormPanel;
        this.currencyFormPanel = currencyFormPanel;
        this.tablePanel = tablePanel;
        this.quanLyGiaoDich = quanLyGiaoDich; // Inject service từ MainFrame
        
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
        // Xử lý sự kiện thay đổi loại giao dịch trong tab
        tabPanel.setTypeChangeListener(this::handleTransactionTypeChange);
        
        // Xử lý sự kiện save form giao dịch vàng
        goldFormPanel.setFormSaveListener(new GoldTransactionFormPanel.FormSaveListener() {
            @Override
            public void onAddGoldTransaction(GiaoDichFormDTO dto) {
                handleAddTransaction(dto);
            }
            
            @Override
            public void onEditGoldTransaction(GiaoDichFormDTO dto) {
                handleEditTransaction(dto, goldFormPanel.getSelectedMaGiaoDich());
            }
        });
        
        // Xử lý sự kiện save form giao dịch tiền tệ
        currencyFormPanel.setFormSaveListener(new CurrencyTransactionFormPanel.FormSaveListener() {
            @Override
            public void onAddCurrencyTransaction(GiaoDichFormDTO dto) {
                handleAddTransaction(dto);
            }
            
            @Override
            public void onEditCurrencyTransaction(GiaoDichFormDTO dto) {
                handleEditTransaction(dto, currencyFormPanel.getSelectedMaGiaoDich());
            }
        });
        
        // Xử lý sự kiện CRUD trong bảng
        tablePanel.setCrudActionListener(new TransactionTablePanel.CrudActionListener() {
            @Override
            public void onEditTransaction(String maGiaoDich) {
                handleEditButtonClick(maGiaoDich);
            }
            
            @Override
            public void onDeleteTransaction(String maGiaoDich) {
                handleDeleteButtonClick(maGiaoDich);
            }
        });
        
        // Xử lý sự kiện nút thêm trong bảng
        tablePanel.setAddActionListener(this::handleAddButtonClick);
    }
    
    /**
     * Xử lý thay đổi loại giao dịch
     */
    private void handleTransactionTypeChange(String type) {
        currentFilterType = type;
        filterAndDisplayTransactions();
    }
    
    /**
     * Xử lý nút thêm giao dịch
     */
    private void handleAddButtonClick() {
        String currentType = tabPanel.getSelectedTransactionType();
        
        if ("VANG".equals(currentType)) {
            goldFormPanel.showAddForm();
        } else {
            currencyFormPanel.showAddForm();
        }
    }
    
    /**
     * Filter và hiển thị giao dịch theo loại
     */
    private void filterAndDisplayTransactions() {
        if (allTransactions == null) return;
        
        List<GiaoDich> filteredTransactions;
        
        if ("VANG".equals(currentFilterType)) {
            filteredTransactions = allTransactions.stream()
                    .filter(gd -> gd instanceof GiaoDichVang)
                    .collect(Collectors.toList());
        } else {
            filteredTransactions = allTransactions.stream()
                    .filter(gd -> gd instanceof GiaoDichTienTe)
                    .collect(Collectors.toList());
        }
        
        tablePanel.loadTransactions(filteredTransactions);
    }
    
    /**
     * Xử lý thêm giao dịch mới
     */
    private void handleAddTransaction(GiaoDichFormDTO dto) {
        try {
            // Thêm giao dịch
            quanLyGiaoDich.add(dto);
            
            // Hiển thị thông báo thành công
            UIUtils.showSuccess(tablePanel, "Thêm giao dịch thành công!");
            
            // Làm mới dữ liệu
            loadAllTransactions();
            
            // Refresh thống kê nếu có
            if (statisticsController != null) {
                statisticsController.refreshStatistics();
            }
            
        } catch (Exception ex) {
            UIUtils.showError(tablePanel, "Lỗi: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý sửa giao dịch
     */
    private void handleEditTransaction(GiaoDichFormDTO dto, String selectedMaGiaoDich) {
        if (selectedMaGiaoDich == null) {
            UIUtils.showWarning(tablePanel, "Lỗi: Không xác định được giao dịch cần sửa!");
            return;
        }
        
        try {
            // Cập nhật giao dịch
            quanLyGiaoDich.edit(selectedMaGiaoDich, dto);
            
            // Hiển thị thông báo thành công
            UIUtils.showSuccess(tablePanel, "Cập nhật giao dịch thành công!");
            
            // Làm mới dữ liệu
            loadAllTransactions();
            
            // Refresh thống kê nếu có
            if (statisticsController != null) {
                statisticsController.refreshStatistics();
            }
            
        } catch (Exception ex) {
            UIUtils.showError(tablePanel, "Lỗi: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý click nút sửa trong bảng
     */
    private void handleEditButtonClick(String maGiaoDich) {
        try {
            Optional<GiaoDich> optional = quanLyGiaoDich.findById(maGiaoDich);
            if (optional.isPresent()) {
                GiaoDich giaoDich = optional.get();
                
                // Hiển thị form tương ứng với loại giao dịch
                if (giaoDich instanceof GiaoDichVang) {
                    goldFormPanel.showEditForm(giaoDich);
                } else if (giaoDich instanceof GiaoDichTienTe) {
                    currencyFormPanel.showEditForm(giaoDich);
                }
            } else {
                UIUtils.showError(tablePanel, "Không tìm thấy giao dịch với mã: " + maGiaoDich);
            }
        } catch (Exception ex) {
            UIUtils.showError(tablePanel, "Lỗi khi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý click nút xóa trong bảng
     */
    private void handleDeleteButtonClick(String maGiaoDich) {
        // Xác nhận xóa
        int result = JOptionPane.showConfirmDialog(
            tablePanel,
            "Bạn có chắc chắn muốn xóa giao dịch: " + maGiaoDich + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                // Xóa giao dịch
                quanLyGiaoDich.remove(maGiaoDich);
                
                // Hiển thị thông báo thành công
                UIUtils.showSuccess(tablePanel, "Xóa giao dịch thành công!");
                
                // Làm mới dữ liệu
                loadAllTransactions();
                
                // Refresh thống kê nếu có
                if (statisticsController != null) {
                    statisticsController.refreshStatistics();
                }
                
            } catch (Exception ex) {
                UIUtils.showError(tablePanel, "Lỗi: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Load tất cả giao dịch
     */
    public void loadAllTransactions() {
        try {
            allTransactions = quanLyGiaoDich.getAll();
            filterAndDisplayTransactions();
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
