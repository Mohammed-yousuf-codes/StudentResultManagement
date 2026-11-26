<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.student.model.StudentResult" %>
<% String pageTitle = "Results"; String nav = "results"; %>
<%@ include file="_top.jspf" %>
<%
    @SuppressWarnings("unchecked") List<StudentResult> results = (List<StudentResult>) request.getAttribute("results");
    String q = (String) request.getAttribute("q");
    String status = (String) request.getAttribute("status");
    String qs = "?q=" + java.net.URLEncoder.encode(q, "UTF-8") + "&status=" + java.net.URLEncoder.encode(status, "UTF-8");
%>
<div class="head">
  <div>
    <h1>Result register</h1>
    <p class="sub">Percentage is total marks over the maximum of the subjects marked so far.</p>
  </div>
  <div class="head-actions no-print">
    <a class="btn" href="<%= ctx %>/results.csv<%= qs %>" download>Download CSV</a>
    <button class="btn" type="button" data-print>Print / save PDF</button>
  </div>
</div>

<form class="search filters no-print" method="get" action="<%= ctx %>/results" role="search">
  <input type="search" name="q" value="<%= Html.esc(q) %>" placeholder="Roll no., name or department" aria-label="Search results">
  <select name="status" aria-label="Status">
    <option value="">Any status</option>
    <% for (String s : new String[] {"Pass", "Fail", "Incomplete", "Pending"}) { %>
      <option <%= s.equals(status) ? "selected" : "" %>><%= s %></option>
    <% } %>
  </select>
  <button class="btn" type="submit">Apply</button>
  <% if (!q.isEmpty() || !status.isEmpty()) { %><a class="btn btn-quiet" href="<%= ctx %>/results">Clear</a><% } %>
</form>

<section class="box flush">
  <table class="ledger">
    <thead><tr>
      <th>Roll no.</th><th>Name</th><th>Department</th>
      <th class="r">Subjects</th><th class="r">Total</th><th class="r">%</th><th class="c">Grade</th><th>Status</th>
      <th class="no-print"><span class="sr">Report</span></th>
    </tr></thead>
    <tbody>
    <% if (results.isEmpty()) { %>
      <tr><td colspan="9" class="empty"><%= q.isEmpty() && status.isEmpty() ? "No students registered yet." : "No results match these filters." %></td></tr>
    <% } %>
    <% for (StudentResult r : results) { %>
      <tr>
        <td class="mono"><%= Html.esc(r.getRollNo()) %></td>
        <td><%= Html.esc(r.getFullName()) %></td>
        <td><%= Html.esc(r.getDepartment()) %> <span class="muted">· S<%= r.getSemester() %></span></td>
        <td class="r num"><%= r.getSubjectCount() %>/<%= r.getSubjectTotal() %></td>
        <td class="r num"><%= r.hasMarks() ? Html.num(r.getTotalMarks()) + "<span class=\"muted\"> / " + Html.num(r.getMaxTotal()) + "</span>" : "—" %></td>
        <td class="r num"><b><%= r.hasMarks() ? Html.pct(r.getPercentage()) : "—" %></b></td>
        <td class="c grade"><%= r.getGrade() %></td>
        <td><span class="status status-<%= r.getStatus().toLowerCase() %>"><%= r.getStatus() %></span>
            <% if (r.getFailedSubjects() > 0) { %><small class="line2 neg"><%= r.getFailedSubjects() %> below 40%</small><% } %></td>
        <td class="r no-print"><a href="<%= ctx %>/report?id=<%= r.getStudentId() %>">Statement</a></td>
      </tr>
    <% } %>
    </tbody>
  </table>
</section>
<%@ include file="_bottom.jspf" %>
