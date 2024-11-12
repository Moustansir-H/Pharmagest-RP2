package mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Utilisateur;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UtilisateurControleur {
    @FXML
    private Button retourMaintenance;
    //Table

    @FXML
    private TextField nomUtilisateur;
    @FXML
    private TextField prenomUtilisateur;
    @FXML
    private TextField mailUtilisateur;
    @FXML
    private TableView<Utilisateur> tableView;
    @FXML
    private TableColumn<Utilisateur, Integer> idColumn;
    @FXML
    private TableColumn<Utilisateur, String> utilisateurColumn;

    private ObservableList<Utilisateur> utilisateurData = FXCollections.observableArrayList();

    //Fin table

    @FXML
    public void retourMaintenanceOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Maintenance.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) retourMaintenance.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void initialize() {
        // Initialisation des colonnes du TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        utilisateurColumn.setCellValueFactory(new PropertyValueFactory<>("identifiant"));


        // Chargement initial des utilisateurs
       // loadUtilisateurs();

        // Forcer la mise à jour des données dans le TableView
        tableView.refresh();
    }

    // Méthode pour charger les utilisateurs depuis la base de données
    /*private void loadUtilisateurs() {
        // utilisateurData.clear();
        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection();
             Statement stmt = connectDB.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, identifiant FROM utilisateur")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String identifiant = rs.getString("identifiant");
                utilisateurData.add(new Pharmacien(identifiant, ""));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }

        // Mise à jour des données dans le TableView
        tableView.setItems(utilisateurData);
    }*/


}
