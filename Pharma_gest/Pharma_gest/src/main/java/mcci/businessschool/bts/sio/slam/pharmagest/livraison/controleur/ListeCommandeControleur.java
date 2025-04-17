package mcci.businessschool.bts.sio.slam.pharmagest.livraison.controleur;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.service.LigneDeCommandeService;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.Livraison;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;

import java.sql.SQLException;
import java.util.List;

public class ListeCommandeControleur {

    @FXML
    private Label lblLivraisonId;
    @FXML
    private Label lblDateLivraison;
    @FXML
    private Label lblFournisseur;
    @FXML
    private Label lblStatus;

    @FXML
    private TableView<LigneDeCommande> tableLignesCommande;
    @FXML
    private TableColumn<LigneDeCommande, String> colMedicament;
    @FXML
    private TableColumn<LigneDeCommande, Integer> colQuantiteCommandee;
    @FXML
    private TableColumn<LigneDeCommande, Integer> colQuantiteRecue;
    @FXML
    private TableColumn<LigneDeCommande, Double> colPrixAchatReel;
    @FXML
    private TableColumn<LigneDeCommande, Double> colPrixVenteReel;

    @FXML
    private Button btnValiderReception;
    @FXML
    private Button btnRetour;

    private Livraison livraison; // L'objet Livraison reçu
    private ObservableList<LigneDeCommande> lignesObservable;

    private LigneDeCommandeService ligneService;
    private MedicamentDao medicamentDao;

    @FXML
    public void initialize() {
        try {
            ligneService = new LigneDeCommandeService();
            medicamentDao = new MedicamentDao();

            // Configurer les colonnes du tableau
            initialiserColonnes();

            // Le contenu du tableau sera chargé après setLivraison()
            // (une fois qu'on connaît la livraison sélectionnée).
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialiserColonnes() {
        colMedicament.setCellValueFactory(cellData -> {
            String nomMed = cellData.getValue().getMedicament().getNom();
            return new SimpleStringProperty(nomMed);
        });
        colQuantiteCommandee.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantiteVendu()).asObject()
        );

        // Pour rendre la colonne éditable, on peut utiliser un TextFieldTableCell
        // et gérer l'event onEditCommit. Pour simplifier, on montre juste le concept :

        colQuantiteRecue.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantiteRecue()).asObject()
        );
        // Pour l'édition (optionnel)
        colQuantiteRecue.setCellFactory(TextFieldTableCell.forTableColumn(
                new javafx.util.converter.IntegerStringConverter()
        ));
        colQuantiteRecue.setOnEditCommit(event -> {
            LigneDeCommande ligne = event.getRowValue();
            ligne.setQuantiteRecue(event.getNewValue());
        });

        colPrixAchatReel.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrixAchatReel()).asObject()
        );
        colPrixAchatReel.setCellFactory(TextFieldTableCell.forTableColumn(
                new javafx.util.converter.DoubleStringConverter()
        ));
        colPrixAchatReel.setOnEditCommit(event -> {
            LigneDeCommande ligne = event.getRowValue();
            ligne.setPrixAchatReel(event.getNewValue());
        });

        colPrixVenteReel.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrixVenteReel()).asObject()
        );
        colPrixVenteReel.setCellFactory(TextFieldTableCell.forTableColumn(
                new javafx.util.converter.DoubleStringConverter()
        ));
        colPrixVenteReel.setOnEditCommit(event -> {
            LigneDeCommande ligne = event.getRowValue();
            ligne.setPrixVenteReel(event.getNewValue());
        });

        tableLignesCommande.setEditable(true);
    }

    /**
     * Méthode appelée depuis LivraisonControleur pour passer la livraison sélectionnée.
     */
    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
        afficherInformationsLivraison();
        chargerLignesCommande();
    }

    private void afficherInformationsLivraison() {
        lblLivraisonId.setText(String.valueOf(livraison.getId()));
        lblDateLivraison.setText(String.valueOf(livraison.getDateLivraison()));
        lblFournisseur.setText(String.valueOf(livraison.getFournisseurId())); // Ou récupérer le nom
        lblStatus.setText(livraison.getStatus());
    }

    private void chargerLignesCommande() {
        try {
            // Récupérer les lignes associées à la commande de cette livraison
            int commandeId = livraison.getCommandeId();
            List<LigneDeCommande> lignes = ligneService.recupererLignesParCommande(commandeId);
            lignesObservable = FXCollections.observableArrayList(lignes);
            tableLignesCommande.setItems(lignesObservable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void validerReception() {
        // Parcourir toutes les lignes pour récupérer les valeurs saisies
        for (LigneDeCommande ligne : lignesObservable) {
            try {
                // Mettre à jour la réception (quantité reçue, prix réels)
                ligneService.mettreAJourReception(
                        ligne.getId(),
                        ligne.getQuantiteRecue(),
                        ligne.getPrixAchatReel(),
                        ligne.getPrixVenteReel()
                );

                // Mettre à jour le stock du médicament
                medicamentDao.mettreAJourStock(ligne.getMedicament().getId(), ligne.getQuantiteRecue());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Éventuellement, mettre à jour le statut de la livraison (par exemple "Livrée")
        // => tu peux appeler livraisonService.updateStatutLivraison(livraison.getId(), "Livrée");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Réception validée");
        alert.setHeaderText(null);
        alert.setContentText("La réception de la livraison a été validée avec succès !");
        alert.showAndWait();
    }

    @FXML
    private void retourListeLivraisons() {
        // Revenir à l'interface "Livraison.fxml"
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/commande/Livraison.fxml")
            );
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) btnRetour.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Liste des Livraisons");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
