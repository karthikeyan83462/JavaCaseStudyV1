package models;

/**
 * Enum for designations mapped to departments
 */
public enum Designation {
    // IT Department
    DEVELOPER("Developer", Department.IT),
    DEVOPS_ENGINEER("DevOps Engineer", Department.IT),
    QA_ENGINEER("QA Engineer", Department.IT),
    IT_MANAGER("IT Manager", Department.IT),

    // HR Department
    HR_SPECIALIST("HR Specialist", Department.HR),
    RECRUITER("Recruiter", Department.HR),
    HR_MANAGER("HR Manager", Department.HR),
    COMPENSATION_SPECIALIST("Compensation Specialist", Department.HR),

    // Finance Department
    ACCOUNTANT("Accountant", Department.FINANCE),
    FINANCIAL_ANALYST("Financial Analyst", Department.FINANCE),
    FINANCE_MANAGER("Finance Manager", Department.FINANCE),
    CFO("CFO", Department.FINANCE),

    // Operations Department
    OPERATIONS_COORDINATOR("Operations Coordinator", Department.OPERATIONS),
    OPERATIONS_SPECIALIST("Operations Specialist", Department.OPERATIONS),
    OPERATIONS_MANAGER("Operations Manager", Department.OPERATIONS),
    OPERATIONS_DIRECTOR("Operations Director", Department.OPERATIONS),

    // Sales Department
    SALES_EXECUTIVE("Sales Executive", Department.SALES),
    SALES_MANAGER("Sales Manager", Department.SALES),
    SALES_DIRECTOR("Sales Director", Department.SALES),
    BUSINESS_DEVELOPMENT("Business Development", Department.SALES),

    // Marketing Department
    MARKETING_EXECUTIVE("Marketing Executive", Department.MARKETING),
    CONTENT_MANAGER("Content Manager", Department.MARKETING),
    MARKETING_MANAGER("Marketing Manager", Department.MARKETING),
    BRAND_MANAGER("Brand Manager", Department.MARKETING);

    private final String title;
    private final Department department;

    Designation(String title, Department department) {
        this.title = title;
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public Department getDepartment() {
        return department;
    }

    /**
     * Get designations by department
     */
    public static Designation[] getDesignationsByDepartment(Department dept) {
        java.util.List<Designation> result = new java.util.ArrayList<>();
        for (Designation d : values()) {
            if (d.department == dept) {
                result.add(d);
            }
        }
        return result.toArray(new Designation[0]);
    }

    /**
     * Get designation by index within department
     */
    public static Designation getDesignationByIndex(Department dept, int index) {
        Designation[] designations = getDesignationsByDepartment(dept);
        if (index < 1 || index > designations.length) {
            return null;
        }
        return designations[index - 1];
    }

    /**
     * Get designation options string for a department
     */
    public static String getDesignationOptions(Department dept) {
        Designation[] designations = getDesignationsByDepartment(dept);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < designations.length; i++) {
            sb.append((i + 1)).append(". ").append(designations[i].title).append("\n");
        }
        return sb.toString();
    }
}
