package com.student.servlet;

import com.student.model.Subject;
import com.student.util.DBConnection;
import com.student.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/subjects")
public class SubjectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Subject form = null;
        Integer edit = Web.intOrNull(req, "edit");
        if (edit != null) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT id, subject_code, subject_name, max_marks FROM subjects WHERE id=?")) {
                ps.setInt(1, edit);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) form = new Subject(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4));
                }
            } catch (SQLException e) {
                throw new ServletException("Could not load the subject.", e);
            }
        }
        render(req, res, form);
    }

    private void render(HttpServletRequest req, HttpServletResponse res, Subject form) throws ServletException, IOException {
        // id, code, name, max, marked count, average %
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT sub.id, sub.subject_code, sub.subject_name, sub.max_marks, COUNT(m.id), " +
                     "AVG(m.marks_obtained * 100 / sub.max_marks) FROM subjects sub " +
                     "LEFT JOIN marks m ON m.subject_id = sub.id " +
                     "GROUP BY sub.id, sub.subject_code, sub.subject_name, sub.max_marks ORDER BY sub.subject_code";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                double avg = rs.getDouble(6);
                rows.add(new Object[] {rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5),
                                       rs.wasNull() ? null : avg});
            }
        } catch (SQLException e) {
            throw new ServletException("Could not load subjects.", e);
        }
        req.setAttribute("subjects", rows);
        req.setAttribute("form", form);
        Web.view(req, res, "subjects");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = Web.str(req, "action");
        Integer id = Web.intOrNull(req, "id");

        if ("delete".equals(action)) {
            if (id != null) {
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement("DELETE FROM subjects WHERE id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    Web.flash(req, "ok", "Subject removed along with its recorded marks.");
                } catch (SQLException e) {
                    throw new ServletException("Could not delete the subject.", e);
                }
            }
            Web.redirect(req, res, "/subjects");
            return;
        }

        boolean editing = "edit".equals(action);
        Integer max = Web.intOrNull(req, "maxMarks");
        Subject s = new Subject(id == null ? 0 : id, Web.str(req, "code").toUpperCase(), Web.str(req, "name"),
                                max == null ? 0 : max);

        List<String> errors = new ArrayList<>();
        if (!s.getSubjectCode().matches("[A-Z0-9_-]{2,20}"))
            errors.add("Subject code must be 2–20 letters or digits.");
        if (s.getSubjectName().length() < 2 || s.getSubjectName().length() > 100)
            errors.add("Subject name must be between 2 and 100 characters.");
        if (max == null || max < 10 || max > 1000)
            errors.add("Maximum marks must be a whole number from 10 to 1000.");

        try (Connection con = DBConnection.getConnection()) {
            if (errors.isEmpty() && editing && id != null) {
                try (PreparedStatement ps = con.prepareStatement("SELECT MAX(marks_obtained) FROM marks WHERE subject_id=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getObject(1) != null && rs.getDouble(1) > max) {
                            errors.add("A student already has " + rs.getBigDecimal(1).stripTrailingZeros().toPlainString()
                                       + " in this subject, so maximum marks can't be lower than that.");
                        }
                    }
                }
            }
            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
                render(req, res, s);
                return;
            }
            String sql = editing
                ? "UPDATE subjects SET subject_code=?, subject_name=?, max_marks=? WHERE id=?"
                : "INSERT INTO subjects(subject_code, subject_name, max_marks) VALUES(?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, s.getSubjectCode());
                ps.setString(2, s.getSubjectName());
                ps.setInt(3, s.getMaxMarks());
                if (editing) ps.setInt(4, id);
                ps.executeUpdate();
            }
            Web.flash(req, "ok", (editing ? "Updated " : "Added ") + s.getSubjectCode() + ".");
            Web.redirect(req, res, "/subjects");
        } catch (SQLIntegrityConstraintViolationException e) {
            req.setAttribute("errors", List.of("Subject code " + s.getSubjectCode() + " already exists."));
            render(req, res, s);
        } catch (SQLException e) {
            throw new ServletException("Could not save the subject.", e);
        }
    }
}
