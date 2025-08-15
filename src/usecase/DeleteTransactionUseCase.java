package usecase;

import java.util.Optional;

import exception.BusinessException;
import exception.ValidationException;
import model.GiaoDich;
import service.QuanLyGiaoDich;

/**
 * Use Case: Delete Transaction
 * Theo Clean Architecture pattern
 */
public class DeleteTransactionUseCase {
    
    private final QuanLyGiaoDich quanLyGiaoDich;
    
    public DeleteTransactionUseCase(QuanLyGiaoDich quanLyGiaoDich) {
        this.quanLyGiaoDich = quanLyGiaoDich;
    }
    
    /**
     * Execute delete transaction use case
     * @param maGiaoDich Mã giao dịch cần xóa
     * @throws ValidationException Nếu validation fails
     * @throws BusinessException Nếu business rules violation
     */
    public void execute(String maGiaoDich) throws ValidationException, BusinessException {
        // Step 1: Validate input parameters
        validateInputParameters(maGiaoDich);
        
        // Step 2: Validate business rules
        validateDeleteBusinessRules(maGiaoDich);
        
        // Step 3: Execute delete operation
        executeDelete(maGiaoDich);
    }
    
    /**
     * Validate input parameters
     */
    private void validateInputParameters(String maGiaoDich) throws ValidationException {
        if (maGiaoDich == null || maGiaoDich.trim().isEmpty()) {
            throw new ValidationException("Mã giao dịch không được để trống", "EMPTY_TRANSACTION_ID");
        }
        
        // Validate format
        String trimmedId = maGiaoDich.trim();
        if (trimmedId.length() < 3 || trimmedId.length() > 20) {
            throw new ValidationException("Mã giao dịch phải có độ dài từ 3-20 ký tự", "INVALID_ID_LENGTH");
        }
        
        // Check for valid characters (alphanumeric only)
        if (!trimmedId.matches("^[a-zA-Z0-9]+$")) {
            throw new ValidationException("Mã giao dịch chỉ được chứa chữ cái và số", "INVALID_ID_FORMAT");
        }
    }
    
    /**
     * Validate business rules for delete operation
     */
    private void validateDeleteBusinessRules(String maGiaoDich) throws BusinessException {
        try {
            // Business rule: Transaction must exist
            Optional<GiaoDich> existingTransaction = quanLyGiaoDich.findById(maGiaoDich);
            if (!existingTransaction.isPresent()) {
                throw new BusinessException("Không tìm thấy giao dịch với mã: " + maGiaoDich, "TRANSACTION_NOT_FOUND");
            }
            
            GiaoDich transaction = existingTransaction.get();
            
            // Business rule: Check if transaction can be deleted
            // (You can add more business rules here if needed)
            // For example: Cannot delete transactions older than X days
            // Cannot delete transactions with certain status, etc.
            
            // Business rule: Verify transaction integrity
            if (transaction.getMaGiaoDich() == null || !transaction.getMaGiaoDich().equals(maGiaoDich)) {
                throw new BusinessException("Dữ liệu giao dịch không nhất quán", "DATA_INTEGRITY_ERROR");
            }
            
        } catch (Exception ex) {
            if (ex instanceof BusinessException) {
                throw ex;
            }
            throw new BusinessException("Lỗi khi kiểm tra giao dịch: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Execute the actual delete operation
     */
    private void executeDelete(String maGiaoDich) throws BusinessException {
        try {
            boolean deleted = quanLyGiaoDich.remove(maGiaoDich);
            if (!deleted) {
                throw new BusinessException("Không thể xóa giao dịch", "DELETE_FAILED");
            }
        } catch (Exception ex) {
            if (ex instanceof BusinessException) {
                throw ex;
            }
            throw new BusinessException("Lỗi hệ thống khi xóa giao dịch: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Kiểm tra xem có thể xóa giao dịch không
     */
    public boolean canDeleteTransaction(String maGiaoDich) {
        try {
            validateInputParameters(maGiaoDich);
            validateDeleteBusinessRules(maGiaoDich);
            return true;
        } catch (ValidationException | BusinessException e) {
            return false;
        }
    }
    
    /**
     * Lấy thông tin giao dịch trước khi xóa
     */
    public Optional<GiaoDich> getTransactionInfo(String maGiaoDich) {
        try {
            return quanLyGiaoDich.findById(maGiaoDich);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
    
    /**
     * Lấy thông tin lỗi validation nếu có
     */
    public String getValidationError(String maGiaoDich) {
        try {
            validateInputParameters(maGiaoDich);
            validateDeleteBusinessRules(maGiaoDich);
            return null;
        } catch (ValidationException | BusinessException e) {
            return e.getMessage();
        }
    }
}
