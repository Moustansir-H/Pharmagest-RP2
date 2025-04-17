package mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.famille.Famille;
import mcci.businessschool.bts.sio.slam.pharmagest.famille.dao.FamilleDao;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MedicamentDao {
    private final FamilleDao familleDao;
    private final FournisseurDao fournisseurDao;
    private Connection baseDeDonneeConnexion;

    public MedicamentDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
        this.familleDao = new FamilleDao();
        this.fournisseurDao = new FournisseurDao();
    }

    // Récupère tous les médicaments depuis la base
    public List<Medicament> recupererMedicaments() {
        String sql = "SELECT id, nom, forme, prixachat, prixvente, stock, seuilcommande, qtemax, " +
                "famille_id, fournisseur_id FROM medicament";
        List<Medicament> medicaments = new ArrayList<>();

        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Medicament medicament = new Medicament(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("forme"),
                        rs.getDouble("prixachat"),
                        rs.getDouble("prixvente"),
                        rs.getInt("stock"),
                        rs.getInt("seuilcommande"),
                        rs.getInt("qtemax"),
                        familleDao.getFamilleById(rs.getInt("famille_id")),
                        fournisseurDao.getFournisseurById(rs.getInt("fournisseur_id"))
                );
                medicaments.add(medicament);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des médicaments : " + e.getMessage());
        }
        return medicaments;
    }

    // Récupère un médicament par son ID
    public Medicament recupererMedicamentParId(int id) {
        String sql = "SELECT id, nom, forme, prixachat, prixvente, stock, seuilcommande, qtemax, famille_id, " +
                "fournisseur_id FROM medicament WHERE id = ?";
        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int medId = rs.getInt("id");  // Récupérer l'ID réel
                String nom = rs.getString("nom");
                String forme = rs.getString("forme");
                double prixAchat = rs.getDouble("prixachat");
                double prixVente = rs.getDouble("prixvente");
                int stock = rs.getInt("stock");
                int seuilCommande = rs.getInt("seuilcommande");
                int qteMax = rs.getInt("qtemax");

                int familleId = rs.getInt("famille_id");
                int fournisseurId = rs.getInt("fournisseur_id");

                Famille famille = familleDao.getFamilleById(familleId);
                Fournisseur fournisseur = fournisseurDao.getFournisseurById(fournisseurId);

                // Utiliser le constructeur qui inclut l'ID
                return new Medicament(medId, nom, forme, prixAchat, prixVente, stock, seuilCommande, qteMax, famille, fournisseur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du médicament : " + e.getMessage());
        }
        return null;
    }


    public Medicament recupererMedicamentParNom(String nom) {
        String sql = "SELECT id, nom, forme, prixachat, prixvente, stock, seuilcommande, qtemax, " +
                "famille_id, fournisseur_id FROM medicament WHERE nom = ?";


        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String forme = rs.getString("forme");
                double prixAchat = rs.getDouble("prixachat");
                double prixVente = rs.getDouble("prixvente");
                int stock = rs.getInt("stock");
                int seuilCommande = rs.getInt("seuilcommande");
                int qteMax = rs.getInt("qtemax");

                int familleId = rs.getInt("famille_id");
                int fournisseurId = rs.getInt("fournisseur_id");

                Famille famille = familleDao.getFamilleById(familleId);
                Fournisseur fournisseur = fournisseurDao.getFournisseurById(fournisseurId);

                return new Medicament(id, nom, forme, prixAchat, prixVente, stock, seuilCommande, qteMax, famille, fournisseur);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du médicament par nom : " + e.getMessage());
        }

        return null;
    }

    public Medicament recupererMedicamentParNomEtForme(String nom, String forme) {
        String sql = """
                    SELECT id, nom, forme, prixachat, prixvente, stock, seuilcommande, qtemax, 
                           famille_id, fournisseur_id 
                    FROM medicament 
                    WHERE LOWER(nom) = LOWER(?) AND LOWER(forme) = LOWER(?)
                """;

        System.out.println("🔍 Recherche du médicament : nom = '" + nom + "', forme = '" + forme + "'");

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, forme);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String nomMedicament = rs.getString("nom");
                String formeMedicament = rs.getString("forme");

                double prixAchat = rs.getDouble("prixachat");
                double prixVente = rs.getDouble("prixvente");
                int stock = rs.getInt("stock");
                int seuilCommande = rs.getInt("seuilcommande");
                int qteMax = rs.getInt("qtemax");

                int familleId = rs.getInt("famille_id");
                int fournisseurId = rs.getInt("fournisseur_id");

                Famille famille = familleDao.getFamilleById(familleId);
                Fournisseur fournisseur = fournisseurDao.getFournisseurById(fournisseurId);

                Medicament medicament = new Medicament(id, nomMedicament, formeMedicament, prixAchat, prixVente, stock, seuilCommande, qteMax, famille, fournisseur);
                System.out.println("✅ Médicament trouvé : " + nomMedicament + " (" + formeMedicament + "), ID = " + id);
                return medicament;
            } else {
                System.out.println("❌ Aucun médicament trouvé avec ce nom et forme !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche du médicament : " + e.getMessage());
        }

        return null;
    }


    public List<Medicament> recupererMedicamentsSousSeuil() throws SQLException {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = """
                    SELECT m.id, m.nom, m.stock, m.qtemax, m.prixachat, 
                           f.id AS fournisseur_id, f.nom AS fournisseur_nom
                    FROM medicament m
                    JOIN fournisseur f ON m.fournisseur_id = f.id
                    WHERE m.stock <= m.seuilcommande
                """;

        try (Statement stmt = baseDeDonneeConnexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fournisseur fournisseur = new Fournisseur(rs.getInt("fournisseur_id"), rs.getString("fournisseur_nom"));
                Medicament medicament = new Medicament(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        "Inconnu", // Forme
                        rs.getDouble("prixachat"),
                        0.0, // Prix de vente (par défaut)
                        rs.getInt("stock"),
                        0, // Seuil commande (par défaut)
                        rs.getInt("qtemax"),
                        null, // Famille (on ne la récupère pas ici)
                        fournisseur
                );

                medicaments.add(medicament);
            }
        }
        return medicaments;
    }

    public List<LigneDeCommande> recupererMedicamentsSousSeuilParFournisseur(int fournisseurId) throws SQLException {
        List<LigneDeCommande> lignesDeCommande = new ArrayList<>();
        String sql = """
                    SELECT m.id, m.nom, m.stock, m.qtemax, m.prixachat, m.seuilcommande,
                           f.id AS fournisseur_id, f.nom AS fournisseur_nom
                    FROM medicament m
                    JOIN fournisseur f ON m.fournisseur_id = f.id
                    WHERE m.stock <= m.seuilcommande AND m.fournisseur_id = ?
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, fournisseurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Fournisseur fournisseur = new Fournisseur(rs.getInt("fournisseur_id"), rs.getString("fournisseur_nom"));
                    Medicament medicament = new Medicament(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            "Inconnu", // Forme
                            rs.getDouble("prixachat"),
                            0.0, // Prix de vente par défaut
                            rs.getInt("stock"),
                            rs.getInt("seuilcommande"),
                            rs.getInt("qtemax"),
                            null, // Famille (on ne la récupère pas ici)
                            fournisseur
                    );


                    // ✅ Calcul de la quantité à commander
                    int quantiteACommander = Math.max(0, medicament.getQteMax() - medicament.getStock());

                    if (quantiteACommander > 0) {
                        lignesDeCommande.add(new LigneDeCommande(null, medicament, quantiteACommander, medicament.getPrixAchat(), 0, 0.0, 0.0));
                    }
                }
            }
        }
        return lignesDeCommande;
    }


    public Integer ajouterMedicament(Medicament medicament) throws SQLException {
        System.out.println("📌 Avant insertion - ID Famille : " + medicament.getFamille().getId());
        System.out.println("📌 Avant insertion - ID Fournisseur : " + medicament.getFournisseur().getId());

        String insertSQL = """
                    INSERT INTO medicament 
                    (nom, forme, prixachat, prixvente, stock, seuilcommande, qtemax, famille_id, fournisseur_id) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) 
                    RETURNING id
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(insertSQL)) {
            stmt.setString(1, medicament.getNom());
            stmt.setString(2, medicament.getForme());
            stmt.setDouble(3, medicament.getPrixAchat());
            stmt.setDouble(4, medicament.getPrixVente());
            stmt.setInt(5, medicament.getStock());
            stmt.setInt(6, medicament.getSeuilCommande());
            stmt.setInt(7, medicament.getQteMax());
            stmt.setInt(8, medicament.getFamille().getId());
            stmt.setInt(9, medicament.getFournisseur().getId());

            System.out.println("🔎 Exécution requête SQL : " + stmt.toString());

            // Exécuter la requête et récupérer l'ID généré
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    System.out.println("✅ Médicament ajouté avec succès, ID généré : " + id);
                    return id;
                } else {
                    System.err.println("❌ Échec de l'ajout, aucun ID retourné !");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'insertion : " + e.getMessage());
            throw e;
        }
    }


    public void modifierMedicament(Medicament medicament) throws SQLException {
        String updateSQL = """
                    UPDATE medicament 
                    SET nom = ?, 
                        forme = ?, 
                        prixachat = ?, 
                        prixvente = ?, 
                        stock = ?, 
                        seuilcommande = ?, 
                        qtemax = ?, 
                        famille_id = ?, 
                        fournisseur_id = ? 
                    WHERE id = ?
                """;

        // Obtenir les IDs à partir des noms
        int familleId = familleDao.getFamilleIdByName(medicament.getFamille().getNom());
        int fournisseurId = fournisseurDao.getFournisseurIdByName(medicament.getFournisseur().getNom());

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(updateSQL)) {
            // Remplir les paramètres
            stmt.setString(1, medicament.getNom());
            stmt.setString(2, medicament.getForme());
            stmt.setDouble(3, medicament.getPrixAchat());
            stmt.setDouble(4, medicament.getPrixVente());
            stmt.setInt(5, medicament.getStock());
            stmt.setInt(6, medicament.getSeuilCommande());
            stmt.setInt(7, medicament.getQteMax());
            stmt.setInt(8, familleId);
            stmt.setInt(9, fournisseurId);
            stmt.setInt(10, medicament.getId()); // ID du médicament à modifier

            // 📌 Affichage des valeurs avant exécution
            System.out.println("\n🔎 Exécution requête UPDATE :");
            System.out.println("UPDATE medicament SET nom = '" + medicament.getNom() + "',");
            System.out.println("                        forme = '" + medicament.getForme() + "',");
            System.out.println("                        prixachat = " + medicament.getPrixAchat() + ",");
            System.out.println("                        prixvente = " + medicament.getPrixVente() + ",");
            System.out.println("                        stock = " + medicament.getStock() + ",");
            System.out.println("                        seuilcommande = " + medicament.getSeuilCommande() + ",");
            System.out.println("                        qtemax = " + medicament.getQteMax() + ",");
            System.out.println("                        famille_id = " + familleId + ",");
            System.out.println("                        fournisseur_id = " + fournisseurId + " WHERE id = " + medicament.getId());

            // Exécuter la requête
            int lignesModifiees = stmt.executeUpdate();

            // 📌 Vérification du nombre de lignes affectées
            if (lignesModifiees > 0) {
                System.out.println("✅ Modification appliquée, lignes affectées : " + lignesModifiees);
            } else {
                System.err.println("❌ Échec de la modification : Aucune ligne affectée !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification du médicament : " + e.getMessage());
            throw e;
        }
    }


    public void supprimerMedicamentParId(Integer id) {
        String deleteSQL = "DELETE FROM medicament WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);

            int ligneSupprimee = stmt.executeUpdate();

            if (ligneSupprimee > 0) {
                System.out.println("Médicament supprimé avec succès !");
            } else {
                System.out.println("Aucun médicament trouvé avec id = " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du médicament : " + e.getMessage());
        }
    }

    public int calculerQuantiteACommander(Medicament medicament) {
        return Math.max(0, medicament.getQteMax() - medicament.getStock()); // ✅ Toujours >= 0
    }

    public boolean mettreAJourStock(int medicamentId, int quantiteAjoutee) throws SQLException {
        String sql = "UPDATE medicament SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, quantiteAjoutee);
            stmt.setInt(2, medicamentId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("❌ Échec de la mise à jour du stock du médicament ID " + medicamentId);
            }

            System.out.println("✅ Stock mis à jour pour Médicament ID " + medicamentId + " (+ " + quantiteAjoutee + " unités)");
            return true; // Return true when successful
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la mise à jour du stock : " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        try {
            // Initialiser l'objet MedicamentDao
            MedicamentDao medicamentDao = new MedicamentDao();

            // Récupérer les médicaments sous le seuil
            List<Medicament> medicamentsSousSeuil = medicamentDao.recupererMedicamentsSousSeuil();

            // Afficher les résultats
            System.out.println("\n📢 Médicaments sous ou égaux au seuil de commande :");
            if (medicamentsSousSeuil.isEmpty()) {
                System.out.println("⚠️ Aucun médicament en dessous du seuil !");
            } else {
                for (Medicament med : medicamentsSousSeuil) {
                    System.out.println("🔹 " + med.getNom() +
                            " | Stock : " + med.getStock() +
                            " | Seuil : " + med.getSeuilCommande() +
                            " | Fournisseur : " + (med.getFournisseur() != null ? med.getFournisseur().getNom() : "Aucun"));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test des médicaments sous seuil : " + e.getMessage());
        }
    }


}