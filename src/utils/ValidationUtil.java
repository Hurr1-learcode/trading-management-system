// Validation utility
package utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import exception.ValidationException;

public class ValidationUtil {
    
    public static void validateNotNull(Object value, String fieldName) throws ValidationException {
        if (value == null) {
            throw new ValidationException(fieldName + " không được để trống", fieldName);
        }
    }
    
    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        validateNotNull(value, fieldName);
        if (value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " không được để trống", fieldName);
        }
    }
    
    public static void validatePositive(BigDecimal value, String fieldName) throws ValidationException {
        validateNotNull(value, fieldName);
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName + " phải lớn hơn 0", fieldName);
        }
    }
    
    public static void validatePositive(int value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " phải lớn hơn 0", fieldName);
        }
    }
    
    public static LocalDate validateAndParseDate(String dateStr, String fieldName) throws ValidationException {
        validateNotEmpty(dateStr, fieldName);
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " không đúng định dạng (yyyy-MM-dd)", fieldName);
        }
    }
    
    public static BigDecimal validateAndParseBigDecimal(String value, String fieldName) throws ValidationException {
        validateNotEmpty(value, fieldName);
        try {
            BigDecimal result = new BigDecimal(value);
            validatePositive(result, fieldName);
            return result;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " phải là số hợp lệ", fieldName);
        }
    }
    
    public static int validateAndParseInt(String value, String fieldName) throws ValidationException {
        validateNotEmpty(value, fieldName);
        try {
            int result = Integer.parseInt(value);
            validatePositive(result, fieldName);
            return result;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " phải là số nguyên hợp lệ", fieldName);
        }
    }
    
    public static void validateStringLength(String value, int maxLength, String fieldName) throws ValidationException {
        validateNotEmpty(value, fieldName);
        if (value.length() > maxLength) {
            throw new ValidationException(fieldName + " không được vượt quá " + maxLength + " ký tự", fieldName);
        }
    }
}
