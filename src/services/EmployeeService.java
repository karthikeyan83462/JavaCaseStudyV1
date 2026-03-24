package services;

import models.Employee;
import models.Department;
import models.Designation;
import models.User;
import utils.CSVManager;
import utils.IDGenerator;
import utils.InputValidator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling employee management operations
 */
public class EmployeeService {

    private static final String EMPLOYEES_FILE = "data/employees.csv";
    private AuthenticationService authService;

    public EmployeeService(AuthenticationService authService) {
        this.authService = authService;
    }

    /**
     * Creates a new employee (Admin only)
     */
    public boolean createEmployee(String firstName, String lastName, String email,
                                 String phone, Department department, Designation designation,
                                 String dateOfBirth, String salary, String reportingManager) {
        
        if (!authService.hasPermission("VIEW_ALL_EMPLOYEES")) {
            return false;
        }

        // Validate required inputs
        if (!InputValidator.isValidName(firstName) || !InputValidator.isValidName(lastName)) {
            return false;
        }
        if (!InputValidator.isValidEmail(email) || employeeExistsByEmail(email)) {
            return false;
        }
        if (!InputValidator.isValidPhone(phone, false)) {
            return false;
        }
        if (department == null || designation == null) {
            return false;
        }
        if (!InputValidator.isValidDate(dateOfBirth)) {
            return false;
        }
        if (!InputValidator.isValidSalary(salary, false)) {
            return false;
        }

        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        
        // Generate employee ID
        int nextSequence = 1;
        if (!employees.isEmpty()) {
            String lastID = employees.get(employees.size() - 1)[0];
            nextSequence = IDGenerator.getNextSequence(lastID, "EMP");
        }
        
        String employeeID = IDGenerator.generateEmployeeID(nextSequence);
        String joinDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Create corresponding user account
        String defaultPassword = "Password123";
        if (!authService.createUser(email, defaultPassword, "EMPLOYEE")) {
            return false;
        }

        User user = authService.getUserByEmail(email);
        String userID = user != null ? user.getUserID() : "USR" + nextSequence;

        // Create employee record
        Employee employee = new Employee(employeeID, userID, firstName, lastName, email,
                phone, department.getShortName(), designation.getTitle(), dateOfBirth, 
                joinDate, "ACTIVE", salary, reportingManager);

        employees.add(employee.toCSVRow());
        return CSVManager.writeCSV(EMPLOYEES_FILE, employees);
    }

    /**
     * Creates a new employee with custom initial password (Admin only)
     */
    public boolean createEmployeeWithPassword(String firstName, String lastName, String email,
                                             String phone, Department department, Designation designation,
                                             String dateOfBirth, String salary, String reportingManager,
                                             String initialPassword) {
        
        if (!authService.hasPermission("VIEW_ALL_EMPLOYEES")) {
            return false;
        }

        // Validate required inputs
        if (!InputValidator.isValidName(firstName) || !InputValidator.isValidName(lastName)) {
            return false;
        }
        if (!InputValidator.isValidEmail(email) || employeeExistsByEmail(email)) {
            return false;
        }
        if (!InputValidator.isValidPhone(phone, false)) {
            return false;
        }
        if (department == null || designation == null) {
            return false;
        }
        if (!InputValidator.isValidDate(dateOfBirth)) {
            return false;
        }
        if (!InputValidator.isValidSalary(salary, false)) {
            return false;
        }
        if (!InputValidator.isValidPassword(initialPassword)) {
            return false;
        }

        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        
        // Generate employee ID
        int nextSequence = 1;
        if (!employees.isEmpty()) {
            String lastID = employees.get(employees.size() - 1)[0];
            nextSequence = IDGenerator.getNextSequence(lastID, "EMP");
        }
        
        String employeeID = IDGenerator.generateEmployeeID(nextSequence);
        String joinDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Create corresponding user account with custom password
        if (!authService.createUser(email, initialPassword, "EMPLOYEE")) {
            return false;
        }

        User user = authService.getUserByEmail(email);
        String userID = user != null ? user.getUserID() : "USR" + nextSequence;

        // Create employee record
        Employee employee = new Employee(employeeID, userID, firstName, lastName, email,
                phone, department.getShortName(), designation.getTitle(), dateOfBirth, 
                joinDate, "ACTIVE", salary, reportingManager);

        employees.add(employee.toCSVRow());
        return CSVManager.writeCSV(EMPLOYEES_FILE, employees);
    }

    /**
     * Gets employee by ID
     */
    public Employee getEmployeeByID(String employeeID) {
        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        String[] row = CSVManager.findRow(employees, 0, employeeID);
        return row != null ? new Employee(row) : null;
    }

    /**
     * Gets employee by email
     */
    public Employee getEmployeeByEmail(String email) {
        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        String[] row = CSVManager.findRow(employees, 4, email);
        return row != null ? new Employee(row) : null;
    }

