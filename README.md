# Student Result and Grade Management System

A Java Servlet / JSP + MySQL web application. An administrator registers students and subjects, enters marks on one sheet per student, and gets totals, percentages, grades and pass/fail calculated automatically. The app also produces a printable statement of marks for each student.

## Features
- Administrator sign-in with salted PBKDF2 password hashing, a lockout after repeated failures, session timeout, and a page for changing the password
- CSRF tokens on every form, HTML escaping on every page, and security headers
- An overview page with status breakdown, top percentages and subject averages
- Students: register, edit, delete and search
- Subjects: add, edit and delete, each with its own maximum marks
- A mark sheet that edits every subject for one student at once, showing percentage and grade as you type
- A result register with search and status filter, CSV export, and print / save as PDF
- A statement of marks for each student, ready to print

## Grading
| Grade | Percentage |
|---|---|
| A+ | 90–100 |
| A | 80–89 |
| B | 70–79 |
| C | 60–69 |
| D | 40–59 |
| F | below 40 |

Percentages are based on each subject's maximum marks. A student's status is one of:
- **Pass**: every subject is marked and each is at least 40%.
- **Fail**: any subject is below 40%.
- **Incomplete**: no subject has failed so far, but some marks are still missing.
- **Pending**: no marks have been entered yet.

## Requirements
- JDK 17 or newer
- Apache Maven 3.8+
- MySQL Server 8.0+
- Apache Tomcat 10.1 (Jakarta Servlet 6.0)

## Setup
1. **Database**: run the script `database/student_result_db.sql`, either in MySQL Workbench or with
   `mysql -u root -p < database/student_result_db.sql`.
   Running it again is safe. It creates the schema, sample data and the default login **admin / admin123**.
2. **Credentials**: copy `src/main/resources/db.properties.example` to `db.properties` and fill it in, or set the environment variables
   `DB_URL`, `DB_USER` and `DB_PASSWORD`, which take priority over the file.
   Don't commit real credentials.
3. **Build**: run `mvn clean package`. This produces `target/StudentResultManagement.war`.
4. **Run**: copy the WAR into Tomcat's `webapps` folder as `ROOT.war` and start Tomcat. Then open http://localhost:8080/
   On Windows, `run.bat` does the build, deployment and start in one step. It looks for Maven and Tomcat under `E:\Tools`.
5. Sign in, then change the default password from **Password** in the header.

## Project layout
```
src/main/java/com/student/
  filter/AuthFilter.java       login guard, CSRF check, security headers
  servlet/                     Login, Logout, Dashboard, Student, Subject, Marks, Results (+CSV), Report, Account
  dao/ResultDao.java           result and mark-sheet queries
  model/                       Student, Subject, MarkEntry, StudentResult
  util/                        DBConnection, PasswordUtil, GradeUtil, Html (escaping), Web helpers
src/main/webapp/
  WEB-INF/views/*.jsp          page templates (not directly reachable)
  css/style.css, js/app.js
database/student_result_db.sql
```
