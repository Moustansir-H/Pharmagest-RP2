package mcci.businessschool.bts.sio.slam.pharmagest.facture.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.facture.Facture;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;

import java.sql.*;

public class FactureDao {
    private Connection connection;

    public FactureDao() throws Exception {
        this.connection = DatabaseConnection.getConnexion();
    }

    public Integer ajouterFacture(Facture facture, Vente vente) throws SQLException {
        String insertSQL = "INSERT INTO facture (dateemission, montanttotal, numerofacture, vente_id) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setTimestamp(1, new Timestamp(facture.getDateEmission().getTime()));
            stmt.setDouble(2, facture.getMontantTotal());
            stmt.setString(3, facture.getNumeroFacture());
            stmt.setInt(4, vente.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    return id;
                } else {
                    throw new SQLException("Aucun ID retourné lors de l'ajout de la facture.");
                }
            }
        }
    }

    // Méthode pour récupérer ou mettre à jour une facture si nécessaire
}