    /**
     * Gets all active employees
     */
    public List<Employee> getAllActiveEmployees() {
        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        List<Employee> activeEmployees = new ArrayList<>();

        for (String[] row : employees) {
            Employee emp = new Employee(row);
            if (emp.getStatus().equals("ACTIVE")) {
                activeEmployees.add(emp);
            }
        }
        return activeEmployees;
    }

    /**
     * Gets all employees (Admin only)
     */
    public List<Employee> getAllEmployees() {
        // if (!authService.hasPermission("VIEW_ALL_EMPLOYEES")) {
        //     return null;
        // }

        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        List<Employee> result = new ArrayList<>();

        for (String[] row : employees) {
            result.add(new Employee(row));
        }
        return result;
    }

    /**
     * Updates employee information
     */
    public boolean updateEmployee(String employeeID, String firstName, String lastName,
                                 String phone, String department, String designation, String salary, String managerId) {
        
        if (!authService.hasPermission("VIEW_ALL_EMPLOYEES")) {
            return false;
        }

        Employee employee = getEmployeeByID(employeeID);
        if (employee == null) {
            return false;
        }

        // Validate inputs (only validate if provided)
        if (firstName != null && !firstName.isEmpty() && !InputValidator.isValidName(firstName)) {
            return false;
        }
        if (lastName != null && !lastName.isEmpty() && !InputValidator.isValidName(lastName)) {
            return false;
        }
        if (phone != null && !phone.isEmpty() && !InputValidator.isValidPhone(phone, false)) {
            return false;
        }
        if (salary != null && !salary.isEmpty() && !InputValidator.isValidSalary(salary, false)) {
            return false;
        }

        // Update fields only if provided
        if (firstName != null && !firstName.isEmpty()) employee.setFirstName(firstName);
        if (lastName != null && !lastName.isEmpty()) employee.setLastName(lastName);
        if (phone != null && !phone.isEmpty()) employee.setPhone(phone);
        if (department != null && !department.isEmpty()) employee.setDepartment(department);
        if (designation != null && !designation.isEmpty()) employee.setDesignation(designation);
        if (salary != null && !salary.isEmpty()) employee.setSalary(salary);
        if (managerId != null ) employee.setReportingManager(managerId);

        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        if (CSVManager.updateRow(employees, 0, employeeID, employee.toCSVRow())) {
            return CSVManager.writeCSV(EMPLOYEES_FILE, employees);
        }
        return false;
    }

    /**
     * Updates employee status
     */
    public boolean updateEmployeeStatus(String employeeID, String newStatus) {
        if (!authService.hasPermission("VIEW_ALL_EMPLOYEES")) {
            return false;
        }

        Employee employee = getEmployeeByID(employeeID);
        if (employee == null) {
            return false;
        }

        employee.setStatus(newStatus);
        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        
        if (CSVManager.updateRow(employees, 0, employeeID, employee.toCSVRow())) {
            return CSVManager.writeCSV(EMPLOYEES_FILE, employees);
        }
        return false;
    }

    /**
     * Gets employees by department
     */
    public List<Employee> getEmployeesByDepartment(String department) {
        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        List<Employee> result = new ArrayList<>();

        for (String[] row : employees) {
            Employee emp = new Employee(row);
            if (emp.getDepartment().equalsIgnoreCase(department) && emp.getStatus().equals("ACTIVE")) {
                result.add(emp);
            }
        }
        return result;
    }

    /**
     * Gets employees reporting to a manager
     */
    public List<Employee> getTeamMembers(String managerEmployeeID) {
        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        List<Employee> team = new ArrayList<>();

        for (String[] row : employees) {
            Employee emp = new Employee(row);
            if (emp.getReportingManager().equals(managerEmployeeID) && emp.getStatus().equals("ACTIVE")) {
                team.add(emp);
            }
        }
        return team;
    }

    /**
     * Checks if employee exists by email
     */
    public boolean employeeExistsByEmail(String email) {
        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        return CSVManager.valueExists(employees, 4, email);
    }

    /**
     * Gets employee count
     */
    public int getEmployeeCount() {
        return CSVManager.readCSV(EMPLOYEES_FILE).size();
    }

    /**
     * Gets active employee count
     */
    public int getActiveEmployeeCount() {
        return getAllActiveEmployees().size();
    }

    /**
     * Deletes an employee (marks as inactive)
     */
    public boolean deleteEmployee(String employeeID) {
        if (!authService.hasPermission("VIEW_ALL_EMPLOYEES")) {
            return false;
        }

        Employee employee = getEmployeeByID(employeeID);
        if (employee == null) {
            return false;
        }

        employee.setStatus("INACTIVE");
        List<String[]> employees = CSVManager.readCSV(EMPLOYEES_FILE);
        
        if (CSVManager.updateRow(employees, 0, employeeID, employee.toCSVRow())) {
            return CSVManager.writeCSV(EMPLOYEES_FILE, employees);
        }
        return false;
    }
}
