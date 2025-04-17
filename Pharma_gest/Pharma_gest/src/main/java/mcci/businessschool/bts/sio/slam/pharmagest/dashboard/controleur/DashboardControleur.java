package mcci.businessschool.bts.sio.slam.pharmagest.dashboard.controleur;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class DashboardControleur {

    public Label bienvenueLabel;
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
    private Button FournisseurButton;

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
    public void quitButtonOnAction(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de sortie");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment quitter l'application ?");

        // Obtenir la position de la fenêtre actuelle
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        alert.initOwner(stage); // Pour que la boîte de dialogue soit centrée sur la fenêtre actuelle

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Quitter l'application
            Platform.exit();
        }
        // Sinon, ne rien faire (la boîte se ferme automatiquement)
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
        stage.setMaximized(true);
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
    @FXML
    public void fournisseurButtonOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fournisseur/Fournisseur.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) FournisseurButton.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
        stage.setMaximized(true);
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        bienvenueLabel.setText("Bienvenue " + nomUtilisateur);
    }
}
