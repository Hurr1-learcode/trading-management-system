// Main UI Frame with tabbed interface
package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import dto.GiaoDichFormDTO;
import dto.ThongKeDTO;
import exception.ValidationException;
import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;
import service.QuanLyGiaoDich;

public class MainFrame extends JFrame {
    private QuanLyGiaoDich quanLyGiaoDich;
    
    // Components for Tab 1 - Quản lý giao dịch
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
    
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;
    
    private JTable tblGiaoDich;
    private DefaultTableModel tableModel;
    
    // Components for Tab 2 - Thống kê
    private JTextArea txtThongKe;
    private JButton btnCapNhatThongKe;
    private JButton btnThongKeHomNay;
    private JButton btnThongKeThangNay;
    private JButton btnThongKeTatCa;
    private JButton btnThongKeTheoNgay;
    private JButton btnInDanhSach;
    private JTextField txtNgayThongKe;
    private JTable tblDonGiaLon;
    private DefaultTableModel thongKeTableModel;
    
    private String selectedMaGiaoDich = null;
    
    public MainFrame() {
        quanLyGiaoDich = new QuanLyGiaoDich();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
        
        setTitle("Quản Lý Giao Dịch");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Tab 1 components
        txtMaGiaoDich = new JTextField(15);
        txtNgayGiaoDich = new JTextField(15);
        txtDonGia = new JTextField(15);
        txtSoLuong = new JTextField(15);
        
        cmbLoaiGiaoDich = new JComboBox<>(new String[]{"VANG", "TIEN_TE"});
        
        txtLoaiVang = new JTextField(15);
        cmbLoaiTien = new JComboBox<>(new String[]{"USD", "EUR", "VND"});
        txtTiGia = new JTextField(15);
        
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");
        
        // Table for transactions
        String[] columns = {"Mã GD", "Ngày", "Đơn giá", "Số lượng", "Loại", "Chi tiết", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGiaoDich = new JTable(tableModel);
        tblGiaoDich.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Tab 2 components
        txtThongKe = new JTextArea(10, 40);
        txtThongKe.setEditable(false);
        txtThongKe.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        btnCapNhatThongKe = new JButton("Cập nhật thống kê");
        btnThongKeHomNay = new JButton("Hôm nay");
        btnThongKeThangNay = new JButton("Tháng này");
        btnThongKeTatCa = new JButton("Tất cả");
        btnThongKeTheoNgay = new JButton("Theo ngày");
        btnInDanhSach = new JButton("In danh sách");
        txtNgayThongKe = new JTextField("2025-08-05", 10);
        
        String[] thongKeColumns = {"Mã GD", "Ngày", "Đơn giá", "Số lượng", "Loại", "Thành tiền"};
        thongKeTableModel = new DefaultTableModel(thongKeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDonGiaLon = new JTable(thongKeTableModel);
    }
    
    private void setupLayout() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1 - Quản lý giao dịch
        JPanel tabQuanLy = createQuanLyTab();
        tabbedPane.addTab("Quản lý Giao dịch", tabQuanLy);
        
        // Tab 2 - Thống kê
        JPanel tabThongKe = createThongKeTab();
        tabbedPane.addTab("Thống kê & Lọc", tabThongKe);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createQuanLyTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Form panel
        JPanel formPanel = createFormPanel();
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(tblGiaoDich);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin giao dịch"));
        
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã giao dịch:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMaGiaoDich, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Ngày GD (yyyy-MM-dd):"), gbc);
        gbc.gridx = 3;
        panel.add(txtNgayGiaoDich, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDonGia, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 3;
        panel.add(txtSoLuong, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Loại giao dịch:"), gbc);
        gbc.gridx = 1;
        panel.add(cmbLoaiGiaoDich, gbc);
        
        // Dynamic panels for specific transaction types
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        
        // Panel for Vang
        pnlVang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlVang.add(new JLabel("Loại vàng:"));
        pnlVang.add(txtLoaiVang);
        
        // Panel for TienTe
        pnlTienTe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTienTe.add(new JLabel("Loại tiền:"));
        pnlTienTe.add(cmbLoaiTien);
        
        JLabel lblTiGia = new JLabel("Tỉ giá:");
        pnlTienTe.add(lblTiGia);
        pnlTienTe.add(txtTiGia);
        
        // Add currency selection listener to auto-update exchange rate and hide/show exchange rate field
        cmbLoaiTien.addActionListener(e -> {
            String selectedCurrency = (String) cmbLoaiTien.getSelectedItem();
            if (selectedCurrency != null) {
                if ("VND".equals(selectedCurrency)) {
                    // Hide exchange rate field for VND
                    lblTiGia.setVisible(false);
                    txtTiGia.setVisible(false);
                    txtTiGia.setText("1"); // Set to 1 for VND (will not be used in calculation)
                } else {
                    // Show exchange rate field for foreign currencies
                    lblTiGia.setVisible(true);
                    txtTiGia.setVisible(true);
                    switch (selectedCurrency) {
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
        });
        
        // Add dynamic panel container
        JPanel dynamicPanel = new JPanel(new CardLayout());
        dynamicPanel.add(pnlVang, "VANG");
        dynamicPanel.add(pnlTienTe, "TIEN_TE");
        panel.add(dynamicPanel, gbc);
        
        // Setup combo box listener to switch panels
        cmbLoaiGiaoDich.addActionListener(e -> {
            CardLayout cl = (CardLayout) dynamicPanel.getLayout();
            cl.show(dynamicPanel, (String) cmbLoaiGiaoDich.getSelectedItem());
        });
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);
        return panel;
    }
    
    private JPanel createThongKeTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Statistics text area
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Thống kê tổng quan"));
        topPanel.add(new JScrollPane(txtThongKe), BorderLayout.CENTER);
        
        // Button panel for statistics
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JLabel("Ngày:"));
        buttonPanel.add(txtNgayThongKe);
        buttonPanel.add(btnThongKeTheoNgay);
        buttonPanel.add(new JLabel("│"));
        buttonPanel.add(btnThongKeHomNay);
        buttonPanel.add(btnThongKeThangNay);
        buttonPanel.add(btnThongKeTatCa);
        buttonPanel.add(btnCapNhatThongKe);
        buttonPanel.add(new JLabel("│"));
        buttonPanel.add(btnInDanhSach);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // High value transactions table
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Giao dịch có đơn giá > 1 tỷ"));
        bottomPanel.add(new JScrollPane(tblDonGiaLon), BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setDividerLocation(250);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Table selection listener
        tblGiaoDich.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblGiaoDich.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedMaGiaoDich = (String) tableModel.getValueAt(selectedRow, 0);
                    loadSelectedTransaction();
                }
            }
        });
        
        // Button listeners
        btnThem.addActionListener(this::handleThem);
        btnSua.addActionListener(this::handleSua);
        btnXoa.addActionListener(this::handleXoa);
        btnLamMoi.addActionListener(this::handleLamMoi);
        btnCapNhatThongKe.addActionListener(this::handleCapNhatThongKe);
        btnThongKeHomNay.addActionListener(this::handleThongKeHomNay);
        btnThongKeThangNay.addActionListener(this::handleThongKeThangNay);
        btnThongKeTatCa.addActionListener(this::handleThongKeTatCa);
        btnThongKeTheoNgay.addActionListener(this::handleThongKeTheoNgay);
        btnInDanhSach.addActionListener(this::handleInDanhSach);
        
        // Set default date to today
        txtNgayGiaoDich.setText(LocalDate.now().toString());
        
        // Trigger initial currency selection to set up UI correctly
        cmbLoaiTien.setSelectedItem("USD"); // This will show exchange rate field and set default rate
    }
    
