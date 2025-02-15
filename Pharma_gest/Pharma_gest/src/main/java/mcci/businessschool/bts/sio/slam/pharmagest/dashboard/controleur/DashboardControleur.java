package mcci.businessschool.bts.sio.slam.pharmagest.dashboard.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardControleur {

    @FXML
    private Button caisseButton;

    @FXML
    private Button venteButton;

    @FXML
    private Button maintenanceButton;

    @FXML
    private Button quitButton;

    @FXML
    private Button changeUserButton;

    @FXML
    private Button approvisionnementButton;

    @FXML
    private Button ouvrirGenerationCommande;

    @FXML
    public void caisseButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/caisse/Caisse.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) caisseButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void venteButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vente/Vente.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) venteButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void maintenanceButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/Maintenance.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) maintenanceButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void quitButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/deconnexion/Deconnexion.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) quitButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void changeUserButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/Login.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) changeUserButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
        stage.setTitle("Login");

    }

    @FXML
    public void ouvrirApprovisionnement(ActionEvent e) throws IOException {
        // Charger la vue de l'approvisionnement
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/approvisionnement/Approvisionnement.fxml"));
        Scene nouvelleScene = new Scene(loader.load());

        // Récupérer la fenêtre actuelle et y placer la nouvelle scène
        Stage stage = (Stage) approvisionnementButton.getScene().getWindow();
        stage.setScene(nouvelleScene);
        stage.setTitle("Approvisionnement");
    }

    @FXML
    public void ouvrirGenerationCommande(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commande/GenerationCommande.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Génération de Commande");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de la génération de commande : " + e.getMessage());
        }
    }


}
