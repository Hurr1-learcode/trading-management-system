// Use Case cho việc submit form thêm giao dịch
package usecase;

import dto.GiaoDichFormDTO;
import exception.BusinessException;
import exception.ValidationException;
import model.GiaoDich;
import service.QuanLyGiaoDich;
import utils.ValidationUtil;

/**
 * Use Case: Submit Add Transaction 
 * Theo Clean Architecture pattern
 */
public class SubmitAddTransactionUseCase {
    
    private final QuanLyGiaoDich quanLyGiaoDich;
    
    public SubmitAddTransactionUseCase(QuanLyGiaoDich quanLyGiaoDich) {
        this.quanLyGiaoDich = quanLyGiaoDich;
    }
    
    /**
     * Execute use case: Submit form thêm giao dịch
     * @param formDTO dữ liệu form đã điền
     * @return GiaoDich đã được tạo và lưu
     * @throws ValidationException khi dữ liệu không hợp lệ
     * @throws BusinessException khi vi phạm business rules
     */
    public GiaoDich execute(GiaoDichFormDTO formDTO) throws ValidationException, BusinessException {
        // 1. Validate form data
        validateFormData(formDTO);
        
        // 2. Validate business rules
        validateBusinessRules(formDTO);
        
        // 3. Save transaction via service
        return quanLyGiaoDich.add(formDTO);
    }
    
    /**
     * Validate form data
     */
    private void validateFormData(GiaoDichFormDTO formDTO) throws ValidationException {
        // Validate mã giao dịch
        ValidationUtil.validateStringLength(formDTO.getMaGiaoDich(), 20, "Mã giao dịch");
        
        // Validate ngày giao dịch
        ValidationUtil.validateNotNull(formDTO.getNgayGiaoDich(), "Ngày giao dịch");
        
        // Validate đơn giá
        ValidationUtil.validatePositive(formDTO.getDonGia(), "Đơn giá");
        
        // Validate số lượng
        ValidationUtil.validatePositive(formDTO.getSoLuong(), "Số lượng");
        
        // Validate loại giao dịch
        if (formDTO.getLoaiGiaoDich() == null || formDTO.getLoaiGiaoDich().trim().isEmpty()) {
            throw new ValidationException("Loại giao dịch không được để trống", "loaiGiaoDich");
        }
        
        // Validate specific fields based on transaction type
        if ("VANG".equals(formDTO.getLoaiGiaoDich())) {
            validateGoldTransactionFields(formDTO);
        } else if ("TIEN_TE".equals(formDTO.getLoaiGiaoDich())) {
            validateCurrencyTransactionFields(formDTO);
        } else {
            throw new ValidationException("Loại giao dịch không hợp lệ: " + formDTO.getLoaiGiaoDich(), "loaiGiaoDich");
        }
    }
    
    /**
     * Validate gold transaction specific fields
     */
    private void validateGoldTransactionFields(GiaoDichFormDTO formDTO) throws ValidationException {
        ValidationUtil.validateStringLength(formDTO.getLoaiVang(), 50, "Loại vàng");
    }
    
    /**
     * Validate currency transaction specific fields
     */
    private void validateCurrencyTransactionFields(GiaoDichFormDTO formDTO) throws ValidationException {
        ValidationUtil.validateStringLength(formDTO.getLoaiTien(), 10, "Loại tiền");
        ValidationUtil.validatePositive(formDTO.getTiGia(), "Tỉ giá");
    }
    
    /**
     * Validate business rules
     */
    private void validateBusinessRules(GiaoDichFormDTO formDTO) throws BusinessException {
        // Business rule: Số lượng vàng không được vượt quá 1000 chỉ
        if ("VANG".equals(formDTO.getLoaiGiaoDich()) && formDTO.getSoLuong() > 1000) {
            throw new BusinessException("Số lượng vàng không được vượt quá 1000 chỉ", "INVALID_GOLD_QUANTITY");
        }
        
        // Business rule: Số lượng tiền tệ không được vượt quá 10,000,000
        if ("TIEN_TE".equals(formDTO.getLoaiGiaoDich()) && formDTO.getSoLuong() > 10000000) {
            throw new BusinessException("Số lượng tiền tệ không được vượt quá 10,000,000", "INVALID_CURRENCY_QUANTITY");
        }
        
        // Business rule: Tỉ giá phải hợp lý (0.01 - 100,000)
        if ("TIEN_TE".equals(formDTO.getLoaiGiaoDich()) && formDTO.getTiGia() != null) {
            if (formDTO.getTiGia().doubleValue() < 0.01 || formDTO.getTiGia().doubleValue() > 100000) {
                throw new BusinessException("Tỉ giá phải trong khoảng 0.01 - 100,000", "INVALID_EXCHANGE_RATE");
            }
        }
    }
    
    /**
     * Kiểm tra xem form data có hợp lệ cơ bản không
     */
    public boolean isValidFormData(GiaoDichFormDTO formDTO) {
        try {
            validateFormData(formDTO);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }
    
    /**
     * Kiểm tra business rules
     */
    public boolean isValidBusinessRules(GiaoDichFormDTO formDTO) {
        try {
            validateBusinessRules(formDTO);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
}
