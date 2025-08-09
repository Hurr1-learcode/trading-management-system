// Panel form nhập liệu giao dịch với validation
package ui.Panels;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dto.GiaoDichFormDTO;
import exception.ValidationException;
import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;
import ui.Utils.UIUtils;

public class TransactionFormPanel extends JPanel {
    
    // Các component trong form
    private JTextField txtMaGiaoDich;
    private JTextField txtNgayGiaoDich;
    private JTextField txtDonGia;
    private JTextField txtSoLuong;
    private JComboBox<String> cmbLoaiGiaoDich;
    private JTextField txtLoaiVang;
    private JComboBox<String> cmbLoaiTien;
    private JTextField txtTiGia;
    private JPanel pnlVang;
    private JPanel pnlTienTe;
    private JPanel dynamicPanel;
    
    // Các nút chức năng
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;
    
    // Biến lưu mã giao dịch được chọn
    private String selectedMaGiaoDich = null;
    
    public TransactionFormPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Khởi tạo các component
     */
    private void initializeComponents() {
        // Khởi tạo các text field
        txtMaGiaoDich = new JTextField(15);
        txtNgayGiaoDich = new JTextField(15);
        txtDonGia = new JTextField(15);
        txtSoLuong = new JTextField(15);
        
        // Khởi tạo combo box loại giao dịch
        cmbLoaiGiaoDich = new JComboBox<>(new String[]{"VANG", "TIEN_TE"});
        
        // Khởi tạo components cho giao dịch vàng
        txtLoaiVang = new JTextField(15);
        
        // Khởi tạo components cho giao dịch tiền tệ
        cmbLoaiTien = new JComboBox<>(new String[]{"USD", "EUR", "VND"});
        txtTiGia = new JTextField(15);
        
        // Khởi tạo các nút chức năng
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");
        
        // Đặt ngày mặc định là hôm nay
        txtNgayGiaoDich.setText(UIUtils.getCurrentDateString());
    }
    
    /**
     * Thiết lập layout
     */
    private void setupLayout() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Thông tin giao dịch"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Dòng 1: Mã giao dịch và Ngày giao dịch
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Mã giao dịch:"), gbc);
        gbc.gridx = 1;
        add(txtMaGiaoDich, gbc);
        
        gbc.gridx = 2;
        add(new JLabel("Ngày GD (yyyy-MM-dd):"), gbc);
        gbc.gridx = 3;
        add(txtNgayGiaoDich, gbc);
        
        // Dòng 2: Đơn giá và Số lượng
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 1;
        add(txtDonGia, gbc);
        
        gbc.gridx = 2;
        add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 3;
        add(txtSoLuong, gbc);
        
        // Dòng 3: Loại giao dịch
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Loại giao dịch:"), gbc);
        gbc.gridx = 1;
        add(cmbLoaiGiaoDich, gbc);
        
        // Dòng 4: Panel động cho chi tiết loại giao dịch
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        
        // Tạo panel cho giao dịch vàng
        pnlVang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlVang.add(new JLabel("Loại vàng:"));
        pnlVang.add(txtLoaiVang);
        
        // Tạo panel cho giao dịch tiền tệ
        pnlTienTe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTienTe.add(new JLabel("Loại tiền:"));
        pnlTienTe.add(cmbLoaiTien);
        
        JLabel lblTiGia = new JLabel("Tỉ giá:");
        pnlTienTe.add(lblTiGia);
        pnlTienTe.add(txtTiGia);
        
        // Tạo panel động với CardLayout
        dynamicPanel = new JPanel(new CardLayout());
        dynamicPanel.add(pnlVang, "VANG");
        dynamicPanel.add(pnlTienTe, "TIEN_TE");
        add(dynamicPanel, gbc);
        
