// Form panel riêng cho giao dịch vàng
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
import model.GiaoDichVang;
import ui.Utils.UIUtils;

public class GoldTransactionFormPanel extends JPanel {
    
    // Form dialog components
    private JDialog formDialog;
    private JTextField txtMaGiaoDich;
    private JTextField txtNgayGiaoDich;
    private JTextField txtDonGia;
    private JTextField txtSoLuong;
    private JTextField txtLoaiVang;
    
    // Form dialog buttons
    private JButton btnSaveForm;
    private JButton btnCancelForm;
    
    // Biến lưu trạng thái form
    private String selectedMaGiaoDich = null;
    private boolean isEditMode = false;
    
    // Interface để callback khi save form
    public interface FormSaveListener {
        void onAddGoldTransaction(GiaoDichFormDTO dto);
        void onEditGoldTransaction(GiaoDichFormDTO dto);
    }
    
    private FormSaveListener formSaveListener;
    
    public GoldTransactionFormPanel() {
        initializeComponents();
        setupLayout();
    }
    
    /**
     * Khởi tạo các component
     */
    private void initializeComponents() {
        txtMaGiaoDich = new JTextField(15);
        txtNgayGiaoDich = new JTextField(15);
        txtDonGia = new JTextField(15);
        txtSoLuong = new JTextField(15);
        txtLoaiVang = new JTextField(15);
        
        btnSaveForm = new JButton("Lưu");
        btnCancelForm = new JButton("Hủy");
        
        // Đặt ngày mặc định là hôm nay
        txtNgayGiaoDich.setText(UIUtils.getCurrentDateString());
    }
    
    /**
     * Thiết lập layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Giao dịch vàng"));
    }
    
    /**
     * Hiển thị form dialog để thêm giao dịch vàng
     */
    public void showAddForm() {
        isEditMode = false;
        selectedMaGiaoDich = null;
        clearFormData();
        showFormDialog("Thêm giao dịch vàng");
    }
    
    /**
     * Hiển thị form dialog để sửa giao dịch vàng
     */
    public void showEditForm(GiaoDich giaoDich) {
        if (giaoDich == null || !(giaoDich instanceof GiaoDichVang)) return;
        
        isEditMode = true;
        selectedMaGiaoDich = giaoDich.getMaGiaoDich();
        loadTransactionDataToForm((GiaoDichVang) giaoDich);
        showFormDialog("Sửa giao dịch vàng");
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
        
        // Dòng 5: Loại vàng
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Loại vàng:"), gbc);
        gbc.gridx = 1;
        panel.add(txtLoaiVang, gbc);
        
        return panel;
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
                    formSaveListener.onEditGoldTransaction(dto);
                } else {
                    formSaveListener.onAddGoldTransaction(dto);
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
        
        // Set loại giao dịch và loại vàng
        dto.setLoaiGiaoDich("VANG");
        String loaiVang = txtLoaiVang.getText().trim();
        if (UIUtils.isEmpty(loaiVang)) {
            throw new ValidationException("Loại vàng không được để trống", "loaiVang");
        }
        dto.setLoaiVang(loaiVang);
        
        return dto;
    }
    
    /**
     * Load dữ liệu giao dịch vàng vào form
     */
    private void loadTransactionDataToForm(GiaoDichVang giaoDichVang) {
        txtMaGiaoDich.setText(giaoDichVang.getMaGiaoDich());
        txtNgayGiaoDich.setText(giaoDichVang.getNgayGiaoDich().toString());
        txtDonGia.setText(giaoDichVang.getDonGia().toString());
        txtSoLuong.setText(String.valueOf(giaoDichVang.getSoLuong()));
        txtLoaiVang.setText(giaoDichVang.getLoaiVang());
    }
    
    /**
     * Xóa dữ liệu form
     */
    private void clearFormData() {
        txtMaGiaoDich.setText("");
        txtNgayGiaoDich.setText(UIUtils.getCurrentDateString());
        txtDonGia.setText("");
        txtSoLuong.setText("");
        txtLoaiVang.setText("");
    }
    
    // Setters cho listeners
    public void setFormSaveListener(FormSaveListener listener) {
        this.formSaveListener = listener;
    }
    
    // Getters/Setters cho mã giao dịch được chọn
    public String getSelectedMaGiaoDich() { return selectedMaGiaoDich; }
    public void setSelectedMaGiaoDich(String maGiaoDich) { this.selectedMaGiaoDich = maGiaoDich; }
}
