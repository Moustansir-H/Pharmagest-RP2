package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FournisseurDao {

    private Connection baseDeDonneeConnexion;

    public FournisseurDao() throws Exception {
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
}
