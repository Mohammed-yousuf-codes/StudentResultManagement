package com.student.servlet;

import com.student.util.DBConnection;
import com.student.util.PasswordUtil;
import com.student.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Lets the signed-in administrator change their password. */
@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Web.view(req, res, "account");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String admin = (String) req.getSession().getAttribute("admin");
        String current = req.getParameter("current");
        String next = req.getParameter("next");
        String confirm = req.getParameter("confirm");

        List<String> errors = new ArrayList<>();
        if (next == null || next.length() < 8) errors.add("The new password must be at least 8 characters.");
        else if (!next.equals(confirm)) errors.add("The two new passwords don't match.");
        else if (next.equals(current)) errors.add("Choose a password different from the current one.");

        try (Connection con = DBConnection.getConnection()) {
            String stored = null;
            try (PreparedStatement ps = con.prepareStatement("SELECT password FROM admin_users WHERE username=?")) {
                ps.setString(1, admin);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stored = rs.getString(1);
                }
            }
            if (!PasswordUtil.verify(current, stored)) errors.add(0, "Your current password is incorrect.");
            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
                Web.view(req, res, "account");
                return;
            }
            try (PreparedStatement ps = con.prepareStatement("UPDATE admin_users SET password=? WHERE username=?")) {
                ps.setString(1, PasswordUtil.hash(next));
                ps.setString(2, admin);
                ps.executeUpdate();
            }
            Web.flash(req, "ok", "Password changed.");
            Web.redirect(req, res, "/account");
        } catch (SQLException e) {
            throw new ServletException("Could not update the password.", e);
        }
    }
}
