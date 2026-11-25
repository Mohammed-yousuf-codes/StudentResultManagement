package com.student.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class Web {
    private Web() {}

    public static String str(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    public static Integer intOrNull(HttpServletRequest req, String name) {
        try {
            return Integer.valueOf(str(req, name));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** One-shot message shown on the next rendered page. type: ok | error */
    public static void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flash", new String[] {type, message});
    }

    public static void view(HttpServletRequest req, HttpServletResponse res, String name)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + name + ".jsp").forward(req, res);
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse res, String path) throws IOException {
        res.sendRedirect(req.getContextPath() + path);
    }
}
