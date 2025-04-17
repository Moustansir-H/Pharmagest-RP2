package mcci.businessschool.bts.sio.slam.pharmagest.commande.controleur;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.service.GestionCommandeService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class GestionCommandeControleur {

    @FXML
    private TableView<Commande> tableCommandes;

    @FXML
    private TableColumn<Commande, Integer> colonneId;

    @FXML
    private TableColumn<Commande, String> colonneDateCreation;

    @FXML
    private TableColumn<Commande, String> colonneFournisseur;

    @FXML
    private TableColumn<Commande, Double> colonneMontant;

    @FXML
    private TableColumn<Commande, String> colonneStatut;

    @FXML
    private TableView<LigneCommandeAffichage> tableLignesCommande;

    @FXML
    private TableColumn<LigneCommandeAffichage, String> colonneMedicament;

    @FXML
    private TableColumn<LigneCommandeAffichage, Integer> colonneQuantite;

    @FXML
    private TableColumn<LigneCommandeAffichage, Double> colonnePrixUnitaire;

    @FXML
    private TableColumn<LigneCommandeAffichage, Double> colonnePrixTotal;

    @FXML
    private Label lblTitre;

    @FXML
    private Label lblStatut;

    @FXML
    private Button btnConfirmer;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnRetour;

    @FXML
    private Button btnOuvrirConfirmation;

    private GestionCommandeService commandeService;
    private ObservableList<Commande> listeCommandes = FXCollections.observableArrayList();
    private ObservableList<LigneCommandeAffichage> listeLignesCommande = FXCollections.observableArrayList();

    // Classe interne pour l'affichage des lignes de commande
    public static class LigneCommandeAffichage {
        private final String nomMedicament;
        private final int quantite;
        private final double prixUnitaire;
        private final double prixTotal;

        public LigneCommandeAffichage(LigneDeCommande ligne) {
            this.nomMedicament = ligne.getMedicament().getNom();
            this.quantite = ligne.getQuantiteVendu();
            this.prixUnitaire = ligne.getPrixUnitaire();
            this.prixTotal = ligne.getQuantiteVendu() * ligne.getPrixUnitaire();
        }

        public String getNomMedicament() {
            return nomMedicament;
        }

        public int getQuantite() {
            return quantite;
        }

        public double getPrixUnitaire() {
            return prixUnitaire;
        }

        public double getPrixTotal() {
            return prixTotal;
        }
    }

    public GestionCommandeControleur() {
        try {
            this.commandeService = new GestionCommandeService();
        } catch (Exception e) {
            afficherErreur("Erreur d'initialisation", "Impossible d'initialiser le service de gestion des commandes: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Configurer les colonnes de la table des commandes
        colonneId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colonneDateCreation.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateCreation() != null) {
                return new SimpleStringProperty(
                        cellData.getValue().getDateCreation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                );
            } else {
                return new SimpleStringProperty("Non définie");
            }
        });

        colonneFournisseur.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFournisseurNom()));

        colonneMontant.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getMontantTotal()).asObject());

        colonneStatut.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatut()));

        // Configurer les colonnes de la table des lignes de commande
        colonneMedicament.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNomMedicament()));

        colonneQuantite.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());

        colonnePrixUnitaire.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrixUnitaire()).asObject());

        colonnePrixTotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrixTotal()).asObject());

        // Ajouter un écouteur pour la sélection d'une commande
        tableCommandes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        afficherDetailsCommande(newSelection);

                        // Activer/désactiver les boutons selon le statut
                        boolean estEnAttente = "En attente de confirmation".equals(newSelection.getStatut());
                        btnConfirmer.setDisable(!estEnAttente);
                    } else {
                        listeLignesCommande.clear();
                        btnConfirmer.setDisable(true);
                        btnSupprimer.setDisable(true);
                    }
                }
        );

        // Initialiser les tables
        tableCommandes.setItems(listeCommandes);
        tableLignesCommande.setItems(listeLignesCommande);

        // Charger les commandes
        chargerCommandes();
    }

    private void chargerCommandes() {
        try {
            List<Commande> commandes = commandeService.recupererToutesLesCommandes();
            listeCommandes.clear();
            listeCommandes.addAll(commandes);

            lblStatut.setText(commandes.size() + " commande(s) trouvée(s)");
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement", "Impossible de charger les commandes: " + e.getMessage());
            lblStatut.setText("Erreur lors du chargement des commandes");
        }
    }

    private void afficherDetailsCommande(Commande commande) {
        listeLignesCommande.clear();

        for (LigneDeCommande ligne : commande.getLignesDeCommande()) {
            listeLignesCommande.add(new LigneCommandeAffichage(ligne));
        }

        btnSupprimer.setDisable(false);
    }

    @FXML
    private void confirmerCommande() {
        Commande commande = tableCommandes.getSelectionModel().getSelectedItem();
        if (commande == null) {
            afficherInfo("Sélection requise", "Veuillez sélectionner une commande à confirmer.");
            return;
        }

        // Demander confirmation
        boolean confirmation = afficherConfirmation(
                "Confirmer la commande",
                "Êtes-vous sûr de vouloir confirmer la commande #" + commande.getId() + " ?\n\n" +
                        "Cette action va mettre à jour les stocks des médicaments."
        );

        if (confirmation) {
            try {
                commandeService.confirmerCommande(commande.getId());
                afficherInfo("Succès", "Commande #" + commande.getId() + " confirmée avec succès.");

                // Recharger les commandes
                chargerCommandes();
            } catch (SQLException e) {
                afficherErreur("Erreur de confirmation", "Impossible de confirmer la commande: " + e.getMessage());
            }
        }
    }

    @FXML
    private void supprimerCommande() {
        Commande commande = tableCommandes.getSelectionModel().getSelectedItem();
        if (commande == null) {
            afficherInfo("Sélection requise", "Veuillez sélectionner une commande à supprimer.");
            return;
        }

        // Demander confirmation
        boolean confirmation = afficherConfirmation(
                "Supprimer la commande",
                "Êtes-vous sûr de vouloir supprimer la commande #" + commande.getId() + " ?\n\n" +
                        "Cette action est irréversible."
        );

        if (confirmation) {
            try {
                commandeService.supprimerCommande(commande.getId());
                afficherInfo("Succès", "Commande #" + commande.getId() + " supprimée avec succès.");

                // Recharger les commandes
                chargerCommandes();
            } catch (SQLException e) {
                afficherErreur("Erreur de suppression", "Impossible de supprimer la commande: " + e.getMessage());
            }
        }
    }

    @FXML
    private void ouvrirEcranConfirmation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commande/Livraison.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnOuvrirConfirmation.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Confirmation des commandes automatiques");
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur de navigation", "Impossible d'ouvrir l'écran de confirmation: " + e.getMessage());
        }
    }

    @FXML
    private void retourDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur de navigation", "Impossible de retourner au dashboard: " + e.getMessage());
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean afficherConfirmation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}