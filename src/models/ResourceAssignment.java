package models;

/**
 * ResourceAssignment entity representing employee assignments to projects
 */
public class ResourceAssignment {
    private String assignmentID;
    private String projectID;
    private String employeeID;
    private String role; // Developer, Designer, Tester, etc.
    private String startDate;
    private String endDate;
    private String allocationPercentage;
    private String status; // ASSIGNED, IN_PROGRESS, COMPLETED

    /**
     * Constructor for creating a new assignment
     */
    public ResourceAssignment(String assignmentID, String projectID, String employeeID,
                             String role, String startDate, String endDate,
                             String allocationPercentage, String status) {
        this.assignmentID = assignmentID;
        this.projectID = projectID;
        this.employeeID = employeeID;
        this.role = role;
        this.startDate = startDate;
        this.endDate = endDate;
        this.allocationPercentage = allocationPercentage;
        this.status = status;
    }

    /**
     * Constructor from CSV data
     */
    public ResourceAssignment(String[] csvRow) {
        if (csvRow.length >= 8) {
            this.assignmentID = csvRow[0];
            this.projectID = csvRow[1];
            this.employeeID = csvRow[2];
            this.role = csvRow[3];
            this.startDate = csvRow[4];
            this.endDate = csvRow[5];
            this.allocationPercentage = csvRow[6];
            this.status = csvRow[7];
        }
    }

    /**
     * Converts assignment to CSV row
     */
    public String[] toCSVRow() {
        return new String[]{
            assignmentID,
            projectID,
            employeeID,
            role,
            startDate,
            endDate,
            allocationPercentage,
            status
        };
    }

    // Getters and Setters
    public String getAssignmentID() {
        return assignmentID;
    }

    public String getProjectID() {
        return projectID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(String allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ResourceAssignment{" +
                "assignmentID='" + assignmentID + '\'' +
                ", projectID='" + projectID + '\'' +
                ", employeeID='" + employeeID + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
