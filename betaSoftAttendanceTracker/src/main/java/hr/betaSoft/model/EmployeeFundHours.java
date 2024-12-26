package hr.betaSoft.model;

public class EmployeeFundHours {

    private Integer serialNumber;

    private String employeeName;

    private Integer mondayWorkHours;

    private Integer tuesdayWorkHours;

    private Integer wednesdayWorkHours;

    private Integer thursdayWorkHours;

    private Integer fridayWorkHours;

    private Integer saturdayWorkHours;

    private Integer sundayWorkHours;

    private Integer totalWeeklyWorkHours;

    private Integer totalEmployeeFundHours;

    private Integer totalEmployeeWorkHours;

    private Integer nonWorkingHours;

    public EmployeeFundHours(
            Integer serialNumber,
            String employeeName,
            Integer mondayWorkHours,
            Integer tuesdayWorkHours,
            Integer wednesdayWorkHours,
            Integer thursdayWorkHours,
            Integer fridayWorkHours,
            Integer saturdayWorkHours,
            Integer sundayWorkHours,
            Integer totalWeeklyWorkHours,
            Integer totalEmployeeFundHours,
            Integer totalEmployeeWorkHours,
            Integer nonWorkingHours) {
        this.serialNumber = serialNumber;
        this.employeeName = employeeName;
        this.mondayWorkHours = mondayWorkHours;
        this.tuesdayWorkHours = tuesdayWorkHours;
        this.wednesdayWorkHours = wednesdayWorkHours;
        this.thursdayWorkHours = thursdayWorkHours;
        this.fridayWorkHours = fridayWorkHours;
        this.saturdayWorkHours = saturdayWorkHours;
        this.sundayWorkHours = sundayWorkHours;
        this.totalWeeklyWorkHours = totalWeeklyWorkHours;
        this.totalEmployeeFundHours = totalEmployeeFundHours;
        this.totalEmployeeWorkHours = totalEmployeeWorkHours;
        this.nonWorkingHours = nonWorkingHours;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Integer getMondayWorkHours() {
        return mondayWorkHours;
    }

    public void setMondayWorkHours(Integer mondayWorkHours) {
        this.mondayWorkHours = mondayWorkHours;
    }

    public Integer getTuesdayWorkHours() {
        return tuesdayWorkHours;
    }

    public void setTuesdayWorkHours(Integer tuesdayWorkHours) {
        this.tuesdayWorkHours = tuesdayWorkHours;
    }

    public Integer getWednesdayWorkHours() {
        return wednesdayWorkHours;
    }

    public void setWednesdayWorkHours(Integer wednesdayWorkHours) {
        this.wednesdayWorkHours = wednesdayWorkHours;
    }

    public Integer getThursdayWorkHours() {
        return thursdayWorkHours;
    }

    public void setThursdayWorkHours(Integer thursdayWorkHours) {
        this.thursdayWorkHours = thursdayWorkHours;
    }

    public Integer getFridayWorkHours() {
        return fridayWorkHours;
    }

    public void setFridayWorkHours(Integer fridayWorkHours) {
        this.fridayWorkHours = fridayWorkHours;
    }

    public Integer getSaturdayWorkHours() {
        return saturdayWorkHours;
    }

    public void setSaturdayWorkHours(Integer saturdayWorkHours) {
        this.saturdayWorkHours = saturdayWorkHours;
    }

    public Integer getSundayWorkHours() {
        return sundayWorkHours;
    }

    public void setSundayWorkHours(Integer sundayWorkHours) {
        this.sundayWorkHours = sundayWorkHours;
    }

    public Integer getTotalWeeklyWorkHours() {
        return totalWeeklyWorkHours;
    }

    public void setTotalWeeklyWorkHours(Integer totalWeeklyWorkHours) {
        this.totalWeeklyWorkHours = totalWeeklyWorkHours;
    }

    public Integer getTotalEmployeeFundHours() {
        return totalEmployeeFundHours;
    }

    public void setTotalEmployeeFundHours(Integer totalEmployeeFundHours) {
        this.totalEmployeeFundHours = totalEmployeeFundHours;
    }

    public Integer getTotalEmployeeWorkHours() {
        return totalEmployeeWorkHours;
    }

    public void setTotalEmployeeWorkHours(Integer totalEmployeeWorkHours) {
        this.totalEmployeeWorkHours = totalEmployeeWorkHours;
    }

    public Integer getNonWorkingHours() {
        return nonWorkingHours;
    }

    public void setNonWorkingHours(Integer nonWorkingHours) {
        this.nonWorkingHours = nonWorkingHours;
    }
}
