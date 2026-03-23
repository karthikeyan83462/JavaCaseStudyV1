package models;

/**
 * Attendance entity representing employee attendance records
 */
public class Attendance {
    private String attendanceID;
    private String employeeID;
    private String date;
    private String status; // PRESENT, ABSENT, LEAVE, HALF_DAY
    private String checkInTime;
    private String checkOutTime;
    private String remarks;

    /**
     * Constructor for creating a new attendance record
     */
    public Attendance(String attendanceID, String employeeID, String date,
                     String status, String checkInTime, String checkOutTime, String remarks) {
        this.attendanceID = attendanceID;
        this.employeeID = employeeID;
        this.date = date;
        this.status = status;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.remarks = remarks;
    }

    /**
     * Constructor from CSV data
     */
    public Attendance(String[] csvRow) {
        if (csvRow.length >= 7) {
            this.attendanceID = csvRow[0];
            this.employeeID = csvRow[1];
            this.date = csvRow[2];
            this.status = csvRow[3];
            this.checkInTime = csvRow[4];
            this.checkOutTime = csvRow[5];
            this.remarks = csvRow[6];
        }
    }

    /**
     * Converts attendance to CSV row
     */
    public String[] toCSVRow() {
        return new String[]{
            attendanceID,
            employeeID,
            date,
            status,
            checkInTime,
            checkOutTime,
            remarks
        };
    }

    // Getters and Setters
    public String getAttendanceID() {
        return attendanceID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceID='" + attendanceID + '\'' +
                ", employeeID='" + employeeID + '\'' +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
