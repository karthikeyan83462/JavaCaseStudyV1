package services;

import models.Project;
import models.ResourceAssignment;
import utils.CSVManager;
import utils.IDGenerator;
import utils.InputValidator;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling project management operations
 */
public class ProjectService {

    private static final String PROJECTS_FILE = "data/projects.csv";
    private static final String ASSIGNMENTS_FILE = "data/assignments.csv";
    private AuthenticationService authService;
    private EmployeeService employeeService;

    public ProjectService(AuthenticationService authService, EmployeeService employeeService) {
        this.authService = authService;
        this.employeeService = employeeService;
    }

    /**
     * Creates a new project (Admin or Manager)
     */
    public boolean createProject(String projectName, String description, String startDate,
                                String endDate, String projectManager, String budget) {
        
        if (!authService.hasPermission("CREATE_PROJECT")) {
            return false;
        }

        // Validate project name
        if (!InputValidator.isValidString(projectName) || projectName.length() > 100) {
            return false;
        }
        
        // Validate description
        if (!InputValidator.isValidString(description)) {
            return false;
        }
        
        // Validate dates
        if (!InputValidator.isValidDate(startDate) || !InputValidator.isValidDate(endDate)) {
            return false;
        }
        
        // Check if end date is after start date
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
            java.time.LocalDate start = java.time.LocalDate.parse(startDate, formatter);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate, formatter);
            if (!end.isAfter(start)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        
        // Validate project manager
        if (!InputValidator.isValidString(projectManager) || employeeService.getEmployeeByID(projectManager) == null) {
            return false;
        }
        
        // Validate budget
        if (!InputValidator.isValidSalary(budget, false)) {
            return false;
        }

        List<String[]> projects = CSVManager.readCSV(PROJECTS_FILE);
        
        // Generate project ID
        int nextSequence = 1;
        if (!projects.isEmpty()) {
            String lastID = projects.get(projects.size() - 1)[0];
            nextSequence = IDGenerator.getNextSequence(lastID, "PROJ");
        }
        
        String projectID = IDGenerator.generateProjectID(nextSequence);

        Project project = new Project(projectID, projectName, description, startDate,
                endDate, "PLANNED", projectManager, budget);

        projects.add(project.toCSVRow());
        return CSVManager.writeCSV(PROJECTS_FILE, projects);
    }

    /**
     * Gets project by ID
     */
    public Project getProjectByID(String projectID) {
        List<String[]> projects = CSVManager.readCSV(PROJECTS_FILE);
        String[] row = CSVManager.findRow(projects, 0, projectID);
        return row != null ? new Project(row) : null;
    }

    /**
     * Gets all projects
     */
    public List<Project> getAllProjects() {
        List<String[]> projects = CSVManager.readCSV(PROJECTS_FILE);
        List<Project> result = new ArrayList<>();

        for (String[] row : projects) {
            result.add(new Project(row));
        }
        return result;
    }

    /**
     * Gets projects by status
     */
    public List<Project> getProjectsByStatus(String status) {
        List<String[]> projects = CSVManager.readCSV(PROJECTS_FILE);
        List<Project> result = new ArrayList<>();

        for (String[] row : projects) {
            Project project = new Project(row);
            if (project.getStatus().equals(status)) {
                result.add(project);
            }
        }
        return result;
    }

    /**
     * Gets projects managed by employee
     */
    public List<Project> getProjectsByManager(String managerEmployeeID) {
        List<String[]> projects = CSVManager.readCSV(PROJECTS_FILE);
        List<Project> result = new ArrayList<>();

        for (String[] row : projects) {
            Project project = new Project(row);
            if (project.getProjectManager().equals(managerEmployeeID)) {
                result.add(project);
            }
        }
        return result;
    }

    /**
     * Updates project information
     */
    public boolean updateProject(String projectID, String projectName, String description,
                                String endDate, String status, String budget) {
        
        if (!authService.hasPermission("CREATE_PROJECT")) {
            return false;
        }

        Project project = getProjectByID(projectID);
        if (project == null) {
            return false;
        }

        if (projectName != null) project.setProjectName(projectName);
        if (description != null) project.setDescription(description);
        if (endDate != null && InputValidator.isValidDate(endDate)) project.setEndDate(endDate);
        if (status != null) project.setStatus(status);
        if (budget != null) project.setBudget(budget);

        List<String[]> projects = CSVManager.readCSV(PROJECTS_FILE);
        if (CSVManager.updateRow(projects, 0, projectID, project.toCSVRow())) {
            return CSVManager.writeCSV(PROJECTS_FILE, projects);
        }
        return false;
    }

