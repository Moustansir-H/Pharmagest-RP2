package configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ChargeurDeConfigurationTest {

    @Test
    void testChargementDuProprietes() throws Exception {
        //GIVEN (Etant donne)
        Properties properties = new Properties();
        String fichierDeProprietes = "application-test.properties";

        //WHEN (Quand)
        ChargeurDeConfiguration chargeurDeConfiguration = new ChargeurDeConfiguration(properties, fichierDeProprietes);

        //THEN (Alors)
        Assertions.assertNotNull(chargeurDeConfiguration.getProperties());
        Assertions.assertNotNull(chargeurDeConfiguration.getProperty("cle.1"));
        Assertions.assertNotNull(chargeurDeConfiguration.getProperty("cle.2"));
        Assertions.assertNull(chargeurDeConfiguration.getProperty("cle.3"));

        Assertions.assertEquals("valeur1", chargeurDeConfiguration.getProperty("cle.1"));


    }

    @Test
    void testErreurFichierIntrouvable() {
        //GIVEN (Etant donne)
        Properties properties = new Properties();
        String fichierDeProprietes = "application-toto.properties";

        //WHEN (Quand)
        Exception exception = Assertions.assertThrows(
                Exception.class, //Type d'exception attendu
                () -> {
                    // Le code qui doit lancer l'exception
                    new ChargeurDeConfiguration(properties, fichierDeProprietes);
                }
        );

        //THEN (Alors)
        Assertions.assertEquals("Desole, impossible de trouver la ressource application-toto.properties", exception.getMessage());
    }

    @Test
    void testErreurDeChargementDeRessource() throws IOException {
        //GIVEN (Etant donne)
        Properties properties = Mockito.mock(Properties.class);
        String fichierDeProprietes = "application-test.properties";
        Mockito.doThrow(IOException.class).when(properties).load(any(InputStream.class));

        //WHEN (Quand)
        Exception exception = Assertions.assertThrows(
                Exception.class, //Type d'exception attendu
                () -> {
                    // Le code qui doit lancer l'exception
                    new ChargeurDeConfiguration(properties, fichierDeProprietes);
                }
        );

        //THEN (Alors)
        Assertions.assertEquals("Impossible de charger la ressource", exception.getMessage());

    }
}