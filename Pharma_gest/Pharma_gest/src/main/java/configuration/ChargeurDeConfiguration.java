package configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ChargeurDeConfiguration {

    private Properties properties;


    public ChargeurDeConfiguration(Properties properties, String fichierDeProprietes) {
        this.properties = properties;
        try (InputStream ressource = getClass().getClassLoader().getResourceAsStream(fichierDeProprietes)) {
            if (ressource == null) {
                System.out.println("Desole, impossible de trouver la ressource " + fichierDeProprietes);
                return;
            }
            // Charge les propriétés du fichier
            properties.load(ressource);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Méthode pour récupérer une propriété
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void main(String[] args) {
        // Charger les propriétés depuis le fichier application.properties
        Properties properties = new Properties();
        ChargeurDeConfiguration chargeurDeConfiguration = new ChargeurDeConfiguration(properties, "application.properties");

        // Récupérer des valeurs spécifiques
        String dbUrl = chargeurDeConfiguration.getProperty("datasource.url");
        String dbPass = chargeurDeConfiguration.getProperty("datasource.password");

        // Afficher les valeurs
        System.out.println("URL de la base de données : " + dbUrl + " mot de passe: " + dbPass);
    }
}
