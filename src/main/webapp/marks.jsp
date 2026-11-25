<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="includes/header.jspf" %>
<div class="page-heading"><div><div class="eyebrow">ASSESSMENTS</div><h1>Enter marks</h1><p class="muted">Record or update a student's subject score.</p></div></div>
<div class="panel form-panel"><div class="panel-heading"><div><h3>Marks entry form</h3><p class="muted">Scores are out of 100. Existing scores are updated automatically.</p></div></div>
<form action="marks" method="post" class="form-grid">
<div><label>Select student</label><select name="studentId" required><% ResultSet students=(ResultSet)request.getAttribute("students"); while(students.next()){ %><option value="<%=students.getInt("id")%>"><%=students.getString("roll_no")%> — <%=students.getString("full_name")%></option><% } %></select></div>
<div><label>Select subject</label><select name="subjectId" required><% ResultSet subjects=(ResultSet)request.getAttribute("subjects"); while(subjects.next()){ %><option value="<%=subjects.getInt("id")%>"><%=subjects.getString("subject_code")%> — <%=subjects.getString("subject_name")%></option><% } %></select></div>
<div><label>Marks obtained (0–100)</label><input type="number" name="marks" min="0" max="100" step="0.5" required placeholder="Enter marks"></div>
<div class="form-end"><button class="btn btn-primary" type="submit">Save marks</button></div>
</form></div>
<div class="hint-box"><b>Tip:</b> Add marks for each subject for a student. The Results page calculates totals, percentage and grade automatically.</div>
<%@ include file="includes/footer.jspf" %>
