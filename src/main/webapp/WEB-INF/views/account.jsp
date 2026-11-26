<%@ page contentType="text/html;charset=UTF-8" %>
<% String pageTitle = "Password"; String nav = ""; %>
<%@ include file="_top.jspf" %>
<div class="head">
  <div>
    <h1>Change password</h1>
    <p class="sub">Signed in as <b><%= Html.esc(admin) %></b>. Passwords are stored as salted PBKDF2 hashes.</p>
  </div>
</div>
<section class="box narrow">
  <form method="post" action="<%= ctx %>/account" class="fields" data-validate>
    <input type="hidden" name="_csrf" value="<%= Html.esc(csrf) %>">
    <label>Current password
      <input type="password" name="current" required autocomplete="current-password" data-msg="Enter your current password">
    </label>
    <label>New password
      <input type="password" name="next" required minlength="8" autocomplete="new-password" data-msg="At least 8 characters">
    </label>
    <label>Repeat new password
      <input type="password" name="confirm" required minlength="8" autocomplete="new-password" data-match="next" data-msg="Must match the new password">
    </label>
    <div class="actions"><button class="btn btn-primary" type="submit">Change password</button></div>
  </form>
</section>
<%@ include file="_bottom.jspf" %>
