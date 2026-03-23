package utils;

/**
 * Utility class for generating unique IDs with prefixes
 */
public class IDGenerator {

    /**
     * Generates employee ID (EMP001, EMP002, etc.)
     */
    public static String generateEmployeeID(int sequenceNumber) {
        return String.format("EMP%03d", sequenceNumber);
    }

    /**
     * Generates admin ID (ADM001, ADM002, etc.)
     */
    public static String generateAdminID(int sequenceNumber) {
        return String.format("ADM%03d", sequenceNumber);
    }

    /**
     * Generates project ID (PROJ001, PROJ002, etc.)
     */
    public static String generateProjectID(int sequenceNumber) {
        return String.format("PROJ%03d", sequenceNumber);
    }

    /**
     * Generates assignment ID (ASSIGN001, ASSIGN002, etc.)
     */
    public static String generateAssignmentID(int sequenceNumber) {
        return String.format("ASSIGN%03d", sequenceNumber);
    }

    /**
     * Generates attendance record ID (ATT001, ATT002, etc.)
     */
    public static String generateAttendanceID(int sequenceNumber) {
        return String.format("ATT%04d", sequenceNumber);
    }

    /**
     * Generates leave request ID (LEAVE001, LEAVE002, etc.)
     */
    public static String generateLeaveID(int sequenceNumber) {
        return String.format("LEAVE%03d", sequenceNumber);
    }

    /**
     * Gets next sequence number from current max ID
     */
    public static int getNextSequence(String lastID, String prefix) {
        if (lastID == null || lastID.isEmpty()) {
            return 1;
        }
        try {
            String numPart = lastID.substring(prefix.length());
            return Integer.parseInt(numPart) + 1;
        } catch (Exception e) {
            return 1;
        }
    }
}
