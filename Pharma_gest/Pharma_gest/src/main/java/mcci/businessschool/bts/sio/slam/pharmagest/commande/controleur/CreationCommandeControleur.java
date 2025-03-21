package mcci.businessschool.bts.sio.slam.pharmagest.commande.controleur;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.service.CommandeService;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreationCommandeControleur {
    @FXML
    private ComboBox<Fournisseur> comboFournisseurs;
    @FXML
    private TableView<LigneDeCommande> tableMedicaments;
    @FXML
    private TableColumn<LigneDeCommande, String> colMedicament;
    @FXML
    private TableColumn<LigneDeCommande, Integer> colStockActuel;
    @FXML
    private TableColumn<LigneDeCommande, Integer> colSeuil;
    @FXML
    private TableColumn<LigneDeCommande, Integer> colQteMax;
    @FXML
    private TableColumn<LigneDeCommande, Integer> colQuantiteACommander;
    @FXML
    private Button btnCreerCommande;
    @FXML
    private Label lblMontantTotal;


    private CommandeService commandeService;
    private Map<Fournisseur, List<LigneDeCommande>> commandesParFournisseur;

    public CreationCommandeControleur() {
        try {
            this.commandeService = new CommandeService(); // Initialisation propre ici
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            commandesParFournisseur = commandeService.preparerNouvelleCommande();

            // ✅ S'assurer que chaque fournisseur apparaît une seule fois
            ObservableList<Fournisseur> fournisseurs = FXCollections.observableArrayList(commandesParFournisseur.keySet());
            comboFournisseurs.setItems(fournisseurs);

            // ✅ Détecter la sélection d'un fournisseur pour afficher ses médicaments
            comboFournisseurs.setOnAction(event -> afficherMedicamentsSousSeuil());

        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des fournisseurs : " + e.getMessage());
        }

        // ✅ Associer les colonnes aux propriétés des médicaments
        colMedicament.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicament().getNom()));
        colStockActuel.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMedicament().getStock()).asObject());
        colSeuil.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMedicament().getSeuilCommande()).asObject());
        colQteMax.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMedicament().getQteMax()).asObject());
        colQuantiteACommander.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantiteVendu()).asObject());

        // ✅ Mise à jour automatique du montant total si l'utilisateur ajuste la quantité
        tableMedicaments.setEditable(true);
        colQuantiteACommander.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQuantiteACommander.setOnEditCommit(event -> miseAJourMontantTotal());

        // ✅ Permettre l'édition de la colonne Quantité à Commander
        tableMedicaments.setEditable(true);
        colQuantiteACommander.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // ✅ Mettre à jour le montant total dès qu'une quantité est modifiée
        colQuantiteACommander.setOnEditCommit(event -> {
            LigneDeCommande ligne = event.getRowValue();
            ligne.setQuantiteVendu(event.getNewValue()); // Mettre à jour la quantité dans l'objet
            tableMedicaments.refresh(); // Rafraîchir la table
            miseAJourMontantTotal(); // Mettre à jour le montant total
        });
    }


    @FXML
    public void afficherMedicamentsSousSeuil() {
        Fournisseur fournisseurSelectionne = comboFournisseurs.getValue();
        if (fournisseurSelectionne != null) {
            List<LigneDeCommande> lignes = commandesParFournisseur.get(fournisseurSelectionne);
            ObservableList<LigneDeCommande> observableList = FXCollections.observableArrayList(lignes);
            tableMedicaments.setItems(observableList);

            // ✅ Mettre à jour le montant total initial
            miseAJourMontantTotal();
        }
    }

    private void miseAJourMontantTotal() {
        double montantTotal = tableMedicaments.getItems().stream()
                .mapToDouble(ligne -> ligne.getQuantiteVendu() * ligne.getMedicament().getPrixAchat())
                .sum();

        System.out.println("💰 Montant total de la commande : " + montantTotal + " €");
        // ✅ Affichage du montant total dans l'interface
        lblMontantTotal.setText("Montant total : " + String.format("%.2f", montantTotal) + " €");
    }


    @FXML
    public void creerCommande() {
        Fournisseur fournisseurSelectionne = comboFournisseurs.getValue();
        if (fournisseurSelectionne == null) {
            afficherErreur("Veuillez sélectionner un fournisseur.");
            return;
        }

        List<LigneDeCommande> lignes = new ArrayList<>(tableMedicaments.getItems());
        if (lignes.isEmpty()) {
            afficherErreur("Aucun médicament à commander.");
            return;
        }

        try {
            Pharmacien pharmacien = new Pharmacien(1); // Simulé pour l'instant
            int commandeId = commandeService.ajouterCommandeAvecLignes(pharmacien, fournisseurSelectionne, lignes);
            afficherSucces("Commande créée avec succès ! ID : " + commandeId);
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la création de la commande : " + e.getMessage());
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherSucces(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void retourApprovisionnement(ActionEvent event) {
        try {
            // Charger l'interface Approvisionnement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/approvisionnement/Approvisionnement.fxml"));
            Scene scene = new Scene(loader.load());

            // Récupérer la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Mettre à jour la scène avec l'interface Approvisionnement
            stage.setScene(scene);
            stage.setTitle("Approvisionnement");
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour à l'approvisionnement : " + e.getMessage());
            e.printStackTrace();
        }
    }


}
