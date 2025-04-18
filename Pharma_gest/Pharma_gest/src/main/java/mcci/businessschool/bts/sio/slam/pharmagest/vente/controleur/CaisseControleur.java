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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.service.PaiementService;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.service.VendeurService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.service.LigneVenteService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.service.VenteService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ticket.GenerateurPDF;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class CaisseControleur {

    // Tableau des ventes
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

    // Champs de paiement
    @FXML
    private TextField txtMontantRecu;
    @FXML
    private TextField txtModePaiement;
    @FXML
    private Button retourDashboard;

    // Calculatrice
    @FXML
    private TextField txtCalculatrice;
    @FXML
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    @FXML
    private Button btnPlus, btnMinus, btnMult, btnDiv, btnEqual, btnDot, btnClear;

    private ObservableList<Vente> listeVentes = FXCollections.observableArrayList();

    private VenteService venteService;
    private LigneVenteService ligneVenteService;
    private PaiementService paiementService;
    private VendeurService vendeurService;

    // Variables pour la calculatrice
    private boolean debutNouveauNombre = true;

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
        // Initialisation des colonnes du tableau des ventes
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

        tableVentes.setItems(listeVentes);

        // Gestion de la sélection de vente
        tableVentes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) afficherDetailsVente(newVal);
        });

        // Mise à jour automatique du montant reçu depuis la calculatrice
        txtCalculatrice.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                try {
                    double valeur = Double.parseDouble(newVal);
                    txtMontantRecu.setText(newVal);
                } catch (NumberFormatException e) {
                    // Ignorer si ce n'est pas un nombre valide
                }
            }
        });

        // Initialisation de la calculatrice
        txtCalculatrice.setText("0");

        chargerVentesEnAttente();
    }

    private void afficherDetailsVente(Vente vente) {
        // Affichage des détails de la vente sélectionnée
        txtMontantRecu.clear();
        txtModePaiement.clear();
    }

    private void chargerVentesEnAttente() {
        listeVentes.clear();
        List<Vente> enAttente = venteService.recupererVentesEnAttente();
        if (enAttente != null) {
            listeVentes.setAll(enAttente);
        }
    }

    @FXML
    private void handleValiderPaiement(ActionEvent event) {
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

        String modePaiement = txtModePaiement.getText();
        if (modePaiement == null || modePaiement.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Mode de paiement manquant", "Veuillez indiquer le mode de paiement.");
            return;
        }

        try {
            // Valider le paiement et mettre à jour le stock
            venteService.validerPaiementEtMettreAJourStock(selectedVente.getId());

            double monnaie = montantRecu - selectedVente.getMontantTotal();
            showAlert(Alert.AlertType.INFORMATION, "Paiement validé",
                    String.format("💰 Monnaie à rendre : %.2f €", monnaie));

            // Génération de la facture
            List<LigneVente> lignesVente = ligneVenteService.recupererLignesParVente(selectedVente.getId());

            try {
                // Utilisation de notre GenerateurPDF corrigé
                File fichierFacture = GenerateurPDF.genererFacturePDF(selectedVente, lignesVente, montantRecu);

                if (fichierFacture != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Facture générée");
                    alert.setHeaderText("✅ Paiement validé et facture générée !");
                    alert.setContentText("📄 Fichier : " + fichierFacture.getName());

                    ButtonType btnOuvrir = new ButtonType("Ouvrir");
                    ButtonType btnFermer = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(btnOuvrir, btnFermer);

                    alert.showAndWait().ifPresent(reponse -> {
                        if (reponse == btnOuvrir) {
                            try {
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().open(fichierFacture);
                                }
                            } catch (Exception ex) {
                                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le fichier PDF.");
                            }
                        }
                    });
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur PDF", "La facture n'a pas pu être générée !");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur PDF", "Erreur lors de la génération de la facture: " + e.getMessage());
                e.printStackTrace();
            }

            // Nettoyage et rafraîchissement
            chargerVentesEnAttente();
            txtMontantRecu.clear();
            txtModePaiement.clear();
            txtCalculatrice.setText("0");
            debutNouveauNombre = true;

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du traitement du paiement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
            Scene nouvelleScene = new Scene(loader.load());
            Stage stage = (Stage) retourDashboard.getScene().getWindow();
            stage.setScene(nouvelleScene);
            stage.setMaximized(true);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le Dashboard.");
        }
    }

    @FXML
    public void handleCalculatrice(ActionEvent event) {
        Button source = (Button) event.getSource();
        String buttonText = source.getText();

        // Gestion des chiffres
        if (buttonText.matches("[0-9]")) {
            if (debutNouveauNombre || txtCalculatrice.getText().equals("0")) {
                txtCalculatrice.setText(buttonText);
                debutNouveauNombre = false;
            } else {
                txtCalculatrice.setText(txtCalculatrice.getText() + buttonText);
            }
            // Mettre à jour le montant reçu
            txtMontantRecu.setText(txtCalculatrice.getText());
            return;
        }

        // Gestion du point décimal
        if (buttonText.equals(".")) {
            if (debutNouveauNombre) {
                txtCalculatrice.setText("0.");
                debutNouveauNombre = false;
            } else if (!txtCalculatrice.getText().contains(".")) {
                txtCalculatrice.setText(txtCalculatrice.getText() + ".");
            }
            // Mettre à jour le montant reçu
            txtMontantRecu.setText(txtCalculatrice.getText());
            return;
        }

        // Gestion du bouton C (Clear)
        if (buttonText.equals("C")) {
            txtCalculatrice.setText("0");
            txtMontantRecu.setText("");
            debutNouveauNombre = true;
            return;
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
