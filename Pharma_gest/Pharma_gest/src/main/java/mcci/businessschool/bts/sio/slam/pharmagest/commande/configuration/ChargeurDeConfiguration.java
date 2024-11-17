package mcci.businessschool.bts.sio.slam.pharmagest.commande.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ChargeurDeConfiguration {

    private Properties properties;


    public ChargeurDeConfiguration(Properties properties, String fichierDeProprietes) throws Exception {
        this.properties = properties;
        try (InputStream ressource = getClass().getClassLoader().getResourceAsStream(fichierDeProprietes)) {
            if (ressource == null) {
                throw new Exception("Desole, impossible de trouver la ressource " + fichierDeProprietes);
            }
            // Charge les propriétés du fichier
            properties.load(ressource);
        } catch (IOException ex) {
            throw new Exception("Impossible de charger la ressource");
        }
    }

    // Méthode pour récupérer une propriété
    public String getProperty(String key) {
        String valeurDeLaPropriete = properties.getProperty(key);
        if (valeurDeLaPropriete == null) {
            System.out.println("Le propriete " + key + " n'existe pas");
        }
        return valeurDeLaPropriete;
    }

    public Properties getProperties() {
        return properties;
    }
}
