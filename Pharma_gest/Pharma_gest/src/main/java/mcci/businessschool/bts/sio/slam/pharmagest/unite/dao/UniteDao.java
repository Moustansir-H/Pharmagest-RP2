package mcci.businessschool.bts.sio.slam.pharmagest.unite.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.unite.Unite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UniteDao {

    private Connection baseDeDonneeConnexion;

    public UniteDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
    }

    // Méthode pour récupérer une unité par son id
    public Unite recupererUniteeParId(int id) {
        String selectUniteSql = "SELECT nomunite FROM unite WHERE id = ?";
        Unite unite = null;

        try (PreparedStatement pstmt = baseDeDonneeConnexion.prepareStatement(selectUniteSql)) {
            pstmt.setInt(1, id); // Injecte l'id dans la requête SQL
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nomUnite = rs.getString("nomunite");
                unite = new Unite(nomUnite); // Crée une instance d'Unite
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'unité : " + e.getMessage());
        }

        return unite;
    }
}
