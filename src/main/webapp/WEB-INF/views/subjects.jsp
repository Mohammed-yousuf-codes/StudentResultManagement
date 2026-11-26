<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.student.model.Subject" %>
<% String pageTitle = "Subjects"; String nav = "subjects"; %>
<%@ include file="_top.jspf" %>
<%
    @SuppressWarnings("unchecked") List<Object[]> subjects = (List<Object[]>) request.getAttribute("subjects");
    Subject f = (Subject) request.getAttribute("form");
    boolean editing = f != null && f.getId() > 0;
%>
<div class="head">
  <div>
    <h1>Subjects</h1>
    <p class="sub"><%= subjects.size() %> offered. Every student is graded on every subject listed here.</p>
  </div>
</div>

<div class="cols cols-form">
  <section class="box" id="entry">
    <h2><%= editing ? "Edit " + Html.esc(f.getSubjectCode()) : "Add a subject" %></h2>
    <form method="post" action="<%= ctx %>/subjects" class="fields" data-validate>
      <input type="hidden" name="_csrf" value="<%= Html.esc(csrf) %>">
      <input type="hidden" name="action" value="<%= editing ? "edit" : "add" %>">
      <% if (editing) { %><input type="hidden" name="id" value="<%= f.getId() %>"><% } %>
      <div class="pair">
        <label>Code
          <input name="code" required maxlength="20" pattern="[A-Za-z0-9_\-]{2,20}" class="mono upper"
                 value="<%= f == null ? "" : Html.esc(f.getSubjectCode()) %>" placeholder="CN106" data-msg="2–20 letters or digits">
        </label>
        <label>Maximum marks
          <input name="maxMarks" type="number" required min="10" max="1000" step="1"
                 value="<%= f == null ? "100" : f.getMaxMarks() %>" data-msg="Whole number from 10 to 1000">
        </label>
      </div>
      <label>Subject name
        <input name="name" required minlength="2" maxlength="100" value="<%= f == null ? "" : Html.esc(f.getSubjectName()) %>"
               placeholder="Computer Networks" data-msg="Enter the subject name">
      </label>
      <div class="actions">
        <button class="btn btn-primary" type="submit"><%= editing ? "Save changes" : "Add subject" %></button>
        <% if (f != null) { %><a class="btn btn-quiet" href="<%= ctx %>/subjects">Cancel</a><% } %>
      </div>
    </form>
  </section>

  <section class="box flush">
    <table class="ledger">
      <thead><tr><th>Code</th><th>Subject</th><th class="r">Max</th><th class="r">Marked</th><th class="r">Average</th><th class="r"><span class="sr">Actions</span></th></tr></thead>
      <tbody>
      <% if (subjects.isEmpty()) { %><tr><td colspan="6" class="empty">No subjects yet.</td></tr><% } %>
      <% for (Object[] s : subjects) { int n = (Integer) s[4]; %>
        <tr class="<%= editing && f.getId() == (Integer) s[0] ? "current" : "" %>">
          <td class="mono"><%= Html.esc(s[1]) %></td>
          <td><%= Html.esc(s[2]) %></td>
          <td class="r num"><%= s[3] %></td>
          <td class="r num"><%= n %></td>
          <td class="r num"><%= s[5] == null ? "—" : Html.pct((Double) s[5]) + "%" %></td>
          <td class="r row-actions">
            <a href="<%= ctx %>/subjects?edit=<%= s[0] %>#entry">Edit</a>
            <form method="post" action="<%= ctx %>/subjects"
                  data-confirm="Delete <%= Html.esc(s[1]) %>?<%= n > 0 ? " Its " + n + " recorded mark(s) will be deleted too." : "" %>">
              <input type="hidden" name="_csrf" value="<%= Html.esc(csrf) %>">
              <input type="hidden" name="action" value="delete">
              <input type="hidden" name="id" value="<%= s[0] %>">
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
