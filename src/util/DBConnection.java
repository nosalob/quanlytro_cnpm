package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String URL =
    "jdbc:mysql://localhost:3306/quan_ly_tro_cnpm?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        // Load the driver explicitly so the connection does not depend on
        // JDBC service-provider discovery in the IDE's runtime classpath.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "MySQL JDBC driver was not found. Add lib/mysql-connector-j-26.10.0.jar to the runtime classpath.",
                e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
