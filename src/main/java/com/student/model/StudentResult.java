package com.student.model;

import com.student.util.GradeUtil;

/** Aggregated result for one student across the subjects that have marks recorded. */
public class StudentResult {
    private int studentId;
    private String rollNo;
    private String fullName;
    private String department;
    private int semester;
    private int subjectCount;      // subjects with marks
    private int subjectTotal;      // subjects offered
    private double totalMarks;
    private double maxTotal;
    private int failedSubjects;

    public int getStudentId() { return studentId; }
    public void setStudentId(int v) { studentId = v; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String v) { rollNo = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }
    public String getDepartment() { return department; }
    public void setDepartment(String v) { department = v; }
    public int getSemester() { return semester; }
    public void setSemester(int v) { semester = v; }
    public int getSubjectCount() { return subjectCount; }
    public void setSubjectCount(int v) { subjectCount = v; }
    public int getSubjectTotal() { return subjectTotal; }
    public void setSubjectTotal(int v) { subjectTotal = v; }
    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double v) { totalMarks = v; }
    public double getMaxTotal() { return maxTotal; }
    public void setMaxTotal(double v) { maxTotal = v; }
    public int getFailedSubjects() { return failedSubjects; }
    public void setFailedSubjects(int v) { failedSubjects = v; }

    public boolean hasMarks() { return subjectCount > 0; }

    /** Percentage over the maximum marks of the subjects that have been marked. */
    public double getPercentage() {
        return maxTotal > 0 ? totalMarks * 100.0 / maxTotal : 0;
    }

    public String getGrade() {
        return hasMarks() ? GradeUtil.grade(getPercentage()) : "";
    }

    /** Pass only when every subject is marked and each is at least 40%. */
    public String getStatus() {
        if (!hasMarks()) return "Pending";
        if (failedSubjects > 0) return "Fail";
        if (subjectCount < subjectTotal) return "Incomplete";
        return "Pass";
    }
}
