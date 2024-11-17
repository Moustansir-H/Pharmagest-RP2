package mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Role;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Utilisateur;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.service.UtilisateurService;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.Vendeur;

import java.io.IOException;
import java.util.List;


public class UtilisateurControleur {
    @FXML
    private Button retourMaintenance;
    @FXML
    private TextField identifiantUtilisateur; // Champ pour l'identifiant
    @FXML
    private TextField motdepasseUtilisateur; // Champ pour le mot de passe
    @FXML
    private TableView<Utilisateur> tableView;
    @FXML
    private TableColumn<Utilisateur, String> idColumn;
    @FXML
    private TableColumn<Utilisateur, Role> roleColumn;
    @FXML
    private TableColumn<Utilisateur, String> motDePasseColumn;
    @FXML
    private TableColumn<Utilisateur, Integer> idBaseColumn;
    @FXML
    private ComboBox<String> roleUtilisateur;
    @FXML
    private ComboBox<String> nouveauRoleUtilisateur;
    @FXML
    private TextField nouveauIdentifiantUtilisateur;
    @FXML
    private TextField nouveauMotdePasseUtilisateur;
    @FXML
    private TextField rechercheUtilisateur;
    @FXML
    private Button boutonRechercheUtilisateur;
    @FXML
    private Button boutonTousUtilisateurs;


    private ObservableList<Utilisateur> donneesUtilisateur = FXCollections.observableArrayList();

    private ObservableList<String> donneesRoleUtilisateur = FXCollections.observableArrayList("PHARMACIEN", "VENDEUR");

    private UtilisateurService utilisateurService;

    public UtilisateurControleur() throws Exception {
        this.utilisateurService = new UtilisateurService();
    }

    //Fin table

