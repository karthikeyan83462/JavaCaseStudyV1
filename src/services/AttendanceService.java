package services;

import models.Attendance;
import utils.CSVManager;
import utils.IDGenerator;
import utils.InputValidator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling attendance management operations
 */
public class AttendanceService {

    private static final String ATTENDANCE_FILE = "data/attendance.csv";
    private AuthenticationService authService;
    private EmployeeService employeeService;

    public AttendanceService(AuthenticationService authService, EmployeeService employeeService) {
        this.authService = authService;
        this.employeeService = employeeService;
    }

    /**
     * Marks attendance (simplified version of recordAttendance)
     */
    public boolean markAttendance(String employeeID, String date, String status) {
        return recordAttendance(employeeID, date, status, "", "", "");
    }

    /**
     * Allows an employee to mark their own attendance
     */
    public boolean markOwnAttendance(String employeeID, String date, String status) {
        // Validate employee ID
        if (!InputValidator.isValidString(employeeID) || employeeService.getEmployeeByID(employeeID) == null) {
            return false;
        }
        
        // Validate date
        if (!InputValidator.isValidDate(date)) {
            return false;
        }
        
        // Validate status
        if (!isValidAttendanceStatus(status)) {
            return false;
        }
        
        // Check if date is not in future
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate selectedDate = LocalDate.parse(date, formatter);
            if (selectedDate.isAfter(LocalDate.now())) {
                return false; // Cannot mark attendance for future dates
            }
        } catch (Exception e) {
            return false;
        }
        
        // Check if attendance already exists for this date
        List<String[]> attendances = CSVManager.readCSV(ATTENDANCE_FILE);
        for (String[] row : attendances) {
            if (row.length >= 3 && row[1].equals(employeeID) && row[2].equals(date)) {
                return false; // Already recorded
            }
        }

        // Generate attendance ID
        int nextSequence = 1;
        if (!attendances.isEmpty()) {
            String lastID = attendances.get(attendances.size() - 1)[0];
            nextSequence = IDGenerator.getNextSequence(lastID, "ATT");
        }
        
        String attendanceID = IDGenerator.generateAttendanceID(nextSequence);

        // Create attendance with PENDING status (to be approved by manager)
        Attendance attendance = new Attendance(attendanceID, employeeID, date, status, "", "", "");

