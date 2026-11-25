package com.student.model;

import com.student.util.GradeUtil;

/** One subject row on a mark sheet; obtained is null when no mark is recorded. */
public class MarkEntry {
    private final int subjectId;
    private final String subjectCode;
    private final String subjectName;
    private final int maxMarks;
    private final Double obtained;

    public MarkEntry(int subjectId, String subjectCode, String subjectName, int maxMarks, Double obtained) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.maxMarks = maxMarks;
        this.obtained = obtained;
    }

    public int getSubjectId() { return subjectId; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public int getMaxMarks() { return maxMarks; }
    public Double getObtained() { return obtained; }
    public boolean isRecorded() { return obtained != null; }
    public double getPercent() { return obtained == null ? 0 : obtained * 100.0 / maxMarks; }
    public String getGrade() { return obtained == null ? "" : GradeUtil.grade(getPercent()); }
    public boolean isPassed() { return obtained != null && getPercent() >= GradeUtil.PASS_PERCENT; }
}
