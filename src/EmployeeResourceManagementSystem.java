import services.*;
import models.*;
import utils.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmployeeResourceManagementSystem {

    private AuthenticationService authService;
    private EmployeeService employeeService;
    private ProjectService projectService;
    private AttendanceService attendanceService;
    private LeaveService leaveService;
    private ReportingService reportingService;
    private ManagerService managerService;
    private Scanner scanner;

    public EmployeeResourceManagementSystem() {
        this.authService = new AuthenticationService();
        this.employeeService = new EmployeeService(authService);
        this.projectService = new ProjectService(authService, employeeService);
        this.attendanceService = new AttendanceService(authService, employeeService);
        this.leaveService = new LeaveService(authService, employeeService);
        this.reportingService = new ReportingService(authService, employeeService);
        this.managerService = new ManagerService(authService, employeeService);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        AuthenticationService.initializeSystem();

        EmployeeResourceManagementSystem system = new EmployeeResourceManagementSystem();
        system.run();
    }

    private void run() {
        ConsoleUI.printWelcomeBanner();

        while (true) {
            if (authService.getCurrentUser() == null) {
                handleLogin();
            } else {
                User currentUser = authService.getCurrentUser();

                if (currentUser.isPasswordChangeRequired()) {
                    handleFirstTimePasswordChange();
                } else {
                    String role = currentUser.getRole();
                    switch (role) {
                        case "ADMIN":
                            showAdminMenu();
                            break;
                        case "MANAGER":
                            showManagerMenu();
                            break;
                        case "EMPLOYEE":
                            showEmployeeMenu();
                            break;
                        default:
                            System.out.println("Unknown role: " + role);
                            authService.logout();
                    }
                }
            }
        }
    }

    private void handleLogin() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("LOGIN");
        System.out.println("=".repeat(50));
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = authService.login(email, password);

        if (user != null) {
            System.out.println("\nLogin successful! Welcome, " + email);
        } else {
            System.out.println("\nInvalid email or password. Please try again.");
        }
    }

    /**
     * Handles first-time password change
     */
    private void handleFirstTimePasswordChange() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("FIRST TIME LOGIN - STRONG PASSWORD REQUIRED");
        System.out.println("=".repeat(80));

        User currentUser = authService.getCurrentUser();
        String currentPassword = currentUser.getPassword();

        while (true) {
            System.out.println("\nPassword Requirements:");
            System.out.println(InputValidator.getPasswordValidationMessage());
            System.out.print("\nEnter new password: ");
            String newPassword = scanner.nextLine();

            if (!InputValidator.isValidPassword(newPassword)) {
                System.out.println("Password does not meet requirements. Please try again.");
                continue;
            }

            if (newPassword.equals(currentPassword)) {
                System.out.println("New password must be different from current password.");
                continue;
            }

            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Please try again.");
                continue;
            }

            if (authService.changePassword(currentPassword, newPassword)) {
                currentUser.setPasswordChangeRequired(false);
                System.out.println("Password changed successfully!");
                return;
            } else {
                System.out.println("Failed to change password. Please try again.");
            }
        }
    }

    /**
     * Admin menu
     */
    private void showAdminMenu() {
        while (authService.getCurrentUser() != null) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("ADMIN MENU");
            System.out.println("=".repeat(50));
            System.out.println("1. Employee Management");
            System.out.println("2. Project Management");
            System.out.println("3. Attendance Management");
            System.out.println("4. Leave Management");
            System.out.println("5. Reports");
            System.out.println("6. User Management");
            System.out.println("7. Change Password");
            System.out.println("8. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    adminEmployeeManagement();
                    break;
                case "2":
                    adminProjectManagement();
                    break;
                case "3":
                    adminAttendanceManagement();
                    break;
                case "4":
                    adminLeaveManagement();
                    break;
                case "5":
                    adminReports();
                    break;
                case "6":
                    adminUserManagement();
                    break;
                case "7":
                    changePassword();
                    break;
                case "8":
                    authService.logout();
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Admin employee management
     */
    private void adminEmployeeManagement() {
        while (true) {
            System.out.println("\n--- Employee Management ---");
            System.out.println("1. Create Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Update Employee");
            System.out.println("4. Delete Employee");
            System.out.println("5. View Employee Details");
            System.out.println("6. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createEmployee();
                    break;
                case "2":
                    viewAllEmployees();
                    break;
                case "3":
                    updateEmployee();
                    break;
                case "4":
                    deleteEmployee();
                    break;
                case "5":
                    viewEmployeeDetails();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Create new employee
     */
    private void createEmployee() {
        System.out.println("\n--- Create New Employee ---");

        // Validate First Name
        String firstName = "";
        while (true) {
            System.out.print("First Name: ");
            firstName = scanner.nextLine().trim();
            if (InputValidator.isValidName(firstName)) {
                break;
            }
            System.out.println("Invalid first name. Use only letters, spaces, hyphens, and apostrophes.");
        }

        // Validate Last Name
        String lastName = "";
        while (true) {
            System.out.print("Last Name: ");
            lastName = scanner.nextLine().trim();
            if (InputValidator.isValidName(lastName)) {
                break;
            }
            System.out.println("Invalid last name. Use only letters, spaces, hyphens, and apostrophes.");
        }

        // Validate Email
        String email = "";
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();
            if (InputValidator.isValidEmail(email)) {
                break;
            }
            System.out.println("Invalid email format. Please enter a valid email address.");
        }

        // Validate Phone
        String phone = "";
        while (true) {
            System.out.print("Phone (10 digits): ");
            phone = scanner.nextLine().trim();
            if (InputValidator.isValidPhone(phone, false)) {
                break;
            }
            System.out.println("Invalid phone number. Please enter exactly 10 digits.");
        }

        // Validate Date of Birth (Must be 18+)
        String dob = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        while (true) {
            System.out.print("Date of Birth (dd-MM-yyyy): ");
            dob = scanner.nextLine().trim();

            if (!InputValidator.isValidDate(dob)) {
                System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 15-06-1990).");
                continue;
            }

            try {
                LocalDate birthDate = LocalDate.parse(dob, formatter);
                LocalDate today = LocalDate.now();
                LocalDate minimumEligibleDate = today.minusYears(18);

                if (birthDate.isAfter(today)) {
                    System.out.println("Date of birth cannot be in the future.");
                    continue;
                }

                if (birthDate.isAfter(minimumEligibleDate)) {
                    System.out.println("You are not eligible. Applicant must be at least 18 years old.");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Error parsing date. Please try again.");
            }
        }

        System.out.println("\nSelect Department:");
        System.out.print(Department.getDepartmentOptions());

        Department dept = null;
        while (dept == null) {
            System.out.print("Enter choice (1-6): ");
            String deptChoice = scanner.nextLine().trim();

            if (!InputValidator.isValidInteger(deptChoice, 1, 6)) {
                System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                continue;
            }

            int deptIndex = Integer.parseInt(deptChoice);
            dept = Department.getDepartmentByIndex(deptIndex);
            if (dept == null) {
                System.out.println("Invalid department selection. Please try again.");
            }
        }

        System.out.println("\nSelect Designation for " + dept.getFullName() + ":");
        System.out.print(Designation.getDesignationOptions(dept));

        Designation desig = null;
        while (desig == null) {
            System.out.print("Enter choice: ");
            String desigChoice = scanner.nextLine().trim();

            if (!InputValidator.isValidInteger(desigChoice)) {
                System.out.println("Invalid input. Please enter a valid number.");
                continue;
            }

            int desigIndex = Integer.parseInt(desigChoice);
            desig = Designation.getDesignationByIndex(dept, desigIndex);
            if (desig == null) {
                System.out.println("Invalid designation selection. Please try again.");
            }
        }

        // Validate Salary
        String salary = "";
        while (true) {
            System.out.print("Salary: ");
            salary = scanner.nextLine().trim();
            if (InputValidator.isValidSalary(salary, false)) {
                break;
            }
            System.out.println("Invalid salary. Please enter a positive number.");
        }

        // Optional Reporting Manager
        System.out.print("Reporting Manager ID (optional, press Enter to skip): ");
        String reportingManager = scanner.nextLine().trim();
        if (reportingManager.isEmpty()) {
            reportingManager = null;
        } else {
            Employee manager = employeeService.getEmployeeByID(reportingManager);
            if (manager == null) {
                System.out.println("Warning: Reporting manager ID not found. Proceeding without manager assignment.");
                reportingManager = null;
            }
        }

        // Set initial password with confirmation
        String initialPassword = "";
        while (true) {
            System.out.println("\n--- Set Initial Password ---");
            System.out.println(InputValidator.getPasswordValidationMessage());
            System.out.print("Enter password: ");
            initialPassword = scanner.nextLine();

            if (!InputValidator.isValidPassword(initialPassword)) {
                System.out.println("Password does not meet requirements. Please try again.");
                continue;
            }

            System.out.print("Confirm password: ");
            String confirmPassword = scanner.nextLine();

            if (!initialPassword.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Please try again.");
                continue;
            }

            break;
        }

        if (employeeService.createEmployeeWithPassword(firstName, lastName, email, phone, dept, desig, dob, salary,
                reportingManager, initialPassword)) {
            System.out.println("Employee created successfully!");
        } else {
            System.out.println("Failed to create employee. Email may already exist or an error occurred.");
        }
    }

    /**
     * View all employees
     */
    private void viewAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();

        if (employees == null || employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("\n--- All Employees ---");
        System.out.printf("%-10s | %-12s | %-11s | %-19s | %-12s | %-10s\n",
                "ID", "First Name", "Last Name", "Email", "Department", "Status");
        System.out.println("-".repeat(120));

        for (Employee emp : employees) {
            System.out.printf("%-10s | %-12s | %-11s | %-19s | %-11s | %-10s\n",
                    emp.getEmployeeID(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail(),
                    emp.getDepartment(),
                    emp.getStatus()
                );
        }
    }

    /**
     * Update employee information
     */
    private void updateEmployee() {
        System.out.print("Enter Employee ID: ");
        String employeeID = scanner.nextLine().trim();

        Employee emp = employeeService.getEmployeeByID(employeeID);
        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.println("\n--- Update Employee ---");
        System.out.println("(Leave blank to keep current value)");

        // First Name
        String firstName;
        while (true) {
            System.out.print("First Name [" + emp.getFirstName() + "]: ");
            firstName = scanner.nextLine().trim();
            if (firstName.isEmpty() || InputValidator.isValidName(firstName))
                break;
            System.out.println("Invalid first name.");
        }

        // Last Name
        String lastName;
        while (true) {
            System.out.print("Last Name [" + emp.getLastName() + "]: ");
            lastName = scanner.nextLine().trim();
            if (lastName.isEmpty() || InputValidator.isValidName(lastName))
                break;
            System.out.println("Invalid last name.");
        }

        // Phone
        String phone;
        while (true) {
            System.out.print("Phone [" + emp.getPhone() + "]: ");
            phone = scanner.nextLine().trim();
            if (phone.isEmpty() || InputValidator.isValidPhone(phone, false))
                break;
            System.out.println("Invalid phone number.");
        }

        // Salary
        String salary;
        while (true) {
            System.out.print("Salary [" + emp.getSalary() + "]: ");
            salary = scanner.nextLine().trim();
            if (salary.isEmpty() || InputValidator.isValidSalary(salary, false))
                break;
            System.out.println("Invalid salary.");
        }

        String reportingManagerId;
        while (true) {
            System.out.print("Reporting Manager [" + emp.getReportingManager() + "]: ");
            reportingManagerId = scanner.nextLine().trim();

            // Keep existing
            if (reportingManagerId.isEmpty()) {
                reportingManagerId = emp.getReportingManager();
                break;
            }

            // Cannot report to self
            if (reportingManagerId.equals(employeeID)) {
                System.out.println("Employee cannot be their own manager.");
                continue;
            }

            Employee manager = employeeService.getEmployeeByID(reportingManagerId);
            if (manager == null) {
                System.out.println("Reporting Manager ID does not exist.");
                continue;
            }

            User managerUser = authService.getUserByEmail(manager.getEmail());
            if (managerUser == null || !"MANAGER".equals(managerUser.getRole())) {
                System.out.println("Selected employee is NOT a MANAGER.");
                continue;
            }

            break;
        }

        boolean updated = employeeService.updateEmployee(
                employeeID,
                firstName,
                lastName,
                phone,
                emp.getDepartment(),
                emp.getDesignation(),
                salary,
                reportingManagerId);

        if (updated) {
            System.out.println("Employee updated successfully.");
        } else {
            System.out.println("Failed to update employee.");
        }
    }

    /**
     * Delete employee
     */
    private void deleteEmployee() {
        System.out.print("Enter Employee ID: ");
        String employeeID = scanner.nextLine().trim();

        if (!InputValidator.isValidString(employeeID)) {
            System.out.println("Invalid employee ID.");
            return;
        }

        Employee emp = employeeService.getEmployeeByID(employeeID);
        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.println("\nEmployee Details:");
        System.out.println("Name: " + emp.getFirstName() + " " + emp.getLastName());
        System.out.println("Email: " + emp.getEmail());
        System.out.println("Department: " + emp.getDepartment());

        System.out.print("\nAre you sure you want to delete this employee? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            if (employeeService.deleteEmployee(employeeID)) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Failed to delete employee.");
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    /**
     * View employee details
     */
    private void viewEmployeeDetails() {
        System.out.print("Enter Employee ID: ");
        String employeeID = scanner.nextLine().trim();

        Employee emp = employeeService.getEmployeeByID(employeeID);
        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.println("\n--- Employee Details ---");
        System.out.println("ID: " + emp.getEmployeeID());
        System.out.println("Name: " + emp.getFirstName() + " " + emp.getLastName());
        System.out.println("Email: " + emp.getEmail());
        System.out.println("Phone: " + emp.getPhone());
        System.out.println("Department: " + emp.getDepartment());
        System.out.println("Designation: " + emp.getDesignation());
        System.out.println("Date of Birth: " + emp.getDateOfBirth());
        System.out.println("Salary: " + emp.getSalary());
        System.out.println("Join Date: " + emp.getJoinDate());
        System.out.println("Status: " + emp.getStatus());
    }

    /**
     * Admin project management
     */
    private void adminProjectManagement() {
        while (true) {
            System.out.println("\n--- Project Management ---");
            System.out.println("1. Create Project");
            System.out.println("2. View All Projects");
            System.out.println("3. Update Project Status");
            System.out.println("4. Assign Resource to Project");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createProject();
                    break;
                case "2":
                    viewAllProjects();
                    break;
                case "3":
                    updateProjectStatus();
                    break;
                case "4":
                    assignResourceToProject();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Create project
     */
    private void createProject() {
        System.out.println("\n--- Create New Project ---");

        // Validate Project Name
        String projectName = "";
        while (true) {
            System.out.print("Project Name: ");
            projectName = scanner.nextLine().trim();
            if (InputValidator.isValidString(projectName) && projectName.length() <= 100) {
                break;
            }
            System.out.println("Invalid project name. Please enter a non-empty name (max 100 characters).");
        }

        // Validate Description
        String description = "";
        while (true) {
            System.out.print("Description: ");
            description = scanner.nextLine().trim();
            if (InputValidator.isValidString(description)) {
                break;
            }
            System.out.println("Invalid description. Please enter a non-empty description.");
        }

        // Validate Start Date
        String startDate = "";
        while (true) {
            System.out.print("Start Date (dd-MM-yyyy): ");
            startDate = scanner.nextLine().trim();
            if (InputValidator.isValidDate(startDate)) {
                break;
            }
            System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 15-06-2024).");
        }

        // Validate End Date
        String endDate = "";
        while (true) {
            System.out.print("End Date (dd-MM-yyyy) Press up arrow to select previous date: ");
            endDate = scanner.nextLine().trim();
            if (InputValidator.isValidDate(endDate)) {
                // Check if end date is after start date
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate start = LocalDate.parse(startDate, formatter);
                    LocalDate end = LocalDate.parse(endDate, formatter);
                    if (end.isAfter(start)) {
                        break;
                    } else {
                        System.out.println("End date must be after start date.");
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing dates. Please try again.");
                }
            } else {
                System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 31-12-2024).");
            }
        }
        // Validate Project Manager
        String projectManager = "";
        while (true) {
            System.out.print("Project Manager (Employee ID): ");
            projectManager = scanner.nextLine().trim();

            if (!InputValidator.isValidString(projectManager)) {
                System.out.println("Invalid Employee ID. Please try again.");
                continue;
            }

            Employee manager = employeeService.getEmployeeByID(projectManager);
            if (manager == null) {
                System.out.println("Employee ID not found. Please enter a valid Employee ID.");
                continue;
            }

            User user = authService.getUserByEmail(manager.getEmail());
            if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) {
                System.out.println("Selected employee is NOT a manager.");
                System.out.println("Only users with MANAGER role can be assigned as Project Manager.");
                continue;
            }

            System.out.println("Project Manager confirmed: " +
                    manager.getFirstName() + " " + manager.getLastName());
            break;
        }
        // Validate Budget
        String budget = "";
        while (true) {
            System.out.print("Budget: ");
            budget = scanner.nextLine().trim();
            if (InputValidator.isValidSalary(budget, false)) {
                break;
            }
            System.out.println("Invalid budget. Please enter a positive number.");
        }

        if (projectService.createProject(projectName, description, startDate, endDate, projectManager, budget)) {
            System.out.println("Project created successfully.");
        } else {
            System.out.println("Failed to create project. An error occurred.");
        }
    }

    /**
     * View all projects
     */
    private void viewAllProjects() {
        List<Project> projects = projectService.getAllProjects();

        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        System.out.println("\n--- All Projects ---");
        System.out.printf("%-10s | %-20s | %-15s | %-15s\n",
                "ID", "Name", "Status", "Budget");
        System.out.println("-".repeat(70));

        for (Project proj : projects) {
            System.out.printf("%-10s | %-20s | %-15s | %-15s\n",
                    proj.getProjectID(),
                    proj.getProjectName(),
                    proj.getStatus(),
                    proj.getBudget());
        }
    }

    /**
     * Update project status
     */
    private void updateProjectStatus() {
        System.out.print("Enter Project ID: ");
        String projectID = scanner.nextLine().trim();

        Project project = projectService.getProjectByID(projectID);
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        System.out.println("\nProject: " + project.getProjectName());
        System.out.println("Current Status: " + project.getStatus());
        System.out.println("\nAvailable statuses:");
        System.out.println("1. PLANNED");
        System.out.println("2. ACTIVE");
        System.out.println("3. COMPLETED");
        System.out.println("4. ON-HOLD");

        String status = "";
        while (true) {
            System.out.print("Enter new status (or choice 1-4): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("1") || input.equals("PLANNED")) {
                status = "PLANNED";
                break;
            } else if (input.equals("2") || input.equals("ACTIVE")) {
                status = "ACTIVE";
                break;
            } else if (input.equals("3") || input.equals("COMPLETED")) {
                status = "COMPLETED";
                break;
            } else if (input.equals("4") || input.equals("ON-HOLD")) {
                status = "ON-HOLD";
                break;
            } else {
                System.out.println("Invalid status. Please select a valid option.");
            }
        }

        if (projectService.updateProjectStatus(projectID, status)) {
            System.out.println("Project updated successfully to status: " + status);
        } else {
            System.out.println("Failed to update project.");
        }
    }

    /**
     * Assign resource to project
     */
    private void assignResourceToProject() {
        // Validate Project ID
        String projectID = "";
        Project project = null;
        while (project == null) {
            System.out.print("Enter Project ID: ");
            projectID = scanner.nextLine().trim();
            if (!InputValidator.isValidString(projectID)) {
                System.out.println("Invalid Project ID.");
                continue;
            }
            project = projectService.getProjectByID(projectID);
            if (project == null) {
                System.out.println("Project not found. Please enter a valid Project ID.");
            }
        }

        System.out.println("Project: " + project.getProjectName());

        // Validate Employee ID
        String employeeID = "";
        Employee employee = null;
        while (employee == null) {
            System.out.print("Enter Employee ID: ");
            employeeID = scanner.nextLine().trim();
            if (!InputValidator.isValidString(employeeID)) {
                System.out.println("Invalid Employee ID.");
                continue;
            }
            employee = employeeService.getEmployeeByID(employeeID);
            if (employee == null) {
                System.out.println("Employee not found. Please enter a valid Employee ID.");
            }
        }

        System.out.println("Employee: " + employee.getFirstName() + " " + employee.getLastName());

        // Validate Role
        String role = "";
        while (true) {
            System.out.print("Enter Role: ");
            role = scanner.nextLine().trim();
            if (InputValidator.isValidString(role) && role.length() <= 50) {
                break;
            }
            System.out.println("Invalid role. Please enter a non-empty role name.");
        }

        // Validate Start Date
        String startDate = "";
        while (true) {
            System.out.print("Enter Start Date (dd-MM-yyyy): ");
            startDate = scanner.nextLine().trim();
            if (InputValidator.isValidDate(startDate)) {
                break;
            }
            System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 01-01-2024).");
        }

        // Validate End Date
        String endDate = "";
        while (true) {
            System.out.print("Enter End Date (dd-MM-yyyy) Press up arrow to select same date: ");
            endDate = scanner.nextLine().trim();
            if (InputValidator.isValidDate(endDate)) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate start = LocalDate.parse(startDate, formatter);
                    LocalDate end = LocalDate.parse(endDate, formatter);
                    if (end.isAfter(start) || end.isEqual(start)) {
                        break;
                    } else {
                        System.out.println("End date must be after or equal to start date.");
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing dates. Please try again.");
                }
            } else {
                System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 31-12-2024).");
            }
        }

        // Validate Allocation Percentage
        String allocation = "";
        while (true) {
            System.out.print("Enter Allocation Percentage (0-100): ");
            allocation = scanner.nextLine().trim();
            if (InputValidator.isValidPercentage(allocation)) {
                break;
            }
            System.out.println("Invalid allocation. Please enter a number between 0 and 100.");
        }

        if (projectService.assignResourceToProject(projectID, employeeID, role, startDate, endDate, allocation)) {
            System.out.println("Resource assigned successfully to project.");
        } else {
            System.out.println("Failed to assign resource. Check if overlapping assignments exist.");
        }
    }

    /**
     * Admin attendance management
     */
    private void adminAttendanceManagement() {
        while (true) {
            System.out.println("\n--- Attendance Management ---");
            System.out.println("1. Mark Attendance");
            System.out.println("2. View Attendance Records");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    markAttendance();
                    break;
                case "2":
                    viewAttendanceRecords();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Mark attendance
     */
    private void markAttendance() {
        // Validate Employee ID
        String employeeID = "";
        Employee employee = null;
        while (employee == null) {
            System.out.print("Enter Employee ID: ");
            employeeID = scanner.nextLine().trim();
            if (!InputValidator.isValidString(employeeID)) {
                System.out.println("Invalid Employee ID.");
                continue;
            }
            employee = employeeService.getEmployeeByID(employeeID);
            if (employee == null) {
                System.out.println("Employee not found. Please enter a valid Employee ID.");
            }
        }

        System.out.println("Employee: " + employee.getFirstName() + " " + employee.getLastName());

        // Validate Date
        String dateStr = "";
        while (true) {
            System.out.print("Date (dd-MM-yyyy) [Press enter for today]: ");
            dateStr = scanner.nextLine().trim();
            if (dateStr.isEmpty()) {
                dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                break;
            }
            if (InputValidator.isValidDate(dateStr)) {
                break;
            }
            System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 23-03-2026).");
        }

        // Validate Status
        String status = "";
        while (true) {
            System.out.println("\nAttendance Status:");
            System.out.println("1. PRESENT");
            System.out.println("2. ABSENT");
            System.out.println("3. LEAVE");
            System.out.print("Enter choice or status: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("1") || input.equals("PRESENT")) {
                status = "PRESENT";
                break;
            } else if (input.equals("2") || input.equals("ABSENT")) {
                status = "ABSENT";
                break;
            } else if (input.equals("3") || input.equals("LEAVE")) {
                status = "LEAVE";
                break;
            } else {
                System.out.println("Invalid status. Please select a valid option.");
            }
        }

        if (attendanceService.markAttendance(employeeID, dateStr, status)) {
            System.out.println("Attendance marked successfully as " + status + " for " + dateStr);
        } else {
            System.out.println("Failed to mark attendance. This may be a duplicate entry or an error occurred.");
        }
    }

    /**
     * View attendance records
     */
    private void viewAttendanceRecords() {
        // Validate Employee ID
        String employeeID = "";
        Employee employee = null;
        while (employee == null) {
            System.out.print("Enter Employee ID: ");
            employeeID = scanner.nextLine().trim();
            if (!InputValidator.isValidString(employeeID)) {
                System.out.println("Invalid Employee ID.");
                continue;
            }
            employee = employeeService.getEmployeeByID(employeeID);
            if (employee == null) {
                System.out.println("Employee not found. Please enter a valid Employee ID.");
            }
        }

        List<Attendance> records = attendanceService.getEmployeeAttendance(employeeID);

        if (records == null || records.isEmpty()) {
            System.out.println(
                    "No attendance records found for " + employee.getFirstName() + " " + employee.getLastName());
            return;
        }

        System.out.println(
                "\n--- Attendance Records for " + employee.getFirstName() + " " + employee.getLastName() + " ---");
        System.out.printf("%-12s | %-15s\n", "Date", "Status");
        System.out.println("-".repeat(30));

        for (Attendance att : records) {
            System.out.printf("%-12s | %-15s\n", att.getDate(), att.getStatus());
        }
    }

    /**
     * Admin leave management
     */
    private void adminLeaveManagement() {
        while (true) {
            System.out.println("\n--- Leave Management ---");
            System.out.println("1. View Pending Leave Requests");
            System.out.println("2. Approve Leave");
            System.out.println("3. Reject Leave");
            System.out.println("4. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewPendingLeaves();
                    break;
                case "2":
                    approveLeave();
                    break;
                case "3":
                    rejectLeave();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * View pending leaves
     */
    private void viewPendingLeaves() {
        List<LeaveRequest> leaves = leaveService.getPendingLeaveRequests();

        if (leaves == null || leaves.isEmpty()) {
            System.out.println("No pending leave requests.");
            return;
        }

        System.out.println("\n--- Pending Leave Requests ---");
        System.out.printf("%-10s | %-12s | %-12s | %-12s | %-15s\n",
                "Leave ID", "Emp ID", "Start Date", "End Date", "Status");
        System.out.println("-".repeat(70));

        for (LeaveRequest leave : leaves) {
            System.out.printf("%-10s | %-12s | %-12s | %-12s | %-15s\n",
                    leave.getLeaveID(),
                    leave.getEmployeeID(),
                    leave.getStartDate(),
                    leave.getEndDate(),
                    leave.getStatus());
        }
    }

    /**
     * Approve leave
     */
    private void approveLeave() {
        // Validate Leave ID
        String leaveID = "";
        while (true) {
            System.out.print("Enter Leave ID: ");
            leaveID = scanner.nextLine().trim();
            if (InputValidator.isValidString(leaveID)) {
                break;
            }
            System.out.println("Invalid Leave ID. Please enter a non-empty value.");
        }

        // Validate Comments (optional)
        System.out.print("Comments (optional): ");
        String comments = scanner.nextLine().trim();

        if (leaveService.approveLeaveRequest(leaveID, comments)) {
            System.out.println("Leave approved successfully with comments.");
        } else {
            System.out.println("Failed to approve leave. Leave ID may not exist or may already be processed.");
        }
    }

    /**
     * Reject leave
     */
    private void rejectLeave() {
        // Validate Leave ID
        String leaveID = "";
        while (true) {
            System.out.print("Enter Leave ID: ");
            leaveID = scanner.nextLine().trim();
            if (InputValidator.isValidString(leaveID)) {
                break;
            }
            System.out.println("Invalid Leave ID. Please enter a non-empty value.");
        }

        // Validate Reason
        String reason = "";
        while (true) {
            System.out.print("Reason for rejection: ");
            reason = scanner.nextLine().trim();
            if (InputValidator.isValidString(reason)) {
                break;
            }
            System.out.println("Reason cannot be empty. Please provide a reason.");
        }

        if (leaveService.rejectLeaveRequest(leaveID, reason)) {
            System.out.println("Leave rejected successfully with reason provided.");
        } else {
            System.out.println("Failed to reject leave. Leave ID may not exist or may already be processed.");
        }
    }

    /**
     * Admin reports
     */
    private void adminReports() {
        while (true) {
            System.out.println("\n--- Reports ---");
            System.out.println("1. Employee Report");
            System.out.println("2. Project Report");
            System.out.println("3. Attendance Report");
            System.out.println("4. Leave Report");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayEmployeeReport();
                    break;
                case "2":
                    displayProjectReport();
                    break;
                case "3":
                    displayAttendanceReport();
                    break;
                case "4":
                    displayLeaveReport();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Display employee report in descriptive format
     */
    private void displayEmployeeReport() {
        Map<String, Object> report = reportingService.getEmployeeReport();
        if (report == null) {
            System.out.println("Cannot access employee report.");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("EMPLOYEE REPORT");
        System.out.println("=".repeat(70));
        System.out.println("Total Employees: " + report.get("totalEmployees"));
        System.out.println("Active Employees: " + report.get("activeEmployees"));
        System.out.println("Inactive Employees: " + report.get("inactiveEmployees"));
        System.out.println("Total Salary Expense: " + report.get("totalSalary"));
        System.out.println("Average Salary: " + report.get("averageSalary"));
        System.out.println("Total Departments: " + report.get("totalDepartments"));

        if (report.containsKey("departmentBreakdown")) {
            System.out.println("\nEmployees by Department:");
            @SuppressWarnings("unchecked")
            Map<String, Integer> deptMap = (Map<String, Integer>) report.get("departmentBreakdown");
            for (Map.Entry<String, Integer> entry : deptMap.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }

        if (report.containsKey("designationBreakdown")) {
            System.out.println("\nEmployees by Designation:");
            @SuppressWarnings("unchecked")
            Map<String, Integer> desigMap = (Map<String, Integer>) report.get("designationBreakdown");
            for (Map.Entry<String, Integer> entry : desigMap.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    /**
     * Display project report in descriptive format
     */
    private void displayProjectReport() {
        Map<String, Object> report = reportingService.getProjectReport();
        if (report == null) {
            System.out.println("Cannot access project report.");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("PROJECT REPORT");
        System.out.println("=".repeat(70));
        System.out.println("Total Projects: " + report.get("totalProjects"));
        System.out.println("Active Projects: " + report.get("activeProjects"));
        System.out.println("Completed Projects: " + report.get("completedProjects"));
        System.out.println("On-Hold Projects: " + report.get("onHoldProjects"));
        System.out.println("Total Budget: " + report.get("totalBudget"));

        if (report.containsKey("statusBreakdown")) {
            System.out.println("\nProjects by Status:");
            @SuppressWarnings("unchecked")
            Map<String, Integer> statusMap = (Map<String, Integer>) report.get("statusBreakdown");
            for (Map.Entry<String, Integer> entry : statusMap.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    /**
     * Display attendance report in descriptive format
     */
    private void displayAttendanceReport() {
        Map<String, Object> report = reportingService.getAttendanceReport();
        if (report == null) {
            System.out.println("Cannot access attendance report.");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("ATTENDANCE REPORT");
        System.out.println("=".repeat(70));
        System.out.println("Total Records: " + report.get("totalRecords"));
        System.out.println("Present: " + report.get("presentCount") + " (" + report.get("presentPercentage") + ")");
        System.out.println("Absent: " + report.get("absentCount") + " (" + report.get("absentPercentage") + ")");
        System.out.println("Leave: " + report.get("leaveCount"));
        System.out.println("Current Month Records: " + report.get("currentMonthRecords"));

        if (report.containsKey("statusBreakdown")) {
            System.out.println("\nAttendance by Status:");
            @SuppressWarnings("unchecked")
            Map<String, Integer> statusMap = (Map<String, Integer>) report.get("statusBreakdown");
            for (Map.Entry<String, Integer> entry : statusMap.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    /**
     * Display leave report in descriptive format
     */
    private void displayLeaveReport() {
        Map<String, Object> report = reportingService.getLeaveReport();
        if (report == null) {
            System.out.println("Cannot access leave report.");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("LEAVE REPORT");
        System.out.println("=".repeat(70));
        System.out.println("Total Leave Requests: " + report.get("totalLeaves"));
        System.out.println("Approved: " + report.get("approvedLeaves"));
        System.out.println("Pending: " + report.get("pendingLeaves"));
        System.out.println("Rejected: " + report.get("rejectedLeaves"));
        System.out.println("Total Leave Days Used: " + report.get("totalLeaveDays"));

        if (report.containsKey("leaveTypeBreakdown")) {
            System.out.println("\nLeaves by Type:");
            @SuppressWarnings("unchecked")
            Map<String, Integer> typeMap = (Map<String, Integer>) report.get("leaveTypeBreakdown");
            for (Map.Entry<String, Integer> entry : typeMap.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }

        if (report.containsKey("leaveStatusBreakdown")) {
            System.out.println("\nLeaves by Status:");
            @SuppressWarnings("unchecked")
            Map<String, Integer> statusMap = (Map<String, Integer>) report.get("leaveStatusBreakdown");
            for (Map.Entry<String, Integer> entry : statusMap.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    /**
     * Admin user management
     */
    private void adminUserManagement() {
        while (true) {
            System.out.println("\n--- User Management ---");
            System.out.println("1. View All Users");
            System.out.println("2. Change User Role");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    changeUserRole();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * View all users with detailed role and department information
     */
    private void viewAllUsers() {
        List<String[]> users = authService.getAllUsers();

        if (users == null || users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n--- All Users and Their Roles ---");
        System.out.printf("%-10s | %-25s | %-10s | %-15s | %-15s | %-8s\n",
                "User ID", "Email", "Role", "First Name", "Last Name", "Status");
        System.out.println("-".repeat(100));

        Map<String, Integer> roleCount = new HashMap<>();

        for (String[] row : users) {
            String userID = row[0];
            String email = row[1];
            String role = row[3];

            roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);

            // Get employee details if available
            Employee emp = employeeService.getEmployeeByEmail(email);
            String firstName = emp != null ? emp.getFirstName() : "N/A";
            String lastName = emp != null ? emp.getLastName() : "N/A";
            String status = emp != null ? emp.getStatus() : "N/A";

            System.out.printf("%-10s | %-25s | %-10s | %-15s | %-15s | %-8s\n",
                    userID, email, role, firstName, lastName, status);
        }

        System.out.println("\n--- Role Distribution ---");
        for (Map.Entry<String, Integer> entry : roleCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " users");
        }
    }

    /**
     * Change user role
     */
    private void changeUserRole() {
        // Validate User ID
        String userID = "";
        while (true) {
            System.out.print("Enter User ID: ");
            userID = scanner.nextLine().trim();
            if (InputValidator.isValidString(userID)) {
                break;
            }
            System.out.println("Invalid User ID. Please enter a non-empty value.");
        }

        System.out.println("\nAvailable Roles:");
        System.out.println("1. ADMIN");
        System.out.println("2. MANAGER");
        System.out.println("3. EMPLOYEE");

        // Validate Role Selection
        String newRole = "";
        while (true) {
            System.out.print("Enter choice (1-3) or role name: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("1") || input.equals("ADMIN")) {
                newRole = "ADMIN";
                break;
            } else if (input.equals("2") || input.equals("MANAGER")) {
                newRole = "MANAGER";
                break;
            } else if (input.equals("3") || input.equals("EMPLOYEE")) {
                newRole = "EMPLOYEE";
                break;
            } else {
                System.out.println("Invalid role. Please select a valid option.");
            }
        }

        if (authService.updateUserRole(userID, newRole)) {
            System.out.println("User role updated successfully to " + newRole);
        } else {
            System.out.println("Failed to update user role. User ID may not exist.");
        }
    }

    /**
     * Manager menu
     */
    private void showManagerMenu() {
        while (authService.getCurrentUser() != null) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("MANAGER MENU");
            System.out.println("=".repeat(50));
            System.out.println("1. View Team Members");
            System.out.println("2. View Team Attendance");
            System.out.println("3. Approve/Reject Leave Requests");
            System.out.println("4. View Team Projects");
            System.out.println("5. View Team Workload");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewTeamMembers();
                    break;
                case "2":
                    viewTeamAttendance();
                    break;
                case "3":
                    managerApproveLeave();
                    break;
                case "4":
                    viewTeamProjects();
                    break;
                case "5":
                    System.out.println(managerService.getTeamWorkloadOverview());
                    break;
                case "6":
                    changePassword();
                    break;
                case "7":
                    authService.logout();
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * View team members
     */
    private void viewTeamMembers() {
        List<Employee> team = managerService.getTeamMembers();

        if (team.isEmpty()) {
            System.out.println("No team members found.");
            return;
        }

        System.out.println("\n--- Team Members ---");
        System.out.printf("%-10s | %-15s | %-15s | %-15s\n",
                "ID", "First Name", "Last Name", "Department");
        System.out.println("-".repeat(60));

        for (Employee emp : team) {
            System.out.printf("%-10s | %-15s | %-15s | %-15s\n",
                    emp.getEmployeeID(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getDepartment());
        }
    }

    /**
     * View team attendance
     */
    private void viewTeamAttendance() {
        // Validate Date
        String dateStr = "";
        while (true) {
            System.out.print("Enter Date (dd-MM-yyyy) [Press enter for today]: ");
            dateStr = scanner.nextLine().trim();
            if (dateStr.isEmpty()) {
                dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                break;
            }
            if (InputValidator.isValidDate(dateStr)) {
                break;
            }
            System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 23-03-2026).");
        }

        List<Attendance> attendance = managerService.getTeamAttendance(dateStr);

        if (attendance == null || attendance.isEmpty()) {
            System.out.println("No attendance records for this date: " + dateStr);
            return;
        }

        System.out.println("\n--- Team Attendance for " + dateStr + " ---");
        System.out.printf("%-15s | %-20s\n", "Employee ID", "Status");
        System.out.println("-".repeat(40));

        for (Attendance att : attendance) {
            System.out.printf("%-15s | %-20s\n", att.getEmployeeID(), att.getStatus());
        }
    }

    /**
     * Manager approve/reject leave
     */
    private void managerApproveLeave() {
        List<LeaveRequest> pending = managerService.getPendingLeaveRequests();

        if (pending == null || pending.isEmpty()) {
            System.out.println("No pending leave requests to process.");
            return;
        }

        System.out.println("\n--- Pending Leave Requests ---");
        System.out.printf("%-10s | %-12s | %-12s | %-12s\n",
                "Leave ID", "Emp ID", "Start Date", "End Date");
        System.out.println("-".repeat(50));

        for (LeaveRequest leave : pending) {
            System.out.printf("%-10s | %-12s | %-12s | %-12s\n",
                    leave.getLeaveID(),
                    leave.getEmployeeID(),
                    leave.getStartDate(),
                    leave.getEndDate());
        }

        // Validate Leave ID
        String leaveID = "";
        while (true) {
            System.out.print("\nEnter Leave ID to process: ");
            leaveID = scanner.nextLine().trim();
            if (InputValidator.isValidString(leaveID)) {
                break;
            }
            System.out.println("Invalid Leave ID. Please enter a non-empty value.");
        }

        // Validate Action
        String action = "";
        while (true) {
            System.out.print("Approve (A/YES) or Reject (R/NO)? ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("A") || input.equals("YES")) {
                action = "A";
                break;
            } else if (input.equals("R") || input.equals("NO")) {
                action = "R";
                break;
            } else {
                System.out.println("Invalid action. Please enter A for Approve or R for Reject.");
            }
        }

        if (action.equals("A")) {
            System.out.print("Comments (optional): ");
            String comments = scanner.nextLine().trim();
            if (managerService.approveLeaveRequest(leaveID, comments)) {
                System.out.println("Leave approved successfully.");
            } else {
                System.out.println("Failed to approve leave. Leave ID may not exist or already processed.");
            }
        } else if (action.equals("R")) {
            // Validate Rejection Reason
            String reason = "";
            while (true) {
                System.out.print("Reason for rejection: ");
                reason = scanner.nextLine().trim();
                if (InputValidator.isValidString(reason)) {
                    break;
                }
                System.out.println("Reason cannot be empty. Please provide a reason.");
            }

            if (managerService.rejectLeaveRequest(leaveID, reason)) {
                System.out.println("Leave rejected successfully with reason provided.");
            } else {
                System.out.println("Failed to reject leave. Leave ID may not exist or already processed.");
            }
        }
    }

    /**
     * View team projects
     */
    private void viewTeamProjects() {
        List<Project> projects = managerService.getTeamProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects assigned to team.");
            return;
        }

        System.out.println("\n--- Team Projects ---");
        System.out.printf("%-10s | %-20s | %-15s\n",
                "Project ID", "Name", "Status");
        System.out.println("-".repeat(50));

        for (Project proj : projects) {
            System.out.printf("%-10s | %-20s | %-15s\n",
                    proj.getProjectID(),
                    proj.getProjectName(),
                    proj.getStatus());
        }
    }

    /**
     * Employee menu
     */
    private void showEmployeeMenu() {
        while (authService.getCurrentUser() != null) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("EMPLOYEE MENU");
            System.out.println("=".repeat(50));
            System.out.println("1. View Profile");
            System.out.println("2. View Attendance");
            System.out.println("3. Mark Attendance");
            System.out.println("4. Request Leave");
            System.out.println("5. View My Leaves");
            System.out.println("6. View Project Assignments");
            System.out.println("7. View Team Hierarchy");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewProfile();
                    break;
                case "2":
                    viewMyAttendance();
                    break;
                case "3":
                    markMyAttendance();
                    break;
                case "4":
                    requestLeave();
                    break;
                case "5":
                    viewMyLeaves();
                    break;
                case "6":
                    viewMyAssignments();
                    break;
                case "7":
                    viewTeamHierarchy();
                    break;
                case "8":
                    changePassword();
                    break;
                case "9":
                    authService.logout();
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * View profile
     */
    private void viewProfile() {
        String email = authService.getCurrentUser().getEmail();
        Employee emp = employeeService.getEmployeeByEmail(email);

        if (emp == null) {
            System.out.println("Employee profile not found.");
            return;
        }

        System.out.println("\n--- Your Profile ---");
        System.out.println("Name: " + emp.getFirstName() + " " + emp.getLastName());
        System.out.println("Email: " + emp.getEmail());
        System.out.println("Phone: " + emp.getPhone());
        System.out.println("Department: " + emp.getDepartment());
        System.out.println("Designation: " + emp.getDesignation());
        System.out.println("Employee ID: " + emp.getEmployeeID());
        System.out.println("Salary: " + emp.getSalary());
    }

    /**
     * View my attendance
     */
    private void viewMyAttendance() {
        String email = authService.getCurrentUser().getEmail();
        Employee emp = employeeService.getEmployeeByEmail(email);

        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        List<Attendance> records = attendanceService.getEmployeeAttendance(emp.getEmployeeID());

        if (records == null || records.isEmpty()) {
            System.out.println("No attendance records.");
            return;
        }

        System.out.println("\n--- Your Attendance ---");
        System.out.printf("%-12s | %-15s\n", "Date", "Status");
        System.out.println("-".repeat(30));

        for (Attendance att : records) {
            System.out.printf("%-12s | %-15s\n", att.getDate(), att.getStatus());
        }
    }

    /**
     * Mark own attendance
     */
    private void markMyAttendance() {
        String email = authService.getCurrentUser().getEmail();
        Employee emp = employeeService.getEmployeeByEmail(email);

        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.println("\n--- Mark Your Attendance ---");
        System.out.println("Employee: " + emp.getFirstName() + " " + emp.getLastName());

        // Validate Date (default today)
        String dateStr = "";
        while (true) {
            System.out.print("Date (dd-MM-yyyy) [Press enter for today]: ");
            dateStr = scanner.nextLine().trim();
            if (dateStr.isEmpty()) {
                dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                break;
            }
            if (InputValidator.isValidDate(dateStr)) {
                // Check if date is not in future
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate selectedDate = LocalDate.parse(dateStr, formatter);
                    if (selectedDate.isAfter(LocalDate.now())) {
                        System.out.println("Cannot mark attendance for future dates.");
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("Error parsing date. Please try again.");
                }
            } else {
                System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 23-03-2026).");
            }
        }

        // Validate Status
        String status = "";
        while (true) {
            System.out.println("\nAttendance Status:");
            System.out.println("1. PRESENT");
            System.out.println("2. ABSENT");
            System.out.println("3. LEAVE");
            System.out.print("Enter choice (1-3): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("1") || input.equals("PRESENT")) {
                status = "PRESENT";
                break;
            } else if (input.equals("2") || input.equals("ABSENT")) {
                status = "ABSENT";
                break;
            } else if (input.equals("3") || input.equals("LEAVE")) {
                status = "LEAVE";
                break;
            } else {
                System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }

        // Mark attendance
        if (attendanceService.markOwnAttendance(emp.getEmployeeID(), dateStr, status)) {
            System.out.println("Attendance marked successfully as " + status + " for " + dateStr);
        } else {
            System.out.println("Failed to mark attendance. You may have already marked attendance for this date.");
        }
    }

    /**
     * Request leave
     */
    private void requestLeave() {
        String email = authService.getCurrentUser().getEmail();
        Employee emp = employeeService.getEmployeeByEmail(email);

        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.println("\n--- Request Leave ---");

        // Validate Leave Type
        String leaveType = "";
        while (true) {
            System.out.println("Leave Type Options:");
            System.out.println("1. SICK");
            System.out.println("2. CASUAL");
            System.out.println("3. PERSONAL");
            System.out.print("Enter choice (1-3) or type: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("1") || input.equals("SICK")) {
                leaveType = "SICK";
                break;
            } else if (input.equals("2") || input.equals("CASUAL")) {
                leaveType = "CASUAL";
                break;
            } else if (input.equals("3") || input.equals("PERSONAL")) {
                leaveType = "PERSONAL";
                break;
            } else {
                System.out.println("Invalid leave type. Please select a valid option.");
            }
        }

        // Validate Start Date
        String startDate = "";
        while (true) {
            System.out.print("Start Date (dd-MM-yyyy): ");
            startDate = scanner.nextLine().trim();
            if (InputValidator.isValidDate(startDate)) {
                break;
            }
            System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 25-03-2026).");
        }

        // Validate End Date
        String endDate = "";
        while (true) {
            System.out.print("End Date (dd-MM-yyyy) Press up arrow to select previous date: ");
            endDate = scanner.nextLine().trim();
            if (InputValidator.isValidDate(endDate)) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate start = LocalDate.parse(startDate, formatter);
                    LocalDate end = LocalDate.parse(endDate, formatter);
                    if (end.isAfter(start) || end.isEqual(start)) {
                        break;
                    } else {
                        System.out.println("End date must be after or equal to start date.");
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing dates. Please try again.");
                }
            } else {
                System.out.println("Invalid date format. Use dd-MM-yyyy (e.g., 28-03-2026).");
            }
        }

        // Validate Reason
        String reason = "";
        while (true) {
            System.out.print("Reason for leave: ");
            reason = scanner.nextLine().trim();
            if (InputValidator.isValidString(reason) && reason.length() <= 200) {
                break;
            }
            System.out.println("Invalid reason. Please provide a reason (max 200 characters).");
        }

        if (leaveService.requestLeave(emp.getEmployeeID(), leaveType, startDate, endDate, reason)) {
            System.out.println("Leave request submitted successfully. Awaiting manager approval.");
        } else {
            System.out.println("Failed to submit leave request. Check for conflicting or overlapping leaves.");
        }
    }

    /**
     * View my leaves
     */
    private void viewMyLeaves() {
        String email = authService.getCurrentUser().getEmail();
        Employee emp = employeeService.getEmployeeByEmail(email);

        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        List<LeaveRequest> leaves = leaveService.getEmployeeLeaves(emp.getEmployeeID());

        if (leaves == null || leaves.isEmpty()) {
            System.out.println("No leave requests found.");
            return;
        }

        System.out.println("\n--- Your Leave Requests ---");
        System.out.printf("%-10s | %-12s | %-12s | %-12s | %-10s\n",
                "Leave ID", "Type", "Start Date", "End Date", "Status");
        System.out.println("-".repeat(70));

        for (LeaveRequest leave : leaves) {
            System.out.printf("%-10s | %-12s | %-12s | %-12s | %-10s\n",
                    leave.getLeaveID(),
                    leave.getLeaveType(),
                    leave.getStartDate(),
                    leave.getEndDate(),
                    leave.getStatus());
        }
    }

    /**
     * View my assignments
     */
    private void viewMyAssignments() {
        String email = authService.getCurrentUser().getEmail();
        Employee emp = employeeService.getEmployeeByEmail(email);

        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        List<ResourceAssignment> assignments = projectService.getEmployeeAssignments(emp.getEmployeeID());

        if (assignments == null || assignments.isEmpty()) {
            System.out.println("No project assignments.");
            return;
        }

        System.out.println("\n--- Your Project Assignments ---");
        System.out.printf("%-10s | %-20s | %-12s\n",
                "Project ID", "Project Name", "Allocation %");
        System.out.println("-".repeat(50));

        for (ResourceAssignment assign : assignments) {
            System.out.printf("%-10s | %-20s | %-12s\n",
                    assign.getProjectID(),
                    assign.getProjectID(),
                    assign.getAllocationPercentage() + "%");
        }
    }

    /**
     * View team hierarchy and company structure
     */
    private void viewTeamHierarchy() {
        String email = authService.getCurrentUser().getEmail();
        Employee emp = employeeService.getEmployeeByEmail(email);

        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        List<Employee> allEmployees = employeeService.getAllEmployees();
        if (allEmployees == null || allEmployees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("COMPANY HIERARCHY AND TEAM STRUCTURE");
        System.out.println("=".repeat(80));

        // Display current employee's manager
        System.out.println("\n--- Your Information ---");
        System.out.println("Name: " + emp.getFirstName() + " " + emp.getLastName());
        System.out.println("Employee ID: " + emp.getEmployeeID());
        System.out.println("Department: " + emp.getDepartment());
        System.out.println("Designation: " + emp.getDesignation());

        if (emp.getReportingManager() != null && !emp.getReportingManager().isEmpty()) {
            Employee manager = employeeService.getEmployeeByID(emp.getReportingManager());
            if (manager != null) {
                System.out.println("\n--- Your Manager ---");
                System.out.println("Name: " + manager.getFirstName() + " " + manager.getLastName());
                System.out.println("Employee ID: " + manager.getEmployeeID());
                System.out.println("Designation: " + manager.getDesignation());
            }
        } else {
            System.out.println("Reporting Manager: None (Top-level position)");
        }

        // Display team members (people reporting to same manager)
        List<Employee> teamMembers = new ArrayList<>();
        for (Employee other : allEmployees) {
            if (!other.getEmployeeID().equals(emp.getEmployeeID()) &&
                    emp.getReportingManager() != null &&
                    emp.getReportingManager().equals(other.getReportingManager())) {
                teamMembers.add(other);
            }
        }

        if (!teamMembers.isEmpty()) {
            System.out.println("\n--- Team Members (Same Manager) ---");
            System.out.printf("%-10s | %-20s | %-15s | %-15s\n",
                    "Emp ID", "Name", "Department", "Designation");
            System.out.println("-".repeat(65));
            for (Employee member : teamMembers) {
                System.out.printf("%-10s | %-20s | %-15s | %-15s\n",
                        member.getEmployeeID(),
                        member.getFirstName() + " " + member.getLastName(),
                        member.getDepartment(),
                        member.getDesignation());
            }
        }

        // Display company hierarchy by department
        System.out.println("\n--- Company Structure by Department ---");
        Map<String, List<Employee>> deptMap = new HashMap<>();
        for (Employee e : allEmployees) {
            deptMap.computeIfAbsent(e.getDepartment(), k -> new ArrayList<>()).add(e);
        }

        for (String dept : deptMap.keySet()) {
            System.out.println("\n" + dept + " (" + deptMap.get(dept).size() + " employees):");
            System.out.printf("  %-10s | %-20s | %-15s\n", "Emp ID", "Name", "Designation");
            System.out.println("  " + "-".repeat(50));
            for (Employee e : deptMap.get(dept)) {
                System.out.printf("  %-10s | %-20s | %-15s\n",
                        e.getEmployeeID(),
                        e.getFirstName() + " " + e.getLastName(),
                        e.getDesignation());
            }
        }

        // Display reporting chain
        System.out.println("\n--- Reporting Chain (Your Position in Hierarchy) ---");
        List<String> chain = new ArrayList<>();
        Employee current = emp;
        chain.add(current.getFirstName() + " " + current.getLastName() + " (" + current.getDesignation() + ")");

        while (current.getReportingManager() != null && !current.getReportingManager().isEmpty()) {
            current = employeeService.getEmployeeByID(current.getReportingManager());
            if (current != null) {
                chain.add(current.getFirstName() + " " + current.getLastName() + " (" + current.getDesignation() + ")");
            } else {
                break;
            }
        }

        for (int i = 0; i < chain.size(); i++) {
            if (i > 0) {
                System.out.print("    | Reports to\n");
            }
            System.out.println(chain.get(i));
        }
    }

    /**
     * Change password
     */
    private void changePassword() {
        System.out.println("\n--- Change Password ---");

        // Validate Current Password
        String currentPassword = "";
        while (true) {
            System.out.print("Current Password: ");
            currentPassword = scanner.nextLine();
            if (!currentPassword.isEmpty()) {
                break;
            }
            System.out.println("Password cannot be empty.");
        }

        // Validate New Password
        String newPassword = "";
        while (true) {
            System.out.println("\nPassword Requirements:");
            System.out.println(InputValidator.getPasswordValidationMessage());
            System.out.print("Enter new password: ");
            newPassword = scanner.nextLine();

            if (!InputValidator.isValidPassword(newPassword)) {
                System.out.println("Password does not meet requirements. Please try again.");
                continue;
            }

            if (newPassword.equals(currentPassword)) {
                System.out.println("New password must be different from current password.");
                continue;
            }

            break;
        }

        // Confirm New Password
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return;
        }

        if (authService.changePassword(currentPassword, newPassword)) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Failed to change password. Current password may be incorrect.");
        }
    }
}
