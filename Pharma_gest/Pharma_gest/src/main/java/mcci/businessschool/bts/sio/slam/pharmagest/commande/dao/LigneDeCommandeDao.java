package mcci.businessschool.bts.sio.slam.pharmagest.commande.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "SELECT * FROM lignedecommande WHERE commande_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = new Commande(rs.getInt("commande_id"));
                    Medicament medicament = new Medicament(rs.getInt("medicament_id"));

                    LigneDeCommande ligne = new LigneDeCommande(
                            rs.getInt("id"),
                            commande,
                            medicament,
                            rs.getInt("quantitevendu"),
                            rs.getDouble("prixunitaire"),
                            rs.getInt("quantiterecue"),
                            rs.getDouble("prixachatreel"),
                            rs.getDouble("prixventereel")
                    );
                    lignesDeCommande.add(ligne);
                }
            }
        }
        return lignesDeCommande;
    }

    // Mettre à jour les quantités reçues et les prix après réception
    public void mettreAJourReception(int ligneDeCommandeId, int quantiteRecue, double prixAchatReel, double prixVenteReel) throws SQLException {
        String sql = "UPDATE lignedecommande SET quantiterecue = ?, prixachatreel = ?, prixventereel = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantiteRecue);
            stmt.setDouble(2, prixAchatReel);
            stmt.setDouble(3, prixVenteReel);
            stmt.setInt(4, ligneDeCommandeId);
            stmt.executeUpdate();
        }
    }

    /*
    public static void main(String[] args) {
        try {
            Connection connection = DatabaseConnection.getConnexion();
            CommandeDao commandeDao = new CommandeDao();
            LigneDeCommandeDao ligneDeCommandeDao = new LigneDeCommandeDao();

            // ✅ 1. Création d'une commande test (Pharmacien ID = 1, Fournisseur ID = 1)
            Commande commandeTest = new Commande(0.0, new Pharmacien(1), new Fournisseur(1, "FournisseurTest"), new ArrayList<>());

            int commandeId = commandeDao.ajouterCommande(commandeTest);
            System.out.println("✅ Commande créée avec ID : " + commandeId);

            // ✅ 2. Ajout d'une ligne de commande test (Médicament ID = 1)
            LigneDeCommande ligneTest = new LigneDeCommande(
                    new Commande(commandeId),
                    new Medicament(58),  // ID d'un médicament existant
                    5,  // Quantité commandée
                    10.0,  // Prix unitaire
                    0,  // Quantité reçue (car non encore livrée)
                    0.0,  // Prix d'achat réel (sera mis à jour plus tard)
                    0.0   // Prix de vente réel (sera mis à jour plus tard)
            );
            ligneDeCommandeDao.ajouterLigneDeCommande(ligneTest);
            System.out.println("✅ Ligne de commande ajoutée avec succès.");

            // ✅ 3. Vérification des lignes de commande de cette commande
            List<LigneDeCommande> lignes = ligneDeCommandeDao.recupererLignesParCommande(commandeId);
            System.out.println("📢 Lignes de commande récupérées pour la commande ID " + commandeId + " :");
            for (LigneDeCommande ligne : lignes) {
                System.out.println(ligne);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }*/
}
