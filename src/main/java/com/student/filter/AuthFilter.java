package com.student.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Guards every page except login and static assets, disables caching of
 * authenticated pages, and checks the CSRF token on every POST.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (req.getCharacterEncoding() == null) req.setCharacterEncoding("UTF-8");

        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("Referrer-Policy", "same-origin");

        String path = req.getServletPath();
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        res.setHeader("Cache-Control", "no-store");

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String expected = (String) session.getAttribute("csrf");
            String actual = req.getParameter("_csrf");
            if (expected == null || actual == null || !MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8))) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Your form expired. Reload the page and try again.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private static boolean isPublic(String path) {
        return path.equals("/login") || path.startsWith("/css/") || path.startsWith("/js/")
            || path.equals("/favicon.svg") || path.equals("/") || path.equals("/index.jsp");
    }
}
