package com.student.servlet;

import com.student.util.DBConnection;
import com.student.util.PasswordUtil;
import com.student.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final int MAX_FAILURES = 5;
    private static final long LOCK_MILLIS = 60_000;
    /** username -> {failures, lockedUntil} */
    private static final Map<String, long[]> ATTEMPTS = new ConcurrentHashMap<>();
    private static final SecureRandom RNG = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("admin") != null) {
            Web.redirect(req, res, "/dashboard");
            return;
        }
        Web.view(req, res, "login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = Web.str(req, "username");
        String password = req.getParameter("password");
        req.setAttribute("username", username);

        if (username.isEmpty() || password == null || password.isEmpty()) {
            fail(req, res, "Enter both your username and password.");
            return;
        }

        String key = username.toLowerCase();
        long[] a = ATTEMPTS.get(key);
        if (a != null && a[1] > System.currentTimeMillis()) {
            long secs = (a[1] - System.currentTimeMillis()) / 1000 + 1;
            fail(req, res, "Too many failed attempts. Try again in " + secs + " seconds.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT username, password FROM admin_users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                String stored = rs.next() ? rs.getString("password") : null;
                if (stored == null || !PasswordUtil.verify(password, stored)) {
                    long[] n = ATTEMPTS.merge(key, new long[] {1, 0}, (o, x) -> new long[] {o[0] + 1, 0});
                    if (n[0] >= MAX_FAILURES) {
                        ATTEMPTS.put(key, new long[] {0, System.currentTimeMillis() + LOCK_MILLIS});
                        fail(req, res, "Too many failed attempts. Sign-in is locked for one minute.");
                    } else {
                        fail(req, res, "That username and password don't match.");
                    }
                    return;
                }
                ATTEMPTS.remove(key);
                String realName = rs.getString("username");
                if (PasswordUtil.needsUpgrade(stored)) {
                    try (PreparedStatement up = con.prepareStatement("UPDATE admin_users SET password=? WHERE username=?")) {
                        up.setString(1, PasswordUtil.hash(password));
                        up.setString(2, realName);
                        up.executeUpdate();
                    }
                }
                HttpSession old = req.getSession(false);
                if (old != null) old.invalidate();
                HttpSession session = req.getSession(true);
                session.setAttribute("admin", realName);
                byte[] t = new byte[24];
                RNG.nextBytes(t);
                session.setAttribute("csrf", Base64.getUrlEncoder().withoutPadding().encodeToString(t));
                Web.redirect(req, res, "/dashboard");
            }
        } catch (SQLException e) {
            log("Login database error", e);
            fail(req, res, "The database is not reachable. Check that MySQL is running and db.properties is correct.");
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse res, String msg) throws ServletException, IOException {
        req.setAttribute("error", msg);
        Web.view(req, res, "login");
    }
}
