package utils;

import java.util.regex.Pattern;

/**
 * Utility class for comprehensive input validation
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[A-Za-z\\s'-]{1,50}$");

    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates name (letters, spaces, hyphens, apostrophes only)
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validates phone number (10 digits, with validation against repeating patterns)
     */
    public static boolean isValidPhone(String phone, boolean isOptional) {
        if (isOptional && (phone == null || phone.trim().isEmpty())) {
            return true;
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        String cleaned = phone.trim().replaceAll("[^0-9]", "");
        
        // Must be exactly 10 digits
        if (!cleaned.matches("^[0-9]{10}$")) {
            return false;
        }
        
        // Reject patterns with all same digit (0000000000, 1111111111, etc)
        if (cleaned.matches("^(.)\\1{9}$")) {
            return false;
        }
        
        // Reject sequential patterns (0123456789, etc)
        if (isSequential(cleaned)) {
            return false;
        }
        
        return true;
    }

    /**
     * Check if phone number is sequential
     */
    private static boolean isSequential(String phone) {
        for (int i = 0; i < phone.length() - 1; i++) {
            if (Math.abs(phone.charAt(i) - phone.charAt(i + 1)) != 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates password with strong requirements:
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character (!@#$%^&*)
     * - No spaces allowed
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Check minimum length
        if (password.length() < 8) {
            return false;
        }
        
        // Check for spaces
        if (password.contains(" ")) {
            return false;
        }
        
        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        
        // Check for at least one digit
        if (!password.matches(".*[0-9].*")) {
            return false;
        }
        
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*].*")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns password validation error message
     */
    public static String getPasswordValidationMessage() {
        return "Password must contain:\n" +
               "- At least 8 characters\n" +
               "- At least one uppercase letter (A-Z)\n" +
               "- At least one lowercase letter (a-z)\n" +
               "- At least one digit (0-9)\n" +
               "- At least one special character (!@#$%^&*)\n" +
               "- No spaces allowed";
    }

    /**
     * Checks if new password is different from old password
     */
    public static boolean isPasswordDifferent(String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null) {
            return false;
        }
        return !oldPassword.equals(newPassword);
    }

    /**
     * Validates salary (positive number)
     */
    public static boolean isValidSalary(String salary, boolean isOptional) {
        if (isOptional && (salary == null || salary.trim().isEmpty())) {
            return true;
        }
        
        if (salary == null || salary.trim().isEmpty()) {
            return false;
        }
        
        try {
            double value = Double.parseDouble(salary.trim());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates percentage (0-100)
     */
    public static boolean isValidPercentage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            double num = Double.parseDouble(value.trim());
            return num >= 0 && num <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates non-empty string
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates optional field (can be empty or non-empty)
     */
    public static boolean isValidOptional(String value) {
        return true;
    }

    /**
     * Validates integer input
     */
    public static boolean isValidInteger(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates integer within range
     */
    public static boolean isValidInteger(String input, int min, int max) {
        try {
            int value = Integer.parseInt(input.trim());
            return value >= min && value <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates date format (dd-MM-yyyy)
     */
    public static boolean isValidDate(String date) {
        if (date == null || !date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return false;
        }
        try {
            String[] parts = date.split("-");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            Integer.parseInt(parts[2]);  // Validate year format

            if (month < 1 || month > 12) return false;
            if (day < 1 || day > 31) return false;
            if (month == 2 && day > 29) return false;
            if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates string is not empty and not just whitespace
     */
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
