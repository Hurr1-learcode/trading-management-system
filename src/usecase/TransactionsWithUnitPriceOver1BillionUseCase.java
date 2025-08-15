package usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import exception.BusinessException;
import exception.ValidationException;
import model.GiaoDich;
import service.QuanLyGiaoDich;

/**
 * Use Case: Transactions With Unit Price Over 1 Billion
 * Theo Clean Architecture pattern
 */
public class TransactionsWithUnitPriceOver1BillionUseCase {
    
    private final QuanLyGiaoDich quanLyGiaoDich;
    
    public TransactionsWithUnitPriceOver1BillionUseCase(QuanLyGiaoDich quanLyGiaoDich) {
        this.quanLyGiaoDich = quanLyGiaoDich;
    }
    
    /**
     * Execute get high value transactions use case
     * @return List of transactions with unit price > 1 billion
     * @throws ValidationException Nếu validation fails
     * @throws BusinessException Nếu business rules violation
     */
    public List<GiaoDich> execute() throws ValidationException, BusinessException {
        
        // Step 1: Validate business rules
        validateBusinessRules();
        
        // Step 2: Get high value transactions
        List<GiaoDich> highValueTransactions = getHighValueTransactions();
        
        // Step 3: Apply additional filtering and validation
        return processHighValueTransactions(highValueTransactions);
    }
    
    /**
     * Execute with custom threshold
     * @param threshold Custom threshold value
     * @return List of transactions with unit price > threshold
     * @throws ValidationException Nếu validation fails
     * @throws BusinessException Nếu business rules violation
     */
    public List<GiaoDich> execute(BigDecimal threshold) throws ValidationException, BusinessException {
        
        // Step 1: Validate input parameters
        validateThreshold(threshold);
        
        // Step 2: Validate business rules
        validateBusinessRules();
        
        // Step 3: Get transactions above threshold
        List<GiaoDich> highValueTransactions = getTransactionsAboveThreshold(threshold);
        
        // Step 4: Apply additional filtering and validation
        return processHighValueTransactions(highValueTransactions);
    }
    
    /**
     * Validate threshold parameter
     */
    private void validateThreshold(BigDecimal threshold) throws ValidationException {
        if (threshold == null) {
            throw new ValidationException("Ngưỡng không được null", "NULL_THRESHOLD");
        }
        
        if (threshold.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Ngưỡng phải lớn hơn 0", "INVALID_THRESHOLD");
        }
        
        // Business rule: Threshold should be reasonable (not too small or too large)
        BigDecimal minThreshold = new BigDecimal("1000"); // 1,000 VND
        BigDecimal maxThreshold = new BigDecimal("1000000000000"); // 1 trillion VND
        
        if (threshold.compareTo(minThreshold) < 0) {
            throw new ValidationException("Ngưỡng quá nhỏ (tối thiểu 1,000 VND)", "THRESHOLD_TOO_SMALL");
        }
        
        if (threshold.compareTo(maxThreshold) > 0) {
            throw new ValidationException("Ngưỡng quá lớn (tối đa 1,000 tỷ VND)", "THRESHOLD_TOO_LARGE");
        }
    }
    
