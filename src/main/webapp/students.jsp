<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="includes/header.jspf" %>
<div class="page-heading"><div><div class="eyebrow">RECORDS</div><h1>Students</h1><p class="muted">Register and manage student information.</p></div></div>
<% if (request.getAttribute("error") != null) { %><div class="alert"><%= request.getAttribute("error") %></div><% } %>
<div class="two-column">
<section class="panel"><div class="panel-heading"><div><h3>Add a student</h3><p class="muted">Enter the student's academic details.</p></div></div>
<form action="students" method="post" class="form-grid">
<div><label>Roll number</label><input name="rollNo" required placeholder="e.g. STU004"></div>
<div><label>Full name</label><input name="fullName" required placeholder="Student name"></div>
<div class="span-2"><label>Email address</label><input type="email" name="email" placeholder="student@example.com"></div>
<div><label>Department</label><input name="department" required placeholder="Computer Science"></div>
<div><label>Semester</label><select name="semester" required><% for(int i=1;i<=8;i++){ %><option value="<%=i%>"><%=i%></option><% } %></select></div>
<div class="span-2"><button class="btn btn-primary" type="submit">＋ Save student</button></div>
</form></section>
<section class="panel"><div class="panel-heading"><div><h3>Student directory</h3><p class="muted">All registered students.</p></div><span class="pill">Live records</span></div>
<div class="table-wrap"><table><thead><tr><th>Roll no.</th><th>Student</th><th>Department</th><th>Sem.</th><th></th></tr></thead><tbody>
<% ResultSet rs=(ResultSet)request.getAttribute("students"); while(rs.next()){ %>
<tr><td><b><%=rs.getString("roll_no")%></b></td><td><%=rs.getString("full_name")%><small class="table-sub"><%=rs.getString("email")==null?"":rs.getString("email")%></small></td><td><%=rs.getString("department")%></td><td><%=rs.getInt("semester")%></td><td><form action="students" method="post" onsubmit="return confirm('Delete this student and their marks?')"><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="<%=rs.getInt("id")%>"><button class="icon-btn danger" title="Delete">×</button></form></td></tr>
<% } %></tbody></table></div></section></div>
<%@ include file="includes/footer.jspf" %>
