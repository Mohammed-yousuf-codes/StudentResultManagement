<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ page import="com.student.util.Html" %>
<%
    String ctx = request.getContextPath();
    Integer code = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    String msg = (String) request.getAttribute("jakarta.servlet.error.message");
    Throwable t = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
    boolean db = false;
    for (Throwable c = t; c != null; c = c.getCause()) if (c instanceof java.sql.SQLException) db = true;
    String title, detail;
    if (code != null && code == 404) { title = "Page not found"; detail = "That address doesn't exist in the register."; }
    else if (code != null && code == 403) { title = "Request refused"; detail = msg != null && !msg.isEmpty() ? msg : "You are not allowed to do that."; }
    else if (db) { title = "Database unavailable"; detail = "The application could not reach MySQL. Make sure the MySQL service is running and the credentials in db.properties are correct."; }
    else { title = "Something went wrong"; detail = t != null && t.getMessage() != null ? t.getMessage() : "An unexpected error occurred."; }
%>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= Html.esc(title) %> · Result Register</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css"></head>
<body class="errpage">
  <main>
    <p class="errcode"><%= code == null ? "Error" : code %></p>
    <h1><%= Html.esc(title) %></h1>
    <p><%= Html.esc(detail) %></p>
    <p><a class="btn btn-primary" href="<%= ctx %>/dashboard">Back to the register</a></p>
  </main>
</body>
</html>
