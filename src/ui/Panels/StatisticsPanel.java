// Panel thống kê và bảng giao dịch đơn giá lớn
package ui.Panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dto.ThongKeDTO;
import model.GiaoDich;
import ui.Utils.UIUtils;

public class StatisticsPanel extends JPanel {
    
    // Components cho thống kê
    private JTextArea txtThongKe;
    private JButton btnCapNhatThongKe;
    private JButton btnThongKeHomNay;
    private JButton btnThongKeThangNay;
    private JButton btnThongKeTatCa;
    private JButton btnThongKeTheoNgay;
    private JButton btnInDanhSach;
    private JTextField txtNgayThongKe;
    
    // Bảng giao dịch đơn giá lớn
    private JTable tblDonGiaLon;
    private DefaultTableModel thongKeTableModel;
    
    public StatisticsPanel() {
        initializeComponents();
        setupLayout();
    }
    
    /**
     * Khởi tạo các component
     */
    private void initializeComponents() {
        // Khởi tạo text area hiển thị thống kê
        txtThongKe = new JTextArea(10, 40);
        txtThongKe.setEditable(false);
        txtThongKe.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // Khởi tạo các nút chức năng
        btnCapNhatThongKe = new JButton("Cập nhật thống kê");
        btnThongKeHomNay = new JButton("Hôm nay");
        btnThongKeThangNay = new JButton("Tháng này");
        btnThongKeTatCa = new JButton("Tất cả");
        btnThongKeTheoNgay = new JButton("Theo ngày");
        btnInDanhSach = new JButton("In danh sách");
        
        // Text field cho ngày thống kê
        txtNgayThongKe = new JTextField("2025-08-05", 10);
        
        // Bảng giao dịch đơn giá lớn
        String[] thongKeColumns = {"Mã GD", "Ngày", "Đơn giá", "Số lượng", "Loại", "Thành tiền"};
        thongKeTableModel = new DefaultTableModel(thongKeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDonGiaLon = new JTable(thongKeTableModel);
        
        // Thiết lập kích thước các cột
        tblDonGiaLon.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mã GD
        tblDonGiaLon.getColumnModel().getColumn(1).setPreferredWidth(100); // Ngày
        tblDonGiaLon.getColumnModel().getColumn(2).setPreferredWidth(120); // Đơn giá
        tblDonGiaLon.getColumnModel().getColumn(3).setPreferredWidth(80);  // Số lượng
        tblDonGiaLon.getColumnModel().getColumn(4).setPreferredWidth(80);  // Loại
        tblDonGiaLon.getColumnModel().getColumn(5).setPreferredWidth(120); // Thành tiền
    }
    
    /**
     * Thiết lập layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel trên: Thống kê tổng quan
        JPanel topPanel = createStatisticsPanel();
        
        // Panel dưới: Bảng giao dịch đơn giá lớn
        JPanel bottomPanel = createHighValueTransactionsPanel();
        
        // Sử dụng JSplitPane để chia đôi
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.4); // 40% cho panel thống kê, 60% cho bảng
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    /**
     * Tạo panel thống kê tổng quan
     */
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thống kê tổng quan"));
        
        // Text area hiển thị thống kê
        JScrollPane scrollPane = new JScrollPane(txtThongKe);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel chứa các nút
        JPanel buttonPanel = createStatisticsButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Tạo panel chứa các nút thống kê
     */
    private JPanel createStatisticsButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        panel.add(new JLabel("Ngày:"));
        panel.add(txtNgayThongKe);
        panel.add(btnThongKeTheoNgay);
        panel.add(new JLabel("│"));
        panel.add(btnThongKeHomNay);
        panel.add(btnThongKeThangNay);
        panel.add(btnThongKeTatCa);
        panel.add(btnCapNhatThongKe);
        panel.add(new JLabel("│"));
        panel.add(btnInDanhSach);
        
        return panel;
    }
    
    /**
     * Tạo panel bảng giao dịch đơn giá lớn
     */
    private JPanel createHighValueTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Giao dịch có đơn giá > 1 tỷ"));
        
        JScrollPane scrollPane = new JScrollPane(tblDonGiaLon);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Hiển thị thống kê tổng quan
     */
    public void displayStatistics(ThongKeDTO thongKe) {
        displayStatistics(thongKe, "TỔNG QUAN");
    }
    
