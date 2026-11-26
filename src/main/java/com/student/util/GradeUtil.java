package com.student.util;

public final class GradeUtil {
    public static final double PASS_PERCENT = 40.0;

    private GradeUtil() {}

    /** A+ 90-100, A 80-89, B 70-79, C 60-69, D 40-59, F below 40. */
    public static String grade(double percent) {
        if (percent >= 90) return "A+";
        if (percent >= 80) return "A";
        if (percent >= 70) return "B";
        if (percent >= 60) return "C";
        if (percent >= 40) return "D";
        return "F";
    }

    public static String remark(String grade) {
        switch (grade) {
            case "A+": return "Outstanding";
            case "A": return "Excellent";
            case "B": return "Good";
            case "C": return "Fair";
            case "D": return "Pass";
            case "F": return "Fail";
            default: return "";
        }
    }
}
