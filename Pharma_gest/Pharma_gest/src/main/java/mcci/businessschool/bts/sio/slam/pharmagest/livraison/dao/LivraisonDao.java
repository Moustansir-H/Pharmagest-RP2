package mcci.businessschool.bts.sio.slam.pharmagest.livraison.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.Livraison;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivraisonDao {
    private Connection connection;

    public LivraisonDao() throws Exception {
        this.connection = DatabaseConnection.getConnexion();
    }

    // ✅ Ajouter une livraison
    public void ajouterLivraison(Livraison livraison) throws SQLException {
        String sql = "INSERT INTO livraison (datelivraison, status, commande_id, fournisseur_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(livraison.getDateLivraison())); // Conversion LocalDate -> SQL Date
            stmt.setString(2, livraison.getStatus());
            stmt.setInt(3, livraison.getCommande().getId());
            stmt.setInt(4, livraison.getFournisseur().getId());

            stmt.executeUpdate();

            // Récupération de l'ID généré
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    livraison.setId(rs.getInt(1));
                    System.out.println("✅ Livraison ajoutée avec succès, ID : " + livraison.getId());
                }
            }
        }
    }

    // ✅ Récupérer toutes les livraisons
    public List<Livraison> recupererToutesLesLivraisons() throws SQLException {
        List<Livraison> livraisons = new ArrayList<>();
        String sql = "SELECT l.id, l.datelivraison, l.status, l.commande_id, l.fournisseur_id, f.nom AS fournisseur_nom " +
                "FROM livraison l " +
                "JOIN fournisseur f ON l.fournisseur_id = f.id";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Commande commande = new Commande(rs.getInt("commande_id")); // Commande simplifiée
                Fournisseur fournisseur = new Fournisseur(rs.getInt("fournisseur_id"), rs.getString("fournisseur_nom"));

                Livraison livraison = new Livraison(
                        rs.getInt("id"),
                        rs.getDate("datelivraison").toLocalDate(),
                        rs.getString("status"),
                        commande,
                        fournisseur
                );
                livraisons.add(livraison);
            }
        }
        return livraisons;
    }

    // ✅ Mettre à jour le statut d'une livraison
    public void mettreAJourStatutLivraison(int livraisonId, String nouveauStatut) throws SQLException {
        String sql = "UPDATE livraison SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nouveauStatut);
            stmt.setInt(2, livraisonId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Statut de la livraison ID " + livraisonId + " mis à jour : " + nouveauStatut);
            } else {
                System.out.println("⚠️ Aucune livraison trouvée avec ID " + livraisonId);
            }
        }
    }

    public Livraison recupererLivraisonParId(int livraisonId) throws SQLException {
        String sql = "SELECT l.id, l.datelivraison, l.status, l.commande_id, l.fournisseur_id, f.nom AS fournisseur_nom " +
                "FROM livraison l " +
                "JOIN fournisseur f ON l.fournisseur_id = f.id " +
                "WHERE l.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, livraisonId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Commande commande = new Commande(rs.getInt("commande_id"));
                    Fournisseur fournisseur = new Fournisseur(rs.getInt("fournisseur_id"), rs.getString("fournisseur_nom"));

                    return new Livraison(
                            rs.getInt("id"),
                            rs.getDate("datelivraison").toLocalDate(),
                            rs.getString("status"),
                            commande,
                            fournisseur
                    );
                }
            }
        }
        return null;
    }

}
