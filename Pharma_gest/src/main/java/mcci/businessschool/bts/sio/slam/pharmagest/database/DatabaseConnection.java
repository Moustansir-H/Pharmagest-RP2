package mcci.businessschool.bts.sio.slam.pharmagest.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public Connection databaselink;

    public Connection getConnection() {
        String databaseName = "Login";
        String databaseUser = "postgres";
        String databasePassword = "iwillfindyou";
        String url = "jdbc:postgresql://localhost:5432/" + databaseName;

        try {
            Class.forName("org.postgresql.Driver");
            databaselink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaselink;
    }
}

