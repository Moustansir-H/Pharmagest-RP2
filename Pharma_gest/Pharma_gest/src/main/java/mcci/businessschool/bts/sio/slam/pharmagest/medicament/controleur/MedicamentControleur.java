package mcci.businessschool.bts.sio.slam.pharmagest.medicament.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class MedicamentControleur {

    @FXML
    private TextField nomField, prixAchatField, prixVenteField, stockField, seuilCommandeField, qteMaxField;
    @FXML
    private ComboBox<String> formeCombo, familleCombo, fournisseurCombo;
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

        // Charger les valeurs des ComboBox
        formeCombo.setItems(listeFormes);
        chargerFamilles();
        chargerFournisseurs();

        tableMedicament.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) remplirFormulaire(newSelection);
        });
    }

    private void loadMedicaments() {
        donneesMedicament.clear();
        List<Medicament> medicaments = medicamentService.recupererMedicaments();
        donneesMedicament.addAll(medicaments);
        tableMedicament.setItems(donneesMedicament);
    }

    private void chargerFamilles() {
        try {
            List<Famille> familles = familleDao.recupererToutesLesFamilles(); // ✅ Utilisation de la nouvelle méthode
            ObservableList<String> nomsFamilles = FXCollections.observableArrayList();
            for (Famille f : familles) {
                nomsFamilles.add(f.getNom());
            }
            familleCombo.setItems(nomsFamilles);
        } catch (Exception e) {
            afficherErreur("Erreur chargement des familles : " + e.getMessage());
        }
    }


    private void chargerFournisseurs() {
        try {
            List<Fournisseur> fournisseurs = fournisseurDao.recupererTousLesFournisseurs(); // ✅ Adapter à ton DAO
            ObservableList<String> nomsFournisseurs = FXCollections.observableArrayList();
            for (Fournisseur f : fournisseurs) {
                nomsFournisseurs.add(f.getNom());
            }
            fournisseurCombo.setItems(nomsFournisseurs);
        } catch (Exception e) {
            afficherErreur("Erreur chargement des fournisseurs : " + e.getMessage());
        }
    }

    private void remplirFormulaire(Medicament medicament) {
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

    @FXML
    private void ajouterMedicament() {
        try {
            if (nomField.getText().isEmpty() || formeCombo.getValue() == null ||
                    prixAchatField.getText().isEmpty() || prixVenteField.getText().isEmpty() ||
                    stockField.getText().isEmpty() || seuilCommandeField.getText().isEmpty() ||
                    qteMaxField.getText().isEmpty() || familleCombo.getValue() == null ||
                    fournisseurCombo.getValue() == null) {
                afficherErreur("Tous les champs doivent être remplis !");
                return;
            }

            String nom = nomField.getText();
            String forme = formeCombo.getValue();
            double prixAchat = Double.parseDouble(prixAchatField.getText());
            double prixVente = Double.parseDouble(prixVenteField.getText());
            int stock = Integer.parseInt(stockField.getText());
            int seuilCommande = Integer.parseInt(seuilCommandeField.getText());
            int qteMax = Integer.parseInt(qteMaxField.getText());

            System.out.println("🔎 Famille sélectionnée : " + familleCombo.getValue());
            System.out.println("🔎 Fournisseur sélectionné : " + fournisseurCombo.getValue());

            // 🔍 Récupération des IDs
            Integer familleId = familleDao.getFamilleIdByName(familleCombo.getValue());
            if (familleId == null) {
                afficherErreur("❌ Erreur : la famille sélectionnée n'existe pas en base !");
                return;
            }

            Integer fournisseurId = fournisseurDao.getFournisseurIdByName(fournisseurCombo.getValue());
            if (fournisseurId == null) {
                afficherErreur("❌ Erreur : le fournisseur sélectionné n'existe pas en base !");
                return;
            }

            System.out.println("📌 ID Famille récupéré : " + familleId);
            System.out.println("📌 ID Fournisseur récupéré : " + fournisseurId);

            // ✅ Récupération des objets
            Famille famille = familleDao.getFamilleById(familleId);
            Fournisseur fournisseur = fournisseurDao.getFournisseurById(fournisseurId);

            // Vérification après récupération
            System.out.println("📌 Objet Famille récupéré : " + (famille != null ? famille.getNom() : "null"));
            System.out.println("📌 Objet Fournisseur récupéré : " + (fournisseur != null ? fournisseur.getNom() : "null"));

            if (famille == null) {
                afficherErreur("❌ Erreur : Impossible de récupérer la famille sélectionnée !");
                return;
            }

            if (fournisseur == null) {
                afficherErreur("❌ Erreur : Impossible de récupérer le fournisseur sélectionné !");
                return;
            }

            // 🔥 Création du médicament
            Medicament medicament = new Medicament(nom, forme, prixAchat, prixVente, stock, seuilCommande, qteMax, famille, fournisseur);

            // ✅ Vérification avant insertion en base
            System.out.println("🆕 Ajout d'un médicament : " + medicament.getNom());
            System.out.println("📌 Forme : " + medicament.getForme());
            System.out.println("📌 Prix Achat : " + medicament.getPrixAchat());
            System.out.println("📌 Prix Vente : " + medicament.getPrixVente());
            System.out.println("📌 Stock : " + medicament.getStock());
            System.out.println("📌 Seuil : " + medicament.getSeuilCommande());
            System.out.println("📌 Quantité max : " + medicament.getQteMax());
            System.out.println("📌 Famille : " + (medicament.getFamille() != null ? medicament.getFamille().getNom() : "null"));
            System.out.println("📌 Fournisseur : " + (medicament.getFournisseur() != null ? medicament.getFournisseur().getNom() : "null"));

            // Vérifie avant d’insérer en base
            if (medicament.getFamille() == null || medicament.getFournisseur() == null) {
                afficherErreur("❌ Erreur : Famille ou Fournisseur non récupérés correctement !");
                return;
            }

            // ✅ Ajout en base de données
            Integer idGenere = medicamentService.ajouterMedicament(medicament);
            System.out.println("✅ ID généré après insertion : " + idGenere);

            if (idGenere == null) {
                afficherErreur("❌ L'ajout du médicament a échoué !");
            } else {
                afficherMessage("✅ Succès", "Médicament ajouté avec succès !");
                loadMedicaments();
            }

        } catch (NumberFormatException e) {
            afficherErreur("❌ Erreur de format : Vérifiez les champs numériques !");
        } catch (SQLException e) {
            afficherErreur("❌ Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            afficherErreur("❌ Erreur inattendue : " + e.getMessage());
        }
    }


    @FXML
    private void modifierMedicament() {
        Medicament medicamentSelectionne = tableMedicament.getSelectionModel().getSelectedItem();
        if (medicamentSelectionne == null) {
            afficherErreur("Veuillez sélectionner un médicament.");
            return;
        }

        try {
            // 📌 Log avant modification
            System.out.println("🔍 Médicament sélectionné pour modification : " + medicamentSelectionne.getNom() + " (ID: " + medicamentSelectionne.getId() + ")");

            // Récupération des nouvelles valeurs du formulaire
            String nouveauNom = nomField.getText();
            String nouvelleForme = formeCombo.getValue();
            double nouveauPrixAchat = Double.parseDouble(prixAchatField.getText());
            double nouveauPrixVente = Double.parseDouble(prixVenteField.getText());
            int nouveauStock = Integer.parseInt(stockField.getText());
            int nouveauSeuilCommande = Integer.parseInt(seuilCommandeField.getText());
            int nouvelleQteMax = Integer.parseInt(qteMaxField.getText());

            System.out.println("🆕 Modification - Nouveau Nom : " + nouveauNom);
            System.out.println("🆕 Modification - Nouvelle Forme : " + nouvelleForme);
            System.out.println("🆕 Modification - Nouveau Prix Achat : " + nouveauPrixAchat);
            System.out.println("🆕 Modification - Nouveau Prix Vente : " + nouveauPrixVente);
            System.out.println("🆕 Modification - Nouveau Stock : " + nouveauStock);
            System.out.println("🆕 Modification - Nouveau Seuil Commande : " + nouveauSeuilCommande);
            System.out.println("🆕 Modification - Nouvelle Quantité Max : " + nouvelleQteMax);

            // 🔍 Vérification et récupération des IDs des nouvelles valeurs
            Integer familleId = familleDao.getFamilleIdByName(familleCombo.getValue());
            Integer fournisseurId = fournisseurDao.getFournisseurIdByName(fournisseurCombo.getValue());

            if (familleId == null || fournisseurId == null) {
                afficherErreur("❌ Erreur : Famille ou fournisseur non trouvé !");
                return;
            }

            System.out.println("📌 ID Famille récupéré : " + familleId);
            System.out.println("📌 ID Fournisseur récupéré : " + fournisseurId);

            // Récupération des objets Famille et Fournisseur
            Famille nouvelleFamille = familleDao.getFamilleById(familleId);
            Fournisseur nouveauFournisseur = fournisseurDao.getFournisseurById(fournisseurId);

            if (nouvelleFamille == null || nouveauFournisseur == null) {
                afficherErreur("❌ Erreur : Impossible de récupérer la famille ou le fournisseur sélectionné !");
                return;
            }

            System.out.println("📌 Objet Famille récupéré : " + nouvelleFamille.getNom());
            System.out.println("📌 Objet Fournisseur récupéré : " + nouveauFournisseur.getNom());

            // Mise à jour des valeurs du médicament sélectionné
            medicamentSelectionne.setNom(nouveauNom);
            medicamentSelectionne.setForme(nouvelleForme);
            medicamentSelectionne.setPrixAchat(nouveauPrixAchat);
            medicamentSelectionne.setPrixVente(nouveauPrixVente);
            medicamentSelectionne.setStock(nouveauStock);
            medicamentSelectionne.setSeuilCommande(nouveauSeuilCommande);
            medicamentSelectionne.setQteMax(nouvelleQteMax);
            medicamentSelectionne.setFamille(nouvelleFamille);
            medicamentSelectionne.setFournisseur(nouveauFournisseur);

            // 🔥 Appel de la modification en base de données
            medicamentService.modifierMedicament(medicamentSelectionne);

            System.out.println("✅ Médicament modifié avec succès !");

            afficherMessage("Succès", "Médicament modifié !");
            loadMedicaments(); // Recharger les données pour voir le changement

        } catch (NumberFormatException e) {
            afficherErreur("❌ Erreur de format : Vérifiez les champs numériques !");
        } catch (Exception e) {
            afficherErreur("❌ Erreur inattendue : " + e.getMessage());
        }
    }


    @FXML
    private void supprimerMedicament() {
        Medicament medicamentSelectionne = tableMedicament.getSelectionModel().getSelectedItem();
        if (medicamentSelectionne == null) {
            afficherErreur("Veuillez sélectionner un médicament.");
            return;
        }

        try {
            medicamentService.supprimerMedicamentParId(medicamentSelectionne.getId());
            afficherMessage("Succès", "Médicament supprimé !");
            loadMedicaments();
        } catch (Exception e) {
            afficherErreur("Erreur lors de la suppression : " + e.getMessage());
        }
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
