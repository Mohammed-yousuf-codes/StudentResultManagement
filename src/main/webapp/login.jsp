<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sign in | Student Result Portal</title><link rel="stylesheet" href="css/style.css">
</head>
<body class="login-page">
<div class="login-shell">
  <div class="login-brand"><div class="brand-mark">SR</div><span>STUDENT<span class="brand-light">PORTAL</span></span></div>
  <div class="login-card">
    <div class="eyebrow">ACADEMIC MANAGEMENT</div>
    <h1>Welcome back</h1><p class="muted">Sign in to manage student records and results.</p>
    <% if ("1".equals(request.getParameter("error"))) { %><div class="alert">Incorrect username or password. Please try again.</div><% } %>
    <form action="login" method="post">
      <label>Username</label><input name="username" required placeholder="Enter admin username" autocomplete="username">
      <label>Password</label><input type="password" name="password" required placeholder="Enter password" autocomplete="current-password">
      <button class="btn btn-primary full" type="submit">Sign in to dashboard <span>→</span></button>
    </form>
    <div class="demo-note">Demo access: <b>admin</b> / <b>admin123</b></div>
  </div>
  <p class="login-footer">Student Result &amp; Grade Management System · 2026</p>
</div>
</body></html>
