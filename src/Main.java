// Main application entry point
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.MainFrame;
import utils.DatabaseUtil;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not set system look and feel", e);
        }
        
        // Initialize database connection
        try {
            DatabaseUtil.getInstance();
            LOGGER.info("Database connection initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database connection", e);
            JOptionPane.showMessageDialog(
                null,
                "Không thể kết nối database!\nVui lòng kiểm tra cấu hình MySQL.",
                "Lỗi Database",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
        
        // Start the application
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame();
                LOGGER.info("Application started successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to start application", e);
                JOptionPane.showMessageDialog(
                    null,
                    "Lỗi khởi tạo ứng dụng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}