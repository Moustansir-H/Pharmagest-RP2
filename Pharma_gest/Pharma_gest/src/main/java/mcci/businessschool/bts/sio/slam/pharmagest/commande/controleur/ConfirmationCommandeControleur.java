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
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ConfirmationCommandeControleur{

    public Button approvisionnementButton;
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
    private TableView<LigneCommandeDetail> tableLignesCommande;

    @FXML
    private TableColumn<LigneCommandeDetail, String> colonneMedicament;

    @FXML
    private TableColumn<LigneCommandeDetail, Integer> colonneQuantite;

    @FXML
    private TableColumn<LigneCommandeDetail, Double> colonnePrixUnitaire;

    @FXML
    private TableColumn<LigneCommandeDetail, Double> colonnePrixTotal;

    @FXML
    private TableColumn<LigneCommandeDetail, Integer> colonneStockActuel;

    @FXML
    private TableColumn<LigneCommandeDetail, Integer> colonneStockApres;

    @FXML
    private Label lblTitre;

    @FXML
    private Label lblStatut;

    @FXML
    private Button btnConfirmerTout;

    @FXML
    private Button btnConfirmerSelection;

    @FXML
    private Button btnRetour;

    private GestionCommandeService commandeService;
    private ObservableList<Commande> listeCommandes = FXCollections.observableArrayList();
    private ObservableList<LigneCommandeDetail> listeLignesCommande = FXCollections.observableArrayList();

    // Classe interne pour l'affichage des lignes de commande avec stock
    public static class LigneCommandeDetail {
        private final String nomMedicament;
        private final int quantite;
        private final double prixUnitaire;
        private final double prixTotal;
        private final int stockActuel;
        private final int stockApres;

        public LigneCommandeDetail(LigneDeCommande ligne) {
            Medicament medicament = ligne.getMedicament();
            this.nomMedicament = medicament.getNom();
            this.quantite = ligne.getQuantiteVendu();
            this.prixUnitaire = ligne.getPrixUnitaire();
            this.prixTotal = ligne.getQuantiteVendu() * ligne.getPrixUnitaire();
            this.stockActuel = medicament.getStock();
            this.stockApres = medicament.getStock() + ligne.getQuantiteVendu();
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

        public int getStockActuel() {
            return stockActuel;
        }

        public int getStockApres() {
            return stockApres;
        }
    }

    public ConfirmationCommandeControleur() {
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

        // Configurer les colonnes de la table des lignes de commande
        colonneMedicament.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNomMedicament()));

        colonneQuantite.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());

        colonnePrixUnitaire.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrixUnitaire()).asObject());

        colonnePrixTotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrixTotal()).asObject());

        colonneStockActuel.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getStockActuel()).asObject());

        colonneStockApres.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getStockApres()).asObject());

        // Ajouter un écouteur pour la sélection d'une commande
        tableCommandes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        afficherDetailsCommande(newSelection);
                        btnConfirmerSelection.setDisable(false);
                    } else {
                        listeLignesCommande.clear();
                        btnConfirmerSelection.setDisable(true);
                    }
                }
        );

        // Initialiser les tables
        tableCommandes.setItems(listeCommandes);
        tableLignesCommande.setItems(listeLignesCommande);

        // Charger les commandes en attente
        chargerCommandesEnAttente();
    }

    private void chargerCommandesEnAttente() {
        try {
            List<Commande> commandes = commandeService.recupererCommandesEnAttente();
            listeCommandes.clear();
            listeCommandes.addAll(commandes);

            lblStatut.setText(commandes.size() + " commande(s) en attente de confirmation");
            btnConfirmerTout.setDisable(commandes.isEmpty());
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement", "Impossible de charger les commandes en attente: " + e.getMessage());
            lblStatut.setText("Erreur lors du chargement des commandes");
        }
    }

    private void afficherDetailsCommande(Commande commande) {
        listeLignesCommande.clear();

        for (LigneDeCommande ligne : commande.getLignesDeCommande()) {
            listeLignesCommande.add(new LigneCommandeDetail(ligne));
        }
    }

    @FXML
    private void confirmerCommandeSelectionnee() {
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
                chargerCommandesEnAttente();
            } catch (SQLException e) {
                afficherErreur("Erreur de confirmation", "Impossible de confirmer la commande: " + e.getMessage());
            }
        }
    }

    @FXML
    private void confirmerToutesLesCommandes() {
        if (listeCommandes.isEmpty()) {
            afficherInfo("Aucune commande", "Il n'y a aucune commande à confirmer.");
            return;
        }

        // Demander confirmation
        boolean confirmation = afficherConfirmation(
                "Confirmer toutes les commandes",
                "Êtes-vous sûr de vouloir confirmer toutes les commandes en attente (" + listeCommandes.size() + ") ?\n\n" +
                        "Cette action va mettre à jour les stocks de tous les médicaments concernés."
        );

        if (confirmation) {
            int compteurSucces = 0;
            int compteurEchecs = 0;

            for (Commande commande : listeCommandes) {
                try {
                    commandeService.confirmerCommande(commande.getId());
                    compteurSucces++;
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la confirmation de la commande #" + commande.getId() + ": " + e.getMessage());
                    compteurEchecs++;
                }
            }

            afficherInfo("Résultat de la confirmation",
                    compteurSucces + " commande(s) confirmée(s) avec succès.\n" +
                            (compteurEchecs > 0 ? compteurEchecs + " commande(s) n'ont pas pu être confirmées." : "")
            );

            // Recharger les commandes
            chargerCommandesEnAttente();
        }
    }

    @FXML
    private void retourGestionCommandes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commande/ListeCommande.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Gestion des commandes");
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur de navigation", "Impossible de retourner à la gestion des commandes: " + e.getMessage());
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