package mcci.businessschool.bts.sio.slam.pharmagest.paiement.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.Paiement;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.StatutPaiement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaiementDao {
    private final Connection connexion;

    public PaiementDao() throws Exception {
        this.connexion = DatabaseConnection.getConnexion();
    }

    /**
     * Ajoute un paiement en base et retourne l'ID généré
     */
    public Integer ajouterPaiement(Paiement paiement) throws SQLException {
        String insertSQL = "INSERT INTO paiement (montant, modepaiement, statut, vendeur_id, vente_id) VALUES (?, ?, ?::statutpaiement, ?, ?) RETURNING id";

        try (PreparedStatement stmt = connexion.prepareStatement(insertSQL)) {
            stmt.setDouble(1, paiement.getMontant());
            stmt.setString(2, paiement.getModePaiement());
            stmt.setString(3, paiement.getStatut().name());
            stmt.setInt(4, paiement.getVendeurId());
            stmt.setInt(5, paiement.getVenteId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    System.out.println("Paiement ajouté avec succès !");
                    return id;
                } else {
                    throw new SQLException("Erreur lors de l'ajout du paiement, aucun ID retourné.");
                }
            }
        }
    }

    /**
     * Récupère un paiement par son ID
     */
    public Paiement getPaiementById(int id) {
        String query = "SELECT * FROM paiement WHERE id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Paiement(
                        rs.getDouble("montant"),
                        rs.getString("modepaiement"),
                        StatutPaiement.valueOf(rs.getString("statut"))
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du paiement : " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère tous les paiements enregistrés
     */
    public List<Paiement> getAllPaiements() {
        List<Paiement> paiements = new ArrayList<>();
        String query = "SELECT * FROM paiement";

        try (PreparedStatement stmt = connexion.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                paiements.add(new Paiement(
                        rs.getDouble("montant"),
                        rs.getString("mode_paiement"),
                        StatutPaiement.valueOf(rs.getString("statut"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des paiements : " + e.getMessage());
        }
        return paiements;
    }

    /**
     * Met à jour le statut d'un paiement
     */
    public boolean mettreAJourStatutPaiement(int id, StatutPaiement statut) {
        String updateSQL = "UPDATE paiement SET statut = ?::statutpaiement WHERE id = ?";

        try (PreparedStatement stmt = connexion.prepareStatement(updateSQL)) {
            stmt.setString(1, statut.name());
            stmt.setInt(2, id);

            int lignesModifiees = stmt.executeUpdate();
            if (lignesModifiees > 0) {
                System.out.println("Statut du paiement mis à jour avec succès !");
            } else {
                System.out.println("Aucun paiement trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut du paiement : " + e.getMessage());
        }
        return false;
    }
}
