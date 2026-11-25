<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.Map, com.student.model.StudentResult" %>
<% String pageTitle = "Overview"; String nav = "dashboard"; %>
<%@ include file="_top.jspf" %>
<%
    @SuppressWarnings("unchecked") Map<String, Integer> byStatus = (Map<String, Integer>) request.getAttribute("byStatus");
    @SuppressWarnings("unchecked") List<StudentResult> top = (List<StudentResult>) request.getAttribute("top");
    @SuppressWarnings("unchecked") List<Object[]> stats = (List<Object[]>) request.getAttribute("subjectStats");
    int students = (Integer) request.getAttribute("studentCount");
    int subjects = (Integer) request.getAttribute("subjectCount");
    int marks = (Integer) request.getAttribute("markCount");
    int expected = students * subjects;
%>
<div class="head">
  <div>
    <h1>Overview</h1>
    <p class="sub">Where the register stands right now.</p>
  </div>
  <div class="head-actions">
    <a class="btn" href="<%= ctx %>/students">Register a student</a>
    <a class="btn btn-primary" href="<%= ctx %>/marks">Open mark sheet</a>
  </div>
</div>

<section class="figures">
  <div><b><%= students %></b><span>students on roll</span></div>
  <div><b><%= subjects %></b><span>subjects offered</span></div>
  <div><b><%= marks %><small> / <%= expected %></small></b><span>marks recorded</span></div>
  <div><b><%= byStatus.get("Pass") %></b><span>cleared every subject</span></div>
</section>

<div class="cols">
  <section class="box">
    <h2>Result status</h2>
    <% if (students == 0) { %>
      <p class="empty">No students yet. <a href="<%= ctx %>/students">Register the first one.</a></p>
    <% } else { %>
      <div class="stack" role="img" aria-label="Students by result status">
        <% for (Map.Entry<String, Integer> e : byStatus.entrySet()) { if (e.getValue() == 0) continue; %>
          <span class="seg seg-<%= e.getKey().toLowerCase() %>" style="flex:<%= e.getValue() %>"></span>
        <% } %>
      </div>
      <dl class="legend">
        <% for (Map.Entry<String, Integer> e : byStatus.entrySet()) { %>
          <div><dt><i class="seg-<%= e.getKey().toLowerCase() %>"></i><a href="<%= ctx %>/results?status=<%= e.getKey() %>"><%= e.getKey() %></a></dt><dd><%= e.getValue() %></dd></div>
        <% } %>
      </dl>
      <p class="note">Incomplete: no subject failed yet, but some marks are still missing.</p>
    <% } %>
  </section>

  <section class="box">
    <h2>Highest percentages</h2>
    <% if (top.isEmpty()) { %>
      <p class="empty">Rankings appear once marks are entered.</p>
    <% } else { %>
      <ol class="rank">
        <% for (StudentResult r : top) { %>
          <li>
            <a href="<%= ctx %>/report?id=<%= r.getStudentId() %>"><span class="mono"><%= Html.esc(r.getRollNo()) %></span> <%= Html.esc(r.getFullName()) %></a>
            <span class="num"><%= Html.pct(r.getPercentage()) %>%</span>
            <span class="grade"><%= r.getGrade() %></span>
          </li>
        <% } %>
      </ol>
    <% } %>
  </section>
</div>

<section class="box">
  <h2>Subject averages</h2>
  <% if (stats.isEmpty()) { %>
    <p class="empty">No subjects yet. <a href="<%= ctx %>/subjects">Add subjects</a> before entering marks.</p>
  <% } else { %>
  <table class="ledger">
    <thead><tr><th>Code</th><th>Subject</th><th class="r">Marked</th><th class="w40">Class average</th><th class="r">Below 40%</th></tr></thead>
    <tbody>
    <% for (Object[] s : stats) { int n = (Integer) s[3]; double avg = (Double) s[2]; int below = (Integer) s[4]; %>
      <tr>
        <td class="mono"><%= Html.esc(s[0]) %></td>
        <td><%= Html.esc(s[1]) %></td>
        <td class="r num"><%= n %></td>
        <td>
          <% if (n == 0) { %><span class="muted">—</span><% } else { %>
          <div class="bar"><span style="width:<%= Math.min(100, avg) %>%" class="<%= avg < 40 ? "low" : "" %>"></span><i title="40% pass line"></i></div>
          <span class="num bar-val"><%= Html.pct(avg) %>%</span>
          <% } %>
        </td>
        <td class="r num <%= below > 0 ? "neg" : "" %>"><%= below %></td>
      </tr>
    <% } %>
    </tbody>
  </table>
  <% } %>
</section>
<%@ include file="_bottom.jspf" %>
