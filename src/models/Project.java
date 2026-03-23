package models;

/**
 * Project entity representing projects in the system
 */
public class Project {
    private String projectID;
    private String projectName;
    private String description;
    private String startDate;
    private String endDate;
    private String status; // PLANNED, IN_PROGRESS, COMPLETED, ON_HOLD
    private String projectManager; // Employee ID
    private String budget;

    /**
     * Constructor for creating a new project
     */
    public Project(String projectID, String projectName, String description,
                   String startDate, String endDate, String status,
                   String projectManager, String budget) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.projectManager = projectManager;
        this.budget = budget;
    }

    /**
     * Constructor from CSV data
     */
    public Project(String[] csvRow) {
        if (csvRow.length >= 8) {
            this.projectID = csvRow[0];
            this.projectName = csvRow[1];
            this.description = csvRow[2];
            this.startDate = csvRow[3];
            this.endDate = csvRow[4];
            this.status = csvRow[5];
            this.projectManager = csvRow[6];
            this.budget = csvRow[7];
        }
    }

    /**
     * Converts project to CSV row
     */
    public String[] toCSVRow() {
        return new String[]{
            projectID,
            projectName,
            description,
            startDate,
            endDate,
            status,
            projectManager,
            budget
        };
    }

    // Getters and Setters
    public String getProjectID() {
        return projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectID='" + projectID + '\'' +
                ", projectName='" + projectName + '\'' +
                ", status='" + status + '\'' +
                ", projectManager='" + projectManager + '\'' +
                '}';
    }
}
