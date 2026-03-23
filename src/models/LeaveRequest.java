package models;

/**
 * LeaveRequest entity representing employee leave requests
 */
public class LeaveRequest {
    private String leaveID;
    private String employeeID;
    private String leaveType; // CASUAL, SICK, MATERNITY, UNPAID, etc.
    private String startDate;
    private String endDate;
    private int numberOfDays;
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    private String approvedBy;
    private String remarks;

    /**
     * Constructor for creating a new leave request
     */
    public LeaveRequest(String leaveID, String employeeID, String leaveType,
                       String startDate, String endDate, int numberOfDays,
                       String reason, String status, String approvedBy, String remarks) {
        this.leaveID = leaveID;
        this.employeeID = employeeID;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfDays = numberOfDays;
        this.reason = reason;
        this.status = status;
        this.approvedBy = approvedBy;
        this.remarks = remarks;
    }

    /**
     * Constructor from CSV data
     */
    public LeaveRequest(String[] csvRow) {
        if (csvRow.length >= 10) {
            this.leaveID = csvRow[0];
            this.employeeID = csvRow[1];
            this.leaveType = csvRow[2];
            this.startDate = csvRow[3];
            this.endDate = csvRow[4];
            this.numberOfDays = Integer.parseInt(csvRow[5]);
            this.reason = csvRow[6];
            this.status = csvRow[7];
            this.approvedBy = csvRow[8];
            this.remarks = csvRow[9];
        }
    }

    /**
     * Converts leave request to CSV row
     */
    public String[] toCSVRow() {
        return new String[]{
            leaveID,
            employeeID,
            leaveType,
            startDate,
            endDate,
            String.valueOf(numberOfDays),
            reason,
            status,
            approvedBy,
            remarks
        };
    }

    // Getters and Setters
    public String getLeaveID() {
        return leaveID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
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

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Sets approval comments (alias for setRemarks)
     */
    public void setApprovalComments(String comments) {
        this.remarks = comments;
    }

    @Override
    public String toString() {
        return "LeaveRequest{" +
                "leaveID='" + leaveID + '\'' +
                ", employeeID='" + employeeID + '\'' +
                ", leaveType='" + leaveType + '\'' +
                ", status='" + status + '\'' +
                ", numberOfDays=" + numberOfDays +
                '}';
    }
}
