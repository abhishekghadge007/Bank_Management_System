package bank;

import java.sql.*;

public class DBConnection {
    static Connection con;

    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankdb",  
                    "root",                              
                    "Abhishek05@"                        
                );
                System.out.println(" Database Connected Successfully!");
            }
        } catch (Exception e) {
            System.out.println(" Database Connection Failed: " + e);
        }
        return con;
    }
}


