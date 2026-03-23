package models;

/**
 * Enum for all departments in the organization
 */
public enum Department {
    IT("IT", "Information Technology"),
    HR("HR", "Human Resources"),
    FINANCE("Finance", "Finance"),
    OPERATIONS("Operations", "Operations"),
    SALES("Sales", "Sales"),
    MARKETING("Marketing", "Marketing");

    private final String shortName;
    private final String fullName;

    Department(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * Get department by index (1-based for user selection)
     */
    public static Department getDepartmentByIndex(int index) {
        if (index < 1 || index > values().length) {
            return null;
        }
        return values()[index - 1];
    }

    /**
     * Get all department names with indices
     */
    public static String getDepartmentOptions() {
        StringBuilder sb = new StringBuilder();
        Department[] depts = values();
        for (int i = 0; i < depts.length; i++) {
            sb.append((i + 1)).append(". ").append(depts[i].fullName).append("\n");
        }
        return sb.toString();
    }
}
