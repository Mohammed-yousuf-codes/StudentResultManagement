package com.student.servlet;

import com.student.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/results")
public class ResultsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String roll = request.getParameter("roll");
        String sql = "SELECT s.id,s.roll_no,s.full_name,s.department,s.semester, " +
            "COUNT(m.id) AS subject_count, COALESCE(SUM(m.marks_obtained),0) AS total, " +
            "COALESCE(AVG(m.marks_obtained),0) AS percentage, " +
            "COALESCE(MIN(m.marks_obtained),0) AS lowest " +
            "FROM students s LEFT JOIN marks m ON s.id=m.student_id ";
        if (roll != null && !roll.trim().isEmpty()) sql += "WHERE s.roll_no LIKE ? ";
        sql += "GROUP BY s.id,s.roll_no,s.full_name,s.department,s.semester ORDER BY s.roll_no";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (roll != null && !roll.trim().isEmpty()) ps.setString(1, "%" + roll.trim() + "%");
            request.setAttribute("results", ps.executeQuery());
            request.getRequestDispatcher("/results.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Could not load results.", e);
        }
    }
}