    /**
     * Validate business rules for high value transaction retrieval
     */
    private void validateBusinessRules() throws BusinessException {
        try {
            // Business rule: Ensure service is available
            if (quanLyGiaoDich == null) {
                throw new BusinessException("Service không khả dụng", "SERVICE_UNAVAILABLE");
            }
            
            // Business rule: Check if system has any transactions
            long totalTransactions = quanLyGiaoDich.getTotalTransactionCount();
            if (totalTransactions == 0) {
                throw new BusinessException("Không có giao dịch nào trong hệ thống", "NO_TRANSACTIONS");
            }
            
            // Business rule: System should have reasonable number of transactions for analysis
            if (totalTransactions < 10) {
                // This is a warning, not an error - we can still proceed
                // Log or handle as needed
            }
            
        } catch (Exception ex) {
            if (ex instanceof BusinessException) {
                throw ex;
            }
            throw new BusinessException("Lỗi khi kiểm tra business rules: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Get transactions with unit price > 1 billion
     */
    private List<GiaoDich> getHighValueTransactions() throws BusinessException {
        try {
            return quanLyGiaoDich.getDonGiaLonHon1Ty();
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi lấy giao dịch đơn giá cao: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Get transactions above custom threshold
     */
    private List<GiaoDich> getTransactionsAboveThreshold(BigDecimal threshold) throws BusinessException {
        try {
            return quanLyGiaoDich.findByDonGiaGreaterThan(threshold);
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi lấy giao dịch theo ngưỡng: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Process and validate high value transactions
     */
    private List<GiaoDich> processHighValueTransactions(List<GiaoDich> transactions) throws BusinessException {
        if (transactions == null) {
            throw new BusinessException("Danh sách giao dịch null", "NULL_TRANSACTION_LIST");
        }
        
        // Additional business logic: Filter out invalid transactions
        List<GiaoDich> validTransactions = transactions.stream()
                .filter(this::isValidTransaction)
                .collect(Collectors.toList());
        
        // Sort by unit price descending for better user experience
        validTransactions.sort((gd1, gd2) -> gd2.getDonGia().compareTo(gd1.getDonGia()));
        
        return validTransactions;
    }
    
    /**
     * Validate individual transaction
     */
    private boolean isValidTransaction(GiaoDich giaoDich) {
        if (giaoDich == null) {
            return false;
        }
        
        // Check required fields
        if (giaoDich.getMaGiaoDich() == null || giaoDich.getMaGiaoDich().trim().isEmpty()) {
            return false;
        }
        
        if (giaoDich.getDonGia() == null || giaoDich.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        if (giaoDich.getSoLuong() <= 0) {
            return false;
        }
        
        if (giaoDich.getNgayGiaoDich() == null) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get count of high value transactions
     */
    public long getHighValueTransactionCount() throws BusinessException {
        try {
            List<GiaoDich> highValueTransactions = execute();
            return highValueTransactions.size();
        } catch (ValidationException | BusinessException ex) {
            throw new BusinessException("Lỗi khi đếm giao dịch đơn giá cao: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Get count of transactions above threshold
     */
    public long getTransactionCountAboveThreshold(BigDecimal threshold) throws BusinessException {
        try {
            List<GiaoDich> transactions = execute(threshold);
            return transactions.size();
        } catch (ValidationException | BusinessException ex) {
            throw new BusinessException("Lỗi khi đếm giao dịch theo ngưỡng: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Get total value of high value transactions
     */
    public BigDecimal getTotalValueOfHighValueTransactions() throws BusinessException {
        try {
            List<GiaoDich> highValueTransactions = execute();
            return highValueTransactions.stream()
                    .map(GiaoDich::tinhThanhTien)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (ValidationException | BusinessException ex) {
            throw new BusinessException("Lỗi khi tính tổng giá trị giao dịch đơn giá cao: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Get average unit price of high value transactions
     */
    public BigDecimal getAverageUnitPriceOfHighValueTransactions() throws BusinessException {
        try {
            List<GiaoDich> highValueTransactions = execute();
            if (highValueTransactions.isEmpty()) {
                return BigDecimal.ZERO;
            }
            
            BigDecimal totalPrice = highValueTransactions.stream()
                    .map(GiaoDich::getDonGia)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            return totalPrice.divide(BigDecimal.valueOf(highValueTransactions.size()), 2, RoundingMode.HALF_UP);
        } catch (ValidationException | BusinessException ex) {
            throw new BusinessException("Lỗi khi tính đơn giá trung bình: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Check if there are any high value transactions
     */
    public boolean hasHighValueTransactions() {
        try {
            return getHighValueTransactionCount() > 0;
        } catch (BusinessException ex) {
            return false;
        }
    }
    
    /**
     * Get validation error if any
     */
    public String getValidationError() {
        try {
            validateBusinessRules();
            return null;
        } catch (BusinessException e) {
            return e.getMessage();
        }
    }
    
    /**
     * Get validation error for threshold
     */
    public String getValidationError(BigDecimal threshold) {
        try {
            validateThreshold(threshold);
            validateBusinessRules();
            return null;
        } catch (ValidationException | BusinessException e) {
            return e.getMessage();
        }
    }
}
