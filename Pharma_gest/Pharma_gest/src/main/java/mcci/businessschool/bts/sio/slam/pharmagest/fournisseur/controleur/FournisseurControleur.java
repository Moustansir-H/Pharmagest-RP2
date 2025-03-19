package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FournisseurControleur {
    @FXML
    private Button retourDashboard;

    @FXML
    public void retourDashboardOnAction(ActionEvent e) throws IOException {
        // Nouvelle scène
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/Dashboard.fxml"));
        Scene nouvelleScene = new Scene(loader.load());
        // La référence de la scène actuelle
        Stage stage = (Stage) retourDashboard.getScene().getWindow();
        // Afficher la nouvelle scène
        stage.setScene(nouvelleScene);
    }

    @FXML
    private TextField nomField, adresseField, contactField, emailField, rechercheField;

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
    public void initialize() {
        colonneId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colonneNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colonneAdresse.setCellValueFactory(cellData -> cellData.getValue().adresseProperty());
        colonneContact.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        colonneEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        afficherTousFournisseurs();

        // Écouteur sur la table pour détecter la sélection d'une ligne
        tableFournisseurs.getSelectionModel().selectedItemProperty().addListener((obs, ancienFournisseur, nouveauFournisseur) -> {
            if (nouveauFournisseur != null) {
                remplirFormulaire(nouveauFournisseur);
            }
        });
    }

    private void remplirFormulaire(Fournisseur fournisseur) {
        nomField.setText(fournisseur.getNom());
        adresseField.setText(fournisseur.getAdresse());
        contactField.setText(fournisseur.getContact());
        emailField.setText(fournisseur.getEmail());
    }

    @FXML
    private void ajouterFournisseur() {
        String nom = nomField.getText();
        String adresse = adresseField.getText();
        String contact = contactField.getText();
        String email = emailField.getText();

        if (nom.isEmpty() || adresse.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        Fournisseur fournisseur = new Fournisseur(0, nom, adresse, contact, email);
        try {
            fournisseurDao.ajouterFournisseur(fournisseur);
            afficherTousFournisseurs();
            effacerChamps();
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter le fournisseur.");
        }
    }

    @FXML
    private void modifierFournisseur() {
        Fournisseur selectionne = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un fournisseur.");
            return;
        }

        selectionne.setNom(nomField.getText());
        selectionne.setAdresse(adresseField.getText());
        selectionne.setContact(contactField.getText());
        selectionne.setEmail(emailField.getText());

        try {
            fournisseurDao.modifierFournisseur(selectionne);
            afficherTousFournisseurs();
            effacerChamps();
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de modifier le fournisseur.");
        }
    }

    @FXML
    private void supprimerFournisseur() {
        Fournisseur selectionne = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un fournisseur.");
            return;
        }

        try {
            fournisseurDao.supprimerFournisseur(selectionne.getId());
            afficherTousFournisseurs();
            effacerChamps();
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de supprimer le fournisseur.");
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


    private void effacerChamps() {
        nomField.clear();
        adresseField.clear();
        contactField.clear();
        emailField.clear();
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}

