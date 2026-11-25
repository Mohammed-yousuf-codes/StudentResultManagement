<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.student.util.Html" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sign in · Result Register</title>
<link rel="icon" href="<%= ctx %>/favicon.svg" type="image/svg+xml">
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body class="login">
<section class="login-side">
  <div class="mast-name"><span class="monogram">RR</span><span><small>Controller of Examinations</small>Result Register</span></div>
  <h1>Marks in, grades out — kept in one register.</h1>
  <table class="scale" aria-label="Grading scale">
    <caption>Grading scale</caption>
    <tr><th>A+</th><td>90 – 100</td></tr>
    <tr><th>A</th><td>80 – 89</td></tr>
    <tr><th>B</th><td>70 – 79</td></tr>
    <tr><th>C</th><td>60 – 69</td></tr>
    <tr><th>D</th><td>40 – 59</td></tr>
    <tr><th>F</th><td>below 40</td></tr>
  </table>
  <p class="login-rule">A student passes only when every subject is 40% or above.</p>
</section>
<section class="login-main">
  <form method="post" action="<%= ctx %>/login" class="login-form" novalidate>
    <h2>Administrator sign-in</h2>
    <% if (request.getAttribute("error") != null) { %>
      <p class="notice notice-err" role="alert"><%= Html.esc(request.getAttribute("error")) %></p>
    <% } else if ("1".equals(request.getParameter("out"))) { %>
      <p class="notice notice-ok" role="status">You have been signed out.</p>
    <% } %>
    <label for="username">Username</label>
    <input id="username" name="username" autocomplete="username" required autofocus
           value="<%= Html.esc(request.getAttribute("username")) %>">
    <label for="password">Password</label>
    <input id="password" name="password" type="password" autocomplete="current-password" required>
    <button type="submit" class="btn btn-primary btn-block">Sign in</button>
    <p class="hint">Demo account: <code>admin</code> / <code>admin123</code> — change it under “Password” after signing in.</p>
  </form>
</section>
</body>
</html>
