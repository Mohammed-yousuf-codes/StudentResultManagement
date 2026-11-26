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

/** Printable statement of marks for one student. */
@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Integer id = Web.intOrNull(req, "id");
        try (Connection con = DBConnection.getConnection()) {
            Student s = id == null ? null : ResultDao.student(con, id);
            if (s == null) {
                Web.flash(req, "error", "Choose a student to view their statement of marks.");
                Web.redirect(req, res, "/results");
                return;
            }
            req.setAttribute("student", s);
            req.setAttribute("sheet", ResultDao.sheet(con, id));
            req.setAttribute("result", ResultDao.one(con, id));
            Web.view(req, res, "report");
        } catch (SQLException e) {
            throw new ServletException("Could not load the report.", e);
        }
    }
}
