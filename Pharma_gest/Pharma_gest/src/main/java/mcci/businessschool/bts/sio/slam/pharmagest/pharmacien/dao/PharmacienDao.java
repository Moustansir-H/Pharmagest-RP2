package mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PharmacienDao {

    private Connection baseDeDonneeConnexion;

    public PharmacienDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
    }

    // 🔹 Ajouter un pharmacien
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

    // 🔹 Supprimer un pharmacien
    public void supprimerPharmacien(Integer idUtilisateur) {
        String deleteSQL = "DELETE FROM pharmacien WHERE utilisateur_id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idUtilisateur);

            int ligneSupprimee = stmt.executeUpdate();

            if (ligneSupprimee > 0) {
                System.out.println("Pharmacien supprimé avec succès !");
            } else {
                System.out.println("Aucun pharmacien trouvé avec idUtilisateur = " + idUtilisateur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du pharmacien : " + e.getMessage());
        }
    }

    // 🔹 Récupérer un pharmacien par son ID
    public Pharmacien recupererPharmacienParId(int idPharmacien) {
        String sql = """
                    SELECT u.id, u.identifiant, u.motdepasse 
                    FROM utilisateur u
                    JOIN pharmacien p ON u.id = p.utilisateur_id
                    WHERE p.utilisateur_id = ?
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, idPharmacien);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String identifiant = rs.getString("identifiant");
                String motDePasse = rs.getString("motdepasse");

                return new Pharmacien(id, identifiant, motDePasse);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du pharmacien : " + e.getMessage());
        }

        return null;
    }
}
