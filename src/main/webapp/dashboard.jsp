<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="includes/header.jspf" %>
<div class="page-heading"><div><div class="eyebrow">OVERVIEW</div><h1>Dashboard</h1><p class="muted">Here's what's happening in your academic workspace.</p></div><a class="btn btn-primary" href="students">＋ Add student</a></div>
<div class="stat-grid">
  <div class="stat-card"><div class="stat-top"><span>Total students</span><span class="stat-icon blue">♙</span></div><strong><%= request.getAttribute("studentCount") %></strong><small>Registered learners</small></div>
  <div class="stat-card"><div class="stat-top"><span>Subjects</span><span class="stat-icon violet">▤</span></div><strong><%= request.getAttribute("subjectCount") %></strong><small>Available subjects</small></div>
  <div class="stat-card"><div class="stat-top"><span>Marks entries</span><span class="stat-icon green">✓</span></div><strong><%= request.getAttribute("markCount") %></strong><small>Recorded assessments</small></div>
  <div class="stat-card"><div class="stat-top"><span>Portal status</span><span class="stat-icon amber">●</span></div><strong class="status-text">Active</strong><small>Database connected</small></div>
</div>
<div class="content-grid">
 <section class="panel welcome-panel"><div class="eyebrow">QUICK START</div><h2>Manage academic results with ease.</h2><p class="muted">Register students, record subject marks and calculate grades from one place.</p><div class="quick-actions"><a href="students" class="quick-action"><span class="quick-icon">＋</span><span><b>Add students</b><small>Create student records</small></span><i>→</i></a><a href="marks" class="quick-action"><span class="quick-icon">✎</span><span><b>Enter marks</b><small>Record subject scores</small></span><i>→</i></a><a href="results" class="quick-action"><span class="quick-icon">▤</span><span><b>View results</b><small>Check grades and totals</small></span><i>→</i></a></div></section>
 <section class="panel info-panel"><div class="eyebrow">GRADING GUIDE</div><h3>Grade boundaries</h3><div class="grade-row"><span>A+</span><b>90–100%</b><em>Excellent</em></div><div class="grade-row"><span>A</span><b>80–89%</b><em>Very good</em></div><div class="grade-row"><span>B</span><b>70–79%</b><em>Good</em></div><div class="grade-row"><span>C</span><b>60–69%</b><em>Average</em></div><div class="grade-row"><span>D</span><b>40–59%</b><em>Pass</em></div><div class="grade-row"><span>F</span><b>Below 40%</b><em>Fail</em></div><p class="tiny-note">Pass requires at least 40 marks in every subject.</p></section>
</div>
<%@ include file="includes/footer.jspf" %>
