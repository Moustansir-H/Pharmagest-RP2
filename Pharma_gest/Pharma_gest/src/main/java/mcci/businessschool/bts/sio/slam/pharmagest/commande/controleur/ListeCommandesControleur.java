package mcci.businessschool.bts.sio.slam.pharmagest.commande.controleur;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.service.GestionCommandeService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListeCommandesControleur implements Initializable {
    @FXML
    public Button approvisionnementButton;
    @FXML
    private TableView<Commande> tableCommandes;

    @FXML
    private TableColumn<Commande, Integer> colonneId;

    @FXML
    private TableColumn<Commande, String> colonneDate;

    @FXML
    private TableColumn<Commande, String> colonneFournisseur;

    @FXML
    private TableColumn<Commande, String> colonnePharmacien;

    @FXML
    private TableColumn<Commande, Double> colonneMontant;

    @FXML
    private TableColumn<Commande, String> colonneStatut;

    @FXML
    private Button btnSupprimer;

    private GestionCommandeService commandeService;
    private ObservableList<Commande> listeCommandes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialiser le service
            commandeService = new GestionCommandeService();

            // Configurer les colonnes
            colonneId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colonneDate.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDateCreationFormatee()));
            colonneFournisseur.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getFournisseurNom()));
            colonneMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
            colonneStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

            // Formater la colonne montant pour afficher 2 décimales
            colonneMontant.setCellFactory(tc -> new TableCell<>() {
                @Override
                protected void updateItem(Double montant, boolean empty) {
                    super.updateItem(montant, empty);
                    if (empty || montant == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f €", montant));
                    }
                }
            });

            // Charger les données
            chargerCommandes();

            // Configurer la sélection de ligne pour activer/désactiver le bouton supprimer
            tableCommandes.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            // Activer le bouton supprimer uniquement pour les commandes validées
                            btnSupprimer.setDisable(!"Validée".equals(newValue.getStatut()));
                        } else {
                            btnSupprimer.setDisable(true);
                        }
                    });

            // Désactiver le bouton supprimer par défaut
            btnSupprimer.setDisable(true);

        } catch (Exception e) {
            afficherErreur("Erreur d'initialisation",
                    "Impossible d'initialiser la liste des commandes", e);
        }
    }

    /**
     * Charge toutes les commandes depuis la base de données
     */
    private void chargerCommandes() {
        try {
            List<Commande> commandes = commandeService.recupererToutesLesCommandes();
            listeCommandes = FXCollections.observableArrayList(commandes);
            tableCommandes.setItems(listeCommandes);
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement",
                    "Impossible de charger les commandes", e);
        }
    }

    /**
     * Gère l'action du bouton supprimer
     */
    @FXML
    private void supprimerCommande() {
        Commande commandeSelectionnee = tableCommandes.getSelectionModel().getSelectedItem();

        if (commandeSelectionnee == null) {
            return;
        }

        // Vérifier que la commande est bien validée
        if (!"Validée".equals(commandeSelectionnee.getStatut())) {
            afficherAlerte("Action impossible",
                    "Seules les commandes validées peuvent être supprimées.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Demander confirmation
        boolean confirmation = demanderConfirmation(
                "Confirmation de suppression",
                "Êtes-vous sûr de vouloir supprimer la commande n°" +
                        commandeSelectionnee.getId() + " ?");

        if (confirmation) {
            try {
                // Supprimer la commande
                commandeService.supprimerCommande(commandeSelectionnee.getId());

                // Rafraîchir la liste
                listeCommandes.remove(commandeSelectionnee);

                afficherAlerte("Suppression réussie",
                        "La commande a été supprimée avec succès.",
                        Alert.AlertType.INFORMATION);

            } catch (SQLException e) {
                afficherErreur("Erreur de suppression",
                        "Impossible de supprimer la commande", e);
            }
        }
    }

    /**
     * Affiche une boîte de dialogue d'erreur
     */
    private void afficherErreur(String titre, String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.setContentText("Détails: " + e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }

    /**
     * Affiche une alerte
     */
    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Demande une confirmation à l'utilisateur
     * @return true si l'utilisateur confirme, false sinon
     */
    private boolean demanderConfirmation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
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
        stage.setMaximized(true);
    }
}