        // Dòng 5: Các nút chức năng
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, gbc);
    }
    
    /**
     * Tạo panel chứa các nút chức năng
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);
        return panel;
    }
    
    /**
     * Thiết lập các event handler
     */
    private void setupEventHandlers() {
        // Listener cho combo box loại giao dịch
        cmbLoaiGiaoDich.addActionListener(e -> {
            CardLayout cl = (CardLayout) dynamicPanel.getLayout();
            cl.show(dynamicPanel, (String) cmbLoaiGiaoDich.getSelectedItem());
        });
        
        // Listener cho combo box loại tiền tệ
        cmbLoaiTien.addActionListener(e -> {
            String selectedCurrency = (String) cmbLoaiTien.getSelectedItem();
            if (selectedCurrency != null) {
                updateExchangeRateVisibility(selectedCurrency);
            }
        });
        
        // Khởi tạo trạng thái ban đầu cho loại tiền tệ
        cmbLoaiTien.setSelectedItem("USD");
    }
    
    /**
     * Cập nhật hiển thị tỉ giá dựa vào loại tiền được chọn
     */
    private void updateExchangeRateVisibility(String currency) {
        JLabel lblTiGia = (JLabel) pnlTienTe.getComponent(2);
        
        if ("VND".equals(currency)) {
            // Ẩn trường tỉ giá cho VND
            lblTiGia.setVisible(false);
            txtTiGia.setVisible(false);
            txtTiGia.setText("1");
        } else {
            // Hiển thị trường tỉ giá cho ngoại tệ
            lblTiGia.setVisible(true);
            txtTiGia.setVisible(true);
            // Đặt tỉ giá mặc định
            switch (currency) {
                case "USD":
                    txtTiGia.setText("26220");
                    break;
                case "EUR":
                    txtTiGia.setText("30514");
                    break;
                default:
                    txtTiGia.setText("");
                    break;
            }
        }
        pnlTienTe.revalidate();
        pnlTienTe.repaint();
    }
    
    /**
     * Lấy dữ liệu từ form và validate
     */
    public GiaoDichFormDTO getFormData() throws ValidationException {
        GiaoDichFormDTO dto = new GiaoDichFormDTO();
        
        // Validate và set mã giao dịch
        String maGiaoDich = txtMaGiaoDich.getText().trim();
        if (UIUtils.isEmpty(maGiaoDich)) {
            throw new ValidationException("Mã giao dịch không được để trống", "maGiaoDich");
        }
        dto.setMaGiaoDich(maGiaoDich);
        
        // Validate và set ngày giao dịch
        try {
            LocalDate ngayGiaoDich = UIUtils.parseDate(txtNgayGiaoDich.getText());
            dto.setNgayGiaoDich(ngayGiaoDich);
        } catch (Exception ex) {
            throw new ValidationException(ex.getMessage(), "ngayGiaoDich");
        }
        
        // Validate và set đơn giá
        try {
            String donGiaText = UIUtils.cleanNumberString(txtDonGia.getText());
            dto.setDonGia(new BigDecimal(donGiaText));
        } catch (NumberFormatException ex) {
            throw new ValidationException("Đơn giá phải là số hợp lệ", "donGia");
        }
        
        // Validate và set số lượng
        try {
            dto.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
        } catch (NumberFormatException ex) {
            throw new ValidationException("Số lượng phải là số nguyên hợp lệ", "soLuong");
        }
        
        // Set loại giao dịch
        dto.setLoaiGiaoDich((String) cmbLoaiGiaoDich.getSelectedItem());
        
        // Validate và set chi tiết theo loại giao dịch
        if ("VANG".equals(dto.getLoaiGiaoDich())) {
            String loaiVang = txtLoaiVang.getText().trim();
            if (UIUtils.isEmpty(loaiVang)) {
                throw new ValidationException("Loại vàng không được để trống", "loaiVang");
            }
            dto.setLoaiVang(loaiVang);
        } else if ("TIEN_TE".equals(dto.getLoaiGiaoDich())) {
            dto.setLoaiTien((String) cmbLoaiTien.getSelectedItem());
            
            // Validate tỉ giá cho ngoại tệ
            if (!"VND".equals(dto.getLoaiTien())) {
                try {
                    String tiGiaText = txtTiGia.getText().trim();
                    if (UIUtils.isEmpty(tiGiaText)) {
                        throw new ValidationException("Tỉ giá không được để trống cho ngoại tệ", "tiGia");
                    }
                    dto.setTiGia(new BigDecimal(tiGiaText));
                } catch (NumberFormatException ex) {
                    throw new ValidationException("Tỉ giá phải là số hợp lệ", "tiGia");
                }
            } else {
                dto.setTiGia(BigDecimal.ONE);
            }
        }
        
        return dto;
    }
    
    /**
     * Load dữ liệu giao dịch vào form
     */
    public void loadTransactionData(GiaoDich giaoDich) {
        if (giaoDich == null) return;
        
        txtMaGiaoDich.setText(giaoDich.getMaGiaoDich());
        txtNgayGiaoDich.setText(giaoDich.getNgayGiaoDich().toString());
        txtDonGia.setText(giaoDich.getDonGia().toString());
        txtSoLuong.setText(String.valueOf(giaoDich.getSoLuong()));
        
        if (giaoDich instanceof GiaoDichVang) {
            cmbLoaiGiaoDich.setSelectedItem("VANG");
            txtLoaiVang.setText(((GiaoDichVang) giaoDich).getLoaiVang());
        } else if (giaoDich instanceof GiaoDichTienTe) {
            cmbLoaiGiaoDich.setSelectedItem("TIEN_TE");
            GiaoDichTienTe gdtt = (GiaoDichTienTe) giaoDich;
            cmbLoaiTien.setSelectedItem(gdtt.getLoaiTien());
            
            if (!"VND".equals(gdtt.getLoaiTien())) {
                txtTiGia.setText(gdtt.getTiGia().toString());
            }
        }
        
        selectedMaGiaoDich = giaoDich.getMaGiaoDich();
    }
    
    /**
     * Xóa dữ liệu form
     */
    public void clearForm() {
        txtMaGiaoDich.setText("");
        txtNgayGiaoDich.setText(UIUtils.getCurrentDateString());
        txtDonGia.setText("");
        txtSoLuong.setText("");
        txtLoaiVang.setText("");
        cmbLoaiTien.setSelectedIndex(0);
        
        // Đặt tỉ giá mặc định
        String selectedCurrency = (String) cmbLoaiTien.getSelectedItem();
        if ("USD".equals(selectedCurrency)) {
            txtTiGia.setText("26220");
        } else {
            txtTiGia.setText("");
        }
        
        cmbLoaiGiaoDich.setSelectedIndex(0);
        selectedMaGiaoDich = null;
    }
    
    // Getters cho các nút
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    
    // Getters/Setters cho mã giao dịch được chọn
    public String getSelectedMaGiaoDich() { return selectedMaGiaoDich; }
    public void setSelectedMaGiaoDich(String maGiaoDich) { this.selectedMaGiaoDich = maGiaoDich; }
}
