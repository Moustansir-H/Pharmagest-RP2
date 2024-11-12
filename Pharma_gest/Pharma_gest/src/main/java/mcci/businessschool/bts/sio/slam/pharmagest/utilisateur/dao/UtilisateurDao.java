package mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Utilisateur;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDao {

    public static final String SELECT_UTILISATEURS = "SELECT id, identifiant FROM utilisateur";
    private final Connection baseDeDonneeConnexion;

    public UtilisateurDao(Connection baseDeDonneeConnexion) {
        this.baseDeDonneeConnexion = baseDeDonneeConnexion;
    }


    // Méthode pour charger les utilisateurs depuis la base de données
    private List<Utilisateur> recupererUtilisateurs() {
        // utilisateurData.clear();
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_UTILISATEURS)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String identifiant = rs.getString("identifiant");
                if (utilisateurs)
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }

    }


}
