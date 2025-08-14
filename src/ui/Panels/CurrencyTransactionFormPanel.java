// Form panel riêng cho giao dịch tiền tệ
package ui.Panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import dto.GiaoDichFormDTO;
import exception.ValidationException;
import model.GiaoDich;
import model.GiaoDichTienTe;
import ui.Utils.UIUtils;

public class CurrencyTransactionFormPanel extends JPanel {
    
    // Form dialog components
    private JDialog formDialog;
    private JTextField txtMaGiaoDich;
    private JTextField txtNgayGiaoDich;
    private JTextField txtDonGia;
    private JTextField txtSoLuong;
    private JComboBox<String> cmbLoaiTien;
    private JTextField txtTiGia;
    private JLabel lblTiGia;
    
    // Form dialog buttons
    private JButton btnSaveForm;
    private JButton btnCancelForm;
    
    // Biến lưu trạng thái form
    private String selectedMaGiaoDich = null;
    private boolean isEditMode = false;
    
    // Interface để callback khi save form
    public interface FormSaveListener {
        void onAddCurrencyTransaction(GiaoDichFormDTO dto);
        void onEditCurrencyTransaction(GiaoDichFormDTO dto);
    }
    
    private FormSaveListener formSaveListener;
    
    public CurrencyTransactionFormPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Khởi tạo các component
     */
    private void initializeComponents() {
        txtMaGiaoDich = new JTextField(15);
        txtNgayGiaoDich = new JTextField(15);
        txtDonGia = new JTextField(15);
        txtSoLuong = new JTextField(15);
        cmbLoaiTien = new JComboBox<>(new String[]{"USD", "EUR", "VND"});
        txtTiGia = new JTextField(15);
        lblTiGia = new JLabel("Tỉ giá:");
        
        btnSaveForm = new JButton("Lưu");
        btnCancelForm = new JButton("Hủy");
        
        // Đặt ngày mặc định là hôm nay
        txtNgayGiaoDich.setText(UIUtils.getCurrentDateString());
        
        // Đặt mặc định là USD
        cmbLoaiTien.setSelectedItem("USD");
    }
    
    /**
     * Thiết lập layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Giao dịch tiền tệ"));
    }
    
    /**
     * Thiết lập event handlers
     */
    private void setupEventHandlers() {
        // Listener cho combo box loại tiền tệ
        cmbLoaiTien.addActionListener(e -> {
            String selectedCurrency = (String) cmbLoaiTien.getSelectedItem();
            if (selectedCurrency != null) {
                updateExchangeRateVisibility(selectedCurrency);
            }
        });
    }
    
    /**
     * Hiển thị form dialog để thêm giao dịch tiền tệ
     */
    public void showAddForm() {
        isEditMode = false;
        selectedMaGiaoDich = null;
        clearFormData();
        showFormDialog("Thêm giao dịch tiền tệ");
    }
    
    /**
     * Hiển thị form dialog để sửa giao dịch tiền tệ
     */
    public void showEditForm(GiaoDich giaoDich) {
        if (giaoDich == null || !(giaoDich instanceof GiaoDichTienTe)) return;
        
        isEditMode = true;
        selectedMaGiaoDich = giaoDich.getMaGiaoDich();
        loadTransactionDataToForm((GiaoDichTienTe) giaoDich);
        showFormDialog("Sửa giao dịch tiền tệ");
    }
    
    /**
     * Hiển thị form dialog
     */
    private void showFormDialog(String title) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        formDialog = new JDialog(parentFrame, title, true);
        formDialog.setLayout(new BorderLayout());
        
        // Tạo panel form
        JPanel formPanel = createFormPanel();
        formDialog.add(formPanel, BorderLayout.CENTER);
        
