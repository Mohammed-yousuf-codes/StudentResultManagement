package com.student.servlet;

import com.student.dao.ResultDao;
import com.student.model.Student;
import com.student.util.DBConnection;
import com.student.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Student form = null;
        Integer edit = Web.intOrNull(req, "edit");
        if (edit != null) {
            try (Connection con = DBConnection.getConnection()) {
                form = ResultDao.student(con, edit);
            } catch (SQLException e) {
                throw new ServletException("Could not load the student.", e);
            }
            if (form == null) {
                Web.flash(req, "error", "That student no longer exists.");
                Web.redirect(req, res, "/students");
                return;
            }
        }
        render(req, res, form);
    }

    private void render(HttpServletRequest req, HttpServletResponse res, Student form)
            throws ServletException, IOException {
        String q = Web.str(req, "q");
        try (Connection con = DBConnection.getConnection()) {
            req.setAttribute("students", ResultDao.students(con, q));
        } catch (SQLException e) {
            throw new ServletException("Could not load students.", e);
        }
        req.setAttribute("q", q);
        req.setAttribute("form", form);
        Web.view(req, res, "students");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = Web.str(req, "action");
        if ("delete".equals(action)) {
            delete(req, res);
            return;
        }

        boolean editing = "edit".equals(action);
        Integer id = Web.intOrNull(req, "id");
        Integer semester = Web.intOrNull(req, "semester");
        Student s = new Student(id == null ? 0 : id, Web.str(req, "rollNo").toUpperCase(), Web.str(req, "fullName"),
                                Web.str(req, "email"), Web.str(req, "department"), semester == null ? 0 : semester);

        List<String> errors = new ArrayList<>();
        if (!s.getRollNo().matches("[A-Z0-9_-]{2,30}"))
            errors.add("Roll number must be 2–30 letters, digits, hyphens or underscores.");
        if (s.getFullName().length() < 2 || s.getFullName().length() > 120)
            errors.add("Full name must be between 2 and 120 characters.");
        if (!s.getEmail().isEmpty() && (s.getEmail().length() > 120 || !s.getEmail().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")))
            errors.add("Email address doesn't look valid.");
        if (s.getDepartment().isEmpty() || s.getDepartment().length() > 80)
            errors.add("Department is required (up to 80 characters).");
        if (semester == null || semester < 1 || semester > 8)
            errors.add("Semester must be between 1 and 8.");
        if (editing && id == null)
            errors.add("Missing student id.");

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            render(req, res, s);
            return;
        }

        String sql = editing
            ? "UPDATE students SET roll_no=?, full_name=?, email=?, department=?, semester=? WHERE id=?"
            : "INSERT INTO students(roll_no, full_name, email, department, semester) VALUES(?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getEmail().isEmpty() ? null : s.getEmail());
            ps.setString(4, s.getDepartment());
            ps.setInt(5, s.getSemester());
            if (editing) ps.setInt(6, id);
            ps.executeUpdate();
            Web.flash(req, "ok", (editing ? "Updated " : "Registered ") + s.getRollNo() + " — " + s.getFullName() + ".");
            Web.redirect(req, res, "/students");
        } catch (SQLIntegrityConstraintViolationException e) {
            req.setAttribute("errors", List.of("Roll number " + s.getRollNo() + " is already registered to another student."));
            render(req, res, s);
        } catch (SQLException e) {
            throw new ServletException("Could not save the student.", e);
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Integer id = Web.intOrNull(req, "id");
        if (id == null) {
            Web.redirect(req, res, "/students");
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            Student s = ResultDao.student(con, id);
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            if (s != null) Web.flash(req, "ok", "Removed " + s.getRollNo() + " and their marks.");
            Web.redirect(req, res, "/students");
        } catch (SQLException e) {
            throw new ServletException("Could not delete the student.", e);
        }
    }
}
