package com.student.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String URL =
        "jdbc:mysql://localhost:3306/student_result_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Set your MySQL password here.

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}
