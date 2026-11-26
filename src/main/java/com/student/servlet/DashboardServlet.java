package com.student.servlet;

import com.student.dao.ResultDao;
import com.student.model.StudentResult;
import com.student.util.DBConnection;
import com.student.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection()) {
            List<StudentResult> all = ResultDao.list(con, null, null);
            Map<String, Integer> byStatus = new LinkedHashMap<>();
            for (String s : new String[] {"Pass", "Fail", "Incomplete", "Pending"}) byStatus.put(s, 0);
            for (StudentResult r : all) byStatus.merge(r.getStatus(), 1, Integer::sum);

            List<StudentResult> ranked = new ArrayList<>();
            for (StudentResult r : all) if (r.hasMarks()) ranked.add(r);
            ranked.sort(Comparator.comparingDouble(StudentResult::getPercentage).reversed());

            // code, name, average %, marked count, below-40 count
            List<Object[]> subjects = new ArrayList<>();
            String sql = "SELECT sub.subject_code, sub.subject_name, AVG(m.marks_obtained * 100 / sub.max_marks), " +
                         "COUNT(m.id), COALESCE(SUM(CASE WHEN m.marks_obtained * 100 / sub.max_marks < 40 THEN 1 ELSE 0 END),0) " +
                         "FROM subjects sub LEFT JOIN marks m ON m.subject_id = sub.id " +
                         "GROUP BY sub.id, sub.subject_code, sub.subject_name ORDER BY sub.subject_code";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    subjects.add(new Object[] {rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getInt(4), rs.getInt(5)});
                }
            }
            int marks;
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM marks")) {
                rs.next();
                marks = rs.getInt(1);
            }

            req.setAttribute("studentCount", all.size());
            req.setAttribute("subjectCount", subjects.size());
            req.setAttribute("markCount", marks);
            req.setAttribute("byStatus", byStatus);
            req.setAttribute("top", ranked.subList(0, Math.min(5, ranked.size())));
            req.setAttribute("subjectStats", subjects);
            Web.view(req, res, "dashboard");
        } catch (SQLException e) {
            throw new ServletException("Could not load the dashboard.", e);
        }
    }
}
