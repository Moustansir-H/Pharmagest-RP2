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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.service.PaiementService;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.service.VendeurService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.service.LigneVenteService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.service.VenteService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class CaisseControleur {

    @FXML
    private TableView<Vente> tableVentes;
    @FXML
    private TableColumn<Vente, Integer> colVenteId;
    @FXML
    private TableColumn<Vente, String> colDateVente;
    @FXML
    private TableColumn<Vente, Double> colMontantTotal;
    @FXML
    private TableColumn<Vente, String> colNumeroFacture;

    @FXML
    private TableView<LigneVente> tableLignesVente;
    @FXML
    private TableColumn<LigneVente, String> colMedicamentNom;
    @FXML
    private TableColumn<LigneVente, Integer> colQuantite;
    @FXML
    private TableColumn<LigneVente, Double> colPrixUnitaire;
    @FXML
    private TableColumn<Vente, String> colTypeVente;
    @FXML
    private Label lblIdVente, lblDateVente, lblMontantTotal, lblTypeVente, lblMonnaie;
    @FXML
    private TextField txtMontantRecu;
    @FXML
    private Button retourDashboard;


    private ObservableList<Vente> listeVentes = FXCollections.observableArrayList();
    private ObservableList<LigneVente> lignesVenteObservable = FXCollections.observableArrayList();

    private VenteService venteService;
    private LigneVenteService ligneVenteService;
    private PaiementService paiementService;
    private VendeurService vendeurService;


    public CaisseControleur() {
        try {
            venteService = new VenteService();
            ligneVenteService = new LigneVenteService();
            paiementService = new PaiementService();
            vendeurService = new VendeurService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        // Colonnes ventes
        colVenteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDateVente.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getDateVente();
            return new ReadOnlyStringWrapper(((java.sql.Date) date).toLocalDate().toString());
        });
        colMontantTotal.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colNumeroFacture.setCellValueFactory(cellData -> {
            String facture = (cellData.getValue().getFacture() != null) ?
                    cellData.getValue().getFacture().getNumeroFacture() : "Non générée";
            return new ReadOnlyStringWrapper(facture);
        });

        // Affichage du type de vente : "Prescrite" ou "Libre" en fonction de prescriptionId
        colTypeVente.setCellValueFactory(cellData -> {
            Integer prescriptionId = cellData.getValue().getPrescriptionId();
            String typeVente = (prescriptionId != null) ? "Prescrite" : "Libre";
            return new ReadOnlyStringWrapper(typeVente);
        });

        tableVentes.setItems(listeVentes);

        // Colonnes lignes de vente
        colMedicamentNom.setCellValueFactory(cellData -> {
            Medicament med = cellData.getValue().getMedicament();
            return new ReadOnlyStringWrapper(med.getNom());
        });
        colQuantite.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantiteVendu()));
        colPrixUnitaire.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrixUnitaire()));
        tableLignesVente.setItems(lignesVenteObservable);

        // Gestion de la sélection de vente
        tableVentes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) afficherDetailsVente(newVal);
        });

        // Mise à jour de la monnaie automatiquement
        txtMontantRecu.textProperty().addListener((obs, oldVal, newVal) -> {
            Vente selected = tableVentes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    double montantRecu = Double.parseDouble(newVal);
                    double monnaie = montantRecu - selected.getMontantTotal();
                    lblMonnaie.setText(String.format("Monnaie à rendre : %.2f €", monnaie));
                } catch (NumberFormatException e) {
                    lblMonnaie.setText("Monnaie à rendre : 0.00 €");
                }
            }
        });

        chargerVentesEnAttente();
    }


    private void afficherDetailsVente(Vente vente) {
        lblIdVente.setText(String.valueOf(vente.getId()));
        lblDateVente.setText(((java.sql.Date) vente.getDateVente()).toLocalDate().toString());
        lblMontantTotal.setText(String.format("%.2f €", vente.getMontantTotal()));
        lblTypeVente.setText(vente.getTypeVente().name());


        // Affichage du type de vente dans les détails (prescrite ou libre)
        String typeVente = (vente.getPrescriptionId() != null) ? "Prescrite" : "Libre";
        lblTypeVente.setText(typeVente);

        try {
            lignesVenteObservable.setAll(ligneVenteService.recupererLignesParVente(vente.getId()));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les médicaments.");
        }

        // 🔁 Nettoyage du champ montant et monnaie
        txtMontantRecu.clear();
        lblMonnaie.setText("Monnaie à rendre : 0.00 €");
    }


    private void chargerVentesEnAttente() {
        listeVentes.clear();
        List<Vente> enAttente = venteService.recupererVentesEnAttente();
        if (enAttente != null) {
            listeVentes.setAll(enAttente);
        }
    }

    @FXML
    private void handleValiderPaiement() {
        Vente selectedVente = tableVentes.getSelectionModel().getSelectedItem();
        if (selectedVente == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune vente sélectionnée", "Veuillez sélectionner une vente.");
            return;
        }

        double montantRecu;
        try {
            montantRecu = Double.parseDouble(txtMontantRecu.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Montant invalide", "Veuillez entrer un montant valide.");
            return;
        }

        if (montantRecu < selectedVente.getMontantTotal()) {
            showAlert(Alert.AlertType.WARNING, "Montant insuffisant", "Le montant est insuffisant.");
            return;
        }

        // Appel de la méthode de validation du paiement et mise à jour
        try {
            venteService.validerPaiementEtMettreAJourStock(selectedVente.getId());
            showAlert(Alert.AlertType.INFORMATION, "Paiement validé",
                    String.format("💰 Monnaie à rendre : %.2f €", montantRecu - selectedVente.getMontantTotal()));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }

        chargerVentesEnAttente();
        lignesVenteObservable.clear();
        txtMontantRecu.clear();
        lblIdVente.setText("");
        lblDateVente.setText("");
        lblMontantTotal.setText("");
        lblTypeVente.setText("");
        lblMonnaie.setText("Monnaie à rendre : 0.00 €");
    }
    @FXML
    public void retourDashboardOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
            Scene nouvelleScene = new Scene(loader.load());
            Stage stage = (Stage) retourDashboard.getScene().getWindow();
            stage.setScene(nouvelleScene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le Dashboard.");
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
