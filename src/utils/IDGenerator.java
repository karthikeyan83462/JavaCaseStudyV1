package utils;


public class IDGenerator {


    public static String generateEmployeeID(int sequenceNumber) {
        return String.format("EMP%03d", sequenceNumber);
    }


    public static String generateAdminID(int sequenceNumber) {
        return String.format("ADM%03d", sequenceNumber);
    }

    public static String generateProjectID(int sequenceNumber) {
        return String.format("PROJ%03d", sequenceNumber);
    }

    public static String generateAssignmentID(int sequenceNumber) {
        return String.format("ASSIGN%03d", sequenceNumber);
    }

    public static String generateAttendanceID(int sequenceNumber) {
        return String.format("ATT%04d", sequenceNumber);
    }

    public static String generateLeaveID(int sequenceNumber) {
        return String.format("LEAVE%03d", sequenceNumber);
    }

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
