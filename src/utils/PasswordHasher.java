package utils;

/**
 * Utility class for password handling (plain text for demonstration)
 * Simple password verification for demo purposes
 */
public class PasswordHasher {

    /**
     * Stores password as plain text (simplified)
     */
    public static String hashPassword(String password) {
        return password;
    }

    /**
     * Verifies a password against stored password
     */
    public static boolean verifyPassword(String password, String storedPassword) {
        return password != null && password.equals(storedPassword);
    }
}
