package com.student.servlet;

import com.student.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection()) {
            request.setAttribute("studentCount", count(con, "SELECT COUNT(*) FROM students"));
            request.setAttribute("subjectCount", count(con, "SELECT COUNT(*) FROM subjects"));
            request.setAttribute("markCount", count(con, "SELECT COUNT(*) FROM marks"));
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Could not load dashboard.", e);
        }
    }

    private int count(Connection con, String sql) throws SQLException {
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
