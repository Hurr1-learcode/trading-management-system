// Panel quản lý tab giao dịch vàng và tiền tệ
package ui.Panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class TransactionTabPanel extends JPanel {
    
    // Toggle buttons cho loại giao dịch
    private JToggleButton btnGiaoDichVang;
    private JToggleButton btnGiaoDichTienTe;
    private ButtonGroup toggleGroup;
    
    // Callback interface để thông báo khi loại giao dịch thay đổi
    public interface TransactionTypeChangeListener {
        void onTransactionTypeChanged(String type);
    }
    
    private TransactionTypeChangeListener typeChangeListener;
    
    public TransactionTabPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Khởi tạo các component
     */
    private void initializeComponents() {
        // Khởi tạo toggle buttons
        btnGiaoDichVang = new JToggleButton("Giao dịch vàng");
        btnGiaoDichTienTe = new JToggleButton("Giao dịch tiền tệ");
        
        // Tạo button group cho toggle buttons
        toggleGroup = new ButtonGroup();
        toggleGroup.add(btnGiaoDichVang);
        toggleGroup.add(btnGiaoDichTienTe);
        
        // Chọn mặc định giao dịch vàng
        btnGiaoDichVang.setSelected(true);
    }
    
    /**
     * Thiết lập layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Loại giao dịch"));
        
        // Tạo panel cho toggle buttons
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        togglePanel.add(btnGiaoDichVang);
        togglePanel.add(btnGiaoDichTienTe);
        
        add(togglePanel, BorderLayout.CENTER);
    }
    
    /**
     * Thiết lập các event handler
     */
    private void setupEventHandlers() {
        // Listener cho toggle buttons
        btnGiaoDichVang.addActionListener(e -> {
            if (typeChangeListener != null) {
                typeChangeListener.onTransactionTypeChanged("VANG");
            }
        });
        
        btnGiaoDichTienTe.addActionListener(e -> {
            if (typeChangeListener != null) {
                typeChangeListener.onTransactionTypeChanged("TIEN_TE");
            }
        });
    }
    
    /**
     * Lấy loại giao dịch hiện tại được chọn
     */
    public String getSelectedTransactionType() {
        return btnGiaoDichVang.isSelected() ? "VANG" : "TIEN_TE";
    }
    
    /**
     * Đặt loại giao dịch được chọn
     */
    public void setSelectedTransactionType(String type) {
        if ("VANG".equals(type)) {
            btnGiaoDichVang.setSelected(true);
        } else {
            btnGiaoDichTienTe.setSelected(true);
        }
    }
    
    // Setters cho listeners
    public void setTypeChangeListener(TransactionTypeChangeListener listener) {
        this.typeChangeListener = listener;
    }
}
