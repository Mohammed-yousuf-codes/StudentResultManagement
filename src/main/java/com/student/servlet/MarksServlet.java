package com.student.servlet;

import com.student.dao.ResultDao;
import com.student.model.MarkEntry;
import com.student.model.Student;
import com.student.util.DBConnection;
import com.student.util.Html;
import com.student.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/** Mark sheet: pick a student, then enter or correct every subject's marks on one form. */
@WebServlet("/marks")
public class MarksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        render(req, res, Web.intOrNull(req, "student"), null);
    }

    private void render(HttpServletRequest req, HttpServletResponse res, Integer studentId, Map<Integer, String> typed)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection()) {
            req.setAttribute("students", ResultDao.students(con, null));
            if (studentId != null) {
                Student s = ResultDao.student(con, studentId);
                if (s != null) {
                    req.setAttribute("student", s);
                    req.setAttribute("sheet", ResultDao.sheet(con, studentId));
                    req.setAttribute("result", ResultDao.one(con, studentId));
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Could not load the mark sheet.", e);
        }
        req.setAttribute("typed", typed);
        Web.view(req, res, "marks");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Integer studentId = Web.intOrNull(req, "student");
        if (studentId == null) {
            Web.redirect(req, res, "/marks");
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            Student student = ResultDao.student(con, studentId);
            if (student == null) {
                Web.flash(req, "error", "That student no longer exists.");
                Web.redirect(req, res, "/marks");
                return;
            }
            List<MarkEntry> sheet = ResultDao.sheet(con, studentId);
            Map<Integer, String> typed = new HashMap<>();
            Map<Integer, Double> values = new HashMap<>();
            List<String> errors = new ArrayList<>();

            for (MarkEntry e : sheet) {
                String raw = Web.str(req, "m_" + e.getSubjectId());
                typed.put(e.getSubjectId(), raw);
                if (raw.isEmpty()) {
                    values.put(e.getSubjectId(), null);
                    continue;
                }
                try {
                    double v = Double.parseDouble(raw);
                    if (v < 0 || v > e.getMaxMarks() || Double.isNaN(v)) {
                        errors.add(e.getSubjectCode() + ": marks must be between 0 and " + e.getMaxMarks() + ".");
                    } else if (Math.round(v * 100) != v * 100) {
                        errors.add(e.getSubjectCode() + ": use at most two decimal places.");
                    } else {
                        values.put(e.getSubjectId(), v);
                    }
                } catch (NumberFormatException ex) {
                    errors.add(e.getSubjectCode() + ": \"" + raw + "\" is not a number.");
                }
            }

            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
                render(req, res, studentId, typed);
                return;
            }

            con.setAutoCommit(false);
            int saved = 0, cleared = 0;
            try (PreparedStatement up = con.prepareStatement(
                     "INSERT INTO marks(student_id, subject_id, marks_obtained) VALUES(?,?,?) " +
                     "ON DUPLICATE KEY UPDATE marks_obtained = VALUES(marks_obtained)");
                 PreparedStatement del = con.prepareStatement("DELETE FROM marks WHERE student_id=? AND subject_id=?")) {
                for (MarkEntry e : sheet) {
                    Double v = values.get(e.getSubjectId());
                    if (v == null) {
                        if (e.isRecorded()) {
                            del.setInt(1, studentId);
                            del.setInt(2, e.getSubjectId());
                            del.addBatch();
                            cleared++;
                        }
                    } else if (!v.equals(e.getObtained())) {
                        up.setInt(1, studentId);
                        up.setInt(2, e.getSubjectId());
                        up.setDouble(3, v);
                        up.addBatch();
                        saved++;
                    }
                }
                up.executeBatch();
                del.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

            String msg = saved == 0 && cleared == 0 ? "No changes to save for " + student.getRollNo() + "."
                : "Saved mark sheet for " + student.getRollNo() + ": " + saved + " updated"
                  + (cleared > 0 ? ", " + cleared + " cleared" : "") + ".";
            Web.flash(req, "ok", msg);
            Web.redirect(req, res, "/marks?student=" + studentId);
        } catch (SQLException e) {
            throw new ServletException("Could not save marks.", e);
        }
    }
}
