package com.student.servlet;

import com.student.dao.ResultDao;
import com.student.model.StudentResult;
import com.student.util.DBConnection;
import com.student.util.Html;
import com.student.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;

/** /results shows the class result register; /results.csv downloads the same rows. */
@WebServlet({"/results", "/results.csv"})
public class ResultsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String q = Web.str(req, "q");
        String status = Web.str(req, "status");
        List<StudentResult> results;
        try (Connection con = DBConnection.getConnection()) {
            results = ResultDao.list(con, q, status);
        } catch (SQLException e) {
            throw new ServletException("Could not load results.", e);
        }

        if (req.getServletPath().endsWith(".csv")) {
            writeCsv(res, results);
            return;
        }
        req.setAttribute("results", results);
        req.setAttribute("q", q);
        req.setAttribute("status", status);
        Web.view(req, res, "results");
    }

    private void writeCsv(HttpServletResponse res, List<StudentResult> rows) throws IOException {
        res.setContentType("text/csv;charset=UTF-8");
        res.setHeader("Content-Disposition", "attachment; filename=\"student_results.csv\"");
        PrintWriter w = res.getWriter();
        w.write('﻿'); // lets Excel detect UTF-8
        w.println("Roll No,Name,Department,Semester,Subjects Marked,Total,Out Of,Percentage,Grade,Status");
        for (StudentResult r : rows) {
            w.println(String.join(",",
                cell(r.getRollNo()), cell(r.getFullName()), cell(r.getDepartment()),
                String.valueOf(r.getSemester()), r.getSubjectCount() + "/" + r.getSubjectTotal(),
                Html.num(r.getTotalMarks()), Html.num(r.getMaxTotal()),
                r.hasMarks() ? Html.pct(r.getPercentage()) : "", r.getGrade(), r.getStatus()));
        }
    }

    /** Quotes a CSV cell and neutralises spreadsheet formula injection. */
    private static String cell(String v) {
        if (v == null) v = "";
        if (!v.isEmpty() && "=+-@\t\r".indexOf(v.charAt(0)) >= 0) v = "'" + v;
        return "\"" + v.replace("\"", "\"\"") + "\"";
    }
}