    private void handleThem(ActionEvent e) {
        try {
            GiaoDichFormDTO formDTO = getFormData();
            quanLyGiaoDich.add(formDTO);
            showMessage("Thêm giao dịch thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData();
        } catch (Exception ex) {
            showMessage("Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleSua(ActionEvent e) {
        if (selectedMaGiaoDich == null) {
            showMessage("Vui lòng chọn giao dịch cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            GiaoDichFormDTO formDTO = getFormData();
            quanLyGiaoDich.edit(selectedMaGiaoDich, formDTO);
            showMessage("Cập nhật giao dịch thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData();
        } catch (Exception ex) {
            showMessage("Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleXoa(ActionEvent e) {
        if (selectedMaGiaoDich == null) {
            showMessage("Vui lòng chọn giao dịch cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa giao dịch: " + selectedMaGiaoDich + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                quanLyGiaoDich.remove(selectedMaGiaoDich);
                showMessage("Xóa giao dịch thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            } catch (Exception ex) {
                showMessage("Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleLamMoi(ActionEvent e) {
        clearForm();
        loadData();
    }
    
    private void handleCapNhatThongKe(ActionEvent e) {
        loadThongKe();
    }
    
    private void handleThongKeHomNay(ActionEvent e) {
        loadThongKeHomNay();
    }
    
    private void handleThongKeThangNay(ActionEvent e) {
        loadThongKeThangNay();
    }
    
    private void handleThongKeTatCa(ActionEvent e) {
        loadThongKe();
    }
    
    private void handleThongKeTheoNgay(ActionEvent e) {
        loadThongKeTheoNgay();
    }
    
    private void handleInDanhSach(ActionEvent e) {
        showExportDialog();
    }
    
    private GiaoDichFormDTO getFormData() throws ValidationException {
        GiaoDichFormDTO dto = new GiaoDichFormDTO();
        
        // Validate and set basic fields
        String maGiaoDich = txtMaGiaoDich.getText().trim();
        if (maGiaoDich.isEmpty()) {
            throw new ValidationException("Mã giao dịch không được để trống", "maGiaoDich");
        }
        dto.setMaGiaoDich(maGiaoDich);
        
        // Parse date
        try {
            dto.setNgayGiaoDich(LocalDate.parse(txtNgayGiaoDich.getText().trim()));
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Ngày giao dịch không đúng định dạng (yyyy-MM-dd)", "ngayGiaoDich");
        }
        
        // Parse don gia
        try {
            String donGiaText = txtDonGia.getText().trim().replace(".", "");
            dto.setDonGia(new BigDecimal(donGiaText));
        } catch (NumberFormatException ex) {
            throw new ValidationException("Đơn giá phải là số hợp lệ", "donGia");
        }
        
        // Parse so luong
        try {
            dto.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
        } catch (NumberFormatException ex) {
            throw new ValidationException("Số lượng phải là số nguyên hợp lệ", "soLuong");
        }
        
        dto.setLoaiGiaoDich((String) cmbLoaiGiaoDich.getSelectedItem());
        
        if ("VANG".equals(dto.getLoaiGiaoDich())) {
            dto.setLoaiVang(txtLoaiVang.getText().trim());
        } else if ("TIEN_TE".equals(dto.getLoaiGiaoDich())) {
            dto.setLoaiTien((String) cmbLoaiTien.getSelectedItem());
            // Only validate and set exchange rate for non-VND currencies
            if (!"VND".equals(dto.getLoaiTien())) {
                try {
                    String tiGiaText = txtTiGia.getText().trim();
                    if (tiGiaText.isEmpty()) {
                        throw new ValidationException("Tỉ giá không được để trống cho ngoại tệ", "tiGia");
                    }
                    dto.setTiGia(new BigDecimal(tiGiaText));
                } catch (NumberFormatException ex) {
                    throw new ValidationException("Tỉ giá phải là số hợp lệ", "tiGia");
                }
            } else {
                // For VND, set exchange rate to 1 (not used in calculation anyway)
                dto.setTiGia(BigDecimal.ONE);
            }
        }
        
        return dto;
    }
    
    private void loadSelectedTransaction() {
        try {
            var optional = quanLyGiaoDich.findById(selectedMaGiaoDich);
            if (optional.isPresent()) {
                GiaoDich giaoDich = optional.get();
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
                    
                    // Only display exchange rate for non-VND currencies
                    if (!"VND".equals(gdtt.getLoaiTien())) {
                        txtTiGia.setText(gdtt.getTiGia().toString());
                    }
                }
            }
        } catch (Exception ex) {
            showMessage("Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtMaGiaoDich.setText("");
        txtNgayGiaoDich.setText(LocalDate.now().toString());
        txtDonGia.setText("");
        txtSoLuong.setText("");
        txtLoaiVang.setText("");
        cmbLoaiTien.setSelectedIndex(0);
        
        // Set default exchange rate based on selected currency
        String selectedCurrency = (String) cmbLoaiTien.getSelectedItem();
        if ("USD".equals(selectedCurrency)) {
            txtTiGia.setText("26220");
        } else {
            txtTiGia.setText("");
        }
        
        cmbLoaiGiaoDich.setSelectedIndex(0);
        selectedMaGiaoDich = null;
    }
    
    private void loadData() {
        try {
            List<GiaoDich> giaoDichs = quanLyGiaoDich.getAll();
            tableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (GiaoDich gd : giaoDichs) {
                Object[] row = new Object[7];
                row[0] = gd.getMaGiaoDich();
                row[1] = gd.getNgayGiaoDich().format(formatter);
                row[2] = String.format("%,.0f", gd.getDonGia());
                row[3] = gd.getSoLuong();
                row[4] = gd.getLoaiGiaoDich();
                
                if (gd instanceof GiaoDichVang) {
                    row[5] = ((GiaoDichVang) gd).getLoaiVang();
                } else if (gd instanceof GiaoDichTienTe) {
                    GiaoDichTienTe gdtt = (GiaoDichTienTe) gd;
                    if ("VND".equals(gdtt.getLoaiTien())) {
                        row[5] = gdtt.getLoaiTien();
                    } else {
                        row[5] = gdtt.getLoaiTien() + " (Tỉ giá: " + gdtt.getTiGia() + ")";
                    }
                }
                
                row[6] = String.format("%,.0f", gd.tinhThanhTien());
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            showMessage("Lỗi khi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadThongKe() {
        try {
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoai();
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== THỐNG KÊ TỔNG QUAN ===\n\n");
            sb.append(String.format(" Tổng số giao dịch vàng: %d\n", thongKe.getTongSoLuongVang()));
            sb.append(String.format(" Tổng số giao dịch tiền tệ: %d\n", thongKe.getTongSoLuongTienTe()));
            sb.append(String.format(" Trung bình thành tiền tiền tệ: %,.0f VNĐ\n", thongKe.getTrungBinhThanhTienTienTe()));
            sb.append(String.format(" Số GD đơn giá > 1 tỷ: %d\n\n", thongKe.getSoGiaoDichDonGiaLonHon1Ty()));
            
            sb.append("=== DOANH THU ===\n");
            sb.append(String.format(" Tổng thành tiền vàng: %,.0f VNĐ\n", thongKe.getTongThanhTienVang()));
            sb.append(String.format(" Tổng thành tiền tiền tệ: %,.0f VNĐ\n", thongKe.getTongThanhTienTienTe()));
            sb.append(String.format(" Tổng doanh thu: %,.0f VNĐ\n", thongKe.getTongThanhTienTatCa()));
            
            txtThongKe.setText(sb.toString());
            
            // Load high value transactions
            List<GiaoDich> donGiaLon = quanLyGiaoDich.getDonGiaLonHon1Ty();
            System.out.println("DEBUG: Số giao dịch đơn giá > 1 tỷ tìm thấy: " + donGiaLon.size());
            
            thongKeTableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (GiaoDich gd : donGiaLon) {
                System.out.println("DEBUG: GD " + gd.getMaGiaoDich() + " - Đơn giá: " + gd.getDonGia() + " - Thành tiền: " + gd.tinhThanhTien());
                Object[] row = new Object[6];
                row[0] = gd.getMaGiaoDich();
                row[1] = gd.getNgayGiaoDich().format(formatter);
                row[2] = String.format("%,.0f", gd.getDonGia());
                row[3] = gd.getSoLuong();
                row[4] = gd.getLoaiGiaoDich();
                row[5] = String.format("%,.0f", gd.tinhThanhTien());
                thongKeTableModel.addRow(row);
            }
            
        } catch (Exception ex) {
            showMessage("Lỗi khi tải thống kê: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadThongKeHomNay() {
        try {
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiHomNay();
            displayThongKe(thongKe, "HÔM NAY (" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");
            
        } catch (Exception ex) {
            showMessage("Lỗi khi tải thống kê hôm nay: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadThongKeThangNay() {
        try {
            LocalDate now = LocalDate.now();
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiTheoThang(now.getYear(), now.getMonthValue());
            displayThongKe(thongKe, "THÁNG NÀY (" + now.getMonth().getValue() + "/" + now.getYear() + ")");
            
        } catch (Exception ex) {
            showMessage("Lỗi khi tải thống kê tháng này: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadThongKeTheoNgay() {
        try {
            String ngayStr = txtNgayThongKe.getText().trim();
            LocalDate ngayChon = LocalDate.parse(ngayStr);
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoaiTheoKhoangNgay(ngayChon, ngayChon);
            displayThongKe(thongKe, "NGÀY " + ngayChon.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
        } catch (Exception ex) {
            showMessage("Lỗi khi tải thống kê theo ngày: " + ex.getMessage() + "\nVui lòng nhập đúng định dạng yyyy-MM-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayThongKe(ThongKeDTO thongKe, String kieuThongKe) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== THỐNG KÊ ").append(kieuThongKe).append(" ===\n\n");
        sb.append(String.format("📊 Tổng số giao dịch vàng: %d\n", thongKe.getTongSoLuongVang()));
        sb.append(String.format("💱 Tổng số giao dịch tiền tệ: %d\n", thongKe.getTongSoLuongTienTe()));
        sb.append(String.format("📈 Trung bình thành tiền tiền tệ: %,.0f VNĐ\n", thongKe.getTrungBinhThanhTienTienTe()));
        sb.append(String.format("💰 Số GD đơn giá > 1 tỷ: %d\n\n", thongKe.getSoGiaoDichDonGiaLonHon1Ty()));
        
        sb.append("=== DOANH THU ===\n");
        sb.append(String.format("🥇 Tổng thành tiền vàng: %,.0f VNĐ\n", thongKe.getTongThanhTienVang()));
        sb.append(String.format("💵 Tổng thành tiền tiền tệ: %,.0f VNĐ\n", thongKe.getTongThanhTienTienTe()));
        sb.append(String.format("💎 Tổng doanh thu: %,.0f VNĐ\n", thongKe.getTongThanhTienTatCa()));
        
        txtThongKe.setText(sb.toString());
        
        // Load high value transactions (vẫn hiển thị tất cả)
        try {
            List<GiaoDich> donGiaLon = quanLyGiaoDich.getDonGiaLonHon1Ty();
            thongKeTableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (GiaoDich gd : donGiaLon) {
                Object[] row = new Object[6];
                row[0] = gd.getMaGiaoDich();
                row[1] = gd.getNgayGiaoDich().format(formatter);
                row[2] = String.format("%,.0f", gd.getDonGia());
                row[3] = gd.getSoLuong();
                row[4] = gd.getLoaiGiaoDich();
                row[5] = String.format("%,.0f", gd.tinhThanhTien());
                thongKeTableModel.addRow(row);
            }
            
        } catch (Exception ex) {
            showMessage("Lỗi khi tải bảng giao dịch lớn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    private void showExportDialog() {
        String[] options = {
            "Tất cả giao dịch", 
            "Giao dịch hiển thị trong bảng", 
            "Giao dịch + Thống kê tổng hợp"
        };
        
        int choice = JOptionPane.showOptionDialog(
            this,
            "Chọn nội dung muốn xuất:",
            "In danh sách giao dịch",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice != JOptionPane.CLOSED_OPTION) {
            selectFileAndExport(choice);
        }
    }
    
    private void selectFileAndExport(int exportType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file");
        
        // Add file filters
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
        
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.setFileFilter(txtFilter);
        
        // Set default filename with current date
        String defaultName = "DanhSachGiaoDich_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        fileChooser.setSelectedFile(new File(defaultName + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                exportData(selectedFile, exportType);
                showMessage("Xuất file thành công!\nĐường dẫn: " + selectedFile.getAbsolutePath(), 
                           "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showMessage("Lỗi khi xuất file: " + e.getMessage(), 
                           "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportData(File file, int exportType) throws IOException {
        String fileName = file.getName().toLowerCase();
        
        try (FileWriter writer = new FileWriter(file)) {
            // Write header
            writer.write("=".repeat(80) + "\n");
            writer.write("           DANH SÁCH GIAO DỊCH - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
            writer.write("=".repeat(80) + "\n\n");
            
            List<GiaoDich> dataToExport = getDataForExport(exportType);
            
            if (fileName.endsWith(".csv")) {
                exportToCSV(writer, dataToExport, exportType);
            } else {
                exportToText(writer, dataToExport, exportType);
            }
            
            // Add statistics if requested
            if (exportType == 2) {
                writer.write("\n" + "=".repeat(80) + "\n");
                writer.write("                    THỐNG KÊ TỔNG HỢP\n");
                writer.write("=".repeat(80) + "\n");
                addStatistics(writer, fileName.endsWith(".csv"));
            }
        }
    }
    
    private List<GiaoDich> getDataForExport(int exportType) {
        try {
            switch (exportType) {
                case 0: // Tất cả giao dịch
                    return quanLyGiaoDich.getAll();
                case 1: // Giao dịch hiển thị trong bảng
                case 2: // Giao dịch + thống kê
                    // Get data from current table view
                    return quanLyGiaoDich.getAll(); // For now, return all
                default:
                    return quanLyGiaoDich.getAll();
            }
        } catch (Exception e) {
            showMessage("Lỗi khi lấy dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return List.of();
        }
    }
    
    private void exportToCSV(FileWriter writer, List<GiaoDich> transactions, int exportType) throws IOException {
        // CSV Header
        writer.write("Mã GD,Ngày GD,Đơn giá,Số lượng,Loại GD,Chi tiết,Thành tiền\n");
        
        for (GiaoDich gd : transactions) {
            writer.write(String.format("%s,%s,%s,%d,%s,%s,%s\n",
                gd.getMaGiaoDich(),
                gd.getNgayGiaoDich().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                gd.getDonGia(),
                gd.getSoLuong(),
                gd.getLoaiGiaoDich(),
                getTransactionDetail(gd),
                gd.tinhThanhTien()
            ));
        }
    }
    
    private void exportToText(FileWriter writer, List<GiaoDich> transactions, int exportType) throws IOException {
        String format = "%-12s %-12s %-15s %-8s %-8s %-15s %-15s\n";
        
        // Table header
        writer.write(String.format(format, "Mã GD", "Ngày GD", "Đơn giá", "SL", "Loại", "Chi tiết", "Thành tiền"));
        writer.write("-".repeat(95) + "\n");
        
        for (GiaoDich gd : transactions) {
            writer.write(String.format(format,
                gd.getMaGiaoDich(),
                gd.getNgayGiaoDich().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                String.format("%,d", gd.getDonGia().longValue()),
                gd.getSoLuong(),
                gd.getLoaiGiaoDich(),
                getTransactionDetail(gd),
                String.format("%,d", gd.tinhThanhTien().longValue())
            ));
        }
        
        writer.write("-".repeat(95) + "\n");
        writer.write(String.format("Tổng số giao dịch: %d\n", transactions.size()));
    }
    
    private String getTransactionDetail(GiaoDich gd) {
        if (gd instanceof GiaoDichVang) {
            return ((GiaoDichVang) gd).getLoaiVang();
        } else if (gd instanceof GiaoDichTienTe) {
            GiaoDichTienTe gdtt = (GiaoDichTienTe) gd;
            if ("VND".equals(gdtt.getLoaiTien())) {
                return gdtt.getLoaiTien();
            } else {
                return gdtt.getLoaiTien() + " (" + gdtt.getTiGia() + ")";
            }
        }
        return "";
    }
    
    private void addStatistics(FileWriter writer, boolean isCSV) throws IOException {
        try {
            ThongKeDTO thongKe = quanLyGiaoDich.getTongSoLuongTheoLoai();
            
            if (isCSV) {
                writer.write("Loại thống kê,Giá trị\n");
                writer.write(String.format("Tổng giao dịch vàng,%d\n", thongKe.getTongSoLuongVang()));
                writer.write(String.format("Tổng giao dịch tiền tệ,%d\n", thongKe.getTongSoLuongTienTe()));
                writer.write(String.format("Tổng doanh thu vàng,%s\n", thongKe.getTongThanhTienVang()));
                writer.write(String.format("Tổng doanh thu tiền tệ,%s\n", thongKe.getTongThanhTienTienTe()));
                writer.write(String.format("Tổng doanh thu,%s\n", thongKe.getTongThanhTienTatCa()));
                writer.write(String.format("Trung bình thành tiền tiền tệ,%s\n", thongKe.getTrungBinhThanhTienTienTe()));
                writer.write(String.format("Giao dịch đơn giá > 1 tỷ,%d\n", thongKe.getSoGiaoDichDonGiaLonHon1Ty()));
            } else {
                writer.write(String.format("Tổng số giao dịch vàng: %,d\n", thongKe.getTongSoLuongVang()));
                writer.write(String.format("Tổng số giao dịch tiền tệ: %,d\n", thongKe.getTongSoLuongTienTe()));
                writer.write(String.format("Tổng doanh thu vàng: %,d VND\n", thongKe.getTongThanhTienVang().longValue()));
                writer.write(String.format("Tổng doanh thu tiền tệ: %,d VND\n", thongKe.getTongThanhTienTienTe().longValue()));
                writer.write(String.format("Tổng doanh thu: %,d VND\n", thongKe.getTongThanhTienTatCa().longValue()));
                writer.write(String.format("Trung bình thành tiền tiền tệ: %,d VND\n", thongKe.getTrungBinhThanhTienTienTe().longValue()));
                writer.write(String.format("Số giao dịch đơn giá > 1 tỷ: %,d\n", thongKe.getSoGiaoDichDonGiaLonHon1Ty()));
            }
        } catch (Exception e) {
            writer.write("Lỗi khi lấy thống kê: " + e.getMessage() + "\n");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame();
        });
    }
}