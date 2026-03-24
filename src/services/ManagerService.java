package services;

import models.Employee;
import models.Attendance;
import models.LeaveRequest;
import models.Project;
import utils.CSVManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for manager-specific operations
 */
public class ManagerService {

    private static final String ATTENDANCE_FILE = "data/attendance.csv";
    private static final String LEAVES_FILE = "data/leaves.csv";
    private static final String PROJECTS_FILE = "data/projects.csv";
    private static final String ASSIGNMENTS_FILE = "data/assignments.csv";

    private AuthenticationService authService;
    private EmployeeService employeeService;

    public ManagerService(AuthenticationService authService, EmployeeService employeeService) {
        this.authService = authService;
        this.employeeService = employeeService;
    }

    /**
     * Gets team members for current manager
     */
    public List<Employee> getTeamMembers() {
        Employee currentManager = getManagerEmployee();
        if (currentManager == null) {
            return new ArrayList<>();
        }

        return employeeService.getTeamMembers(currentManager.getEmployeeID());
    }

    /**
     * Gets team attendance for manager
     */
    public List<Attendance> getTeamAttendance(String date) {
        List<Employee> team = getTeamMembers();
        List<Attendance> teamAttendance = new ArrayList<>();

        List<String[]> allAttendance = CSVManager.readCSV(ATTENDANCE_FILE);

        for (Employee emp : team) {
            for (String[] row : allAttendance) {
                if (row.length > 1 && row[1].equals(emp.getEmployeeID()) && row[2].equals(date)) {
                    teamAttendance.add(new Attendance(row));
                }
            }
        }

        return teamAttendance;
    }

    /**
     * Gets pending leave requests for team
     */
    public List<LeaveRequest> getPendingLeaveRequests() {
        List<Employee> team = getTeamMembers();
        List<LeaveRequest> pendingRequests = new ArrayList<>();

        List<String[]> allLeaves = CSVManager.readCSV(LEAVES_FILE);

        for (Employee emp : team) {
            for (String[] row : allLeaves) {
                if (row.length > 1 && row[1].equals(emp.getEmployeeID()) && row[6].equals("PENDING")) {
                    pendingRequests.add(new LeaveRequest(row));
                }
            }
        }

        return pendingRequests;
    }

    /**
     * Approves leave request
     */
    public boolean approveLeaveRequest(String leaveID, String approvalComments) {
        if (!authService.hasPermission("APPROVE_LEAVES")) {
            return false;
        }

        List<String[]> leaves = CSVManager.readCSV(LEAVES_FILE);
        String[] row = CSVManager.findRow(leaves, 0, leaveID);

        if (row != null) {
            LeaveRequest leave = new LeaveRequest(row);
            leave.setStatus("APPROVED");
            leave.setApprovalComments(approvalComments);

            if (CSVManager.updateRow(leaves, 0, leaveID, leave.toCSVRow())) {
                return CSVManager.writeCSV(LEAVES_FILE, leaves);
            }
        }
        return false;
    }

    /**
     * Rejects leave request
     */
    public boolean rejectLeaveRequest(String leaveID, String rejectionReason) {
        if (!authService.hasPermission("APPROVE_LEAVES")) {
            return false;
        }

        List<String[]> leaves = CSVManager.readCSV(LEAVES_FILE);
        String[] row = CSVManager.findRow(leaves, 0, leaveID);

        if (row != null) {
            LeaveRequest leave = new LeaveRequest(row);
            leave.setStatus("REJECTED");
            leave.setApprovalComments(rejectionReason);

            if (CSVManager.updateRow(leaves, 0, leaveID, leave.toCSVRow())) {
                return CSVManager.writeCSV(LEAVES_FILE, leaves);
            }
        }
        return false;
    }

    /**
     * Gets team projects
     */
    public List<Project> getTeamProjects() {
        Employee manager = getManagerEmployee();
        if (manager == null) {
            return new ArrayList<>();
        }

        List<Project> teamProjects = new ArrayList<>();
        List<String[]> allProjects = CSVManager.readCSV(PROJECTS_FILE);

        for (String[] row : allProjects) {
            Project project = new Project(row);

            if (manager.getEmployeeID().equals(project.getProjectManager())) {
                teamProjects.add(project);
            }
        }

        return teamProjects;
    }

    /**
     * Gets team workload overview
     */
    public String getTeamWorkloadOverview() {
        List<Employee> team = getTeamMembers();
        StringBuilder report = new StringBuilder();

        List<String[]> allAssignments = CSVManager.readCSV(ASSIGNMENTS_FILE);

        report.append("=== Team Workload Overview ===\n");
        report.append(String.format("%-20s | %s\n", "Employee", "Total Allocation"));
        report.append("---------------------------------------\n");

        for (Employee emp : team) {
            double totalAllocation = 0;
            for (String[] assignment : allAssignments) {
                if (assignment.length > 3 && assignment[1].equals(emp.getEmployeeID())) {
                    totalAllocation += Double.parseDouble(assignment[3]);
                }
            }

            report.append(
                    String.format("%-20s | %.1f%%\n", emp.getFirstName() + " " + emp.getLastName(), totalAllocation));
        }

        return report.toString();
    }

    /**
     * Gets manager's own employee record
     */
    private Employee getManagerEmployee() {
        String managerEmail = authService.getCurrentUser().getEmail();
        return employeeService.getEmployeeByEmail(managerEmail);
    }

    /**
     * Helper method to convert raw data to Project objects
     */
    // private List<Project> getProjectObjects(List<String[]> rows) {
    //     List<Project> projects = new ArrayList<>();
    //     for (String[] row : rows) {
    //         projects.add(new Project(row));
    //     }
    //     return projects;
    // }

    /**
     * Checks if user is manager
     */
    public boolean isManager() {
        return authService.hasRole("MANAGER");
    }
}
