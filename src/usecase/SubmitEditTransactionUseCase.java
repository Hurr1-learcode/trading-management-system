// Use Case cho việc submit form edit giao dịch
package usecase;

import dto.GiaoDichFormDTO;
import exception.BusinessException;
import exception.ValidationException;
import model.GiaoDich;
import service.QuanLyGiaoDich;
import utils.ValidationUtil;

/**
 * Use Case: Submit Edit Transaction
 * Theo Clean Architecture pattern
 */
public class SubmitEditTransactionUseCase {
    
    private final QuanLyGiaoDich quanLyGiaoDich;
    
    public SubmitEditTransactionUseCase(QuanLyGiaoDich quanLyGiaoDich) {
        this.quanLyGiaoDich = quanLyGiaoDich;
    }
    
    /**
     * Execute use case: Submit form edit giao dịch
     * @param originalMaGiaoDich mã giao dịch gốc cần edit
     * @param formDTO dữ liệu form đã chỉnh sửa
     * @return GiaoDich đã được cập nhật
     * @throws ValidationException khi dữ liệu không hợp lệ
     * @throws BusinessException khi vi phạm business rules
     */
    public GiaoDich execute(String originalMaGiaoDich, GiaoDichFormDTO formDTO) 
            throws ValidationException, BusinessException {
        
        // 1. Validate input parameters
        validateInputParameters(originalMaGiaoDich, formDTO);
        
        // 2. Validate form data
        validateFormData(formDTO);
        
        // 3. Validate business rules for edit
        validateEditBusinessRules(originalMaGiaoDich, formDTO);
        
        // 4. Update transaction via service
        return quanLyGiaoDich.edit(originalMaGiaoDich, formDTO);
    }
    
    /**
     * Validate input parameters
     */
    private void validateInputParameters(String originalMaGiaoDich, GiaoDichFormDTO formDTO) 
            throws ValidationException {
        
        if (originalMaGiaoDich == null || originalMaGiaoDich.trim().isEmpty()) {
            throw new ValidationException("Mã giao dịch gốc không được để trống", "originalMaGiaoDich");
        }
        
        if (formDTO == null) {
            throw new ValidationException("Dữ liệu form không được null", "formDTO");
        }
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
     * Validate business rules specific to edit operation
     */
    private void validateEditBusinessRules(String originalMaGiaoDich, GiaoDichFormDTO formDTO) 
            throws BusinessException {
        
        // Business rule: Kiểm tra giao dịch gốc có tồn tại không
        try {
            if (!quanLyGiaoDich.findById(originalMaGiaoDich).isPresent()) {
                throw new BusinessException("Không tìm thấy giao dịch cần sửa: " + originalMaGiaoDich, "NOT_FOUND");
            }
        } catch (Exception ex) {
            throw new BusinessException("Lỗi khi kiểm tra giao dịch gốc: " + ex.getMessage(), ex);
        }
        
        // Business rule: Nếu thay đổi mã giao dịch, kiểm tra mã mới có trùng không
        if (!originalMaGiaoDich.equals(formDTO.getMaGiaoDich())) {
            try {
                if (quanLyGiaoDich.findById(formDTO.getMaGiaoDich()).isPresent()) {
                    throw new BusinessException("Mã giao dịch mới đã tồn tại: " + formDTO.getMaGiaoDich(), "DUPLICATE_ID");
                }
            } catch (Exception ex) {
                if (ex instanceof BusinessException) {
                    throw ex;
                }
                throw new BusinessException("Lỗi khi kiểm tra mã giao dịch mới: " + ex.getMessage(), ex);
            }
        }
        
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
     * Kiểm tra xem có thể edit giao dịch không
     */
    public boolean canEditTransaction(String maGiaoDich) {
        try {
            return quanLyGiaoDich.findById(maGiaoDich).isPresent();
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Kiểm tra xem form data có hợp lệ cho edit không
     */
    public boolean isValidForEdit(String originalMaGiaoDich, GiaoDichFormDTO formDTO) {
        try {
            validateInputParameters(originalMaGiaoDich, formDTO);
            validateFormData(formDTO);
            validateEditBusinessRules(originalMaGiaoDich, formDTO);
            return true;
        } catch (ValidationException | BusinessException e) {
            return false;
        }
    }
    
    /**
     * Lấy thông tin lỗi validation nếu có
     */
    public String getValidationError(String originalMaGiaoDich, GiaoDichFormDTO formDTO) {
        try {
            validateInputParameters(originalMaGiaoDich, formDTO);
            validateFormData(formDTO);
            validateEditBusinessRules(originalMaGiaoDich, formDTO);
            return null;
        } catch (ValidationException | BusinessException e) {
            return e.getMessage();
        }
    }
}
