package models;

import utils.PasswordHasher;

/**
 * User entity representing system users
 */
public class User {
    private String userID;
    private String email;
    private String passwordHash;
    private String role; // ADMIN, MANAGER, EMPLOYEE
    private boolean firstTimeLogin;
    private String createdDate;

    /**
     * Constructor for creating a new user
     */
    public User(String userID, String email, String password, String role, String createdDate) {
        this.userID = userID;
        this.email = email;
        this.passwordHash = PasswordHasher.hashPassword(password);
        this.role = role;
        this.firstTimeLogin = true;
        this.createdDate = createdDate;
    }

    /**
     * Constructor from CSV data
     */
    public User(String[] csvRow) {
        if (csvRow.length >= 6) {
            this.userID = csvRow[0];
            this.email = csvRow[1];
            this.passwordHash = csvRow[2];
            this.role = csvRow[3];
            this.firstTimeLogin = csvRow[4].equalsIgnoreCase("true");
            this.createdDate = csvRow[5];
        }
    }

    /**
     * Converts user to CSV row
     */
    public String[] toCSVRow() {
        return new String[]{
            userID,
            email,
            passwordHash,
            role,
            String.valueOf(firstTimeLogin),
            createdDate
        };
    }

    /**
     * Verifies password
     */
    public boolean verifyPassword(String password) {
        return PasswordHasher.verifyPassword(password, passwordHash);
    }

    /**
     * Changes password
     */
    public void setPassword(String newPassword) {
        this.passwordHash = PasswordHasher.hashPassword(newPassword);
        this.firstTimeLogin = false;
    }

    /**
     * Gets password (for validation purposes)
     */
    public String getPassword() {
        return passwordHash;
    }

    /**
     * Checks if password change is required
     */
    public boolean isPasswordChangeRequired() {
        return firstTimeLogin;
    }

    /**
     * Sets whether password change is required
     */
    public void setPasswordChangeRequired(boolean required) {
        this.firstTimeLogin = required;
    }

    // Getters and Setters
    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", firstTimeLogin=" + firstTimeLogin +
                '}';
    }
}