        // Tạo panel buttons
        JPanel buttonPanel = createFormButtonPanel();
        formDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        formDialog.pack();
        formDialog.setLocationRelativeTo(parentFrame);
        formDialog.setVisible(true);
    }
    
    /**
     * Tạo panel form
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Dòng 1: Mã giao dịch
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã giao dịch:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMaGiaoDich, gbc);
        
        // Dòng 2: Ngày giao dịch
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ngày GD (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        panel.add(txtNgayGiaoDich, gbc);
        
        // Dòng 3: Đơn giá
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDonGia, gbc);
        
        // Dòng 4: Số lượng
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        panel.add(txtSoLuong, gbc);
        
        // Dòng 5: Loại tiền
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Loại tiền:"), gbc);
        gbc.gridx = 1;
        panel.add(cmbLoaiTien, gbc);
        
        // Dòng 6: Tỉ giá
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(lblTiGia, gbc);
        gbc.gridx = 1;
        panel.add(txtTiGia, gbc);
        
        // Cập nhật hiển thị tỉ giá ban đầu
        updateExchangeRateVisibility((String) cmbLoaiTien.getSelectedItem());
        
        return panel;
    }
    
    /**
     * Cập nhật hiển thị tỉ giá dựa vào loại tiền được chọn
     */
    private void updateExchangeRateVisibility(String currency) {
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
        
        if (formDialog != null) {
            formDialog.revalidate();
            formDialog.repaint();
        }
    }
    
    /**
     * Tạo panel chứa các nút form dialog
     */
    private JPanel createFormButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        // Setup button actions
        btnSaveForm.addActionListener(e -> saveFormData());
        btnCancelForm.addActionListener(e -> formDialog.dispose());
        
        panel.add(btnSaveForm);
        panel.add(btnCancelForm);
        return panel;
    }
    
    /**
     * Lưu dữ liệu form
     */
    private void saveFormData() {
        try {
            GiaoDichFormDTO dto = getFormData();
            
            // Callback để controller xử lý
            if (formSaveListener != null) {
                if (isEditMode) {
                    formSaveListener.onEditCurrencyTransaction(dto);
                } else {
                    formSaveListener.onAddCurrencyTransaction(dto);
                }
            }
            
            formDialog.dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(formDialog, ex.getMessage(), "Lỗi validation", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Lấy dữ liệu từ form và validate
     */
    private GiaoDichFormDTO getFormData() throws ValidationException {
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
        
        // Set loại giao dịch và chi tiết tiền tệ
        dto.setLoaiGiaoDich("TIEN_TE");
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
        
        return dto;
    }
    
    /**
     * Load dữ liệu giao dịch tiền tệ vào form
     */
    private void loadTransactionDataToForm(GiaoDichTienTe giaoDichTienTe) {
        txtMaGiaoDich.setText(giaoDichTienTe.getMaGiaoDich());
        txtNgayGiaoDich.setText(giaoDichTienTe.getNgayGiaoDich().toString());
        txtDonGia.setText(giaoDichTienTe.getDonGia().toString());
        txtSoLuong.setText(String.valueOf(giaoDichTienTe.getSoLuong()));
        cmbLoaiTien.setSelectedItem(giaoDichTienTe.getLoaiTien());
        
        if (!"VND".equals(giaoDichTienTe.getLoaiTien())) {
            txtTiGia.setText(giaoDichTienTe.getTiGia().toString());
        }
    }
    
    /**
     * Xóa dữ liệu form
     */
    private void clearFormData() {
        txtMaGiaoDich.setText("");
        txtNgayGiaoDich.setText(UIUtils.getCurrentDateString());
        txtDonGia.setText("");
        txtSoLuong.setText("");
        cmbLoaiTien.setSelectedIndex(0);
        
        // Đặt tỉ giá mặc định cho USD
        String selectedCurrency = (String) cmbLoaiTien.getSelectedItem();
        if ("USD".equals(selectedCurrency)) {
            txtTiGia.setText("26220");
        } else {
            txtTiGia.setText("");
        }
    }
    
    // Setters cho listeners
    public void setFormSaveListener(FormSaveListener listener) {
        this.formSaveListener = listener;
    }
    
    // Getters/Setters cho mã giao dịch được chọn
    public String getSelectedMaGiaoDich() { return selectedMaGiaoDich; }
    public void setSelectedMaGiaoDich(String maGiaoDich) { this.selectedMaGiaoDich = maGiaoDich; }
}
