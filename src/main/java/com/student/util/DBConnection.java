package com.student.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Connection settings are read, in increasing priority, from:
 * db.properties on the classpath, then environment variables DB_URL / DB_USER / DB_PASSWORD.
 */
public final class DBConnection {
    private static String url = "jdbc:mysql://localhost:3306/student_result_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPassword = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
            // JDBC 4 auto-loading will still find it if present
        }
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                url = p.getProperty("db.url", url);
                dbUser = p.getProperty("db.user", dbUser);
                dbPassword = p.getProperty("db.password", dbPassword);
            }
        } catch (Exception ignored) {
        }
        String v;
        if ((v = System.getenv("DB_URL")) != null && !v.isBlank()) url = v;
        if ((v = System.getenv("DB_USER")) != null && !v.isBlank()) dbUser = v;
        if ((v = System.getenv("DB_PASSWORD")) != null) dbPassword = v;
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }
}
