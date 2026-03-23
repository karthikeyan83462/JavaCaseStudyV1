package services;

import models.Employee;
import models.Attendance;
import models.LeaveRequest;
import models.Project;
import models.ResourceAssignment;
import utils.CSVManager;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Service for generating various reports
 */
public class ReportingService {

    private AuthenticationService authService;
    private EmployeeService employeeService;
    private AttendanceService attendanceService;
    private LeaveService leaveService;
    private ProjectService projectService;

    public ReportingService(AuthenticationService authService, EmployeeService employeeService) {
        this.authService = authService;
        this.employeeService = employeeService;
    }
    
    public void setAttendanceService(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
    
    public void setLeaveService(LeaveService leaveService) {
        this.leaveService = leaveService;
    }
    
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Gets employee report
     */
    public Map<String, Object> getEmployeeReport() {
        return getEmployeeSummaryReport();
    }

    /**
     * Gets employee summary report
     */
    public Map<String, Object> getEmployeeSummaryReport() {
        if (!authService.hasPermission("VIEW_REPORTS")) {
            return null;
        }

        Map<String, Object> report = new HashMap<>();
        List<Employee> allEmployees = employeeService.getAllEmployees();
        
        if (allEmployees == null) {
            return null;
        }
        
        int totalEmployees = allEmployees.size();
        int activeEmployees = 0;
        double totalSalary = 0;
        Map<String, Integer> deptCount = new HashMap<>();
        Map<String, Integer> desigCount = new HashMap<>();
        
        for (Employee emp : allEmployees) {
            if (emp.getStatus().equals("ACTIVE")) {
                activeEmployees++;
            }
            try {
                totalSalary += Double.parseDouble(emp.getSalary());
            } catch (NumberFormatException e) {
                // Skip invalid salary
            }
            deptCount.put(emp.getDepartment(), deptCount.getOrDefault(emp.getDepartment(), 0) + 1);
            desigCount.put(emp.getDesignation(), desigCount.getOrDefault(emp.getDesignation(), 0) + 1);
        }
        
        double avgSalary = totalEmployees > 0 ? totalSalary / totalEmployees : 0;
        
        report.put("totalEmployees", totalEmployees);
        report.put("activeEmployees", activeEmployees);
        report.put("inactiveEmployees", totalEmployees - activeEmployees);
        report.put("totalSalary", String.format("%.2f", totalSalary));
        report.put("averageSalary", String.format("%.2f", avgSalary));
        report.put("totalDepartments", deptCount.size());
        report.put("departmentBreakdown", deptCount);
        report.put("designationBreakdown", desigCount);

        return report;
    }

    /**
     * Gets project summary report with detailed metrics
     */
    public Map<String, Object> getProjectReport() {
        if (!authService.hasPermission("VIEW_REPORTS")) {
            return null;
        }
        
        Map<String, Object> report = new HashMap<>();
        List<String[]> projects = CSVManager.readCSV("data/projects.csv");
        
        if (projects == null || projects.isEmpty()) {
            report.put("totalProjects", 0);
            report.put("activeProjects", 0);
            report.put("completedProjects", 0);
            return report;
        }
        
        int totalProjects = projects.size();
        int activeProjects = 0;
        int completedProjects = 0;
        int onHoldProjects = 0;
        double totalBudget = 0;
        
        Map<String, Integer> statusCount = new HashMap<>();
        
        for (String[] row : projects) {
            if (row.length >= 6) {
                String status = row[5];
                statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
                
                if (status.equals("ACTIVE")) activeProjects++;
                else if (status.equals("COMPLETED")) completedProjects++;
                else if (status.equals("ON-HOLD")) onHoldProjects++;
                
                try {
                    totalBudget += Double.parseDouble(row[7]);
                } catch (NumberFormatException e) {
                    // Skip invalid budget
                }
            }
        }
        
        report.put("totalProjects", totalProjects);
        report.put("activeProjects", activeProjects);
        report.put("completedProjects", completedProjects);
        report.put("onHoldProjects", onHoldProjects);
        report.put("totalBudget", String.format("%.2f", totalBudget));
        report.put("statusBreakdown", statusCount);

        return report;
    }

    /**
     * Gets attendance report with monthly summary
     */
    public Map<String, Object> getAttendanceReport() {
        if (!authService.hasPermission("VIEW_REPORTS")) {
            return null;
        }
        
        Map<String, Object> report = new HashMap<>();
        List<String[]> attendances = CSVManager.readCSV("data/attendance.csv");
        
        if (attendances == null || attendances.isEmpty()) {
            report.put("totalRecords", 0);
            report.put("presentCount", 0);
            report.put("absentCount", 0);
            return report;
        }
        
        int totalRecords = attendances.size();
        int presentCount = 0;
        int absentCount = 0;
        int leaveCount = 0;
        Map<String, Integer> monthlyStats = new HashMap<>();
        Map<String, Integer> statusStats = new HashMap<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        YearMonth currentMonth = YearMonth.now();
        int currentMonthRecords = 0;
        
        for (String[] row : attendances) {
            if (row.length >= 3) {
                String status = row[3];
                String date = row[2];
                
                statusStats.put(status, statusStats.getOrDefault(status, 0) + 1);
                
                if (status.equals("PRESENT")) presentCount++;
                else if (status.equals("ABSENT")) absentCount++;
                else if (status.equals("LEAVE")) leaveCount++;
                
                try {
                    LocalDate attDate = LocalDate.parse(date, formatter);
                    YearMonth attMonth = YearMonth.from(attDate);
                    if (attMonth.equals(currentMonth)) {
                        currentMonthRecords++;
                    }
                } catch (Exception e) {
                    // Skip invalid dates
                }
            }
        }
        
        double presentPercentage = totalRecords > 0 ? (presentCount * 100.0) / totalRecords : 0;
        double absentPercentage = totalRecords > 0 ? (absentCount * 100.0) / totalRecords : 0;
        
        report.put("totalRecords", totalRecords);
        report.put("presentCount", presentCount);
        report.put("presentPercentage", String.format("%.2f%%", presentPercentage));
        report.put("absentCount", absentCount);
        report.put("absentPercentage", String.format("%.2f%%", absentPercentage));
        report.put("leaveCount", leaveCount);
        report.put("currentMonthRecords", currentMonthRecords);
        report.put("statusBreakdown", statusStats);

        return report;
    }

    /**
     * Gets leave report with type breakdown and balance
     */
    public Map<String, Object> getLeaveReport() {
        if (!authService.hasPermission("VIEW_REPORTS")) {
            return null;
        }
        
        Map<String, Object> report = new HashMap<>();
        List<String[]> leaves = CSVManager.readCSV("data/leaves.csv");
        
        if (leaves == null || leaves.isEmpty()) {
            report.put("totalLeaves", 0);
            report.put("approvedLeaves", 0);
            report.put("pendingLeaves", 0);
            return report;
        }
        
        int totalLeaves = leaves.size();
        int approvedLeaves = 0;
        int pendingLeaves = 0;
        int rejectedLeaves = 0;
        Map<String, Integer> typeCount = new HashMap<>();
        Map<String, Integer> statusCount = new HashMap<>();
        int totalDays = 0;
        
        for (String[] row : leaves) {
            if (row.length >= 5) {
                String leaveType = row[2];
                String status = row[7];
                
                typeCount.put(leaveType, typeCount.getOrDefault(leaveType, 0) + 1);
                statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
                
                if (status.equals("APPROVED")) approvedLeaves++;
                else if (status.equals("PENDING")) pendingLeaves++;
                else if (status.equals("REJECTED")) rejectedLeaves++;
                
                try {
                    totalDays += Integer.parseInt(row[5]);
                } catch (NumberFormatException e) {
                    // Skip invalid days
                }
            }
        }
        
        report.put("totalLeaves", totalLeaves);
        report.put("approvedLeaves", approvedLeaves);
        report.put("pendingLeaves", pendingLeaves);
        report.put("rejectedLeaves", rejectedLeaves);
        report.put("totalLeaveDays", totalDays);
        report.put("leaveTypeBreakdown", typeCount);
        report.put("leaveStatusBreakdown", statusCount);

        return report;
    }
}
