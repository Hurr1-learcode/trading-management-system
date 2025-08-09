// Lớp tiện ích cho UI - xử lý message dialogs và formatters
package ui.Utils;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class UIUtils {
    
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Hiển thị thông báo thành công
     */
    public static void showSuccess(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    public static void showError(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Hiển thị thông báo cảnh báo
     */
    public static void showWarning(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Hiển thị hộp thoại xác nhận
     */
    public static boolean showConfirm(JComponent parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(
            parent, 
            message, 
            title, 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Định dạng số tiền
     */
    public static String formatCurrency(Number amount) {
        if (amount == null) return "0";
        return CURRENCY_FORMAT.format(amount);
    }
    
    /**
     * Định dạng ngày để hiển thị
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMAT);
    }
    
    /**
     * Lấy ngày hiện tại dạng chuỗi cho input
     */
    public static String getCurrentDateString() {
        return LocalDate.now().format(INPUT_DATE_FORMAT);
    }
    
    /**
     * Parse ngày từ chuỗi input
     */
    public static LocalDate parseDate(String dateString) throws Exception {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new Exception("Ngày không được để trống");
        }
        try {
            return LocalDate.parse(dateString.trim(), INPUT_DATE_FORMAT);
        } catch (Exception e) {
            throw new Exception("Ngày không đúng định dạng (yyyy-MM-dd)");
        }
    }
    
    /**
     * Làm sạch chuỗi số (loại bỏ dấu phẩy, dấu chấm)
     */
    public static String cleanNumberString(String number) {
        if (number == null) return "0";
        return number.trim().replace(",", "").replace(".", "");
    }
    
    /**
     * Kiểm tra chuỗi có rỗng không
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
