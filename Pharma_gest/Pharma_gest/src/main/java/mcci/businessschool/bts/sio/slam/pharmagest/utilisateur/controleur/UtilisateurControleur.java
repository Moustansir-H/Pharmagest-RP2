package mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Role;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Utilisateur;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.service.UtilisateurService;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.Vendeur;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class UtilisateurControleur {
    @FXML
    private Button retourMaintenance;
    @FXML
    private Button ajoutUtilisateur;
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
    private TextField rechercheUtilisateur;
    @FXML
    private Button boutonRechercheUtilisateur;
    @FXML
    private Button boutonTousUtilisateurs;
    @FXML
    private Button supprimerUtilisateur;
    @FXML
    private Button modifierUtilisateur;

    private ObservableList<Utilisateur> donneesUtilisateur = FXCollections.observableArrayList();
    private ObservableList<String> donneesRoleUtilisateur = FXCollections.observableArrayList("PHARMACIEN", "VENDEUR");
    private UtilisateurService utilisateurService;

    public UtilisateurControleur() throws Exception {
        this.utilisateurService = new UtilisateurService();
    }

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
        // Créer une boîte de dialogue pour l'ajout d'utilisateur
        Dialog<Utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un utilisateur");
        dialog.setHeaderText("Saisir les informations de l'utilisateur");

        // Définir les boutons
        ButtonType boutonAjouter = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(boutonAjouter, ButtonType.CANCEL);

        // Créer la grille pour les champs de formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Créer les champs
        TextField identifiantField = new TextField();
        identifiantField.setPromptText("Identifiant");

        PasswordField motDePasseField = new PasswordField();
        motDePasseField.setPromptText("Mot de passe");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.setItems(donneesRoleUtilisateur);
        roleComboBox.setPromptText("Choisir un rôle");

        // Ajouter les champs à la grille
        grid.add(new Label("Identifiant:"), 0, 0);
        grid.add(identifiantField, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(motDePasseField, 1, 1);
        grid.add(new Label("Rôle:"), 0, 2);
        grid.add(roleComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convertir le résultat en objet Utilisateur
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == boutonAjouter) {
                // Validation des champs
                if (identifiantField.getText().isEmpty() ||
                        motDePasseField.getText().isEmpty() ||
                        roleComboBox.getValue() == null) {
                    afficherErreur("Tous les champs doivent être remplis.");
                    return null;
                }

                try {
                    // Récupération des données saisies
                    String identifiant = identifiantField.getText();
                    String motDePasse = motDePasseField.getText();
                    Role role = Role.valueOf(roleComboBox.getValue());

                    // Création d'un utilisateur en fonction du rôle
                    if (Role.PHARMACIEN.equals(role)) {
                        return new Pharmacien(identifiant, motDePasse);
                    } else if (Role.VENDEUR.equals(role)) {
                        return new Vendeur(identifiant, motDePasse);
                    } else {
                        afficherErreur("Rôle inconnu.");
                        return null;
                    }
                } catch (Exception e) {
                    afficherErreur("Erreur lors de la création de l'utilisateur : " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        // Afficher la boîte de dialogue et traiter le résultat
        Optional<Utilisateur> result = dialog.showAndWait();

        result.ifPresent(utilisateur -> {
            try {
                // Ajout de l'utilisateur via le service
                utilisateurService.ajouterUtilisateur(utilisateur);

                // Afficher un message de succès
                afficherMessage("Utilisateur ajouté avec succès !");

                // Actualiser la liste des utilisateurs
                loadUtilisateurs();
                tableView.refresh();
            } catch (Exception e) {
                afficherErreur("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            }
        });
    }

    @FXML
    private void supprimerUtilisateur() {
        Utilisateur utilisateurSelectionne = tableView.getSelectionModel().getSelectedItem();

        if (utilisateurSelectionne != null) {
            try {
                // Confirmation de suppression
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText(null);
                alert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Suppression via le service DAO
                    utilisateurService.supprimerUtilisateurParId(utilisateurSelectionne.getId());

                    // Afficher un message de confirmation
                    afficherMessage("Utilisateur supprimé avec succès !");

                    // Mettre à jour l'affichage
                    loadUtilisateurs();
                }
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

        // Créer une boîte de dialogue pour la modification
        Dialog<Utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Modifier un utilisateur");
        dialog.setHeaderText("Modifier les informations de l'utilisateur");

        // Définir les boutons
        ButtonType boutonModifier = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(boutonModifier, ButtonType.CANCEL);

        // Créer la grille pour les champs de formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Créer les champs avec les valeurs actuelles
        TextField identifiantField = new TextField(utilisateurSelectionne.getIdentifiant());
        PasswordField motDePasseField = new PasswordField();
        motDePasseField.setText(utilisateurSelectionne.getMotDePasse());

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.setItems(donneesRoleUtilisateur);
        roleComboBox.setValue(utilisateurSelectionne.getRole().toString());

        // Ajouter les champs à la grille
        grid.add(new Label("Identifiant:"), 0, 0);
        grid.add(identifiantField, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(motDePasseField, 1, 1);
        grid.add(new Label("Rôle:"), 0, 2);
        grid.add(roleComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convertir le résultat en objet Utilisateur
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == boutonModifier) {
                try {
                    // Récupérer les nouvelles valeurs
                    String nouvelIdentifiant = identifiantField.getText();
                    String nouveauMotDePasse = motDePasseField.getText();
                    String nouveauRoleSelectionne = roleComboBox.getValue();

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
                        return null;
                    }

                    return utilisateurSelectionne;
                } catch (Exception e) {
                    afficherErreur("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        // Afficher la boîte de dialogue et traiter le résultat
        Optional<Utilisateur> result = dialog.showAndWait();

        result.ifPresent(utilisateur -> {
            try {
                // Appeler le service pour appliquer les modifications
                utilisateurService.modifierUtilisateur(utilisateur);

                // Afficher un message de succès
                afficherMessage("Utilisateur modifié avec succès !");

                // Actualiser la liste et rafraîchir l'interface
                loadUtilisateurs();
                tableView.refresh();
            } catch (Exception e) {
                afficherErreur("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
            }
        });
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
