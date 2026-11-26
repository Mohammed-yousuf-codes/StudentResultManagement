package com.student.model;

public class Subject {
    private int id;
    private String subjectCode;
    private String subjectName;
    private int maxMarks;

    public Subject() {}

    public Subject(int id, String subjectCode, String subjectName, int maxMarks) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.maxMarks = maxMarks;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }
}