    /**
     * Hiển thị thống kê với tiêu đề tùy chỉnh
     */
    public void displayStatistics(ThongKeDTO thongKe, String kieuThongKe) {
        if (thongKe == null) {
            txtThongKe.setText("Không có dữ liệu thống kê");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== THỐNG KÊ ").append(kieuThongKe).append(" ===\n\n");
        sb.append(String.format("📊 Tổng số giao dịch vàng: %d\n", thongKe.getTongSoLuongVang()));
        sb.append(String.format("💱 Tổng số giao dịch tiền tệ: %d\n", thongKe.getTongSoLuongTienTe()));
        sb.append(String.format("📈 Trung bình thành tiền tiền tệ: %s VNĐ\n", 
                UIUtils.formatCurrency(thongKe.getTrungBinhThanhTienTienTe())));
        sb.append(String.format("💰 Số GD đơn giá > 1 tỷ: %d\n\n", thongKe.getSoGiaoDichDonGiaLonHon1Ty()));
        
        sb.append("=== DOANH THU ===\n");
        sb.append(String.format("🥇 Tổng thành tiền vàng: %s VNĐ\n", 
                UIUtils.formatCurrency(thongKe.getTongThanhTienVang())));
        sb.append(String.format("💵 Tổng thành tiền tiền tệ: %s VNĐ\n", 
                UIUtils.formatCurrency(thongKe.getTongThanhTienTienTe())));
        sb.append(String.format("💎 Tổng doanh thu: %s VNĐ\n", 
                UIUtils.formatCurrency(thongKe.getTongThanhTienTatCa())));
        
        txtThongKe.setText(sb.toString());
    }
    
    /**
     * Hiển thị thống kê hôm nay
     */
    public void displayTodayStatistics(ThongKeDTO thongKe) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        displayStatistics(thongKe, "HÔM NAY (" + today + ")");
    }
    
    /**
     * Hiển thị thống kê tháng này
     */
    public void displayMonthStatistics(ThongKeDTO thongKe) {
        LocalDate now = LocalDate.now();
        String monthYear = now.getMonth().getValue() + "/" + now.getYear();
        displayStatistics(thongKe, "THÁNG NÀY (" + monthYear + ")");
    }
    
    /**
     * Hiển thị thống kê theo ngày
     */
    public void displayDateStatistics(ThongKeDTO thongKe, LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        displayStatistics(thongKe, "NGÀY " + dateStr);
    }
    
    /**
     * Load danh sách giao dịch đơn giá lớn
     */
    public void loadHighValueTransactions(List<GiaoDich> giaoDichs) {
        thongKeTableModel.setRowCount(0);
        
        if (giaoDichs == null || giaoDichs.isEmpty()) {
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (GiaoDich gd : giaoDichs) {
            Object[] row = new Object[6];
            row[0] = gd.getMaGiaoDich();
            row[1] = gd.getNgayGiaoDich().format(formatter);
            row[2] = UIUtils.formatCurrency(gd.getDonGia());
            row[3] = gd.getSoLuong();
            row[4] = gd.getLoaiGiaoDich();
            row[5] = UIUtils.formatCurrency(gd.tinhThanhTien());
            
            thongKeTableModel.addRow(row);
        }
    }
    
    /**
     * Lấy ngày được chọn để thống kê
     */
    public String getSelectedDate() {
        return txtNgayThongKe.getText().trim();
    }
    
    /**
     * Đặt ngày thống kê
     */
    public void setSelectedDate(String date) {
        txtNgayThongKe.setText(date);
    }
    
    /**
     * Xóa nội dung thống kê
     */
    public void clearStatistics() {
        txtThongKe.setText("");
        thongKeTableModel.setRowCount(0);
    }
    
    // Getters cho các nút
    public JButton getBtnCapNhatThongKe() { return btnCapNhatThongKe; }
    public JButton getBtnThongKeHomNay() { return btnThongKeHomNay; }
    public JButton getBtnThongKeThangNay() { return btnThongKeThangNay; }
    public JButton getBtnThongKeTatCa() { return btnThongKeTatCa; }
    public JButton getBtnThongKeTheoNgay() { return btnThongKeTheoNgay; }
    public JButton getBtnInDanhSach() { return btnInDanhSach; }
    
    // Getters cho text area và bảng (để export)
    public JTextArea getTxtThongKe() { return txtThongKe; }
    public JTable getTblDonGiaLon() { return tblDonGiaLon; }
    public DefaultTableModel getThongKeTableModel() { return thongKeTableModel; }
}
