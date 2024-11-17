package mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Role;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Utilisateur;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.Vendeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDao {

    private Connection baseDeDonneeConnexion;

    public UtilisateurDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
    }

    // Méthode pour charger les utilisateurs depuis la base de données
    public List<Utilisateur> recupererUtilisateurs() {
        // Requete de recuperation en tous utilisateurs en base
        String selectUtilisateurSql = "SELECT id, identifiant, role, motdepasse FROM utilisateur";

        List<Utilisateur> utilisateurs = new ArrayList<>();
        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(selectUtilisateurSql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String identifiant = rs.getString("identifiant");
                Role role = Role.valueOf(rs.getString("role"));
                String motDePasse = rs.getString("motdepasse");
                if (Role.PHARMACIEN.equals(role)) {
                    utilisateurs.add(new Pharmacien(id, identifiant, motDePasse));
                } else {
                    utilisateurs.add(new Vendeur(id, identifiant, motDePasse));
                }

            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }

        return utilisateurs;


    }

    public Integer ajouterUtilisateur(Utilisateur utilisateur) throws SQLException {
        String insertSQL = "INSERT INTO utilisateur (identifiant, motdepasse, role) VALUES (?, ?, ?::role) RETURNING id";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(insertSQL)) {
            stmt.setString(1, utilisateur.getIdentifiant());
            stmt.setString(2, utilisateur.getMotDePasse());
            stmt.setString(3, utilisateur.getRole().name());


            // Exécuter la requête
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    // Récupérer l'ID généré
                    int id = resultSet.getInt("id");

                    System.out.println("Utilisateur ajouté avec succès !");

                    return id;
                } else {
                    throw new SQLException("Erreur lors de l'ajout de l'utilisateur, aucun ID retourné.");
                }
            }
        }
    }

    public void supprimerUtilisateurParId(Integer id) {
        String deleteSQL = "DELETE FROM utilisateur WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);

            int ligneSupprimee = stmt.executeUpdate();

            if (ligneSupprimee > 0) {
                System.out.println("Utilisateur supprimé avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec id =" + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    public void modifierUtilisateur(Utilisateur utilisateur) {
        String updateSQL = "UPDATE utilisateur SET identifiant = ?, motdepasse = ?, role = ?::role WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(updateSQL)) {
            // Remplir les paramètres
            stmt.setString(1, utilisateur.getIdentifiant());
            stmt.setString(2, utilisateur.getMotDePasse());
            stmt.setString(3, utilisateur.getRole().name()); // Utilisation de l'énumération Role
            stmt.setInt(4, utilisateur.getId());

            // Exécuter la requête
            int lignesModifiees = stmt.executeUpdate();

            if (lignesModifiees > 0) {
                System.out.println("Utilisateur modifié avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec id = " + utilisateur.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
        }
    }

}
