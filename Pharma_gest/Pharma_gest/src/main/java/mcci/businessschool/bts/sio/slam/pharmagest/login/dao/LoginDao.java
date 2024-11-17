package mcci.businessschool.bts.sio.slam.pharmagest.login.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {
    private Connection connexionBD;

    public LoginDao() throws Exception {
        this.connexionBD = DatabaseConnection.getConnexion();

    }

    public boolean seConnecter(String identifiant, String motdePasse) {
        String compterUtilisateurSql = "SELECT count(*) FROM utilisateur WHERE identifiant = ? AND motdepasse = ?";

        try (PreparedStatement stmt = connexionBD.prepareStatement(compterUtilisateurSql)) {
            stmt.setString(1, identifiant);
            stmt.setString(2, motdePasse);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt(1) == 1) {
                        System.out.println("Connexion reussi");
                        return true;
                    }
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du connexion : " + e.getMessage());
        }
        return false;
    }
}

