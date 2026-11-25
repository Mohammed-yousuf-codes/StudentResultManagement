package com.student.servlet;

import com.student.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/marks")
public class MarksServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement students = con.prepareStatement("SELECT id,roll_no,full_name FROM students ORDER BY roll_no");
             ResultSet studentRows = students.executeQuery();
             PreparedStatement subjects = con.prepareStatement("SELECT id,subject_code,subject_name,max_marks FROM subjects ORDER BY subject_name");
             ResultSet subjectRows = subjects.executeQuery()) {
            request.setAttribute("students", studentRows);
            request.setAttribute("subjects", subjectRows);
            request.getRequestDispatcher("/marks.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Could not load marks form.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection()) {
            int studentId = Integer.parseInt(request.getParameter("studentId"));
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            double marks = Double.parseDouble(request.getParameter("marks"));
            if (marks < 0 || marks > 100) throw new IllegalArgumentException("Marks must be between 0 and 100.");
            String sql = "INSERT INTO marks(student_id,subject_id,marks_obtained) VALUES(?,?,?) " +
                         "ON DUPLICATE KEY UPDATE marks_obtained=VALUES(marks_obtained)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, subjectId);
                ps.setDouble(3, marks);
                ps.executeUpdate();
            }
            response.sendRedirect(request.getContextPath() + "/results");
        } catch (SQLException | NumberFormatException | IllegalArgumentException e) {
            throw new ServletException("Could not save marks. Check the selected student, subject and marks (0–100).", e);
        }
    }
}
