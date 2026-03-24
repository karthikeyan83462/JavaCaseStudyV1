package services;

import models.LeaveRequest;
import utils.CSVManager;
import utils.IDGenerator;
import utils.InputValidator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling leave management operations
 */
public class LeaveService {

    private static final String LEAVE_FILE = "data/leaves.csv";
    private static final int CASUAL_LEAVE_LIMIT = 12;
    private static final int SICK_LEAVE_LIMIT = 6;
    private static final int PERSONAL_LEAVE_LIMIT = 90;

    private AuthenticationService authService;
    private EmployeeService employeeService;

    public LeaveService(AuthenticationService authService, EmployeeService employeeService) {
        this.authService = authService;
        this.employeeService = employeeService;
    }

    /**
     * Requests leave (Employee)
     */
    public boolean requestLeave(String employeeID, String leaveType, String startDate,
                               String endDate, String reason) {
        
        if (!authService.hasPermission("REQUEST_LEAVE")) {
            return false;
        }

        // Validate employee ID
        if (!InputValidator.isValidString(employeeID) || employeeService.getEmployeeByID(employeeID) == null) {
            return false;
        }
        
        // Validate leave type
        if (!isValidLeaveType(leaveType)) {
            return false;
        }
        
        // Validate dates
        if (!InputValidator.isValidDate(startDate) || !InputValidator.isValidDate(endDate)) {
            return false;
        }
        
        // Validate reason
        if (!InputValidator.isValidString(reason) || reason.length() > 200) {
            return false;
        }

        // Calculate number of days
        int numberOfDays = calculateDays(startDate, endDate);
        if (numberOfDays <= 0) {
            return false;
        }

        // Check leave balance
        if (!hasLeaveBalance(employeeID, leaveType, numberOfDays)) {
            return false;
        }
        
        // Check for overlapping leave requests
        if (hasOverlappingLeave(employeeID, startDate, endDate)) {
            return false;
        }

        List<String[]> leaves = CSVManager.readCSV(LEAVE_FILE);
        
        // Generate leave ID
        int nextSequence = 1;
        if (!leaves.isEmpty()) {
            String lastID = leaves.get(leaves.size() - 1)[0];
            nextSequence = IDGenerator.getNextSequence(lastID, "LEAVE");
        }
        
        String leaveID = IDGenerator.generateLeaveID(nextSequence);

        LeaveRequest leave = new LeaveRequest(leaveID, employeeID, leaveType, startDate,
                endDate, numberOfDays, reason, "PENDING", "", "");

        leaves.add(leave.toCSVRow());
        return CSVManager.writeCSV(LEAVE_FILE, leaves);
    }
    
    /**
     * Validates leave type
     */
    private boolean isValidLeaveType(String leaveType) {
        if (leaveType == null || leaveType.trim().isEmpty()) {
            return false;
        }
        String type = leaveType.toUpperCase().trim();
        return type.equals("SICK") || type.equals("CASUAL") || type.equals("PERSONAL");
    }
    