        attendances.add(attendance.toCSVRow());
        return CSVManager.writeCSV(ATTENDANCE_FILE, attendances);
    }

    /**
     * Records attendance for an employee
     */
    public boolean recordAttendance(String employeeID, String date, String status,
                                   String checkInTime, String checkOutTime, String remarks) {
        
        if (!authService.hasPermission("MANAGE_ATTENDANCE")) {
            return false;
        }

        // Validate employee ID
        if (!InputValidator.isValidString(employeeID) || employeeService.getEmployeeByID(employeeID) == null) {
            return false;
        }
        
        // Validate date
        if (!InputValidator.isValidDate(date)) {
            return false;
        }
        
        // Validate status
        if (!isValidAttendanceStatus(status)) {
            return false;
        }
        
        // Validate remarks (optional but max length check)
        if (remarks != null && remarks.length() > 200) {
            return false;
        }

        // Check if attendance already exists for this date
        List<String[]> attendances = CSVManager.readCSV(ATTENDANCE_FILE);
        for (String[] row : attendances) {
            if (row.length >= 3 && row[1].equals(employeeID) && row[2].equals(date)) {
                return false; // Already recorded
            }
        }

        // Generate attendance ID
        int nextSequence = 1;
        if (!attendances.isEmpty()) {
            String lastID = attendances.get(attendances.size() - 1)[0];
            nextSequence = IDGenerator.getNextSequence(lastID, "ATT");
        }
        
        String attendanceID = IDGenerator.generateAttendanceID(nextSequence);

        Attendance attendance = new Attendance(attendanceID, employeeID, date, status,
                checkInTime != null ? checkInTime : "", 
                checkOutTime != null ? checkOutTime : "", 
                remarks != null ? remarks : "");

        attendances.add(attendance.toCSVRow());
        return CSVManager.writeCSV(ATTENDANCE_FILE, attendances);
    }
    
    /**
     * Validates attendance status
     */
    private boolean isValidAttendanceStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        String st = status.toUpperCase().trim();
        return st.equals("PRESENT") || st.equals("ABSENT") || st.equals("LEAVE") || st.equals("HOLIDAY");
    }

    /**
     * Gets attendance record by ID
     */
    public Attendance getAttendanceByID(String attendanceID) {
        List<String[]> attendances = CSVManager.readCSV(ATTENDANCE_FILE);
        String[] row = CSVManager.findRow(attendances, 0, attendanceID);
        return row != null ? new Attendance(row) : null;
    }

    /**
     * Gets attendance records for employee on specific date
     */
    public Attendance getEmployeeAttendanceByDate(String employeeID, String date) {
        List<String[]> attendances = CSVManager.readCSV(ATTENDANCE_FILE);

        for (String[] row : attendances) {
            if (row.length >= 3 && row[1].equals(employeeID) && row[2].equals(date)) {
                return new Attendance(row);
            }
        }
        return null;
    }

    /**
     * Gets all attendance records for employee
     */
    public List<Attendance> getEmployeeAttendance(String employeeID) {
        List<String[]> attendances = CSVManager.readCSV(ATTENDANCE_FILE);
        List<Attendance> result = new ArrayList<>();

        for (String[] row : attendances) {
            Attendance att = new Attendance(row);
            if (att.getEmployeeID().equals(employeeID)) {
                result.add(att);
            }
        }
        return result;
    }

    /**
     * Gets attendance records for date range
     */
    public List<Attendance> getEmployeeAttendanceByDateRange(String employeeID, String startDate, String endDate) {
        List<Attendance> allRecords = getEmployeeAttendance(employeeID);
        List<Attendance> result = new ArrayList<>();

        for (Attendance att : allRecords) {
            if (isDateInRange(att.getDate(), startDate, endDate)) {
                result.add(att);
            }
        }
        return result;
    }

    /**
     * Updates attendance record
     */
    public boolean updateAttendance(String attendanceID, String status, String checkInTime,
                                   String checkOutTime, String remarks) {
        
        if (!authService.hasPermission("MANAGE_ATTENDANCE")) {
            return false;
        }

        // Validate attendance ID
        if (!InputValidator.isValidString(attendanceID)) {
            return false;
        }
        
        Attendance attendance = getAttendanceByID(attendanceID);
        if (attendance == null) {
            return false;
        }

        // Validate status if provided
        if (status != null && !status.isEmpty() && !isValidAttendanceStatus(status)) {
            return false;
        }
        
        // Validate remarks if provided
        if (remarks != null && remarks.length() > 200) {
            return false;
        }

        if (status != null && !status.isEmpty()) attendance.setStatus(status);
        if (checkInTime != null && !checkInTime.isEmpty()) attendance.setCheckInTime(checkInTime);
        if (checkOutTime != null && !checkOutTime.isEmpty()) attendance.setCheckOutTime(checkOutTime);
        if (remarks != null && !remarks.isEmpty()) attendance.setRemarks(remarks);

        List<String[]> attendances = CSVManager.readCSV(ATTENDANCE_FILE);
        if (CSVManager.updateRow(attendances, 0, attendanceID, attendance.toCSVRow())) {
            return CSVManager.writeCSV(ATTENDANCE_FILE, attendances);
        }
        return false;
    }

    /**
     * Gets present count for employee in date range
     */
    public int getPresentCount(String employeeID, String startDate, String endDate) {
        List<Attendance> records = getEmployeeAttendanceByDateRange(employeeID, startDate, endDate);
        return (int) records.stream().filter(a -> a.getStatus().equals("PRESENT")).count();
    }

    /**
     * Gets absent count for employee in date range
     */
    public int getAbsentCount(String employeeID, String startDate, String endDate) {
        List<Attendance> records = getEmployeeAttendanceByDateRange(employeeID, startDate, endDate);
        return (int) records.stream().filter(a -> a.getStatus().equals("ABSENT")).count();
    }

    /**
     * Gets half day count for employee in date range
     */
    public int getHalfDayCount(String employeeID, String startDate, String endDate) {
        List<Attendance> records = getEmployeeAttendanceByDateRange(employeeID, startDate, endDate);
        return (int) records.stream().filter(a -> a.getStatus().equals("HALF_DAY")).count();
    }

    /**
     * Gets overall attendance percentage
     */
    public double getAttendancePercentage(String employeeID, String startDate, String endDate) {
        List<Attendance> records = getEmployeeAttendanceByDateRange(employeeID, startDate, endDate);
        if (records.isEmpty()) return 0;

        long presentHalfDays = records.stream()
            .filter(a -> a.getStatus().equals("PRESENT")).count();
        long halfDays = records.stream()
            .filter(a -> a.getStatus().equals("HALF_DAY")).count();

        return ((presentHalfDays * 1.0) + (halfDays * 0.5)) / records.size() * 100;
    }

    /**
     * Checks if date is within range
     */
    private boolean isDateInRange(String date, String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate checkDate = LocalDate.parse(date, formatter);
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            return !checkDate.isBefore(start) && !checkDate.isAfter(end);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets total attendance records
     */
    public int getTotalAttendanceRecords() {
        return CSVManager.readCSV(ATTENDANCE_FILE).size();
    }
}
