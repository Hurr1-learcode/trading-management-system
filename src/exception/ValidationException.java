// Validation exception
package exception;

public class ValidationException extends Exception {
    private final String field;
    
    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
    }
    
    public String getField() {
        return field;
    }
}
