package utils;

import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

/**
 * Utility class for input validation
 */
public class ValidationUtils {
    
    // Constants for validation patterns
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^[0-9]{10,11}$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";
    
    // Constants for minimum lengths
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 20;
    
    // Placeholder texts
    public static final String PLACEHOLDER_USERNAME = "Nhập tên đăng nhập";
    public static final String PLACEHOLDER_ADDRESS = "Nhập địa chỉ";
    public static final String PLACEHOLDER_EMAIL = "example@email.com";
    public static final String PLACEHOLDER_PHONE = "Số điện thoại (10-11 số)";
    
    /**
     * Validates email format
     * @param email Email string to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email.trim());
    }
    
    /**
     * Validates phone number format (10-11 digits)
     * @param phone Phone number string to validate
     * @return true if phone is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String cleanPhone = phone.trim().replaceAll("\\s+", "");
        return Pattern.matches(PHONE_REGEX, cleanPhone);
    }
    
    /**
     * Validates username format (3-20 characters, alphanumeric and underscore only)
     * @param username Username string to validate
     * @return true if username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String trimmedUsername = username.trim();
        return trimmedUsername.length() >= MIN_USERNAME_LENGTH && 
               trimmedUsername.length() <= MAX_USERNAME_LENGTH &&
               Pattern.matches(USERNAME_REGEX, trimmedUsername);
    }
    
    /**
     * Validates password length
     * @param password Password string to validate
     * @return true if password meets minimum length requirement
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }
    
    /**
     * Validates password from JPasswordField
     * @param passwordField JPasswordField to validate
     * @return true if password meets minimum length requirement
     */
    public static boolean isValidPassword(JPasswordField passwordField) {
        if (passwordField == null) return false;
        char[] password = passwordField.getPassword();
        boolean isValid = password.length >= MIN_PASSWORD_LENGTH;
        // Clear password array for security
        java.util.Arrays.fill(password, ' ');
        return isValid;
    }
    
    /**
     * Checks if a text field is empty or contains placeholder text
     * @param field JTextField to check
     * @return true if field is empty or contains placeholder
     */
    public static boolean isFieldEmpty(JTextField field) {
        if (field == null) return true;
        
        String text = field.getText().trim();
        return text.isEmpty() || 
               text.equals(PLACEHOLDER_USERNAME) || 
               text.equals(PLACEHOLDER_ADDRESS) || 
               text.equals(PLACEHOLDER_EMAIL) || 
               text.equals(PLACEHOLDER_PHONE);
    }
    
    /**
     * Gets clean text from field (removes placeholder if present)
     * @param field JTextField to get text from
     * @return Clean text or empty string if field contains placeholder
     */
    public static String getCleanText(JTextField field) {
        if (field == null) return "";
        
        String text = field.getText().trim();
        if (text.equals(PLACEHOLDER_USERNAME) || 
            text.equals(PLACEHOLDER_ADDRESS) || 
            text.equals(PLACEHOLDER_EMAIL) || 
            text.equals(PLACEHOLDER_PHONE)) {
            return "";
        }
        return text;
    }
    
    /**
     * Validates address field
     * @param address Address string to validate
     * @return true if address is not empty and has reasonable length
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        String trimmedAddress = address.trim();
        return trimmedAddress.length() >= 5 && trimmedAddress.length() <= 200;
    }
    
    /**
     * Comprehensive validation for registration form
     * @param username Username to validate
     * @param password Password to validate
     * @param address Address to validate
     * @param email Email to validate
     * @param phone Phone to validate
     * @return ValidationResult containing validation status and error messages
     */
    public static ValidationResult validateRegistrationForm(String username, String password, 
            String address, String email, String phone) {
        
        ValidationResult result = new ValidationResult();
        StringBuilder errors = new StringBuilder();
        
        // Validate username
        if (!isValidUsername(username)) {
            if (username == null || username.trim().isEmpty()) {
                errors.append("- Tên đăng nhập không được để trống\n");
            } else if (username.trim().length() < MIN_USERNAME_LENGTH) {
                errors.append("- Tên đăng nhập phải có ít nhất ").append(MIN_USERNAME_LENGTH).append(" ký tự\n");
            } else if (username.trim().length() > MAX_USERNAME_LENGTH) {
                errors.append("- Tên đăng nhập không được quá ").append(MAX_USERNAME_LENGTH).append(" ký tự\n");
            } else {
                errors.append("- Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới\n");
            }
        }
        
        // Validate password
        if (!isValidPassword(password)) {
            errors.append("- Mật khẩu phải có ít nhất ").append(MIN_PASSWORD_LENGTH).append(" ký tự\n");
        }
        
        // Validate address
        if (!isValidAddress(address)) {
            errors.append("- Địa chỉ phải có từ 5-200 ký tự\n");
        }
        
        // Validate email
        if (!isValidEmail(email)) {
            errors.append("- Email không hợp lệ\n");
        }
        
        // Validate phone
        if (!isValidPhone(phone)) {
            errors.append("- Số điện thoại không hợp lệ (phải có 10-11 số)\n");
        }
        
        result.setValid(errors.length() == 0);
        result.setErrorMessage(errors.toString());
        
        return result;
    }
    
    /**
     * Result class for validation operations
     */
    public static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        
        public ValidationResult() {
            this.valid = true;
            this.errorMessage = "";
        }
        
        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}