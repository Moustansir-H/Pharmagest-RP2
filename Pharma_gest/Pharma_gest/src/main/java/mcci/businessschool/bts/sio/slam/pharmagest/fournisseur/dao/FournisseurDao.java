package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDao {

    private Connection baseDeDonneeConnexion;

    public FournisseurDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
    }

    /**
     * ✅ Récupérer tous les fournisseurs avec leur email
     */
    public List<Fournisseur> recupererTousLesFournisseurs() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT id, nom, adresse, contact, email FROM fournisseur"; // 🔥 Ajout de l'email

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Fournisseur fournisseur = new Fournisseur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("contact"),
                        rs.getString("email") // 🔥 Ajout de l'email
                );
                fournisseurs.add(fournisseur);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des fournisseurs : " + e.getMessage());
        }

        return fournisseurs;
    }

    /**
     * ✅ Récupérer un fournisseur par son ID
     */
    public Fournisseur getFournisseurById(int id) {
        String sql = "SELECT nom, adresse, contact, email FROM fournisseur WHERE id = ?"; // 🔥 Ajout de l'email
        Fournisseur fournisseur = null;

        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                fournisseur = new Fournisseur(
                        id,
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("contact"),
                        rs.getString("email") // 🔥 Ajout de l'email
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du fournisseur : " + e.getMessage());
        }

        return fournisseur;
    }

    /**
     * ✅ Récupérer l'ID d'un fournisseur par son nom
     */
    public int getFournisseurIdByName(String nom) throws SQLException {
        String query = "SELECT id FROM fournisseur WHERE nom = ?";
        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(query)) {
            pstmt.setString(1, nom);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Aucun fournisseur trouvé avec le nom : " + nom);
    }

    /**
     * ✅ Ajouter un fournisseur avec email
     */
    public void ajouterFournisseur(Fournisseur fournisseur) throws SQLException {
        String insertSql = "INSERT INTO fournisseur (nom, adresse, contact, email) VALUES (?, ?, ?, ?)"; // 🔥 Ajout de l'email
        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(insertSql)) {
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getAdresse());
            pstmt.setString(3, fournisseur.getContact());
            pstmt.setString(4, fournisseur.getEmail()); // 🔥 Ajout de l'email
            pstmt.executeUpdate();
        }
    }

    /**
     * ✅ Modifier un fournisseur avec email
     */
    public void modifierFournisseur(Fournisseur fournisseur) throws SQLException {
        String updateSql = "UPDATE fournisseur SET nom = ?, adresse = ?, contact = ?, email = ? WHERE id = ?"; // 🔥 Ajout de l'email

        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(updateSql)) {
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getAdresse());
            pstmt.setString(3, fournisseur.getContact());
            pstmt.setString(4, fournisseur.getEmail()); // 🔥 Ajout de l'email
            pstmt.setInt(5, fournisseur.getId());

            int lignesAffectees = pstmt.executeUpdate();
            if (lignesAffectees == 0) {
                throw new SQLException("Aucun fournisseur modifié, vérifiez l'ID.");
            }
        }
    }

    /**
     * ✅ Supprimer un fournisseur
     */
    public void supprimerFournisseur(int id) throws SQLException {
        String deleteSql = "DELETE FROM fournisseur WHERE id = ?";
        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(deleteSql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Aucun fournisseur supprimé. Vérifiez l'ID fourni.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du fournisseur : " + e.getMessage());
            throw e;
        }
    }

    /*

     * ✅ Tester l'ajout d'un fournisseur (Optionnel)

    public static void main(String[] args) {
        try {
            FournisseurDao fournisseurDao = new FournisseurDao();

            Fournisseur nouveauFournisseur = new Fournisseur(
                    "PharmaPlus", "123 Rue Principale", "0123456789", "contact@pharmaplus.com" // 🔥 Ajout de l'email
            );

            fournisseurDao.ajouterFournisseur(nouveauFournisseur);
            System.out.println("✅ Fournisseur ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'ajout du fournisseur : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Une erreur inattendue est survenue : " + e.getMessage());
        }
    }*/
}
