package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FournisseurDAO {

    private Connection baseDeDonneeConnexion;

    public FournisseurDAO() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
    }

    // Méthode pour récupérer un fournisseur par son id
    public Fournisseur getFournisseurById(int id) {
        String selectFournisseurSql = "SELECT nom, adresse, contact FROM fournisseur WHERE id = ?";
        Fournisseur fournisseur = null;

        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(selectFournisseurSql)) {
            pstmt.setInt(1, id); // Injecte l'id dans la requête SQL
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nom = rs.getString("nom");
                String adresse = rs.getString("adresse");
                String contact = rs.getString("contact");
                fournisseur = new Fournisseur(nom, adresse, contact); // Crée une instance de Fournisseur
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du fournisseur : " + e.getMessage());
        }

        return fournisseur;
    }

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

    // Méthode pour ajouter un fournisseur
    public void ajouterFournisseur(Fournisseur fournisseur) throws SQLException {
        String insertSql = "INSERT INTO fournisseur (nom, adresse, contact) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(insertSql)) {
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getAdresse());
            pstmt.setString(3, fournisseur.getContact());
            pstmt.executeUpdate(); // Exécute la requête d'insertion
        }
        // Pas de bloc catch ici, l'exception sera propagée
    }


    public void modifierFournisseur(Fournisseur fournisseur) throws SQLException {
        String updateFournisseurSql = "UPDATE fournisseur SET nom = ?, adresse = ?, contact = ? WHERE id = ?";

        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(updateFournisseurSql)) {
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getAdresse());
            pstmt.setString(3, fournisseur.getContact());
            pstmt.setInt(4, fournisseur.getId());

            int lignesAffectees = pstmt.executeUpdate();

            // Vérifiez si aucune ligne n'est affectée (optionnel)
            if (lignesAffectees == 0) {
                throw new SQLException("Aucun fournisseur modifié, vérifiez l'ID.");
            }
        }
    }


    public void supprimerFournisseur(int id) throws SQLException {
        String deleteFournisseurSql = "DELETE FROM fournisseur WHERE id = ?";

        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(deleteFournisseurSql)) {
            pstmt.setInt(1, id); // Injecte l'ID du fournisseur à supprimer
            int rowsAffected = pstmt.executeUpdate(); // Exécute la requête de suppression

            if (rowsAffected == 0) {
                throw new SQLException("Aucun fournisseur supprimé. Vérifiez l'ID fourni.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du fournisseur : " + e.getMessage());
            throw e; // Propagation de l'exception pour une gestion en amont
        }
    }



/*
    public static void main(String[] args) {
        try {
            // Crée une instance de FournisseurDao
            FournisseurDao fournisseurDao = new FournisseurDao();

            // Crée un nouvel objet Fournisseur
            Fournisseur nouveauFournisseur = new Fournisseur("PharmaPlus", "123 Rue Principale", "0123456789");

            // Appelle la méthode pour ajouter le fournisseur
            fournisseurDao.ajouterFournisseur(nouveauFournisseur);

            // Affiche un message si tout s'est bien passé
            System.out.println("Fournisseur ajouté avec succès !");
        } catch (SQLException e) {
            // Gère l'exception SQL (par exemple, problème de base de données)
            System.err.println("Erreur SQL lors de l'ajout du fournisseur : " + e.getMessage());
        } catch (Exception e) {
            // Gère toute autre exception inattendue
            System.err.println("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }

*/

/*
    public static void main(String[] args) {
        try {
            // Crée une instance de FournisseurDao
            FournisseurDao fournisseurDao = new FournisseurDao();

            // Crée un objet Fournisseur avec les nouvelles informations
            // Remarque : Assurez-vous que l'ID correspond à un fournisseur existant dans votre base de données
            Fournisseur fournisseurModifie = new Fournisseur(1, "PharmaElite", "456 Avenue Centrale", "0987654321");

            // Appelle la méthode pour modifier le fournisseur
            fournisseurDao.modifierFournisseur(fournisseurModifie);

            // Affiche un message si tout s'est bien passé
            System.out.println("Fournisseur modifié avec succès !");
        } catch (SQLException e) {
            // Gère l'exception SQL (par exemple, problème d'accès à la base ou ID inexistant)
            System.err.println("Erreur SQL lors de la modification du fournisseur : " + e.getMessage());
        } catch (Exception e) {
            // Gère toute autre exception inattendue
            System.err.println("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }


*/

/*
    public static void main(String[] args) {
        try {
            // Crée une instance de FournisseurDao
            FournisseurDao fournisseurDao = new FournisseurDao();

            // ID du fournisseur à supprimer (assurez-vous que cet ID existe dans votre base de données)
            int idFournisseurASupprimer = 1;

            // Appelle la méthode pour supprimer le fournisseur
            fournisseurDao.supprimerFournisseur(idFournisseurASupprimer);

            // Affiche un message si la suppression a réussi
            System.out.println("Fournisseur supprimé avec succès !");
        } catch (SQLException e) {
            // Gère les exceptions SQL (exemple : ID inexistant ou problème de connexion)
            System.err.println("Erreur SQL lors de la suppression du fournisseur : " + e.getMessage());
        } catch (Exception e) {
            // Gère toute autre exception inattendue
            System.err.println("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }

 */

}

