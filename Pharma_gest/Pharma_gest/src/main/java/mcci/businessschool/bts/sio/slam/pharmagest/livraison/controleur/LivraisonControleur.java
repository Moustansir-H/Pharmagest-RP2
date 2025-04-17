package mcci.businessschool.bts.sio.slam.pharmagest.livraison.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.Livraison;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.service.LivraisonService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LivraisonControleur {

    @FXML
    private TableView<Livraison> tableLivraisons;
    @FXML
    private TableColumn<Livraison, Integer> colId;
    @FXML
    private TableColumn<Livraison, String> colDate;
    @FXML
    private TableColumn<Livraison, String> colStatus;
    @FXML
    private TableColumn<Livraison, Integer> colCommandeId;
    @FXML
    private TableColumn<Livraison, Integer> colFournisseur;

    @FXML
    private Button btnVoirDetails;
    @FXML
    private Button btnRetour;

    private LivraisonService livraisonService;
    private ObservableList<Livraison> livraisonsObservable;

    @FXML
    public void initialize() {
        try {
            livraisonService = new LivraisonService();
            initialiserColonnes();
            chargerLivraisons();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialiserColonnes() {
        // Liaison des colonnes aux propriétés de la classe Livraison
        // (Tu peux ajuster selon la structure de ta classe Livraison)
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject()
        );

        colDate.setCellValueFactory(cellData -> {
            // Convertir la date en String (ex. "2025-02-19")
            if (cellData.getValue().getDateLivraison() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateLivraison().toString()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("--");
        });

        colStatus.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus())
        );

        colCommandeId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCommandeId()).asObject()
        );

        colFournisseur.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getFournisseurId()).asObject()
        );
    }

    private void chargerLivraisons() {
        try {
            List<Livraison> liste = livraisonService.recupererToutesLesLivraisons();
            livraisonsObservable = FXCollections.observableArrayList(liste);
            tableLivraisons.setItems(livraisonsObservable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void retourApprovisionnement() {
        try {
            // Retour à l'interface Approvisionnement (à adapter selon ton organisation)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/approvisionnement/Approvisionnement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Approvisionnement");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
