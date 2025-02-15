package mcci.businessschool.bts.sio.slam.pharmagest.approvisionnement.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ApprovisionnementControleur {
    @FXML
    private Button generationCommandeButton;

    @FXML
    private Button retourDashboard;


    @FXML
    private void ouvrirReceptionCommande(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/approvisionnement/ReceptionCommande.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Réception des Commandes");
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de la réception de commande : " + e.getMessage());
        }
    }

    @FXML
    public void ouvrirGenerationCommande(ActionEvent event) {
        try {
            // Nouvelle scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commande/GenerationCommande.fxml"));
            Scene nouvelleScene = new Scene(loader.load());
            // La référence de la scène actuelle
            Stage stage = (Stage) generationCommandeButton.getScene().getWindow();
            // Afficher la nouvelle scène
            stage.setScene(nouvelleScene);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de la génération de commande : " + e.getMessage());
            e.printStackTrace();  // 🔴 Affiche l'erreur complète avec les détails
        }
    }

    @FXML
    private void retourDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour au Dashboard : " + e.getMessage());
        }
    }

    @FXML
    public void retourDashboardOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) retourDashboard.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }
}