    /**
     * Checks for overlapping leave requests
     */
    private boolean hasOverlappingLeave(String employeeID, String startDate, String endDate) {
        List<LeaveRequest> leaves = getEmployeeLeaves(employeeID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        try {
            LocalDate newStart = LocalDate.parse(startDate, formatter);
            LocalDate newEnd = LocalDate.parse(endDate, formatter);
            
            for (LeaveRequest leave : leaves) {
                if (leave.getStatus().equals("PENDING") || leave.getStatus().equals("APPROVED")) {
                    LocalDate existStart = LocalDate.parse(leave.getStartDate(), formatter);
                    LocalDate existEnd = LocalDate.parse(leave.getEndDate(), formatter);
                    
                    // Check for overlap
                    if (!(newEnd.isBefore(existStart) || newStart.isAfter(existEnd))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        
        return false;
    }

    /**
     * Gets leave request by ID
     */
    public LeaveRequest getLeaveByID(String leaveID) {
        List<String[]> leaves = CSVManager.readCSV(LEAVE_FILE);
        String[] row = CSVManager.findRow(leaves, 0, leaveID);
        return row != null ? new LeaveRequest(row) : null;
    }

    /**
     * Gets all leave requests for employee
     */
    public List<LeaveRequest> getEmployeeLeaves(String employeeID) {
        List<String[]> leaves = CSVManager.readCSV(LEAVE_FILE);
        List<LeaveRequest> result = new ArrayList<>();

        for (String[] row : leaves) {
            LeaveRequest leave = new LeaveRequest(row);
            if (leave.getEmployeeID().equals(employeeID)) {
                result.add(leave);
            }
        }
        return result;
    }

    /**
     * Gets pending leave requests (for managers/admins)
     */
    public List<LeaveRequest> getPendingLeaveRequests() {
        List<String[]> leaves = CSVManager.readCSV(LEAVE_FILE);
        List<LeaveRequest> result = new ArrayList<>();

        for (String[] row : leaves) {
            LeaveRequest leave = new LeaveRequest(row);
            if (leave.getStatus().equals("PENDING")) {
                result.add(leave);
            }
        }
        return result;
    }

    /**
     * Approves leave request (Manager/Admin)
     */
    public boolean approveLeave(String leaveID, String approverID, String remarks) {
        if (!authService.hasPermission("APPROVE_LEAVES")) {
            return false;
        }

        // Validate leave ID
        if (!InputValidator.isValidString(leaveID)) {
            return false;
        }
        
        LeaveRequest leave = getLeaveByID(leaveID);
        if (leave == null || !leave.getStatus().equals("PENDING")) {
            return false;
        }

        // Validate approver ID
        if (!InputValidator.isValidString(approverID)) {
            return false;
        }
        
        // Validate remarks (optional but max length check)
        if (remarks != null && remarks.length() > 300) {
            return false;
        }

        leave.setStatus("APPROVED");
        leave.setApprovedBy(approverID);
        leave.setRemarks(remarks != null ? remarks : "");

        List<String[]> leaves = CSVManager.readCSV(LEAVE_FILE);
        if (CSVManager.updateRow(leaves, 0, leaveID, leave.toCSVRow())) {
            return CSVManager.writeCSV(LEAVE_FILE, leaves);
        }
        return false;
    }

    /**
     * Rejects leave request (Manager/Admin)
     */
    public boolean rejectLeave(String leaveID, String approverID, String remarks) {
        if (!authService.hasPermission("APPROVE_LEAVES")) {
            return false;
        }

        // Validate leave ID
        if (!InputValidator.isValidString(leaveID)) {
            return false;
        }

        LeaveRequest leave = getLeaveByID(leaveID);
        if (leave == null || !leave.getStatus().equals("PENDING")) {
            return false;
        }

        // Validate approver ID
        if (!InputValidator.isValidString(approverID)) {
            return false;
        }
        
        // Validate remarks (required for rejection)
        if (!InputValidator.isValidString(remarks) || remarks.length() > 300) {
            return false;
        }

        leave.setStatus("REJECTED");
        leave.setApprovedBy(approverID);
        leave.setRemarks(remarks);

        List<String[]> leaves = CSVManager.readCSV(LEAVE_FILE);
        if (CSVManager.updateRow(leaves, 0, leaveID, leave.toCSVRow())) {
            return CSVManager.writeCSV(LEAVE_FILE, leaves);
        }
        return false;
    }

    /**
     * Approves leave request (alias for approveLeave)
     */
    public boolean approveLeaveRequest(String leaveID, String approverID) {
        return approveLeave(leaveID, approverID, "");
    }

    /**
     * Rejects leave request (alias for rejectLeave)
     */
    public boolean rejectLeaveRequest(String leaveID, String approverID) {
        return rejectLeave(leaveID, approverID, "");
    }

    /**
     * Cancels leave request
     */
    public boolean cancelLeave(String leaveID) {
        LeaveRequest leave = getLeaveByID(leaveID);
        if (leave == null || !leave.getStatus().equals("PENDING")) {
            return false;
        }

        leave.setStatus("CANCELLED");

        List<String[]> leaves = CSVManager.readCSV(LEAVE_FILE);
        if (CSVManager.updateRow(leaves, 0, leaveID, leave.toCSVRow())) {
            return CSVManager.writeCSV(LEAVE_FILE, leaves);
        }
        return false;
    }

    /**
     * Checks if employee has leave balance
     */
    public boolean hasLeaveBalance(String employeeID, String leaveType, int requestedDays) {
        int usedDays = getUsedLeaveDays(employeeID, leaveType);
        int limit = getLeaveLimit(leaveType);
        return (usedDays + requestedDays) <= limit;
    }

    /**
     * Gets used leave days for employee by type
     */
    public int getUsedLeaveDays(String employeeID, String leaveType) {
        List<LeaveRequest> leaves = getEmployeeLeaves(employeeID);
        return (int) leaves.stream()
            .filter(l -> l.getLeaveType().equals(leaveType) && 
                    (l.getStatus().equals("APPROVED") || l.getStatus().equals("PENDING")))
            .mapToInt(LeaveRequest::getNumberOfDays)
            .sum();
    }

    /**
     * Gets remaining leave balance
     */
    public int getRemainingLeaveDays(String employeeID, String leaveType) {
        int limit = getLeaveLimit(leaveType);
        int used = getUsedLeaveDays(employeeID, leaveType);
        return Math.max(0, limit - used);
    }

    /**
     * Gets leave limit for type
     */
    private int getLeaveLimit(String leaveType) {
        switch (leaveType.toUpperCase()) {
            case "CASUAL":
                return CASUAL_LEAVE_LIMIT;
            case "SICK":
                return SICK_LEAVE_LIMIT;
            case "PERSONAL":
                return PERSONAL_LEAVE_LIMIT;
            default:
                return 0;
        }
    }

    /**
     * Calculates number of days between dates (excluding weekends)
     */
    private int calculateDays(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            if (end.isBefore(start)) {
                return 0;
            }

            long days = ChronoUnit.DAYS.between(start, end) + 1; // Inclusive
            return (int) days;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Gets leave requests by status
     */
    public List<LeaveRequest> getLeaveRequestsByStatus(String status) {
        List<String[]> leaves = CSVManager.readCSV(LEAVE_FILE);
        List<LeaveRequest> result = new ArrayList<>();

        for (String[] row : leaves) {
            LeaveRequest leave = new LeaveRequest(row);
            if (leave.getStatus().equals(status)) {
                result.add(leave);
            }
        }
        return result;
    }

    /**
     * Gets total leave requests
     */
    public int getTotalLeaveRequests() {
        return CSVManager.readCSV(LEAVE_FILE).size();
    }
}
