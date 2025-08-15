// Use Case cho việc edit giao dịch
package usecase;

import java.util.Optional;

import exception.BusinessException;
import model.GiaoDich;
import model.GiaoDichTienTe;
import model.GiaoDichVang;
import service.QuanLyGiaoDich;
import ui.Panels.CurrencyTransactionFormPanel;
import ui.Panels.GoldTransactionFormPanel;

/**
 * Use Case: Edit Transaction
 * Theo Clean Architecture pattern
 */
public class EditTransactionUseCase {
    
    private final QuanLyGiaoDich quanLyGiaoDich;
    private final GoldTransactionFormPanel goldFormPanel;
    private final CurrencyTransactionFormPanel currencyFormPanel;
    
    public EditTransactionUseCase(QuanLyGiaoDich quanLyGiaoDich,
                                 GoldTransactionFormPanel goldFormPanel,
                                 CurrencyTransactionFormPanel currencyFormPanel) {
        this.quanLyGiaoDich = quanLyGiaoDich;
        this.goldFormPanel = goldFormPanel;
        this.currencyFormPanel = currencyFormPanel;
    }
    
    /**
     * Execute use case: Mở form edit giao dịch
     * @param maGiaoDich mã giao dịch cần edit
     * @throws BusinessException khi không tìm thấy giao dịch
     */
    public void execute(String maGiaoDich) throws BusinessException {
        // 1. Validate input
        validateInput(maGiaoDich);
        
        // 2. Find transaction by ID
        GiaoDich giaoDich = findTransactionById(maGiaoDich);
        
        // 3. Open appropriate edit form based on transaction type
        openEditForm(giaoDich);
    }
    
    /**
     * Validate input parameters
     */
    private void validateInput(String maGiaoDich) throws BusinessException {
        if (maGiaoDich == null || maGiaoDich.trim().isEmpty()) {
            throw new BusinessException("Mã giao dịch không được để trống", "INVALID_INPUT");
        }
    }
    
    /**
     * Find transaction by ID
     */
    private GiaoDich findTransactionById(String maGiaoDich) throws BusinessException {
        try {
            Optional<GiaoDich> optional = quanLyGiaoDich.findById(maGiaoDich);
            
            if (!optional.isPresent()) {
                throw new BusinessException("Không tìm thấy giao dịch với mã: " + maGiaoDich, "NOT_FOUND");
            }
            
            return optional.get();
            
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi tìm giao dịch: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Open appropriate edit form based on transaction type
     */
    private void openEditForm(GiaoDich giaoDich) throws BusinessException {
        try {
            if (giaoDich instanceof GiaoDichVang) {
                openGoldEditForm((GiaoDichVang) giaoDich);
            } else if (giaoDich instanceof GiaoDichTienTe) {
                openCurrencyEditForm((GiaoDichTienTe) giaoDich);
            } else {
                throw new BusinessException("Loại giao dịch không được hỗ trợ: " + giaoDich.getClass().getSimpleName(), "UNSUPPORTED_TYPE");
            }
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi mở form edit: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Open gold transaction edit form
     */
    private void openGoldEditForm(GiaoDichVang giaoDichVang) {
        goldFormPanel.showEditForm(giaoDichVang);
    }
    
    /**
     * Open currency transaction edit form
     */
    private void openCurrencyEditForm(GiaoDichTienTe giaoDichTienTe) {
        currencyFormPanel.showEditForm(giaoDichTienTe);
    }
    
    /**
     * Kiểm tra xem giao dịch có tồn tại không
     */
    public boolean isTransactionExists(String maGiaoDich) {
        try {
            Optional<GiaoDich> optional = quanLyGiaoDich.findById(maGiaoDich);
            return optional.isPresent();
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Lấy thông tin giao dịch (không mở form)
     */
    public Optional<GiaoDich> getTransactionInfo(String maGiaoDich) {
        try {
            return quanLyGiaoDich.findById(maGiaoDich);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
    
    /**
     * Kiểm tra xem giao dịch có phải là loại vàng không
     */
    public boolean isGoldTransaction(String maGiaoDich) {
        try {
            Optional<GiaoDich> optional = quanLyGiaoDich.findById(maGiaoDich);
            return optional.isPresent() && optional.get() instanceof GiaoDichVang;
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Kiểm tra xem giao dịch có phải là loại tiền tệ không
     */
    public boolean isCurrencyTransaction(String maGiaoDich) {
        try {
            Optional<GiaoDich> optional = quanLyGiaoDich.findById(maGiaoDich);
            return optional.isPresent() && optional.get() instanceof GiaoDichTienTe;
        } catch (Exception ex) {
            return false;
        }
    }
}
