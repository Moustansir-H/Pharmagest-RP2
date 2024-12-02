package mcci.businessschool.bts.sio.slam.pharmagest.famille.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.famille.Famille;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FamilleDao {

    private Connection baseDeDonneeConnexion;

    public FamilleDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
    }

    // Méthode pour récupérer une famille par son id
    public Famille getFamilleById(int id) {
        String selectFamilleSql = "SELECT nom FROM famille WHERE id = ?";
        Famille famille = null;

        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(selectFamilleSql)) {
            pstmt.setInt(1, id); // Injecte l'id dans la requête SQL
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nom = rs.getString("nom");
                famille = new Famille(nom); // Crée une instance de Famille avec le nom récupéré
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la famille : " + e.getMessage());
        }

        return famille;
    }
}
