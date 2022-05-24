package ir.maktab74.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private String url;
    private String userName;
    private String password;
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public DatabaseUtil() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.url = "jdbc:mysql://localhost:3306/onlineshop2";
        this.userName = "root";
        this.password = "Mehrshad@1374";

        this.connection = DriverManager.getConnection(url , userName , password);
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}

