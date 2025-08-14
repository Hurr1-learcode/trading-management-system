// Panel bảng hiển thị danh sách giao dịch với nút CRUD
package ui.Panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;
import ui.Utils.UIUtils;

public class TransactionTablePanel extends JPanel {
    
    private JTable tblGiaoDich;
    private DefaultTableModel tableModel;
    private CrudActionListener crudActionListener;
    
    // Interface để xử lý sự kiện CRUD
    public interface CrudActionListener {
        void onEditTransaction(String maGiaoDich);
        void onDeleteTransaction(String maGiaoDich);
    }
    
    public TransactionTablePanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Khởi tạo các component
     */
    private void initializeComponents() {
        // Tạo model cho bảng với các cột 
        String[] columns = {"Mã GD", "Ngày", "Đơn giá", "Số lượng", "Chi tiết", "Thành tiền", "Thao tác"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Chỉ cho phép click vào cột thao tác
            }
        };
        
        // Tạo bảng
        tblGiaoDich = new JTable(tableModel);
        tblGiaoDich.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Thiết lập kích thước các cột
        tblGiaoDich.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mã GD
        tblGiaoDich.getColumnModel().getColumn(1).setPreferredWidth(100); // Ngày
        tblGiaoDich.getColumnModel().getColumn(2).setPreferredWidth(120); // Đơn giá
        tblGiaoDich.getColumnModel().getColumn(3).setPreferredWidth(80);  // Số lượng
        tblGiaoDich.getColumnModel().getColumn(4).setPreferredWidth(200); // Chi tiết
        tblGiaoDich.getColumnModel().getColumn(5).setPreferredWidth(120); // Thành tiền
        tblGiaoDich.getColumnModel().getColumn(6).setPreferredWidth(140); // Thao tác
        
        // Thiết lập chiều cao dòng để chứa được các nút
        tblGiaoDich.setRowHeight(30);
        
        // Thiết lập custom renderer cho cột thao tác
        tblGiaoDich.getColumnModel().getColumn(6).setCellRenderer(new ActionButtonRenderer());
        tblGiaoDich.getColumnModel().getColumn(6).setCellEditor(new ActionButtonEditor());
    }
    
    /**
     * Thiết lập layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Tạo scroll pane cho bảng
        JScrollPane scrollPane = new JScrollPane(tblGiaoDich);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Thêm nút "Thêm mới" ở phía trên
        JPanel topPanel = new JPanel();
        JButton btnThemMoi = new JButton("+ Thêm giao dịch mới");
        btnThemMoi.addActionListener(e -> {
            if (addActionListener != null) {
                addActionListener.onAddTransaction();
            }
        });
        topPanel.add(btnThemMoi);
        add(topPanel, BorderLayout.NORTH);
    }
    
    /**
     * Thiết lập event handlers
     */
    private void setupEventHandlers() {
        // Không cần listener cho selection vì đã có nút CRUD
    }
    
    /**
     * Load danh sách giao dịch vào bảng
     */
    public void loadTransactions(List<GiaoDich> giaoDichs) {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);
        
        if (giaoDichs == null || giaoDichs.isEmpty()) {
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Thêm từng giao dịch vào bảng
        for (GiaoDich gd : giaoDichs) {
            Object[] row = new Object[7];
            
            row[0] = gd.getMaGiaoDich();
            row[1] = gd.getNgayGiaoDich().format(formatter);
            row[2] = UIUtils.formatCurrency(gd.getDonGia());
            row[3] = gd.getSoLuong();
            row[4] = getTransactionDetail(gd);
            row[5] = UIUtils.formatCurrency(gd.tinhThanhTien());
            row[6] = gd.getMaGiaoDich(); // Lưu mã giao dịch để sử dụng trong button actions
            
            tableModel.addRow(row);
        }
    }
    
    /**
     * Lấy chi tiết giao dịch để hiển thị
     */
    private String getTransactionDetail(GiaoDich gd) {
        if (gd instanceof GiaoDichVang) {
            return "Vàng: " + ((GiaoDichVang) gd).getLoaiVang();
        } else if (gd instanceof GiaoDichTienTe) {
            GiaoDichTienTe gdtt = (GiaoDichTienTe) gd;
            if ("VND".equals(gdtt.getLoaiTien())) {
                return "Tiền tệ: " + gdtt.getLoaiTien();
            } else {
                return "Tiền tệ: " + gdtt.getLoaiTien() + " (Tỉ giá: " + UIUtils.formatCurrency(gdtt.getTiGia()) + ")";
            }
        }
        return "";
    }
    
    /**
     * Renderer cho cột thao tác (hiển thị 2 nút Sửa và Xóa)
     */
    private class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEdit;
        private JButton btnDelete;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            btnEdit = new JButton("Sửa");
            btnDelete = new JButton("Xóa");
            
            btnEdit.setPreferredSize(new Dimension(60, 25));
            btnDelete.setPreferredSize(new Dimension(60, 25));
            
            add(btnEdit);
            add(btnDelete);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            
            return this;
        }
    }
    
    /**
     * Editor cho cột thao tác (xử lý click vào nút)
     */
    private class ActionButtonEditor extends javax.swing.DefaultCellEditor {
        private JPanel panel;
        private JButton btnEdit;
        private JButton btnDelete;
        private String currentMaGiaoDich;
        
        public ActionButtonEditor() {
            super(new javax.swing.JCheckBox());
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            btnEdit = new JButton("Sửa");
            btnDelete = new JButton("Xóa");
            
            btnEdit.setPreferredSize(new Dimension(60, 25));
            btnDelete.setPreferredSize(new Dimension(60, 25));
            
            // Action listeners
            btnEdit.addActionListener(e -> {
                if (crudActionListener != null && currentMaGiaoDich != null) {
                    crudActionListener.onEditTransaction(currentMaGiaoDich);
                }
                fireEditingStopped();
            });
            
            btnDelete.addActionListener(e -> {
                if (crudActionListener != null && currentMaGiaoDich != null) {
                    crudActionListener.onDeleteTransaction(currentMaGiaoDich);
                }
                fireEditingStopped();
            });
            
            panel.add(btnEdit);
            panel.add(btnDelete);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentMaGiaoDich = (String) value;
            
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return currentMaGiaoDich;
        }
    }
    
    /**
     * Interface để xử lý sự kiện thêm giao dịch
     */
    public interface AddActionListener {
        void onAddTransaction();
    }
    
    private AddActionListener addActionListener;
    
    /**
     * Đặt listener cho sự kiện CRUD
     */
    public void setCrudActionListener(CrudActionListener listener) {
        this.crudActionListener = listener;
    }
    
    /**
     * Đặt listener cho sự kiện thêm
     */
    public void setAddActionListener(AddActionListener listener) {
        this.addActionListener = listener;
    }
    
    /**
     * Lấy số lượng giao dịch trong bảng
     */
    public int getTransactionCount() {
        return tableModel.getRowCount();
    }
    
    /**
     * Refresh bảng
     */
    public void refresh(List<GiaoDich> giaoDichs) {
        loadTransactions(giaoDichs);
    }
    
    /**
     * Lấy tham chiếu đến table (để export)
     */
    public JTable getTable() {
        return tblGiaoDich;
    }
    
    /**
     * Lấy tham chiếu đến table model (để export)
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
