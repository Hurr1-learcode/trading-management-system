// MainFrame - Khởi tạo panel + controller, quản lý layout
package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import service.QuanLyGiaoDich;
import ui.Controller.ExportController;
import ui.Controller.StatisticsController;
import ui.Controller.TransactionController;
import ui.Panels.CurrencyTransactionFormPanel;
import ui.Panels.GoldTransactionFormPanel;
import ui.Panels.StatisticsPanel;
import ui.Panels.TransactionTabPanel;
import ui.Panels.TransactionTablePanel;

public class MainFrame extends JFrame {
    
    // Các panel chính
    private TransactionTabPanel tabPanel;
    private GoldTransactionFormPanel goldFormPanel;
    private CurrencyTransactionFormPanel currencyFormPanel;
    private TransactionTablePanel tablePanel;
    private StatisticsPanel statisticsPanel;
    
    // Các controller
    private TransactionController transactionController;
    private StatisticsController statisticsController;
    private ExportController exportController;
    
    // Service
    private QuanLyGiaoDich quanLyGiaoDich;
    
    public MainFrame() {
        // Khởi tạo service
        quanLyGiaoDich = new QuanLyGiaoDich();
        
        // Khởi tạo các panel
        initializePanels();
        
        // Khởi tạo các controller
        initializeControllers();
        
        // Thiết lập layout
        setupLayout();
        
        // Cấu hình frame
        configureFrame();
    }
    
    /**
     * Khởi tạo các panel
     */
    private void initializePanels() {
        // Tạo các panel
        tabPanel = new TransactionTabPanel();
        goldFormPanel = new GoldTransactionFormPanel();
        currencyFormPanel = new CurrencyTransactionFormPanel();
        tablePanel = new TransactionTablePanel();
        statisticsPanel = new StatisticsPanel();
    }
    
    /**
     * Khởi tạo các controller
     */
    private void initializeControllers() {
        // Tạo transaction controller (xử lý CRUD)
        transactionController = new TransactionController(tabPanel, goldFormPanel, currencyFormPanel, tablePanel);
        
        // Tạo statistics controller (xử lý thống kê)
        statisticsController = new StatisticsController(statisticsPanel, quanLyGiaoDich);
        
        // Tạo export controller (xử lý export)
        exportController = new ExportController(statisticsPanel, transactionController, statisticsController);
        
        // Kết nối transaction controller với statistics controller để refresh thống kê
        setupControllerInteraction();
    }
    
    /**
     * Thiết lập tương tác giữa các controller
     */
    private void setupControllerInteraction() {
        // Kết nối transaction controller với statistics controller
        // để refresh thống kê khi có thay đổi giao dịch
        transactionController.setStatisticsController(statisticsController);
    }
    
    /**
     * Thiết lập layout của frame
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Tạo tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Quản lý giao dịch
        JPanel transactionTab = createTransactionTab();
        tabbedPane.addTab("Quản lý Giao dịch", transactionTab);
        
        // Tab 2: Thống kê
        tabbedPane.addTab("Thống kê & Lọc", statisticsPanel);
        
        // Thêm tabbed pane vào frame
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Tạo tab quản lý giao dịch
     */
    private JPanel createTransactionTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Thêm tab panel ở phía trên
        panel.add(tabPanel, BorderLayout.NORTH);
        
        // Thêm table panel ở phía giữa
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Cấu hình frame
     */
    private void configureFrame() {
        setTitle("Hệ Thống Quản Lý Giao Dịch");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // Căn giữa màn hình
        setVisible(true);
    }
    
    /**
     * Refresh tất cả dữ liệu (gọi từ bên ngoài nếu cần)
     */
    public void refreshAllData() {
        transactionController.loadAllTransactions();
        statisticsController.refreshStatistics();
    }
    
    /**
     * Lấy các controller để sử dụng từ bên ngoài (nếu cần)
     */
    public TransactionController getTransactionController() {
        return transactionController;
    }
    
    public StatisticsController getStatisticsController() {
        return statisticsController;
    }
    
    public ExportController getExportController() {
        return exportController;
    }
    
    /**
     * Main method - điểm khởi đầu ứng dụng
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Đặt Look and Feel theo hệ điều hành
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Tạo và hiển thị frame chính
            new MainFrame();
        });
    }
}
