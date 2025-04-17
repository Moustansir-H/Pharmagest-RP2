package mcci.businessschool.bts.sio.slam.pharmagest.commande.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneDeCommandeDao {
    private Connection connection;

    public LigneDeCommandeDao() throws Exception {
        this.connection = DatabaseConnection.getConnexion();
    }

    // Ajouter une nouvelle ligne de commande
    public void ajouterLigneDeCommande(LigneDeCommande ligneDeCommande) throws SQLException {
        String sql = "INSERT INTO lignedecommande (quantitevendu, prixunitaire, commande_id, medicament_id, quantiterecue, prixachatreel, prixventereel) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ligneDeCommande.getQuantiteVendu());
            stmt.setDouble(2, ligneDeCommande.getPrixUnitaire());
            stmt.setInt(3, ligneDeCommande.getCommande().getId());
            stmt.setInt(4, ligneDeCommande.getMedicament().getId());
            stmt.setInt(5, ligneDeCommande.getQuantiteRecue());
            stmt.setDouble(6, ligneDeCommande.getPrixAchatReel());
            stmt.setDouble(7, ligneDeCommande.getPrixVenteReel());
            stmt.executeUpdate();
        }
    }

    // Récupérer toutes les lignes de commande d'une commande spécifique
    public List<LigneDeCommande> recupererLignesParCommande(int commandeId) throws SQLException {
        List<LigneDeCommande> lignesDeCommande = new ArrayList<>();

        // Vérifier si les colonnes avancées existent
        boolean colonnesAvanceeExistent = verifierColonnesAvancees();

        String sql;
        if (colonnesAvanceeExistent) {
            sql = "SELECT * FROM lignedecommande WHERE commande_id = ?";
        } else {
            sql = "SELECT id, commande_id, medicament_id, quantitevendu, prixunitaire FROM lignedecommande WHERE commande_id = ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = new Commande(rs.getInt("commande_id"));
                    MedicamentDao medicamentDao = new MedicamentDao();
                    Medicament medicament = medicamentDao.recupererMedicamentParId(rs.getInt("medicament_id"));

                    LigneDeCommande ligne;
                    if (colonnesAvanceeExistent) {
                        ligne = new LigneDeCommande(
                                rs.getInt("id"),
                                commande,
                                medicament,
                                rs.getInt("quantitevendu"),
                                rs.getDouble("prixunitaire"),
                                rs.getInt("quantiterecue"),
                                rs.getDouble("prixachatreel"),
                                rs.getDouble("prixventereel")
                        );
                    } else {
                        ligne = new LigneDeCommande(
                                rs.getInt("id"),
                                commande,
                                medicament,
                                rs.getInt("quantitevendu"),
                                rs.getDouble("prixunitaire"),
                                0, // quantiterecue par défaut
                                0.0, // prixachatreel par défaut
                                0.0  // prixventereel par défaut
                        );
                    }
                    lignesDeCommande.add(ligne);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return lignesDeCommande;
    }

    // Mettre à jour les quantités reçues et les prix après réception
    public void mettreAJourReception(int ligneDeCommandeId, int quantiteRecue, double prixAchatReel, double prixVenteReel) throws SQLException {
        // Vérifier si les colonnes avancées existent
        boolean colonnesAvanceeExistent = verifierColonnesAvancees();

        if (colonnesAvanceeExistent) {
            String sql = "UPDATE lignedecommande SET quantiterecue = ?, prixachatreel = ?, prixventereel = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, quantiteRecue);
                stmt.setDouble(2, prixAchatReel);
                stmt.setDouble(3, prixVenteReel);
                stmt.setInt(4, ligneDeCommandeId);
                stmt.executeUpdate();
            }
        } else {
            // Si les colonnes n'existent pas, on ne fait rien ou on peut lever une exception
            System.out.println("Les colonnes avancées n'existent pas, impossible de mettre à jour la réception");
        }
    }

    // Vérifier si les colonnes avancées existent
    private boolean verifierColonnesAvancees() {
        try {
            String sql = """
                SELECT EXISTS (
                    SELECT 1 
                    FROM information_schema.columns 
                    WHERE table_name = 'lignedecommande' 
                    AND column_name = 'quantiterecue'
                ) AS colonne_existe
            """;

            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("colonne_existe");
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification des colonnes: " + e.getMessage());
            return false;
        }
    }
}