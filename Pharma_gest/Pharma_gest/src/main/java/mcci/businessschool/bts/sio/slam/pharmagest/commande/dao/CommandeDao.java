package mcci.businessschool.bts.sio.slam.pharmagest.commande.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDao {
    private Connection connection;

    public CommandeDao() throws Exception {
        this.connection = DatabaseConnection.getConnexion();
    }

    // Ajouter une nouvelle commande
    public int ajouterCommande(Commande commande) throws SQLException {
        String sql = "INSERT INTO commande (montant, pharmacien_id, fournisseur_id) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, commande.getMontantTotal());
            stmt.setInt(2, commande.getPharmacien().getId());
            stmt.setInt(3, commande.getFournisseur().getId());
            stmt.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Échec de l'ajout de la commande, aucun ID généré.");
                }
            }
        }
    }

    // Récupérer toutes les commandes
    public List<Commande> recupererToutesLesCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = """
                    SELECT c.id, c.montant, c.pharmacien_id, u.identifiant AS pharmacien_nom,\s
                           f.id AS fournisseur_id, f.nom AS fournisseur_nom,\s
                           (CASE WHEN EXISTS (SELECT 1 FROM livraison l WHERE l.commande_id = c.id)\s
                                 THEN 'Validée' ELSE 'En attente' END) AS statut
                    FROM commande c
                    JOIN pharmacien p ON c.pharmacien_id = p.id
                    JOIN utilisateur u ON p.utilisateur_id = u.id
                    JOIN fournisseur f ON c.fournisseur_id = f.id;
                    
                """;

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pharmacien pharmacien = new Pharmacien(rs.getInt("pharmacien_id"), rs.getString("pharmacien_nom"));

                int fournisseurId = rs.getInt("fournisseur_id");
                String fournisseurNom = rs.getString("fournisseur_nom");

                if (fournisseurNom == null) {
                    throw new SQLException("❌ Erreur : Le fournisseur avec ID " + fournisseurId + " est introuvable.");
                }

                Fournisseur fournisseur = new Fournisseur(fournisseurId, fournisseurNom);

                String statut = rs.getString("statut");

                Commande commande = new Commande(
                        rs.getInt("id"),
                        rs.getDouble("montant"),
                        pharmacien,
                        fournisseur,
                        new ArrayList<>(),
                        statut // Statut déterminé dynamiquement
                );
                commandes.add(commande);
            }
        }
        return commandes;
    }

    public Commande recupererCommandeParId(int commandeId) throws SQLException {
        String sql = """
                    SELECT c.id, c.montant, c.pharmacien_id, u.identifiant AS pharmacien_nom,
                           f.id AS fournisseur_id, f.nom AS fournisseur_nom
                    FROM commande c
                    JOIN pharmacien p ON c.pharmacien_id = p.id
                    JOIN utilisateur u ON p.utilisateur_id = u.id
                    JOIN fournisseur f ON c.fournisseur_id = f.id
                    WHERE c.id = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pharmacien pharmacien = new Pharmacien(rs.getInt("pharmacien_id"), rs.getString("pharmacien_nom"));
                    Fournisseur fournisseur = new Fournisseur(rs.getInt("fournisseur_id"), rs.getString("fournisseur_nom"));

                    return new Commande(
                            rs.getInt("id"),
                            rs.getDouble("montant"),
                            pharmacien,
                            fournisseur,
                            new ArrayList<>(),
                            "En attente" // Le statut sera géré dynamiquement
                    );
                }
            }
        }
        return null;
    }


    // Mettre à jour le montant total d'une commande
    public void mettreAJourMontantCommande(int commandeId, double montantTotal) throws SQLException {
        String sql = "UPDATE commande SET montant = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, montantTotal);
            stmt.setInt(2, commandeId);
            stmt.executeUpdate();
        }
    }

    public List<Fournisseur> recupererFournisseursAvecMedicamentsSousSeuil() throws SQLException {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = """
                    SELECT DISTINCT f.id, f.nom
                    FROM medicament m
                    JOIN fournisseur f ON m.fournisseur_id = f.id
                    WHERE m.stock <= m.seuilcommande
                """;

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fournisseurs.add(new Fournisseur(rs.getInt("id"), rs.getString("nom")));
            }
        }
        return fournisseurs;
    }

/*
    public static void main(String[] args) {
        try {
            CommandeDao commandeDao = new CommandeDao();

            System.out.println("\n📢 📋 Test de l'affichage des commandes avec statut 📋 📢");

            // ✅ Récupérer toutes les commandes et afficher leur statut
            List<Commande> commandes = commandeDao.recupererToutesLesCommandes();
            if (commandes.isEmpty()) {
                System.out.println("⚠️ Aucune commande en base.");
            } else {
                for (Commande commande : commandes) {
                    System.out.println("📌 Commande ID : " + commande.getId() +
                            " | Pharmacien : " + commande.getPharmacien().getIdentifiant() +
                            " | Fournisseur : " + commande.getFournisseur().getNom() +
                            " | Montant : " + commande.getMontantTotal() +
                            " € | Statut : " + commande.getStatut());
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des commandes : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue : " + e.getMessage());
        }
    }*/

    /*

    public static void main(String[] args) {
        try {
            CommandeDao commandeDao = new CommandeDao();
            List<Fournisseur> fournisseurs = commandeDao.recupererFournisseursAvecMedicamentsSousSeuil();

            System.out.println("\n📢 Vérification des fournisseurs avec médicaments sous seuil :");
            for (Fournisseur fournisseur : fournisseurs) {
                System.out.println("🔹 " + fournisseur.getNom() + " (ID: " + fournisseur.getId() + ")");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }*/

}
