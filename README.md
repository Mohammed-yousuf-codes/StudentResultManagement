# Student Result and Grade Management System

A Java Servlet/JSP + MySQL college project with a blue admin interface.

## Requirements
- JDK 17 or newer (your JDK 26 is suitable)
- Apache Maven 3.8+
- MySQL Server 8.0+
- Apache Tomcat 10.1
- Antigravity, VS Code, or another Java IDE

## 1. Create the database
1. Start MySQL Server.
2. Open MySQL Workbench and connect as your MySQL user.
3. Open `database/student_result_db.sql`.
4. Execute the entire script.

The script creates the database, tables, sample students/subjects/marks, and a default admin:
- Username: `admin`
- Password: `admin123`

This is a demo login for a college project. Change it before any real deployment.

## 2. Configure database connection
Open:
`src/main/java/com/student/util/DBConnection.java`

Set `DB_USER` and `DB_PASSWORD` to your local MySQL username and password.
The default is `root` with an empty password. Do not upload real credentials to a public GitHub repository.

## 3. Build the project
Open a terminal in the folder containing `pom.xml` and run:

```bash
mvn clean package
```

A successful build creates:
`target/StudentResultManagement.war`

## 4. Run on Tomcat 10.1
1. Copy `target/StudentResultManagement.war` into Tomcat's `webapps` folder.
2. Start Tomcat (`bin/startup.bat` on Windows).
3. Open `http://localhost:8080/StudentResultManagement/`
4. Sign in with the demo credentials above.

## Features
- Admin login/logout
- Dashboard statistics
- Add, list, and delete student records
- Add subject-wise marks
- Automatic total, percentage, grade, and pass/fail calculation
- Search student results
- Printable result page

## Notes
- Uses Jakarta Servlet 6.0, compatible with Tomcat 10.1.
- Maven compiles with Java 17 language compatibility for broad compatibility; JDK 26 can run Maven.
- This is an educational starter project, not a production-ready student information system.
- The demo admin password is stored simply for classroom demonstration. For production, use password hashing, CSRF protection, role management, validation, HTTPS, and secret environment variables.
