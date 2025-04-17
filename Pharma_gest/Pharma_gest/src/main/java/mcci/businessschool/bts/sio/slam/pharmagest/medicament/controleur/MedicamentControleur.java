package mcci.businessschool.bts.sio.slam.pharmagest.medicament.controleur;

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
import mcci.businessschool.bts.sio.slam.pharmagest.famille.Famille;
import mcci.businessschool.bts.sio.slam.pharmagest.famille.dao.FamilleDao;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.service.MedicamentService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MedicamentControleur {

    @FXML
    private Button ajouterBtn, modifierBtn, supprimerBtn, retourMedicament;
    @FXML
    private TableView<Medicament> tableMedicament;
    @FXML
    private TableColumn<Medicament, Integer> colId;
    @FXML
    private TableColumn<Medicament, String> colNom, colForme, colFamille, colFournisseur;
    @FXML
    private TableColumn<Medicament, Double> colPrixAchat, colPrixVente;
    @FXML
    private TableColumn<Medicament, Integer> colStock, colSeuil, colQteMax;

    private ObservableList<Medicament> donneesMedicament = FXCollections.observableArrayList();
    private ObservableList<String> listeFormes = FXCollections.observableArrayList("Comprimé", "Gélule", "Sirop",
            "Injectable", "Poudre", "Granulé", "Suppositoire",
            "Solution buvable", "Gouttes", "Crème", "Pommade", "Gel", "Aérosol", "Inhalateur",
            "Patch transdermique");
    private MedicamentService medicamentService;
    private FamilleDao familleDao;
    private FournisseurDao fournisseurDao;

    public MedicamentControleur() throws Exception {
        this.medicamentService = new MedicamentService();
        this.familleDao = new FamilleDao();
        this.fournisseurDao = new FournisseurDao();
    }

    @FXML
    public void initialize() {
        // Initialisation des colonnes du TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colForme.setCellValueFactory(new PropertyValueFactory<>("forme"));
        colPrixAchat.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
        colPrixVente.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colSeuil.setCellValueFactory(new PropertyValueFactory<>("seuilCommande"));
        colQteMax.setCellValueFactory(new PropertyValueFactory<>("qteMax"));
        colFamille.setCellValueFactory(new PropertyValueFactory<>("familleNom"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("fournisseurNom"));

        // Chargement initial des médicaments
        loadMedicaments();

        // Configuration des boutons
        modifierBtn.setDisable(true);
        supprimerBtn.setDisable(true);

        // Ajouter un listener pour activer/désactiver les boutons selon la sélection
        tableMedicament.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean itemSelected = newSelection != null;
            modifierBtn.setDisable(!itemSelected);
            supprimerBtn.setDisable(!itemSelected);
        });

        // Double-clic pour afficher les détails
        tableMedicament.setRowFactory(tv -> {
            TableRow<Medicament> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    afficherDetailsMedicament(row.getItem());
                }
            });
            return row;
        });
    }

    private void loadMedicaments() {
        donneesMedicament.clear();
        List<Medicament> medicaments = medicamentService.recupererMedicaments();
        donneesMedicament.addAll(medicaments);
        tableMedicament.setItems(donneesMedicament);
    }

    private void chargerFamilles(ComboBox<String> familleCombo) {
        try {
            List<Famille> familles = familleDao.recupererToutesLesFamilles();
            ObservableList<String> nomsFamilles = FXCollections.observableArrayList();
            for (Famille f : familles) {
                nomsFamilles.add(f.getNom());
            }
            familleCombo.setItems(nomsFamilles);
        } catch (Exception e) {
            afficherErreur("Erreur chargement des familles : " + e.getMessage());
        }
    }

    private void chargerFournisseurs(ComboBox<String> fournisseurCombo) {
        try {
            List<Fournisseur> fournisseurs = fournisseurDao.recupererTousLesFournisseurs();
            ObservableList<String> nomsFournisseurs = FXCollections.observableArrayList();
            for (Fournisseur f : fournisseurs) {
                nomsFournisseurs.add(f.getNom());
            }
            fournisseurCombo.setItems(nomsFournisseurs);
        } catch (Exception e) {
            afficherErreur("Erreur chargement des fournisseurs : " + e.getMessage());
        }
    }

    @FXML
    private void afficherFormAjout() {
        Dialog<Medicament> dialog = creerDialogueMedicament(null);
        Optional<Medicament> result = dialog.showAndWait();

        result.ifPresent(medicament -> {
            try {
                Integer idGenere = medicamentService.ajouterMedicament(medicament);
                if (idGenere == null) {
                    afficherErreur("❌ L'ajout du médicament a échoué !");
                } else {
                    afficherMessage("✅ Succès", "Médicament ajouté avec succès !");
                    loadMedicaments();
                }
            } catch (SQLException e) {
                afficherErreur("❌ Erreur SQL : " + e.getMessage());
            } catch (Exception e) {
                afficherErreur("❌ Erreur inattendue : " + e.getMessage());
            }
        });
    }

    @FXML
    private void afficherFormModification() {
        Medicament medicamentSelectionne = tableMedicament.getSelectionModel().getSelectedItem();
        if (medicamentSelectionne == null) {
            afficherErreur("Veuillez sélectionner un médicament à modifier.");
            return;
        }

        Dialog<Medicament> dialog = creerDialogueMedicament(medicamentSelectionne);
        Optional<Medicament> result = dialog.showAndWait();

        result.ifPresent(medicament -> {
            try {
                medicamentService.modifierMedicament(medicament);
                afficherMessage("Succès", "Médicament modifié !");
                loadMedicaments();
            } catch (Exception e) {
                afficherErreur("Erreur lors de la modification : " + e.getMessage());
            }
        });
    }

    private Dialog<Medicament> creerDialogueMedicament(Medicament medicament) {
        Dialog<Medicament> dialog = new Dialog<>();
        dialog.setTitle(medicament == null ? "Ajouter un médicament" : "Modifier un médicament");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Création des champs du formulaire
        TextField nomField = new TextField();
        ComboBox<String> formeCombo = new ComboBox<>(listeFormes);
        TextField prixAchatField = new TextField();
        TextField prixVenteField = new TextField();
        TextField stockField = new TextField();
        TextField seuilCommandeField = new TextField();
        TextField qteMaxField = new TextField();
        ComboBox<String> familleCombo = new ComboBox<>();
        ComboBox<String> fournisseurCombo = new ComboBox<>();

        // Chargement des données dans les ComboBox
        chargerFamilles(familleCombo);
        chargerFournisseurs(fournisseurCombo);

        // Remplir les champs si on modifie un médicament existant
        if (medicament != null) {
            nomField.setText(medicament.getNom());
            formeCombo.setValue(medicament.getForme());
            prixAchatField.setText(String.valueOf(medicament.getPrixAchat()));
            prixVenteField.setText(String.valueOf(medicament.getPrixVente()));
            stockField.setText(String.valueOf(medicament.getStock()));
            seuilCommandeField.setText(String.valueOf(medicament.getSeuilCommande()));
            qteMaxField.setText(String.valueOf(medicament.getQteMax()));
            familleCombo.setValue(medicament.getFamille().getNom());
            fournisseurCombo.setValue(medicament.getFournisseur().getNom());
        }

        // Création de la grille pour le formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int row = 0;
        grid.add(new Label("Nom:"), 0, row);
        grid.add(nomField, 1, row++);
        grid.add(new Label("Forme:"), 0, row);
        grid.add(formeCombo, 1, row++);
        grid.add(new Label("Prix d'achat:"), 0, row);
        grid.add(prixAchatField, 1, row++);
        grid.add(new Label("Prix de vente:"), 0, row);
        grid.add(prixVenteField, 1, row++);
        grid.add(new Label("Stock:"), 0, row);
        grid.add(stockField, 1, row++);
        grid.add(new Label("Seuil de commande:"), 0, row);
        grid.add(seuilCommandeField, 1, row++);
        grid.add(new Label("Quantité maximale:"), 0, row);
        grid.add(qteMaxField, 1, row++);
        grid.add(new Label("Famille:"), 0, row);
        grid.add(familleCombo, 1, row++);
        grid.add(new Label("Fournisseur:"), 0, row);
        grid.add(fournisseurCombo, 1, row++);

        dialog.getDialogPane().setContent(grid);

        // Conversion du résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validation des champs
                    if (nomField.getText().isEmpty() || formeCombo.getValue() == null ||
                            prixAchatField.getText().isEmpty() || prixVenteField.getText().isEmpty() ||
                            stockField.getText().isEmpty() || seuilCommandeField.getText().isEmpty() ||
                            qteMaxField.getText().isEmpty() || familleCombo.getValue() == null ||
                            fournisseurCombo.getValue() == null) {
                        afficherErreur("Tous les champs doivent être remplis !");
                        return null;
                    }

                    // Récupération des valeurs
                    String nom = nomField.getText();
                    String forme = formeCombo.getValue();
                    double prixAchat = Double.parseDouble(prixAchatField.getText());
                    double prixVente = Double.parseDouble(prixVenteField.getText());
                    int stock = Integer.parseInt(stockField.getText());
                    int seuilCommande = Integer.parseInt(seuilCommandeField.getText());
                    int qteMax = Integer.parseInt(qteMaxField.getText());

                    // Récupération des IDs
                    Integer familleId = familleDao.getFamilleIdByName(familleCombo.getValue());
                    Integer fournisseurId = fournisseurDao.getFournisseurIdByName(fournisseurCombo.getValue());

                    if (familleId == null || fournisseurId == null) {
                        afficherErreur("❌ Erreur : Famille ou fournisseur non trouvé !");
                        return null;
                    }

                    // Récupération des objets
                    Famille famille = familleDao.getFamilleById(familleId);
                    Fournisseur fournisseur = fournisseurDao.getFournisseurById(fournisseurId);

                    if (famille == null || fournisseur == null) {
                        afficherErreur("❌ Erreur : Impossible de récupérer la famille ou le fournisseur !");
                        return null;
                    }

                    // Création ou mise à jour du médicament
                    Medicament result;
                    if (medicament == null) {
                        // Utiliser le constructeur avec tous les paramètres requis
                        result = new Medicament(
                                nom,
                                forme,
                                prixAchat,
                                prixVente,
                                stock,
                                seuilCommande,
                                qteMax,
                                famille,
                                fournisseur
                        );
                    } else {
                        // Si on modifie un médicament existant, on utilise l'objet existant
                        result = medicament;
                        result.setNom(nom);
                        result.setForme(forme);
                        result.setPrixAchat(prixAchat);
                        result.setPrixVente(prixVente);
                        result.setStock(stock);
                        result.setSeuilCommande(seuilCommande);
                        result.setQteMax(qteMax);
                        result.setFamille(famille);
                        result.setFournisseur(fournisseur);
                    }

                    return result;
                } catch (NumberFormatException e) {
                    afficherErreur("❌ Erreur de format : Vérifiez les champs numériques !");
                    return null;
                } catch (Exception e) {
                    afficherErreur("❌ Erreur : " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    @FXML
    private void supprimerMedicament() {
        Medicament medicamentSelectionne = tableMedicament.getSelectionModel().getSelectedItem();
        if (medicamentSelectionne == null) {
            afficherErreur("Veuillez sélectionner un médicament à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce médicament ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                medicamentService.supprimerMedicamentParId(medicamentSelectionne.getId());
                afficherMessage("Succès", "Médicament supprimé !");
                loadMedicaments();
            } catch (Exception e) {
                afficherErreur("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    private void afficherDetailsMedicament(Medicament medicament) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails du médicament");
        dialog.setHeaderText(null);

        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        int row = 0;
        addDetailRow(grid, row++, "ID:", String.valueOf(medicament.getId()));
        addDetailRow(grid, row++, "Nom:", medicament.getNom());
        addDetailRow(grid, row++, "Forme:", medicament.getForme());
        addDetailRow(grid, row++, "Prix d'achat:", String.valueOf(medicament.getPrixAchat()));
        addDetailRow(grid, row++, "Prix de vente:", String.valueOf(medicament.getPrixVente()));
        addDetailRow(grid, row++, "Stock:", String.valueOf(medicament.getStock()));
        addDetailRow(grid, row++, "Seuil de commande:", String.valueOf(medicament.getSeuilCommande()));
        addDetailRow(grid, row++, "Quantité maximale:", String.valueOf(medicament.getQteMax()));
        addDetailRow(grid, row++, "Famille:", medicament.getFamille().getNom());
        addDetailRow(grid, row++, "Fournisseur:", medicament.getFournisseur().getNom());

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setPrefWidth(450);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.showAndWait();
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        Label valueNode = new Label(value);

        labelNode.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        valueNode.setStyle("-fx-font-size: 14px;");
        valueNode.setWrapText(true);

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherMessage(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void retourMedicamentOnAction(ActionEvent e) {
        try {
            System.out.println("🔄 Bouton Retour cliqué !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/maintenance/Maintenance.fxml"));
            Scene nouvelleScene = new Scene(loader.load());

            // Vérification du Stage
            Stage stage = (Stage) retourMedicament.getScene().getWindow();
            System.out.println("✅ Chargement réussi, affichage de Maintenance.fxml");

            // Afficher la nouvelle scène
            stage.setScene(nouvelleScene);
        } catch (IOException ex) {
            System.err.println("❌ Erreur lors du retour à Maintenance : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
