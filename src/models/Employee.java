package models;

/**
 * Employee entity representing employees in the system
 */
public class Employee {
    private String employeeID;
    private String userID;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private String dateOfBirth;
    private String joinDate;
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    private String salary;
    private String reportingManager; // Employee ID of reporting manager

    /**
     * Constructor for creating a new employee
     */
    public Employee(String employeeID, String userID, String firstName, String lastName,
                    String email, String phone, String department, String designation,
                    String dateOfBirth, String joinDate, String status, String salary, String reportingManager) {
        this.employeeID = employeeID;
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.designation = designation;
        this.dateOfBirth = dateOfBirth;
        this.joinDate = joinDate;
        this.status = status;
        this.salary = salary;
        this.reportingManager = reportingManager;
    }

    /**
     * Constructor from CSV data
     */
    public Employee(String[] csvRow) {
        if (csvRow.length >= 13) {
            this.employeeID = csvRow[0];
            this.userID = csvRow[1];
            this.firstName = csvRow[2];
            this.lastName = csvRow[3];
            this.email = csvRow[4];
            this.phone = csvRow[5];
            this.department = csvRow[6];
            this.designation = csvRow[7];
            this.dateOfBirth = csvRow[8];
            this.joinDate = csvRow[9];
            this.status = csvRow[10];
            this.salary = csvRow[11];
            this.reportingManager = csvRow[12];
        }
    }

    /**
     * Converts employee to CSV row
     */
    public String[] toCSVRow() {
        return new String[]{
            employeeID,
            userID,
            firstName,
            lastName,
            email,
            phone,
            department,
            designation,
            dateOfBirth,
            joinDate,
            status,
            salary,
            reportingManager
        };
    }

    /**
     * Gets full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and Setters
    public String getEmployeeID() {
        return employeeID;
    }

    public String getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getReportingManager() {
        return reportingManager;
    }

    public void setReportingManager(String reportingManager) {
        this.reportingManager = reportingManager;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeID='" + employeeID + '\'' +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
