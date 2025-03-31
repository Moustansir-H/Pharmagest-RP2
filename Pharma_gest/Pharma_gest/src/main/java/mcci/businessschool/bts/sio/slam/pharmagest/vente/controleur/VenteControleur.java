package mcci.businessschool.bts.sio.slam.pharmagest.vente.controleur;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.service.MedicamentService;
import mcci.businessschool.bts.sio.slam.pharmagest.patient.Patient;
import mcci.businessschool.bts.sio.slam.pharmagest.patient.service.PatientService;
import mcci.businessschool.bts.sio.slam.pharmagest.prescription.Prescription;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.TypeVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.service.VenteIntegrationService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ticket.GenerateurPDF;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class VenteControleur {

    @FXML
    private TableView<Medicament> tableMedicaments;
    @FXML
    private TableColumn<Medicament, String> colMedNom, colMedForme;
    @FXML
    private TableColumn<Medicament, Integer> colMedStock;
    @FXML
    private TableColumn<Medicament, Double> colMedPrixVente;
    @FXML
    private TextField txtRecherche, txtQuantite;
    @FXML
    private TableView<LigneVente> tableLignesVente;
    @FXML
    private TableColumn<LigneVente, String> colVenteMedicament;
    @FXML
    private TableColumn<LigneVente, Integer> colVenteQuantite;
    @FXML
    private TableColumn<LigneVente, Double> colVentePrixUnitaire, colVenteSousTotal;
    @FXML
    private Label lblMontantTotal;
    @FXML
    private ChoiceBox<String> choiceTypeVente;
    @FXML
    private VBox sectionPrescription;
    @FXML
    private TextField txtPatientNom, txtPatientPrenom, txtPatientAdresse, txtPatientContact, txtNomMedecin;
    @FXML
    private DatePicker datePatientNaissance, datePrescription;
    @FXML
    private Button retourDashboard;
    @FXML
    private TextField txtRecherchePatient;


    private ObservableList<Medicament> listMedicaments = FXCollections.observableArrayList();
    private ObservableList<LigneVente> listLignesVente = FXCollections.observableArrayList();

    private MedicamentService medicamentService;
    private VenteIntegrationService venteIntegrationService;
    private PatientService patientService;


    public VenteControleur() {
        try {
            medicamentService = new MedicamentService();
            venteIntegrationService = new VenteIntegrationService();
            patientService = new PatientService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        // Gérer la visibilité du formulaire de prescription selon le type de vente
        choiceTypeVente.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            sectionPrescription.setVisible("PRESCRITE".equals(newVal));
        });
        // ✅ Sélectionner LIBRE par défaut
        choiceTypeVente.setValue("LIBRE");

        // Configuration de la TableView des médicaments
        colMedNom.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNom()));
        colMedForme.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getForme()));
        colMedStock.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStock()));
        colMedPrixVente.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrixVente()));
        tableMedicaments.setItems(listMedicaments);

        chargerMedicaments();

        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> filtrerMedicaments(newVal));

        // Configuration de la TableView des lignes de vente
        colVenteMedicament.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getMedicament().getNom()));
        colVenteQuantite.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantiteVendu()));
        colVentePrixUnitaire.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrixUnitaire()));
        colVenteSousTotal.setCellValueFactory(cellData -> {
            LigneVente ligne = cellData.getValue();
            return new ReadOnlyObjectWrapper<>(ligne.getQuantiteVendu() * ligne.getPrixUnitaire());
        });
        tableLignesVente.setItems(listLignesVente);

        // ✅ Autoriser la sélection multiple dans la table des lignes de vente
        tableLignesVente.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // ✅ Suppression via la touche Suppr
        tableLignesVente.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DELETE -> handleSupprimerLigneVente();
            }
        });

        lblMontantTotal.setText("0.00 €");
    }


    private void chargerMedicaments() {
        listMedicaments.clear();
        List<Medicament> meds = medicamentService.recupererMedicaments();
        if (meds != null) {
            listMedicaments.setAll(meds);
        }
    }

    private void filtrerMedicaments(String recherche) {
        if (recherche == null || recherche.isBlank()) {
            chargerMedicaments();
            return;
        }
        String lowerRecherche = recherche.toLowerCase();
        List<Medicament> allMeds = medicamentService.recupererMedicaments();
        if (allMeds != null) {
            listMedicaments.setAll(allMeds.stream()
                    .filter(med -> med.getNom().toLowerCase().contains(lowerRecherche))
                    .toList());
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

        if (quantite <= 0 || quantite > selectedMed.getStock()) {
            showAlert(Alert.AlertType.WARNING, "Stock insuffisant", "Quantité invalide.");
            return;
        }

        // Vérifier si le médicament est déjà dans la liste
        for (LigneVente ligne : listLignesVente) {
            if (ligne.getMedicament().getId() == selectedMed.getId()) {
                ligne.setQuantiteVendu(ligne.getQuantiteVendu() + quantite);
                calculerMontantTotal();
                txtQuantite.clear();
                tableLignesVente.refresh();
                return;
            }
        }

        // Ajouter une nouvelle ligne si le médicament n'est pas encore présent
        listLignesVente.add(new LigneVente(quantite, selectedMed.getPrixVente(), selectedMed));
        calculerMontantTotal();
        txtQuantite.clear();
    }

    @FXML
    private void handleSupprimerLigneVente() {
        ObservableList<LigneVente> lignesSelectionnees = tableLignesVente.getSelectionModel().getSelectedItems();

        if (lignesSelectionnees == null || lignesSelectionnees.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une ou plusieurs lignes à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer les lignes sélectionnées ?");
        confirmation.setContentText("Cette action va retirer les médicaments de la vente en cours.");

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                listLignesVente.removeAll(lignesSelectionnees);
                calculerMontantTotal();
                tableLignesVente.getSelectionModel().clearSelection();
            }
        });
    }


    private void calculerMontantTotal() {
        lblMontantTotal.setText(String.format("%.2f €", listLignesVente.stream()
                .mapToDouble(ligne -> ligne.getQuantiteVendu() * ligne.getPrixUnitaire()).sum()));
    }

    @FXML
    public void retourDashboardOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
            Scene nouvelleScene = new Scene(loader.load());
            Stage stage = (Stage) retourDashboard.getScene().getWindow();
            stage.setScene(nouvelleScene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le Dashboard !");
        }
    }

    @FXML
    private void handleValiderVente() {
        if (listLignesVente.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune ligne", "Ajoutez au moins un médicament à la vente.");
            return;
        }

        TypeVente type = TypeVente.valueOf(choiceTypeVente.getValue());
        Patient patient = null;
        Prescription prescription = null;

        if (type == TypeVente.PRESCRITE) {
            if (txtPatientNom.getText().isBlank() || txtPatientPrenom.getText().isBlank() ||
                    datePatientNaissance.getValue() == null || txtPatientAdresse.getText().isBlank() ||
                    txtPatientContact.getText().isBlank() || txtNomMedecin.getText().isBlank() ||
                    datePrescription.getValue() == null) {

                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir toutes les informations du patient et de la prescription.");
                return;
            }

            // Création du patient
            patient = new Patient(
                    txtPatientNom.getText(),
                    txtPatientPrenom.getText(),
                    java.sql.Date.valueOf(datePatientNaissance.getValue()),
                    txtPatientAdresse.getText(),
                    txtPatientContact.getText()
            );

            // Création de la prescription
            prescription = new Prescription(
                    txtNomMedecin.getText(),
                    java.sql.Date.valueOf(datePrescription.getValue())
            );
        }

        // ✅ Création et validation de la vente
        Vente vente = venteIntegrationService.creerVentePharmacien(listLignesVente, type, patient, prescription);

        // ✅ Génération du ticket PDF
        try {
            File fichierPDF = GenerateurPDF.genererTicketPDF(vente, listLignesVente, patient, prescription);

            if (fichierPDF != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ticket généré");
                alert.setHeaderText("Vente validé, et le ticket a été sauvegardé!");
                alert.setContentText("Ticket : " + fichierPDF.getName());

                ButtonType btnOuvrir = new ButtonType("Ouvrir");
                ButtonType btnFermer = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(btnOuvrir, btnFermer);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == btnOuvrir) {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(fichierPDF);
                        }
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir le ticket PDF.");
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur PDF", "Le ticket n’a pas pu être généré.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur PDF", "La vente a été créée mais le ticket n'a pas pu être généré.");
            e.printStackTrace();
        }

        // ✅ Réinitialisation de l'interface
        resetInterface();
    }


    @FXML
    private void handleRechercherPatient() {
        String nomRecherche = txtRecherchePatient.getText().trim();

        if (nomRecherche.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Recherche vide", "Veuillez entrer un nom de patient.");
            return;
        }

        // Recherche du patient en base
        Patient patientTrouve = patientService.rechercherPatientParNom(nomRecherche);

        if (patientTrouve != null) {
            // Remplir automatiquement les champs avec les infos du patient trouvé
            txtPatientNom.setText(patientTrouve.getNom());
            txtPatientPrenom.setText(patientTrouve.getPrenom());
            datePatientNaissance.setValue(patientTrouve.getDateNaissance().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate());

            txtPatientAdresse.setText(patientTrouve.getAdresse());
            txtPatientContact.setText(patientTrouve.getContact());

            showAlert(Alert.AlertType.INFORMATION, "Patient trouvé", "Les informations du patient ont été chargées.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun patient trouvé", "Aucun patient correspondant à ce nom.");
        }
    }


    private void resetInterface() {
        // Réinitialiser les champs du patient
        txtPatientNom.clear();
        txtPatientPrenom.clear();
        txtPatientAdresse.clear();
        txtPatientContact.clear();
        datePatientNaissance.setValue(null);

        // Réinitialiser les champs de la prescription
        txtNomMedecin.clear();
        datePrescription.setValue(null);

        // Réinitialiser le type de vente
        choiceTypeVente.setValue("LIBRE");
        sectionPrescription.setVisible(false);

        // Vider la liste des lignes de vente
        listLignesVente.clear();
        tableLignesVente.refresh();

        // Remettre le montant total à zéro
        lblMontantTotal.setText("0.00");
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
