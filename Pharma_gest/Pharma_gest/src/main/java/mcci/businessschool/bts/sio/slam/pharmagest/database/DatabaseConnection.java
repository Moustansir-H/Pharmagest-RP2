package mcci.businessschool.bts.sio.slam.pharmagest.database;

import configuration.ChargeurDeConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {

    private Connection baseDeDonneConnexion;
    private ChargeurDeConfiguration chargeurDeConfiguration;

    private static DatabaseConnection databaseConnectionInstance; // Null si on appel pas

    public DatabaseConnection() throws Exception {
        //Chargement de application.propertie
        chargeurDeConfiguration = new ChargeurDeConfiguration(new Properties(), "application.properties");

        //Recuperation proprietes de la base a partir du fichier
        String databaseUser = chargeurDeConfiguration.getProperty("datasource.username");
        String databasePassword = chargeurDeConfiguration.getProperty("datasource.pwd");
        String url = chargeurDeConfiguration.getProperty("datasource.url");
        String driver = chargeurDeConfiguration.getProperty("datasource.driver-class-name");

        try {
            Class.forName(driver);
            baseDeDonneConnexion = DriverManager.getConnection(url, databaseUser, databasePassword); //Connexion
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnexion() throws Exception {
        if (databaseConnectionInstance == null) {
            databaseConnectionInstance = new DatabaseConnection();
        }
        return databaseConnectionInstance.getBaseDeDonneConnexion();
    }

    public Connection getBaseDeDonneConnexion() {
        return baseDeDonneConnexion;
    }


}

