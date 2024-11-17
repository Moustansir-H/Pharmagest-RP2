package mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PharmacienDao {

    private Connection baseDeDonneeConnexion;

    public PharmacienDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
    }

    public void ajouterPharmacien(Integer idUtilisateur) {
        String insertSQL = "INSERT INTO pharmacien (utilisateur_id) VALUES (?)";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(insertSQL)) {
            stmt.setInt(1, idUtilisateur);

            stmt.executeUpdate();
            System.out.println("Pharmacien ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du Pharmacien : " + e.getMessage());
        }
    }

    public void supprimerPharmacien(Integer idUtilisateur) {
        String deleteSQL = "DELETE FROM pharmacien WHERE utilisateur_id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idUtilisateur);

            int ligneSupprimee = stmt.executeUpdate();

            if (ligneSupprimee > 0) {
                System.out.println("Pharmacien supprimé avec succès !");
            } else {
                System.out.println("Aucun pharmacien trouvé avec idUtilisateur =" + idUtilisateur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du pharmacien : " + e.getMessage());
        }
    }

}