    /**
     * Assigns employee to project
     */
    public boolean assignResourceToProject(String projectID, String employeeID, String role,
                                          String startDate, String endDate, String allocationPercentage) {
        
        if (!authService.hasPermission("ASSIGN_RESOURCES")) {
            return false;
        }

        // Validate project ID and project existence
        if (!InputValidator.isValidString(projectID) || getProjectByID(projectID) == null) {
            return false;
        }
        
        // Validate employee ID and employee existence
        if (!InputValidator.isValidString(employeeID) || employeeService.getEmployeeByID(employeeID) == null) {
            return false;
        }
        
        // Validate role
        if (!InputValidator.isValidString(role) || role.length() > 50) {
            return false;
        }
        
        // Validate dates
        if (!InputValidator.isValidDate(startDate) || !InputValidator.isValidDate(endDate)) {
            return false;
        }
        
        // Check if end date is after start date
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
            java.time.LocalDate start = java.time.LocalDate.parse(startDate, formatter);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate, formatter);
            if (!end.isAfter(start) && !end.isEqual(start)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        // Check allocation percentage
        if (!InputValidator.isValidPercentage(allocationPercentage)) {
            return false;
        }
        
        // Check for overlapping assignments
        if (hasOverlappingAssignment(employeeID, startDate, endDate)) {
            return false;
        }

        List<String[]> assignments = CSVManager.readCSV(ASSIGNMENTS_FILE);
        
        // Generate assignment ID
        int nextSequence = 1;
        if (!assignments.isEmpty()) {
            String lastID = assignments.get(assignments.size() - 1)[0];
            nextSequence = IDGenerator.getNextSequence(lastID, "ASSIGN");
        }
        
        String assignmentID = IDGenerator.generateAssignmentID(nextSequence);

        ResourceAssignment assignment = new ResourceAssignment(assignmentID, projectID, employeeID,
                role, startDate, endDate, allocationPercentage, "ASSIGNED");

        assignments.add(assignment.toCSVRow());
        return CSVManager.writeCSV(ASSIGNMENTS_FILE, assignments);
    }
    
    /**
     * Checks for overlapping assignments for an employee
     */
    private boolean hasOverlappingAssignment(String employeeID, String startDate, String endDate) {
        List<ResourceAssignment> assignments = getEmployeeAssignments(employeeID);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        try {
            java.time.LocalDate newStart = java.time.LocalDate.parse(startDate, formatter);
            java.time.LocalDate newEnd = java.time.LocalDate.parse(endDate, formatter);
            
            for (ResourceAssignment assign : assignments) {
                if (assign.getStatus().equals("ASSIGNED")) {
                    java.time.LocalDate existStart = java.time.LocalDate.parse(assign.getStartDate(), formatter);
                    java.time.LocalDate existEnd = java.time.LocalDate.parse(assign.getEndDate(), formatter);
                    
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
     * Gets assignments for project
     */
    public List<ResourceAssignment> getProjectAssignments(String projectID) {
        List<String[]> assignments = CSVManager.readCSV(ASSIGNMENTS_FILE);
        List<ResourceAssignment> result = new ArrayList<>();

        for (String[] row : assignments) {
            ResourceAssignment assignment = new ResourceAssignment(row);
            if (assignment.getProjectID().equals(projectID)) {
                result.add(assignment);
            }
        }
        return result;
    }

    /**
     * Gets assignments for employee
     */
    public List<ResourceAssignment> getEmployeeAssignments(String employeeID) {
        List<String[]> assignments = CSVManager.readCSV(ASSIGNMENTS_FILE);
        List<ResourceAssignment> result = new ArrayList<>();

        for (String[] row : assignments) {
            ResourceAssignment assignment = new ResourceAssignment(row);
            if (assignment.getEmployeeID().equals(employeeID)) {
                result.add(assignment);
            }
        }
        return result;
    }

    /**
     * Updates assignment
     */
    public boolean updateAssignment(String assignmentID, String role, String endDate, String status) {
        if (!authService.hasPermission("ASSIGN_RESOURCES")) {
            return false;
        }

        List<String[]> assignments = CSVManager.readCSV(ASSIGNMENTS_FILE);
        String[] row = CSVManager.findRow(assignments, 0, assignmentID);

        if (row != null) {
            ResourceAssignment assignment = new ResourceAssignment(row);
            if (role != null) assignment.setRole(role);
            if (endDate != null && InputValidator.isValidDate(endDate)) assignment.setEndDate(endDate);
            if (status != null) assignment.setStatus(status);

            if (CSVManager.updateRow(assignments, 0, assignmentID, assignment.toCSVRow())) {
                return CSVManager.writeCSV(ASSIGNMENTS_FILE, assignments);
            }
        }
        return false;
    }

    /**
     * Gets project count
     */
    public int getProjectCount() {
        return CSVManager.readCSV(PROJECTS_FILE).size();
    }

    /**
     * Gets active project count
     */
    public int getActiveProjectCount() {
        return getProjectsByStatus("IN_PROGRESS").size();
    }

    /**
     * Updates project status
     */
    public boolean updateProjectStatus(String projectID, String status) {
        return updateProject(projectID, null, null, null, status, null);
    }
}
