<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="includes/header.jspf" %>
<div class="page-heading no-print"><div><div class="eyebrow">ACADEMIC PERFORMANCE</div><h1>Student results</h1><p class="muted">Review totals, percentages and grades.</p></div><button class="btn btn-outline" onclick="window.print()">⇩ Print results</button></div>
<div class="panel no-print search-panel"><form action="results" method="get" class="search-form"><input name="roll" placeholder="Search by roll number..." value="<%=request.getParameter("roll")==null?"":request.getParameter("roll")%>"><button class="btn btn-primary">Search</button><a class="btn btn-light" href="results">Clear</a></form></div>
<div class="panel"><div class="panel-heading"><div><h3>Result summary</h3><p class="muted">Percentage is based on the subjects with marks entered.</p></div><span class="pill">Grade report</span></div>
<div class="table-wrap"><table><thead><tr><th>Roll no.</th><th>Student</th><th>Department</th><th>Subjects</th><th>Total</th><th>Percentage</th><th>Grade</th><th>Status</th></tr></thead><tbody>
<% ResultSet rs=(ResultSet)request.getAttribute("results"); boolean any=false; while(rs.next()){ any=true; int count=rs.getInt("subject_count"); double pct=rs.getDouble("percentage"); double lowest=rs.getDouble("lowest"); String grade=count==0?"—":pct>=90?"A+":pct>=80?"A":pct>=70?"B":pct>=60?"C":pct>=40?"D":"F"; boolean pass=count>0 && lowest>=40; %>
<tr><td><b><%=rs.getString("roll_no")%></b></td><td><%=rs.getString("full_name")%></td><td><%=rs.getString("department")%></td><td><%=count%></td><td><%=String.format("%.1f",rs.getDouble("total"))%></td><td><b><%=count==0?"—":String.format("%.1f%%",pct)%></b></td><td><span class="grade-badge"><%=grade%></span></td><td><span class="result-status <%=pass?"passed":"failed"%>"><%=count==0?"Pending":pass?"Pass":"Fail"%></span></td></tr>
<% } if(!any){ %><tr><td colspan="8" class="empty-state">No students found. Add a student to get started.</td></tr><% } %>
</tbody></table></div></div>
<%@ include file="includes/footer.jspf" %>
