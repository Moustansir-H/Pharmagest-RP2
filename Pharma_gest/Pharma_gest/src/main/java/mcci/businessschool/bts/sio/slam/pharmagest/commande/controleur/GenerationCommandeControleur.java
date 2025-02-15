package mcci.businessschool.bts.sio.slam.pharmagest.commande.controleur;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.ligne.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.service.CommandeService;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.service.LigneDeCommandeService;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.service.FournisseurService;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.dao.PharmacienDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class GenerationCommandeControleur {

    @FXML
    private ComboBox<Fournisseur> fournisseurComboBox;

    @FXML
    private TableView<LigneDeCommande> commandeTable;

    @FXML
    private Label montantTotalLabel;

    @FXML
    private TableColumn<LigneDeCommande, String> colCommandeMedicament;

    @FXML
    private TableColumn<LigneDeCommande, Integer> colCommandeQuantite;

    @FXML
    private TableColumn<LigneDeCommande, Double> colCommandePrixUnitaire;

    @FXML
    private TableColumn<LigneDeCommande, Double> colCommandeTotal;

    private CommandeService commandeService;
    private LigneDeCommandeService ligneDeCommandeService;
    private MedicamentDao medicamentDao;
    private FournisseurService fournisseurService;
    private ObservableList<LigneDeCommande> lignesCommande;
    private double montantTotal = 0;

    public GenerationCommandeControleur() throws Exception {
        this.commandeService = new CommandeService();
        this.ligneDeCommandeService = new LigneDeCommandeService();
        this.medicamentDao = new MedicamentDao();
        this.fournisseurService = new FournisseurService();
        this.lignesCommande = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        colCommandeMedicament.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMedicament().getNom()));

        colCommandeQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteVendu"));
        colCommandeQuantite.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colCommandeQuantite.setOnEditCommit(event -> {
            LigneDeCommande ligne = event.getRowValue();
            int nouvelleQuantite = event.getNewValue();

            if (nouvelleQuantite <= 0) {
                afficherAlerte("Erreur", "La quantité doit être supérieure à 0.");
                commandeTable.refresh();
                return;
            }

            ligne.setQuantiteVendu(nouvelleQuantite);
            recalculerMontantTotal();
            commandeTable.refresh();
        });

        colCommandePrixUnitaire.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));

        colCommandeTotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getQuantiteVendu() * cellData.getValue().getPrixUnitaire()).asObject());

        fournisseurComboBox.setItems(FXCollections.observableArrayList(fournisseurService.recupererTousLesFournisseurs()));

        fournisseurComboBox.setOnAction(event -> chargerMedicamentsParFournisseur());

        commandeTable.setItems(lignesCommande);
    }

    private void chargerMedicamentsParFournisseur() {
        Fournisseur fournisseurSelectionne = fournisseurComboBox.getSelectionModel().getSelectedItem();
        if (fournisseurSelectionne == null) return;

        lignesCommande.clear();

        List<Medicament> medicaments = medicamentDao.recupererMedicamentsSousSeuilParFournisseur(fournisseurSelectionne.getId());

        for (Medicament medicament : medicaments) {
            int quantiteACommander = medicament.getQteMax() - medicament.getStock();
            if (quantiteACommander > 0) {
                LigneDeCommande ligne = new LigneDeCommande(quantiteACommander, medicament.getPrixAchat(), medicament);
                lignesCommande.add(ligne);
            }
        }

        recalculerMontantTotal();
    }

    private void recalculerMontantTotal() {
        montantTotal = lignesCommande.stream()
                .mapToDouble(ligne -> ligne.getQuantiteVendu() * ligne.getPrixUnitaire())
                .sum();
        montantTotalLabel.setText(String.format("%.2f €", montantTotal));
    }

    @FXML
    private void validerCommande(ActionEvent event) {
        Fournisseur fournisseurSelectionne = fournisseurComboBox.getSelectionModel().getSelectedItem();
        if (fournisseurSelectionne == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un fournisseur.");
            return;
        }

        try {
            Pharmacien pharmacien = new PharmacienDao().recupererPharmacienParId(1);
            Commande nouvelleCommande = new Commande(0, montantTotal, pharmacien);
            Integer commandeId = commandeService.ajouterCommande(nouvelleCommande);

            for (LigneDeCommande ligne : lignesCommande) {
                ligneDeCommandeService.ajouterLigneDeCommande(commandeId, ligne);
            }

            File fichierPdf = genererBonDeCommandePDF(fournisseurSelectionne, lignesCommande);
            envoyerEmailFournisseur(fournisseurSelectionne, fichierPdf);

            afficherAlerte("Succès", "Commande créée et envoyée par email avec succès !");
        } catch (Exception e) {
            afficherAlerte("Erreur", "Erreur lors de la validation et de l'envoi.");
        }
    }

    private File genererBonDeCommandePDF(Fournisseur fournisseur, List<LigneDeCommande> lignesCommande) throws Exception {
        Document document = new Document();
        File fichierPdf = new File("Commande_" + fournisseur.getNom() + ".pdf");
        PdfWriter.getInstance(document, new FileOutputStream(fichierPdf));
        document.open();

        document.add(new Paragraph("Bon de Commande\n\n"));

        PdfPTable table = new PdfPTable(4);
        table.addCell("Médicament");
        table.addCell("Quantité");
        table.addCell("Prix Unitaire (€)");
        table.addCell("Total (€)");

        for (LigneDeCommande ligne : lignesCommande) {
            table.addCell(ligne.getMedicament().getNom());
            table.addCell(String.valueOf(ligne.getQuantiteVendu()));
            table.addCell(String.valueOf(ligne.getPrixUnitaire()));
            table.addCell(String.valueOf(ligne.getQuantiteVendu() * ligne.getPrixUnitaire()));
        }

        document.add(table);
        document.close();

        return fichierPdf;
    }

    private void envoyerEmailFournisseur(Fournisseur fournisseur, File fichierPdf) {
        // Implémentation identique à la réponse précédente
    }

    @FXML
    private void retournerAuMenu(ActionEvent event) throws IOException {
        Stage stage = (Stage) montantTotalLabel.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/maintenance/Maintenance.fxml"))));
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
