<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.student.model.Student, com.student.model.MarkEntry, com.student.model.StudentResult, com.student.util.GradeUtil" %>
<% String pageTitle = "Statement of marks"; String nav = "results"; %>
<%@ include file="_top.jspf" %>
<%
    Student st = (Student) request.getAttribute("student");
    StudentResult r = (StudentResult) request.getAttribute("result");
    @SuppressWarnings("unchecked") List<MarkEntry> sheet = (List<MarkEntry>) request.getAttribute("sheet");
%>
<div class="head no-print">
  <div><a class="back" href="<%= ctx %>/results">← Result register</a></div>
  <div class="head-actions">
    <a class="btn" href="<%= ctx %>/marks?student=<%= st.getId() %>">Edit marks</a>
    <button class="btn btn-primary" type="button" data-print>Print / save PDF</button>
  </div>
</div>

<article class="statement">
  <header>
    <p class="st-office">Office of the Controller of Examinations</p>
    <h1>Statement of Marks</h1>
  </header>
  <dl class="st-who">
    <div><dt>Name</dt><dd><%= Html.esc(st.getFullName()) %></dd></div>
    <div><dt>Roll number</dt><dd class="mono"><%= Html.esc(st.getRollNo()) %></dd></div>
    <div><dt>Department</dt><dd><%= Html.esc(st.getDepartment()) %></dd></div>
    <div><dt>Semester</dt><dd><%= st.getSemester() %></dd></div>
  </dl>

  <table class="ledger st-table">
    <thead><tr><th>Code</th><th>Subject</th><th class="r">Max</th><th class="r">Obtained</th><th class="r">%</th><th class="c">Grade</th><th>Result</th></tr></thead>
    <tbody>
    <% for (MarkEntry e : sheet) { %>
      <tr>
        <td class="mono"><%= Html.esc(e.getSubjectCode()) %></td>
        <td><%= Html.esc(e.getSubjectName()) %></td>
        <td class="r num"><%= e.getMaxMarks() %></td>
        <td class="r num"><%= e.isRecorded() ? Html.num(e.getObtained()) : "—" %></td>
        <td class="r num"><%= e.isRecorded() ? Html.pct(e.getPercent()) : "" %></td>
        <td class="c grade"><%= e.getGrade() %></td>
        <td><%= !e.isRecorded() ? "<span class=\"muted\">Not entered</span>" : e.isPassed() ? "Pass" : "<b class=\"neg\">Fail</b>" %></td>
      </tr>
    <% } %>
    </tbody>
    <% if (r != null && r.hasMarks()) { %>
    <tfoot><tr>
      <th colspan="2">Total</th>
      <td class="r num"><%= Html.num(r.getMaxTotal()) %></td>
      <td class="r num"><b><%= Html.num(r.getTotalMarks()) %></b></td>
      <td class="r num"><b><%= Html.pct(r.getPercentage()) %></b></td>
      <td class="c grade"><%= r.getGrade() %></td>
      <td></td>
    </tr></tfoot>
    <% } %>
  </table>

  <div class="st-verdict">
    <% if (r == null || !r.hasMarks()) { %>
      <p>No marks have been entered for this student yet.</p>
    <% } else { %>
      <div class="stamp stamp-<%= r.getStatus().toLowerCase() %>"><%= r.getStatus() %></div>
      <p>
        Overall grade <b><%= r.getGrade() %></b> (<%= GradeUtil.remark(r.getGrade()) %>) with
        <b><%= Html.pct(r.getPercentage()) %>%</b> across <%= r.getSubjectCount() %> of <%= r.getSubjectTotal() %> subjects.
        <% if ("Fail".equals(r.getStatus())) { %> <%= r.getFailedSubjects() %> subject(s) fall below the 40% pass mark.<% } %>
        <% if ("Incomplete".equals(r.getStatus())) { %> The result is final only once every subject has been marked.<% } %>
      </p>
    <% } %>
  </div>

  <footer class="st-sign">
    <div><span></span>Prepared by</div>
    <div><span></span>Controller of Examinations</div>
  </footer>
  <p class="st-scale">Scale: A+ 90–100 · A 80–89 · B 70–79 · C 60–69 · D 40–59 · F below 40. Pass requires 40% or more in every subject.</p>
</article>
<%@ include file="_bottom.jspf" %>
