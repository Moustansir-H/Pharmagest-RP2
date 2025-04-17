package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FournisseurControleur {
    @FXML
    private Button retourDashboard;

    @FXML
    private TextField rechercheField;

    @FXML
    private Button ajoutFournisseur, modifierFournisseur, supprimerFournisseur, boutonTousFournisseurs;

    @FXML
    private TableView<Fournisseur> tableFournisseurs;

    @FXML
    private TableColumn<Fournisseur, Integer> colonneId;
    @FXML
    private TableColumn<Fournisseur, String> colonneNom;
    @FXML
    private TableColumn<Fournisseur, String> colonneAdresse;
    @FXML
    private TableColumn<Fournisseur, String> colonneContact;
    @FXML
    private TableColumn<Fournisseur, String> colonneEmail;

    private FournisseurDao fournisseurDao;
    private ObservableList<Fournisseur> fournisseurListe = FXCollections.observableArrayList();

    public FournisseurControleur() {
        try {
            this.fournisseurDao = new FournisseurDao();
        } catch (Exception e) {
            System.err.println("Erreur d'accès à la base de données : " + e.getMessage());
        }
    }

    @FXML
    public void retourDashboardOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) retourDashboard.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
        stage.setMaximized(true);

    }

    @FXML
    public void initialize() {
        colonneId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colonneNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colonneAdresse.setCellValueFactory(cellData -> cellData.getValue().adresseProperty());
        colonneContact.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        colonneEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        afficherTousFournisseurs();

        // Double-clic pour voir les détails
        tableFournisseurs.setRowFactory(tv -> {
            TableRow<Fournisseur> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    afficherDetailsFournisseur(row.getItem());
                }
            });
            return row;
        });
    }

    @FXML
    private void ajouterFournisseur() {
        Dialog<Fournisseur> dialog = creerFournisseurDialog(null);
        Optional<Fournisseur> result = dialog.showAndWait();

        result.ifPresent(fournisseur -> {
            try {
                fournisseurDao.ajouterFournisseur(fournisseur);
                afficherTousFournisseurs();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Fournisseur ajouté avec succès.");
            } catch (SQLException e) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter le fournisseur: " + e.getMessage());
            }
        });
    }

    @FXML
    private void modifierFournisseur() {
        Fournisseur selectionne = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un fournisseur.");
            return;
        }

        Dialog<Fournisseur> dialog = creerFournisseurDialog(selectionne);
        Optional<Fournisseur> result = dialog.showAndWait();

        result.ifPresent(fournisseur -> {
            try {
                fournisseurDao.modifierFournisseur(fournisseur);
                afficherTousFournisseurs();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Fournisseur modifié avec succès.");
            } catch (SQLException e) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de modifier le fournisseur: " + e.getMessage());
            }
        });
    }

    @FXML
    private void supprimerFournisseur() {
        Fournisseur selectionne = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un fournisseur.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce fournisseur ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                fournisseurDao.supprimerFournisseur(selectionne.getId());
                afficherTousFournisseurs();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Fournisseur supprimé avec succès.");
            } catch (SQLException e) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de supprimer le fournisseur: " + e.getMessage());
            }
        }
    }

    @FXML
    private void afficherTousFournisseurs() {
        fournisseurListe.clear();
        List<Fournisseur> fournisseurs = fournisseurDao.recupererTousLesFournisseurs();
        fournisseurListe.addAll(fournisseurs);
        tableFournisseurs.setItems(fournisseurListe);
    }

    @FXML
    private void rechercherFournisseur() {
        String nomRecherche = rechercheField.getText();
        if (nomRecherche.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Recherche vide", "Veuillez entrer un nom.");
            return;
        }

        // 🔍 Vérifier si l'ID existe
        Integer id = fournisseurDao.getFournisseurIdByName(nomRecherche);
        if (id == null) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Aucun résultat", "Aucun fournisseur trouvé.");
            return;
        }

        // ✅ Récupérer l'objet fournisseur
        Fournisseur fournisseur = fournisseurDao.getFournisseurById(id);
        if (fournisseur != null) {
            fournisseurListe.clear();
            fournisseurListe.add(fournisseur);
            tableFournisseurs.setItems(fournisseurListe);
        } else {
            afficherAlerte(Alert.AlertType.INFORMATION, "Aucun résultat", "Aucun fournisseur trouvé.");
        }
    }

    private Dialog<Fournisseur> creerFournisseurDialog(Fournisseur fournisseur) {
        Dialog<Fournisseur> dialog = new Dialog<>();
        dialog.setTitle(fournisseur == null ? "Ajouter un fournisseur" : "Modifier un fournisseur");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Créer les champs du formulaire
        TextField nomField = new TextField();
        TextField adresseField = new TextField();
        TextField contactField = new TextField();
        TextField emailField = new TextField();

        // Pré-remplir les champs si on modifie un fournisseur existant
        if (fournisseur != null) {
            nomField.setText(fournisseur.getNom());
            adresseField.setText(fournisseur.getAdresse());
            contactField.setText(fournisseur.getContact());
            emailField.setText(fournisseur.getEmail());
        }

        // Créer la grille pour le formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(adresseField, 1, 1);
        grid.add(new Label("Contact:"), 0, 2);
        grid.add(contactField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convertir le résultat en objet Fournisseur
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String nom = nomField.getText();
                String adresse = adresseField.getText();
                String contact = contactField.getText();
                String email = emailField.getText();

                if (fournisseur == null) {
                    // Pour un nouveau fournisseur, utiliser le constructeur avec id=0
                    return new Fournisseur(0, nom, adresse, contact, email);
                } else {
                    // Pour un fournisseur existant, mettre à jour les valeurs
                    fournisseur.setNom(nom);
                    fournisseur.setAdresse(adresse);
                    fournisseur.setContact(contact);
                    fournisseur.setEmail(email);
                    return fournisseur;
                }
            }
            return null;
        });

        return dialog;
    }
    private void afficherDetailsFournisseur(Fournisseur fournisseur) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails du fournisseur");
        dialog.setHeaderText(null);

        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        int row = 0;
        grid.add(new Label("ID:"), 0, row);
        grid.add(new Label(String.valueOf(fournisseur.getId())), 1, row++);
        grid.add(new Label("Nom:"), 0, row);
        grid.add(new Label(fournisseur.getNom()), 1, row++);
        grid.add(new Label("Adresse:"), 0, row);
        grid.add(new Label(fournisseur.getAdresse()), 1, row++);
        grid.add(new Label("Contact:"), 0, row);
        grid.add(new Label(fournisseur.getContact()), 1, row++);
        grid.add(new Label("Email:"), 0, row);
        grid.add(new Label(fournisseur.getEmail()), 1, row++);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}