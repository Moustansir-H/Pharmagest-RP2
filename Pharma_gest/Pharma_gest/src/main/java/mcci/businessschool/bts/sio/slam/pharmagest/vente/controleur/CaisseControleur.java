package mcci.businessschool.bts.sio.slam.pharmagest.vente.controleur;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.service.VenteIntegrationService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.service.VenteService;

import java.time.ZoneId;
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
    private TextField txtMontantRecu;
    @FXML
    private TextField txtModePaiement;

    private ObservableList<Vente> listeVentes = FXCollections.observableArrayList();

    private VenteService venteService;
    private VenteIntegrationService venteIntegrationService;

    public CaisseControleur() {
        try {
            venteService = new VenteService();
            venteIntegrationService = new VenteIntegrationService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        colVenteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDateVente.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getDateVente();
            String dateStr = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
            return new ReadOnlyStringWrapper(dateStr);
        });
        colMontantTotal.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colNumeroFacture.setCellValueFactory(cellData -> {
            String numFacture = cellData.getValue().getFacture() != null ? cellData.getValue().getFacture().getNumeroFacture() : "Non générée";
            return new ReadOnlyStringWrapper(numFacture);
        });
        tableVentes.setItems(listeVentes);

        chargerVentesEnAttente();
    }

    private void chargerVentesEnAttente() {
        listeVentes.clear();
        List<Vente> ventesEnAttente = venteService.recupererVentesEnAttente();
        if (ventesEnAttente != null) {
            listeVentes.setAll(ventesEnAttente);
        }
    }

/*
    @FXML
    private void handleValiderPaiement() {
        Vente selectedVente = tableVentes.getSelectionModel().getSelectedItem();
        if (selectedVente == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune vente sélectionnée", "Veuillez sélectionner une vente à finaliser.");
            return;
        }
        double montantRecu;
        try {
            montantRecu = Double.parseDouble(txtMontantRecu.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Montant invalide", "Veuillez saisir un montant valide.");
            return;
        }
        if (montantRecu < selectedVente.getMontantTotal()) {
            showAlert(Alert.AlertType.WARNING, "Montant insuffisant", "Le montant reçu est inférieur au montant total de la vente.");
            return;
        }
        String modePaiement = txtModePaiement.getText();
        if (modePaiement == null || modePaiement.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Mode de paiement manquant", "Veuillez saisir un mode de paiement.");
            return;
        }

        // Récupérer l'utilisateur connecté
        Utilisateur user = SessionUtilisateur.getInstance().getUtilisateurConnecte();
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de session", "Aucun utilisateur connecté. Veuillez vous reconnecter.");
            return;
        }
        if (!(user instanceof Vendeur)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de rôle", "L'utilisateur connecté n'est pas un vendeur.");
            return;
        }
        int vendeurId = user.getId();

        // Créer et configurer l'objet Paiement
        Paiement paiement = new Paiement(selectedVente.getMontantTotal(), modePaiement, StatutPaiement.VALIDE);
        paiement.setVenteId(selectedVente.getId());
        paiement.setVendeurId(vendeurId);

        try {
            venteIntegrationService.finaliserPaiementVendeur(selectedVente.getId(), paiement, vendeurId);
            showAlert(Alert.AlertType.INFORMATION, "Paiement validé", "La vente a été finalisée.");
            chargerVentesEnAttente();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la finalisation", e.getMessage());
        }
    }*/


    @FXML
    private void handleRetour() {
        try {
            // Par exemple, revenir au Dashboard via FXMLLoader (à adapter)
            System.out.println("Retour au Dashboard.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
