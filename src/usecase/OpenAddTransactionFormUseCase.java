// Use Case cho việc mở form thêm giao dịch
package usecase;

import ui.Panels.CurrencyTransactionFormPanel;
import ui.Panels.GoldTransactionFormPanel;

/**
 * Use Case: Mở form thêm giao dịch phù hợp theo loại
 * Theo Clean Architecture pattern
 */
public class OpenAddTransactionFormUseCase {
    
    private GoldTransactionFormPanel goldFormPanel;
    private CurrencyTransactionFormPanel currencyFormPanel;
    
    public OpenAddTransactionFormUseCase(GoldTransactionFormPanel goldFormPanel, 
                                       CurrencyTransactionFormPanel currencyFormPanel) {
        this.goldFormPanel = goldFormPanel;
        this.currencyFormPanel = currencyFormPanel;
    }
    
    /**
     * Execute use case: Mở form thêm giao dịch theo loại
     * @param transactionType "VANG" hoặc "TIEN_TE"
     */
    public void execute(String transactionType) {
        if ("VANG".equals(transactionType)) {
            openGoldTransactionForm();
        } else if ("TIEN_TE".equals(transactionType)) {
            openCurrencyTransactionForm();
        } else {
            throw new IllegalArgumentException("Loại giao dịch không hợp lệ: " + transactionType);
        }
    }
    
    /**
     * Mở form thêm giao dịch vàng
     */
    private void openGoldTransactionForm() {
        goldFormPanel.showAddForm();
    }
    
    /**
     * Mở form thêm giao dịch tiền tệ
     */
    private void openCurrencyTransactionForm() {
        currencyFormPanel.showAddForm();
    }
    
    /**
     * Kiểm tra xem loại giao dịch có hợp lệ không
     */
    public boolean isValidTransactionType(String transactionType) {
        return "VANG".equals(transactionType) || "TIEN_TE".equals(transactionType);
    }
}
