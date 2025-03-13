package mcci.businessschool.bts.sio.slam.pharmagest.vente.controleur;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.service.MedicamentService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.TypeVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.service.VenteIntegrationService;

import java.io.IOException;
import java.util.List;

public class VenteControleur {

    @FXML
    private TableView<Medicament> tableMedicaments;
    @FXML
    private TableColumn<Medicament, String> colMedNom;
    @FXML
    private TableColumn<Medicament, String> colMedForme;
    @FXML
    private TableColumn<Medicament, Integer> colMedStock;
    @FXML
    private TableColumn<Medicament, Double> colMedPrixVente;
    @FXML
    private Button retourDashboard;
    @FXML
    private TextField txtRecherche; // Recherche en temps réel

    @FXML
    private TextField txtQuantite;
    @FXML
    private TableView<LigneVente> tableLignesVente;
    @FXML
    private TableColumn<LigneVente, String> colVenteMedicament;
    @FXML
    private TableColumn<LigneVente, Integer> colVenteQuantite;
    @FXML
    private TableColumn<LigneVente, Double> colVentePrixUnitaire;
    @FXML
    private TableColumn<LigneVente, Double> colVenteSousTotal;
    @FXML
    private Label lblMontantTotal;

    private ObservableList<Medicament> listMedicaments = FXCollections.observableArrayList();
    private ObservableList<LigneVente> listLignesVente = FXCollections.observableArrayList();

    private MedicamentService medicamentService;
    private VenteIntegrationService venteIntegrationService;

    public VenteControleur() {
        try {
            medicamentService = new MedicamentService();
            venteIntegrationService = new VenteIntegrationService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        // Configuration de la table des médicaments
        colMedNom.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNom()));
        colMedForme.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getForme()));
        colMedStock.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStock()));
        colMedPrixVente.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrixVente()));
        tableMedicaments.setItems(listMedicaments);

        // Charger la liste initiale
        chargerMedicaments();

        // Ecouter les changements de texte pour filtrer en temps réel
        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> filtrerMedicaments(newVal));

        // Configuration de la table des lignes de vente
        colVenteMedicament.setCellValueFactory(cellData -> {
            Medicament med = cellData.getValue().getMedicament();
            return new ReadOnlyStringWrapper(med != null ? med.getNom() : "Inconnu");
        });
        colVenteQuantite.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantiteVendu()));
        colVentePrixUnitaire.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrixUnitaire()));
        colVenteSousTotal.setCellValueFactory(cellData -> {
            LigneVente ligne = cellData.getValue();
            return new ReadOnlyObjectWrapper<>(ligne.getQuantiteVendu() * ligne.getPrixUnitaire());
        });
        tableLignesVente.setItems(listLignesVente);

        lblMontantTotal.setText("0.00");
    }

    private void chargerMedicaments() {
        listMedicaments.clear();
        List<Medicament> meds = medicamentService.recupererMedicaments();
        if (meds != null) {
            listMedicaments.setAll(meds);
        }
    }

    // Filtrer en fonction du texte saisi
    private void filtrerMedicaments(String recherche) {
        if (recherche == null || recherche.isBlank()) {
            // Revenir à la liste complète si le champ est vide
            chargerMedicaments();
            return;
        }
        String lowerRecherche = recherche.toLowerCase();

        List<Medicament> allMeds = medicamentService.recupererMedicaments();
        if (allMeds != null) {
            List<Medicament> filtered = allMeds.stream()
                    .filter(med -> med.getNom().toLowerCase().contains(lowerRecherche))
                    .toList();
            listMedicaments.setAll(filtered);
        }
    }

    @FXML
    private void handleAjouterMedicament() {
        Medicament selectedMed = tableMedicaments.getSelectionModel().getSelectedItem();
        if (selectedMed == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun médicament sélectionné", "Veuillez sélectionner un médicament.");
            return;
        }
        int quantite;
        try {
            quantite = Integer.parseInt(txtQuantite.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Quantité invalide", "Veuillez saisir une quantité numérique.");
            return;
        }
        if (quantite <= 0) {
            showAlert(Alert.AlertType.WARNING, "Quantité invalide", "La quantité doit être supérieure à 0.");
            return;
        }
        if (quantite > selectedMed.getStock()) {
            showAlert(Alert.AlertType.WARNING, "Stock insuffisant", "La quantité demandée dépasse le stock disponible.");
            return;
        }
        LigneVente ligne = new LigneVente(quantite, selectedMed.getPrixVente(), selectedMed);
        listLignesVente.add(ligne);
        calculerMontantTotal();
        txtQuantite.clear();
    }

    private void calculerMontantTotal() {
        double total = 0.0;
        for (LigneVente ligne : listLignesVente) {
            total += ligne.getQuantiteVendu() * ligne.getPrixUnitaire();
        }
        lblMontantTotal.setText(String.format("%.2f", total));
    }

    @FXML
    private void handleValiderVente() {
        if (listLignesVente.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune ligne", "Ajoutez au moins un médicament à la vente.");
            return;
        }
        try {
            Vente venteCree = venteIntegrationService.creerVentePharmacien(listLignesVente, TypeVente.LIBRE);
            showAlert(Alert.AlertType.INFORMATION, "Vente créée",
                    "Vente ID : " + venteCree.getId() +
                            "\nFacture : " + (venteCree.getFacture() != null ? venteCree.getFacture().getNumeroFacture() : "Inconnue") +
                            "\nMontant Total : " + lblMontantTotal.getText());
            listLignesVente.clear();
            lblMontantTotal.setText("0.00");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la validation de la vente", e.getMessage());
        }
    }

    // Méthode pour gérer le bouton Retour
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
