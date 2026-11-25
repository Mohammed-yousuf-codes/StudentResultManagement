<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.Map, com.student.model.Student, com.student.model.MarkEntry, com.student.model.StudentResult" %>
<% String pageTitle = "Mark sheet"; String nav = "marks"; %>
<%@ include file="_top.jspf" %>
<%
    @SuppressWarnings("unchecked") List<Student> students = (List<Student>) request.getAttribute("students");
    @SuppressWarnings("unchecked") List<MarkEntry> sheet = (List<MarkEntry>) request.getAttribute("sheet");
    @SuppressWarnings("unchecked") Map<Integer, String> typed = (Map<Integer, String>) request.getAttribute("typed");
    Student st = (Student) request.getAttribute("student");
    StudentResult res = (StudentResult) request.getAttribute("result");
%>
<div class="head">
  <div>
    <h1>Mark sheet</h1>
    <p class="sub">Pick a student, then enter every subject on one sheet. Leave a box empty to clear that mark.</p>
  </div>
</div>

<div class="cols cols-picker">
  <aside class="box flush picker">
    <input type="search" class="picker-filter" placeholder="Filter students" aria-label="Filter students" data-filter="#roster">
    <ul id="roster">
      <% if (students.isEmpty()) { %><li class="empty">No students. <a href="<%= ctx %>/students">Register one</a>.</li><% } %>
      <% for (Student s : students) { %>
        <li><a href="<%= ctx %>/marks?student=<%= s.getId() %>" class="<%= st != null && st.getId() == s.getId() ? "on" : "" %>">
          <span class="mono"><%= Html.esc(s.getRollNo()) %></span> <%= Html.esc(s.getFullName()) %></a></li>
      <% } %>
    </ul>
  </aside>

  <section class="box">
  <% if (st == null) { %>
    <p class="empty">Choose a student from the list to open their sheet.</p>
  <% } else if (sheet.isEmpty()) { %>
    <p class="empty">There are no subjects yet. <a href="<%= ctx %>/subjects">Add subjects</a> first.</p>
  <% } else { %>
    <div class="sheet-head">
      <div>
        <h2><%= Html.esc(st.getFullName()) %></h2>
        <p class="sub"><span class="mono"><%= Html.esc(st.getRollNo()) %></span> · <%= Html.esc(st.getDepartment()) %> · Semester <%= st.getSemester() %></p>
      </div>
      <% if (res != null && res.hasMarks()) { %>
        <div class="sheet-sum">
          <span><b class="num"><%= Html.pct(res.getPercentage()) %>%</b> overall</span>
          <span class="grade"><%= res.getGrade() %></span>
          <span class="status status-<%= res.getStatus().toLowerCase() %>"><%= res.getStatus() %></span>
        </div>
      <% } %>
    </div>
    <form method="post" action="<%= ctx %>/marks" data-marksheet>
      <input type="hidden" name="_csrf" value="<%= Html.esc(csrf) %>">
      <input type="hidden" name="student" value="<%= st.getId() %>">
      <table class="ledger marks">
        <thead><tr><th>Code</th><th>Subject</th><th class="r">Marks</th><th class="r">Out of</th><th class="r">%</th><th class="c">Grade</th></tr></thead>
        <tbody>
        <% for (MarkEntry e : sheet) {
             String v = typed != null ? typed.get(e.getSubjectId()) : (e.isRecorded() ? Html.num(e.getObtained()) : ""); %>
          <tr>
            <td class="mono"><%= Html.esc(e.getSubjectCode()) %></td>
            <td><label for="m<%= e.getSubjectId() %>"><%= Html.esc(e.getSubjectName()) %></label></td>
            <td class="r"><input id="m<%= e.getSubjectId() %>" name="m_<%= e.getSubjectId() %>" class="mark num" inputmode="decimal"
                   type="number" min="0" max="<%= e.getMaxMarks() %>" step="0.01" value="<%= Html.esc(v) %>" data-max="<%= e.getMaxMarks() %>"
                   data-msg="0 to <%= e.getMaxMarks() %>"></td>
            <td class="r num muted">/ <%= e.getMaxMarks() %></td>
            <td class="r num" data-pct><%= e.isRecorded() ? Html.pct(e.getPercent()) : "" %></td>
            <td class="c grade" data-grade><%= e.getGrade() %></td>
          </tr>
        <% } %>
        </tbody>
      </table>
      <div class="actions sticky">
        <button class="btn btn-primary" type="submit">Save mark sheet</button>
        <a class="btn btn-quiet" href="<%= ctx %>/report?id=<%= st.getId() %>">View statement</a>
        <span class="note" data-dirty hidden>Unsaved changes</span>
      </div>
    </form>
  <% } %>
  </section>
</div>
<%@ include file="_bottom.jspf" %>
