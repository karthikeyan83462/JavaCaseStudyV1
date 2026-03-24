package services;

import models.Employee;
import utils.CSVManager;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Service for generating various reports
 */
public class ReportingService {

    private AuthenticationService authService;
    private EmployeeService employeeService;

    public ReportingService(AuthenticationService authService, EmployeeService employeeService) {
        this.authService = authService;
        this.employeeService = employeeService;
    }

    /* ===================== EMPLOYEE REPORT ===================== */

    public Map<String, Object> getEmployeeReport() {
        if (!authService.hasPermission("VIEW_REPORTS")) {
            return null;
        }

        Map<String, Object> report = new HashMap<>();
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees == null) return null;

        int total = employees.size();
        int active = 0;
        double totalSalary = 0;

        Map<String, Integer> deptMap = new HashMap<>();
        Map<String, Integer> desigMap = new HashMap<>();

        for (Employee e : employees) {
            if ("ACTIVE".equals(e.getStatus())) active++;

            try {
                totalSalary += Double.parseDouble(e.getSalary());
            } catch (Exception ignored) {}

            deptMap.put(e.getDepartment(), deptMap.getOrDefault(e.getDepartment(), 0) + 1);
            desigMap.put(e.getDesignation(), desigMap.getOrDefault(e.getDesignation(), 0) + 1);
        }

        report.put("totalEmployees", total);
        report.put("activeEmployees", active);
        report.put("inactiveEmployees", total - active);
        report.put("totalSalary", String.format("%.2f", totalSalary));
        report.put("averageSalary", String.format("%.2f", total > 0 ? totalSalary / total : 0));
        report.put("totalDepartments", deptMap.size());
        report.put("departmentBreakdown", deptMap);
        report.put("designationBreakdown", desigMap);

        return report;
    }

    /* ===================== PROJECT REPORT ===================== */

    public Map<String, Object> getProjectReport() {
        if (!authService.hasPermission("VIEW_REPORTS")) {
            return null;
        }

        Map<String, Object> report = new HashMap<>();
        List<String[]> projects = CSVManager.readCSV("data/projects.csv");

        int total = 0, active = 0, completed = 0, onHold = 0;
        double totalBudget = 0;

        Map<String, Integer> statusMap = new HashMap<>();

        for (String[] row : projects) {
            if (row.length >= 8) {
                total++;
                String status = row[6];
                statusMap.put(status, statusMap.getOrDefault(status, 0) + 1);

                if ("ACTIVE".equals(status)) active++;
                else if ("COMPLETED".equals(status)) completed++;
                else if ("ON-HOLD".equals(status)) onHold++;

                try {
                    totalBudget += Double.parseDouble(row[7]);
                } catch (Exception ignored) {}
            }
        }

        report.put("totalProjects", total);
        report.put("activeProjects", active);
        report.put("completedProjects", completed);
        report.put("onHoldProjects", onHold);
        report.put("totalBudget", String.format("%.2f", totalBudget));
        report.put("statusBreakdown", statusMap);

        return report;
    }

    /* ===================== ATTENDANCE REPORT ===================== */

    public Map<String, Object> getAttendanceReport() {
        if (!authService.hasPermission("VIEW_REPORTS")) {
            return null;
        }

        Map<String, Object> report = new HashMap<>();
        List<String[]> records = CSVManager.readCSV("data/attendance.csv");

        int present = 0, absent = 0, leave = 0;
        int total = records.size();
        int currentMonth = 0;

        Map<String, Integer> statusMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        YearMonth now = YearMonth.now();

        for (String[] row : records) {
            if (row.length >= 4) {
                String status = row[3];
                statusMap.put(status, statusMap.getOrDefault(status, 0) + 1);

                if ("PRESENT".equals(status)) present++;
                else if ("ABSENT".equals(status)) absent++;
                else if ("LEAVE".equals(status)) leave++;

                try {
                    LocalDate d = LocalDate.parse(row[2], formatter);
                    if (YearMonth.from(d).equals(now)) currentMonth++;
                } catch (Exception ignored) {}
            }
        }

        report.put("totalRecords", total);
        report.put("presentCount", present);
        report.put("absentCount", absent);
        report.put("leaveCount", leave);
        report.put("presentPercentage", String.format("%.2f%%", total > 0 ? present * 100.0 / total : 0));
        report.put("absentPercentage", String.format("%.2f%%", total > 0 ? absent * 100.0 / total : 0));
        report.put("currentMonthRecords", currentMonth);
        report.put("statusBreakdown", statusMap);

        return report;
    }

    /* ===================== LEAVE REPORT ===================== */

    public Map<String, Object> getLeaveReport() {
        if (!authService.hasPermission("VIEW_REPORTS")) {
            return null;
        }

        Map<String, Object> report = new HashMap<>();
        List<String[]> leaves = CSVManager.readCSV("data/leaves.csv");

        int approved = 0, pending = 0, rejected = 0, totalDays = 0;

        Map<String, Integer> typeMap = new HashMap<>();
        Map<String, Integer> statusMap = new HashMap<>();

        for (String[] row : leaves) {
            if (row.length >= 8) {
                String type = row[2];
                String status = row[7];

                typeMap.put(type, typeMap.getOrDefault(type, 0) + 1);
                statusMap.put(status, statusMap.getOrDefault(status, 0) + 1);

                if ("APPROVED".equals(status)) approved++;
                else if ("PENDING".equals(status)) pending++;
                else if ("REJECTED".equals(status)) rejected++;

                try {
                    totalDays += Integer.parseInt(row[5]);
                } catch (Exception ignored) {}
            }
        }

        report.put("totalLeaves", leaves.size());
        report.put("approvedLeaves", approved);
        report.put("pendingLeaves", pending);
        report.put("rejectedLeaves", rejected);
        report.put("totalLeaveDays", totalDays);
        report.put("leaveTypeBreakdown", typeMap);
        report.put("leaveStatusBreakdown", statusMap);

        return report;
    }
}