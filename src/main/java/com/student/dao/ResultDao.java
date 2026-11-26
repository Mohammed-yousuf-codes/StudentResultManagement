package com.student.dao;

import com.student.model.MarkEntry;
import com.student.model.Student;
import com.student.model.StudentResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class ResultDao {
    private ResultDao() {}

    private static final String RESULT_SQL =
        "SELECT s.id, s.roll_no, s.full_name, s.department, s.semester, " +
        "COUNT(m.id) AS cnt, COALESCE(SUM(m.marks_obtained),0) AS total, " +
        "COALESCE(SUM(CASE WHEN m.id IS NOT NULL THEN sub.max_marks END),0) AS max_total, " +
        "COALESCE(SUM(CASE WHEN m.marks_obtained * 100 / sub.max_marks < 40 THEN 1 ELSE 0 END),0) AS failed " +
        "FROM students s LEFT JOIN marks m ON m.student_id = s.id " +
        "LEFT JOIN subjects sub ON sub.id = m.subject_id ";

    private static final String GROUP = " GROUP BY s.id, s.roll_no, s.full_name, s.department, s.semester ";

    public static int subjectTotal(Connection c) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM subjects")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** All results, optionally filtered by a roll/name/department search and a status. */
    public static List<StudentResult> list(Connection c, String query, String status) throws SQLException {
        boolean search = query != null && !query.isBlank();
        String sql = RESULT_SQL
                   + (search ? "WHERE s.roll_no LIKE ? OR s.full_name LIKE ? OR s.department LIKE ? " : "")
                   + GROUP + "ORDER BY s.roll_no";
        int subjects = subjectTotal(c);
        List<StudentResult> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (search) {
                String like = "%" + query.trim() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentResult r = map(rs, subjects);
                    if (status == null || status.isBlank() || status.equalsIgnoreCase(r.getStatus())) out.add(r);
                }
            }
        }
        return out;
    }

    public static StudentResult one(Connection c, int studentId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(RESULT_SQL + "WHERE s.id = ?" + GROUP)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs, subjectTotal(c)) : null;
            }
        }
    }

    /** Every subject, with this student's mark where one exists. */
    public static List<MarkEntry> sheet(Connection c, int studentId) throws SQLException {
        String sql = "SELECT sub.id, sub.subject_code, sub.subject_name, sub.max_marks, m.marks_obtained " +
                     "FROM subjects sub LEFT JOIN marks m ON m.subject_id = sub.id AND m.student_id = ? " +
                     "ORDER BY sub.subject_code";
        List<MarkEntry> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double v = rs.getDouble(5);
                    Double obtained = rs.wasNull() ? null : v; // must be checked right after reading column 5
                    out.add(new MarkEntry(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), obtained));
                }
            }
        }
        return out;
    }

    public static Student student(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id, roll_no, full_name, email, department, semester FROM students WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Student(rs.getInt(1), rs.getString(2), rs.getString(3),
                                               rs.getString(4), rs.getString(5), rs.getInt(6)) : null;
            }
        }
    }

    public static List<Student> students(Connection c, String query) throws SQLException {
        boolean search = query != null && !query.isBlank();
        String sql = "SELECT id, roll_no, full_name, email, department, semester FROM students "
                   + (search ? "WHERE roll_no LIKE ? OR full_name LIKE ? OR department LIKE ? OR email LIKE ? " : "")
                   + "ORDER BY roll_no";
        List<Student> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (search) {
                String like = "%" + query.trim() + "%";
                for (int i = 1; i <= 4; i++) ps.setString(i, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Student(rs.getInt(1), rs.getString(2), rs.getString(3),
                                        rs.getString(4), rs.getString(5), rs.getInt(6)));
                }
            }
        }
        return out;
    }

    private static StudentResult map(ResultSet rs, int subjects) throws SQLException {
        StudentResult r = new StudentResult();
        r.setStudentId(rs.getInt("id"));
        r.setRollNo(rs.getString("roll_no"));
        r.setFullName(rs.getString("full_name"));
        r.setDepartment(rs.getString("department"));
        r.setSemester(rs.getInt("semester"));
        r.setSubjectCount(rs.getInt("cnt"));
        r.setTotalMarks(rs.getDouble("total"));
        r.setMaxTotal(rs.getDouble("max_total"));
        r.setFailedSubjects(rs.getInt("failed"));
        r.setSubjectTotal(subjects);
        return r;
    }
}