    @FXML
    public void retourMaintenanceOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/Maintenance.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) retourMaintenance.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    public void initialize() {
        // Ajouter les rôles au ComboBox
        roleUtilisateur.setItems(donneesRoleUtilisateur);
        roleUtilisateur.setPromptText("Choisir un rôle");

        nouveauRoleUtilisateur.setItems(donneesRoleUtilisateur);
        nouveauRoleUtilisateur.setPromptText("Choisir un role");

        // Initialisation des colonnes du TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("identifiant"));

        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        motDePasseColumn.setCellValueFactory(new PropertyValueFactory<>("motDePasse"));
        motDePasseColumn.setVisible(false);

        idBaseColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idBaseColumn.setVisible(false);


        // Chargement initial des utilisateurs
        loadUtilisateurs();


        // Forcer la mise à jour des données dans le TableView
        tableView.refresh();

        rechercheUtilisateur.textProperty().addListener((observable, oldValue, newValue) -> rechercherUtilisateur());
    }

    // Méthode pour charger les utilisateurs depuis la base de données
    private void loadUtilisateurs() {
        // Initialisation des données utilisateurs
        donneesUtilisateur.clear();

        // Recuperation des listes utilisateurs en base
        List<Utilisateur> utilisateurs = utilisateurService.recupererUtilisateurs();

        // Ajout des donnees recuperees en base dans les donnees utilisateurs
        donneesUtilisateur.addAll(utilisateurs);

        // Mise à jour des données dans le TableView
        tableView.setItems(donneesUtilisateur);
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void ajouterUtilisateur() {

        // Validation des champs
        if (identifiantUtilisateur.getText().isEmpty() ||
                motdepasseUtilisateur.getText().isEmpty() ||
                roleUtilisateur.getValue() == null) {
            afficherErreur("Tous les champs doivent être remplis.");
            return;
        }

        try {
            // Récupération des données saisies
            String identifiant = identifiantUtilisateur.getText();
            String motDePasse = motdepasseUtilisateur.getText();
            Role role = Role.valueOf(roleUtilisateur.getValue());

            // Création d'un utilisateur en fonction du rôle
            Utilisateur utilisateur;
            if (Role.PHARMACIEN.equals(role)) {
                utilisateur = new Pharmacien(identifiant, motDePasse);
            } else if (Role.VENDEUR.equals(role)) {
                utilisateur = new Vendeur(identifiant, motDePasse);
            } else {
                afficherErreur("Rôle inconnu.");
                return;
            }

            // Ajout de l'utilisateur via le service
            utilisateurService.ajouterUtilisateur(utilisateur);

            // Afficher un message de succès
            afficherMessage("Utilisateur ajouté avec succès !");

            // Actualiser la liste des utilisateurs
            loadUtilisateurs();

            tableView.refresh();

            // Réinitialiser les champs
            identifiantUtilisateur.clear();
            motdepasseUtilisateur.clear();
            roleUtilisateur.setValue(null);

        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }

    }

    @FXML
    private void supprimerUtilisateur() {
        Utilisateur utilisateurSelectionne = tableView.getSelectionModel().getSelectedItem();

        if (utilisateurSelectionne != null) {
            try {
                // Suppression via le service DAO
                utilisateurService.supprimerUtilisateurParId(utilisateurSelectionne.getId());

                // Afficher un message de confirmation
                afficherMessage("Utilisateur supprimé avec succès !");

                // Mettre à jour l'affichage
                loadUtilisateurs();

            } catch (Exception e) {
                // Gérer les erreurs et afficher un message
                afficherErreur("Erreur lors de la suppression : " + e.getMessage());
            }
        } else {
            // Aucun utilisateur sélectionné
            afficherErreur("Veuillez sélectionner un utilisateur à supprimer.");
        }
    }

    @FXML
    private void modifierUtilisateur() {
        // Récupérer l'utilisateur sélectionné
        Utilisateur utilisateurSelectionne = tableView.getSelectionModel().getSelectedItem();

        if (utilisateurSelectionne == null) {
            afficherErreur("Veuillez sélectionner un utilisateur à modifier.");
            return;
        }

        try {
            // Récupérer les nouvelles valeurs des champs
            String nouvelIdentifiant = nouveauIdentifiantUtilisateur.getText();
            String nouveauMotDePasse = nouveauMotdePasseUtilisateur.getText();
            String nouveauRoleSelectionne = nouveauRoleUtilisateur.getValue();

            boolean modificationEffectuee = false;

            // Vérifier et mettre à jour l'identifiant si nécessaire
            if (!nouvelIdentifiant.isEmpty() && !nouvelIdentifiant.equals(utilisateurSelectionne.getIdentifiant())) {
                utilisateurSelectionne.setIdentifiant(nouvelIdentifiant);
                modificationEffectuee = true;
            }

            // Vérifier et mettre à jour le mot de passe si nécessaire
            if (!nouveauMotDePasse.isEmpty() && !nouveauMotDePasse.equals(utilisateurSelectionne.getMotDePasse())) {
                utilisateurSelectionne.setMotDePasse(nouveauMotDePasse);
                modificationEffectuee = true;
            }

            // Vérifier et mettre à jour le rôle si nécessaire
            if (nouveauRoleSelectionne != null) {
                Role nouveauRole = Role.valueOf(nouveauRoleSelectionne);
                if (!nouveauRole.equals(utilisateurSelectionne.getRole())) {
                    utilisateurSelectionne.setRole(nouveauRole);
                    modificationEffectuee = true;
                }
            }

            // Si aucune modification n'a été effectuée, ne rien faire
            if (!modificationEffectuee) {
                return;
            }

            // Appeler le service pour appliquer les modifications
            utilisateurService.modifierUtilisateur(utilisateurSelectionne);

            // Afficher un message de succès
            afficherMessage("Utilisateur modifié avec succès !");

            // Actualiser la liste et rafraîchir l'interface
            loadUtilisateurs();
            tableView.refresh();

            // Réinitialiser les champs
            nouveauIdentifiantUtilisateur.clear();
            nouveauMotdePasseUtilisateur.clear();
            nouveauRoleUtilisateur.setValue(null);

        } catch (Exception e) {
            afficherErreur("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
        }
    }


    @FXML
    private void rechercherUtilisateur() {
        String filtre = rechercheUtilisateur.getText().toLowerCase();

        if (filtre.isEmpty()) {
            // Si aucun filtre, afficher tous les utilisateurs
            loadUtilisateurs();
            return;
        }

        // Filtrer les utilisateurs en fonction du texte saisi
        ObservableList<Utilisateur> utilisateursFiltres = FXCollections.observableArrayList();

        for (Utilisateur utilisateur : donneesUtilisateur) {
            if (utilisateur.getIdentifiant().toLowerCase().contains(filtre)) {
                utilisateursFiltres.add(utilisateur);
            }
        }


        // Mettre à jour le TableView avec les utilisateurs filtrés
        tableView.setItems(utilisateursFiltres);
        tableView.refresh();
    }

    @FXML
    private void afficherTousUtilisateurs() {

        loadUtilisateurs();

        // Effacer le champ de recherche
        rechercheUtilisateur.clear();

        // Actualiser le TableView pour afficher tous les utilisateurs
        tableView.refresh();
    }


}
