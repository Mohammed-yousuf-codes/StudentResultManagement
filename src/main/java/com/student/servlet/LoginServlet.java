package com.student.servlet;

import com.student.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String sql = "SELECT username FROM admin_users WHERE username=? AND password=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    request.getSession(true).setAttribute("admin", rs.getString("username"));
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Unable to connect to the database. Check DBConnection.java.", e);
        }
    }
}
