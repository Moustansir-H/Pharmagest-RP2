package mcci.businessschool.bts.sio.slam.pharmagest.vendeur.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VendeurDao {

    private Connection baseDeDonneeConnexion;

    public VendeurDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
    }

    // Méthode pour ajouter un vendeur en utilisant l'id de l'utilisateur associé
    public void ajouterVendeur(Integer idUtilisateur) {
        String insertSQL = "INSERT INTO vendeur (utilisateur_id) VALUES (?)";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(insertSQL)) {
            stmt.setInt(1, idUtilisateur);
            stmt.executeUpdate();
            System.out.println("Vendeur ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du Vendeur : " + e.getMessage());
        }
    }

    public void supprimerVendeur(Integer idUtilisateur) {
        String deleteSQL = "DELETE FROM vendeur WHERE utilisateur_id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idUtilisateur);
            int ligneSupprimee = stmt.executeUpdate();

            if (ligneSupprimee > 0) {
                System.out.println("Vendeur supprimé avec succès !");
            } else {
                System.out.println("Aucun vendeur trouvé avec idUtilisateur =" + idUtilisateur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du Vendeur : " + e.getMessage());
        }
    }

    // Vérifie si un vendeur existe dans la table vendeur
    public boolean existeVendeur(Integer idVendeur) {
        String selectSQL = "SELECT COUNT(*) FROM vendeur WHERE id = ?";
        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(selectSQL)) {
            stmt.setInt(1, idVendeur);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du vendeur : " + e.getMessage());
        }
        return false;
    }

    /*
    public Vendeur recupererVendeurParUtilisateurId(Integer idUtilisateur) {
        String selectSQL = "SELECT utilisateur_id FROM vendeur WHERE utilisateur_id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(selectSQL)) {
            stmt.setInt(1, idUtilisateur);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Créer un vendeur avec l'ID utilisateur récupéré
                    return new Vendeur(idUtilisateur, "Inconnu", "Inconnu");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du vendeur : " + e.getMessage());
        }
        return null;
    }*/

}
