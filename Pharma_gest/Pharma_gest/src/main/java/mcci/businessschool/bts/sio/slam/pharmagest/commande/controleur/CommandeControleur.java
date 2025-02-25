package mcci.businessschool.bts.sio.slam.pharmagest.commande.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.service.CommandeService;

import java.sql.SQLException;
import java.util.List;

public class CommandeControleur {

    @FXML
    private TableView<Commande> tableCommandes;
    @FXML
    private TableColumn<Commande, Integer> colId;
    @FXML
    private TableColumn<Commande, String> colPharmacien;
    @FXML
    private TableColumn<Commande, String> colFournisseur;
    @FXML
    private TableColumn<Commande, Double> colMontant;
    @FXML
    private TableColumn<Commande, String> colStatut;
    @FXML
    private Button btnVoirLignes;
    @FXML
    private Button btnValiderCommande;

    private CommandeService commandeService;

    public CommandeControleur() throws Exception {
        this.commandeService = new CommandeService();
    }

    @FXML
    public void initialize() {
        // Initialiser les colonnes du TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPharmacien.setCellValueFactory(new PropertyValueFactory<>("pharmacienNom"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("fournisseurNom"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Charger les commandes
        chargerCommandes();
    }

    private void chargerCommandes() {
        try {
            List<Commande> commandes = commandeService.recupererToutesLesCommandes();
            ObservableList<Commande> observableList = FXCollections.observableArrayList(commandes);
            tableCommandes.setItems(observableList);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des commandes : " + e.getMessage());
        }
    }

    @FXML
    public void voirLignesCommande() {
        Commande commandeSelectionnee = tableCommandes.getSelectionModel().getSelectedItem();
        if (commandeSelectionnee != null) {
            System.out.println("📢 Lignes de commande pour la commande ID : " + commandeSelectionnee.getId());
            // Ici, on pourra ouvrir une nouvelle fenêtre pour afficher les lignes de commande
        } else {
            System.out.println("⚠️ Veuillez sélectionner une commande.");
        }
    }


    @FXML
    public void validerCommande() {
        Commande commandeSelectionnee = tableCommandes.getSelectionModel().getSelectedItem();
        if (commandeSelectionnee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText(null);
            alert.setContentText("⚠️ Veuillez sélectionner une commande à valider.");
            alert.showAndWait();
            return;
        }

        try {
            // Vérification si la commande est déjà validée
            if ("Validée".equals(commandeSelectionnee.getStatut())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Commande déjà validée");
                alert.setHeaderText(null);
                alert.setContentText("⚠️ Cette commande est déjà validée.");
                alert.showAndWait();
                return;
            }

            // Validation de la commande
            commandeService.validerCommande(commandeSelectionnee.getId());

            // Affichage de confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Validation réussie");
            alert.setHeaderText(null);
            alert.setContentText("✅ Commande ID " + commandeSelectionnee.getId() + " validée avec succès.");
            alert.showAndWait();

            // Rafraîchir l'affichage
            chargerCommandes();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("❌ Erreur lors de la validation de la commande.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


}
