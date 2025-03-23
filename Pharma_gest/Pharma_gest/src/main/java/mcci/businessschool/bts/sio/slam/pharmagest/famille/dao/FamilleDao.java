package mcci.businessschool.bts.sio.slam.pharmagest.famille.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.famille.Famille;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                famille = new Famille(id, nom); // ✅ Maintenant l'ID est bien récupéré !

            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la famille : " + e.getMessage());
        }

        return famille;
    }

    public Integer getFamilleIdByName(String nom) {
        String sql = "SELECT id FROM famille WHERE nom = ?";
        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setString(1, nom);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération de l'ID de la famille : " + e.getMessage());
        }
        return null; // ✅ Retourne `null` si non trouvé
    }


    public List<Famille> recupererToutesLesFamilles() {
        List<Famille> familles = new ArrayList<>();
        String sql = "SELECT id, nom FROM famille";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Famille famille = new Famille(rs.getInt("id"), rs.getString("nom"));
                familles.add(famille);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des familles : " + e.getMessage());
        }

        return familles;
    }


}
