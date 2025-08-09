// Panel bảng hiển thị danh sách giao dịch
package ui.Panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;
import ui.Utils.UIUtils;

public class TransactionTablePanel extends JPanel {
    
    private JTable tblGiaoDich;
    private DefaultTableModel tableModel;
    private TransactionSelectionListener selectionListener;
    
    // Interface để xử lý sự kiện chọn giao dịch
    public interface TransactionSelectionListener {
        void onTransactionSelected(String maGiaoDich);
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
        String[] columns = {"Mã GD", "Ngày", "Đơn giá", "Số lượng", "Loại", "Chi tiết", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
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
        tblGiaoDich.getColumnModel().getColumn(4).setPreferredWidth(80);  // Loại
        tblGiaoDich.getColumnModel().getColumn(5).setPreferredWidth(150); // Chi tiết
        tblGiaoDich.getColumnModel().getColumn(6).setPreferredWidth(120); // Thành tiền
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
    }
    
    /**
     * Thiết lập event handlers
     */
    private void setupEventHandlers() {
        // Listener cho việc chọn hàng trong bảng
        tblGiaoDich.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && selectionListener != null) {
                int selectedRow = tblGiaoDich.getSelectedRow();
                if (selectedRow >= 0) {
                    String maGiaoDich = (String) tableModel.getValueAt(selectedRow, 0);
                    selectionListener.onTransactionSelected(maGiaoDich);
                }
            }
        });
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
            row[4] = gd.getLoaiGiaoDich();
            row[5] = getTransactionDetail(gd);
            row[6] = UIUtils.formatCurrency(gd.tinhThanhTien());
            
            tableModel.addRow(row);
        }
    }
    
    /**
     * Lấy chi tiết giao dịch để hiển thị
     */
    private String getTransactionDetail(GiaoDich gd) {
        if (gd instanceof GiaoDichVang) {
            return ((GiaoDichVang) gd).getLoaiVang();
        } else if (gd instanceof GiaoDichTienTe) {
            GiaoDichTienTe gdtt = (GiaoDichTienTe) gd;
            if ("VND".equals(gdtt.getLoaiTien())) {
                return gdtt.getLoaiTien();
            } else {
                return gdtt.getLoaiTien() + " (Tỉ giá: " + UIUtils.formatCurrency(gdtt.getTiGia()) + ")";
            }
        }
        return "";
    }
    
    /**
     * Lấy giao dịch được chọn hiện tại
     */
    public String getSelectedTransactionId() {
        int selectedRow = tblGiaoDich.getSelectedRow();
        if (selectedRow >= 0) {
            return (String) tableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }
    
    /**
     * Xóa selection hiện tại
     */
    public void clearSelection() {
        tblGiaoDich.clearSelection();
    }
    
    /**
     * Đặt listener cho sự kiện chọn giao dịch
     */
    public void setSelectionListener(TransactionSelectionListener listener) {
        this.selectionListener = listener;
    }
    
    /**
     * Lấy số lượng giao dịch trong bảng
     */
    public int getTransactionCount() {
        return tableModel.getRowCount();
    }
    
    /**
     * Refresh bảng (giữ nguyên selection nếu có)
     */
    public void refresh(List<GiaoDich> giaoDichs) {
        String selectedId = getSelectedTransactionId();
        loadTransactions(giaoDichs);
        
        // Cố gắng chọn lại giao dịch đã chọn trước đó
        if (selectedId != null) {
            selectTransactionById(selectedId);
        }
    }
    
    /**
     * Chọn giao dịch theo mã
     */
    private void selectTransactionById(String maGiaoDich) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (maGiaoDich.equals(tableModel.getValueAt(i, 0))) {
                tblGiaoDich.setRowSelectionInterval(i, i);
                break;
            }
        }
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
