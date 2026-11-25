package com.student.servlet;

import com.student.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM students ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {
            request.setAttribute("students", rs);
            request.getRequestDispatcher("/students.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Could not load students.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try (Connection con = DBConnection.getConnection()) {
            if ("delete".equals(action)) {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE id=?")) {
                    ps.setInt(1, Integer.parseInt(request.getParameter("id")));
                    ps.executeUpdate();
                }
            } else {
                String sql = "INSERT INTO students(roll_no,full_name,email,department,semester) VALUES(?,?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, request.getParameter("rollNo"));
                    ps.setString(2, request.getParameter("fullName"));
                    ps.setString(3, request.getParameter("email"));
                    ps.setString(4, request.getParameter("department"));
                    ps.setInt(5, Integer.parseInt(request.getParameter("semester")));
                    ps.executeUpdate();
                }
            }
            response.sendRedirect(request.getContextPath() + "/students");
        } catch (SQLException | NumberFormatException e) {
            request.setAttribute("error", "Could not save student. Check that the roll number is unique and all fields are valid.");
            doGet(request, response);
        }
    }
}
