package services;

import models.User;
import utils.CSVManager;
import utils.IDGenerator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for handling authentication operations
 */
public class AuthenticationService {

    private static final String USERS_FILE = "data/users.csv";
    private User currentUser = null;

    /**
     * Authenticates user with email and password
     */
    public User login(String email, String password) {
        List<String[]> users = CSVManager.readCSV(USERS_FILE);

        for (String[] row : users) {
            if (row.length >= 2 && row[1].equalsIgnoreCase(email)) {
                User user = new User(row);
                if (user.verifyPassword(password)) {
                    this.currentUser = user;
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Creates a new user (Admin only)
     */
    public boolean createUser(String email, String password, String role) {
        // Check if email already exists
        if (userExists(email)) {
            return false;
        }

        List<String[]> users = CSVManager.readCSV(USERS_FILE);
        
        // Generate new user ID
        int nextSequence = 1;
        if (!users.isEmpty()) {
            String lastID = users.get(users.size() - 1)[0];
            nextSequence = IDGenerator.getNextSequence(lastID, "USR");
        }
        
        String userID = String.format("USR%03d", nextSequence);
        String createdDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        User newUser = new User(userID, email, password, role, createdDate);
        users.add(newUser.toCSVRow());

        return CSVManager.writeCSV(USERS_FILE, users);
    }

    /**
     * Changes password for current user (must be different from old password)
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }

        // Verify old password matches
        if (!currentUser.verifyPassword(oldPassword)) {
            return false;
        }

        // Check if new password is different from old password
        if (oldPassword.equals(newPassword)) {
            return false;  // New password must be different
        }

        List<String[]> users = CSVManager.readCSV(USERS_FILE);
        currentUser.setPassword(newPassword);

        if (CSVManager.updateRow(users, 0, currentUser.getUserID(), currentUser.toCSVRow())) {
            return CSVManager.writeCSV(USERS_FILE, users);
        }
        return false;
    }

    /**
     * Checks if email already exists
     */
    public boolean userExists(String email) {
        List<String[]> users = CSVManager.readCSV(USERS_FILE);
        return CSVManager.valueExists(users, 1, email);
    }

    /**
     * Gets user by email
     */
    public User getUserByEmail(String email) {
        List<String[]> users = CSVManager.readCSV(USERS_FILE);
        String[] row = CSVManager.findRow(users, 1, email);
        return row != null ? new User(row) : null;
    }

    /**
     * Gets user by ID
     */
    public User getUserByID(String userID) {
        List<String[]> users = CSVManager.readCSV(USERS_FILE);
        String[] row = CSVManager.findRow(users, 0, userID);
        return row != null ? new User(row) : null;
    }

    /**
     * Gets current authenticated user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Logs out current user
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Checks if user has permission for an action
     */
    public boolean hasPermission(String action) {
        if (currentUser == null) {
            return false;
        }

        String role = currentUser.getRole();
        
        switch (action) {
            // Admin permissions
            case "CREATE_USER":
            case "DELETE_USER":
            case "MANAGE_ROLES":
            case "VIEW_ALL_EMPLOYEES":
            case "CREATE_PROJECT":
            case "MANAGE_ATTENDANCE":
            case "MANAGE_LEAVES":
            case "VIEW_REPORTS":
            case "MANAGE_RESOURCES":
                return role.equals("ADMIN");

            // Manager permissions
            case "CREATE_PROJECT_MANAGER":
            case "MANAGE_TEAM":
            case "ASSIGN_RESOURCES":
            case "VIEW_TEAM_ATTENDANCE":
            case "APPROVE_LEAVES":
            case "VIEW_TEAM_REPORTS":
                return role.equals("MANAGER") || role.equals("ADMIN");

            // Employee permissions
            case "VIEW_PROFILE":
            case "CHANGE_PASSWORD":
            case "REQUEST_LEAVE":
            case "VIEW_ATTENDANCE":
            case "VIEW_ASSIGNMENTS":
                return role.equals("EMPLOYEE") || role.equals("MANAGER") || role.equals("ADMIN");

            default:
                return false;
        }
    }

    /**
     * Verifies if current user has required role
     */
    public boolean hasRole(String requiredRole) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole().equals(requiredRole);
    }

    /**
     * Gets all users (Admin only)
     */
    public List<String[]> getAllUsers() {
        if (!hasPermission("VIEW_ALL_EMPLOYEES")) {
            return null;
        }
        return CSVManager.readCSV(USERS_FILE);
    }

    /**
     * Updates user role (Admin only)
     */
    public boolean updateUserRole(String userID, String newRole) {
        if (!hasPermission("MANAGE_ROLES")) {
            return false;
        }

        List<String[]> users = CSVManager.readCSV(USERS_FILE);
        String[] row = CSVManager.findRow(users, 0, userID);

        if (row != null) {
            User user = new User(row);
            user.setRole(newRole);
            if (CSVManager.updateRow(users, 0, userID, user.toCSVRow())) {
                return CSVManager.writeCSV(USERS_FILE, users);
            }
        }
        return false;
    }

    /**
     * Initializes system with default admin user if no users exist
     */
    public static void initializeSystem() {
        List<String[]> users = CSVManager.readCSV(USERS_FILE);
        
        if (users.isEmpty()) {
            // Create default admin user
            AuthenticationService authService = new AuthenticationService();
            authService.createUser("admin@tcs.com", "Admin@123", "ADMIN");
        }
    }
}
