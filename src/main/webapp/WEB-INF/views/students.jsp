<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.student.model.Student" %>
<% String pageTitle = "Students"; String nav = "students"; %>
<%@ include file="_top.jspf" %>
<%
    @SuppressWarnings("unchecked") List<Student> students = (List<Student>) request.getAttribute("students");
    Student f = (Student) request.getAttribute("form");
    boolean editing = f != null && f.getId() > 0;
    String q = (String) request.getAttribute("q");
%>
<div class="head">
  <div>
    <h1>Students</h1>
    <p class="sub"><%= students.size() %> <%= q.isEmpty() ? "on roll" : "matching “" + Html.esc(q) + "”" %></p>
  </div>
  <form class="search" method="get" action="<%= ctx %>/students" role="search">
    <input type="search" name="q" value="<%= Html.esc(q) %>" placeholder="Roll no., name, department or email" aria-label="Search students">
    <button class="btn" type="submit">Search</button>
    <% if (!q.isEmpty()) { %><a class="btn btn-quiet" href="<%= ctx %>/students">Clear</a><% } %>
  </form>
</div>

<div class="cols cols-form">
  <section class="box" id="entry">
    <h2><%= editing ? "Edit " + Html.esc(f.getRollNo()) : "Register a student" %></h2>
    <form method="post" action="<%= ctx %>/students" class="fields" data-validate>
      <input type="hidden" name="_csrf" value="<%= Html.esc(csrf) %>">
      <input type="hidden" name="action" value="<%= editing ? "edit" : "add" %>">
      <% if (editing) { %><input type="hidden" name="id" value="<%= f.getId() %>"><% } %>
      <label>Roll number
        <input name="rollNo" required maxlength="30" pattern="[A-Za-z0-9_\-]{2,30}" class="mono upper"
               value="<%= f == null ? "" : Html.esc(f.getRollNo()) %>" placeholder="STU004"
               data-msg="2–30 letters, digits, - or _">
      </label>
      <label>Full name
        <input name="fullName" required minlength="2" maxlength="120" value="<%= f == null ? "" : Html.esc(f.getFullName()) %>"
               data-msg="Enter the student's full name">
      </label>
      <label>Email <span class="opt">optional</span>
        <input name="email" type="email" maxlength="120" value="<%= f == null ? "" : Html.esc(f.getEmail()) %>"
               data-msg="Enter a valid email address or leave blank">
      </label>
      <div class="pair">
        <label>Department
          <input name="department" required maxlength="80" list="depts" value="<%= f == null ? "" : Html.esc(f.getDepartment()) %>"
                 data-msg="Department is required">
        </label>
        <label>Semester
          <select name="semester" required>
            <% for (int i = 1; i <= 8; i++) { %>
              <option value="<%= i %>" <%= f != null && f.getSemester() == i ? "selected" : "" %>><%= i %></option>
            <% } %>
          </select>
        </label>
      </div>
      <datalist id="depts">
        <% java.util.Set<String> seen = new java.util.TreeSet<>(); for (Student s : students) seen.add(s.getDepartment());
           for (String d : seen) { %><option value="<%= Html.esc(d) %>"><% } %>
      </datalist>
      <div class="actions">
        <button class="btn btn-primary" type="submit"><%= editing ? "Save changes" : "Register student" %></button>
        <% if (editing || f != null) { %><a class="btn btn-quiet" href="<%= ctx %>/students">Cancel</a><% } %>
      </div>
    </form>
  </section>

  <section class="box flush">
    <table class="ledger">
      <thead><tr><th>Roll no.</th><th>Name</th><th>Department</th><th class="r">Sem.</th><th class="r"><span class="sr">Actions</span></th></tr></thead>
      <tbody>
      <% if (students.isEmpty()) { %>
        <tr><td colspan="5" class="empty"><%= q.isEmpty() ? "No students registered yet." : "Nobody matches that search." %></td></tr>
      <% } %>
      <% for (Student s : students) { boolean cur = editing && f.getId() == s.getId(); %>
        <tr class="<%= cur ? "current" : "" %>">
          <td class="mono"><%= Html.esc(s.getRollNo()) %></td>
          <td><%= Html.esc(s.getFullName()) %><% if (s.getEmail() != null && !s.getEmail().isEmpty()) { %><small class="line2"><%= Html.esc(s.getEmail()) %></small><% } %></td>
          <td><%= Html.esc(s.getDepartment()) %></td>
          <td class="r num"><%= s.getSemester() %></td>
          <td class="r row-actions">
            <a href="<%= ctx %>/marks?student=<%= s.getId() %>">Marks</a>
            <a href="<%= ctx %>/report?id=<%= s.getId() %>">Report</a>
            <a href="<%= ctx %>/students?edit=<%= s.getId() %>#entry">Edit</a>
            <form method="post" action="<%= ctx %>/students" data-confirm="Delete <%= Html.esc(s.getRollNo()) %> (<%= Html.esc(s.getFullName()) %>) and all their marks? This cannot be undone.">
              <input type="hidden" name="_csrf" value="<%= Html.esc(csrf) %>">
              <input type="hidden" name="action" value="delete">
              <input type="hidden" name="id" value="<%= s.getId() %>">
              <button type="submit" class="link-danger">Delete</button>
            </form>
          </td>
        </tr>
      <% } %>
      </tbody>
    </table>
  </section>
</div>
<%@ include file="_bottom.jspf" %>
