package mcci.businessschool.bts.sio.slam.pharmagest.livraison.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.Livraison;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivraisonDao {
    private Connection connection;

    public LivraisonDao() throws Exception {
        this.connection = DatabaseConnection.getConnexion();
    }

    // Créer une nouvelle livraison et retourner l'ID généré
    public int creerLivraison(Livraison livraison) throws SQLException {
        String sql = "INSERT INTO livraison (datelivraison, status, commande_id, fournisseur_id) " +
                "VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Si la date de livraison n'est pas précisée, on prend la date actuelle
            if (livraison.getDateLivraison() == null) {
                livraison.setDateLivraison(new java.util.Date());
            }
            java.sql.Date sqlDate = new java.sql.Date(livraison.getDateLivraison().getTime());
            stmt.setDate(1, sqlDate);
            stmt.setString(2, livraison.getStatus());
            stmt.setInt(3, livraison.getCommandeId());
            stmt.setInt(4, livraison.getFournisseurId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    livraison.setId(id);
                    return id;
                } else {
                    throw new SQLException("La création de la livraison a échoué, aucun ID retourné.");
                }
            }
        }
    }

    // Mettre à jour le statut d'une livraison
    public void mettreAJourStatutLivraison(int livraisonId, String nouveauStatut) throws SQLException {
        String sql = "UPDATE livraison SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nouveauStatut);
            stmt.setInt(2, livraisonId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Aucune livraison mise à jour pour l'ID " + livraisonId);
            }
        }
    }

    // Récupérer une livraison par l'ID de commande
    public Livraison recupererLivraisonParCommandeId(int commandeId) throws SQLException {
        String sql = "SELECT id, datelivraison, status, commande_id, fournisseur_id FROM livraison WHERE commande_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    Date dateLivraison = rs.getDate("datelivraison");
                    String status = rs.getString("status");
                    int commande_id = rs.getInt("commande_id");
                    int fournisseur_id = rs.getInt("fournisseur_id");
                    return new Livraison(id, dateLivraison, status, commande_id, fournisseur_id);
                }
            }
        }
        return null;
    }

    // Récupérer toutes les livraisons
    public List<Livraison> recupererToutesLesLivraisons() throws SQLException {
        List<Livraison> livraisons = new ArrayList<>();
        String sql = "SELECT id, datelivraison, status, commande_id, fournisseur_id FROM livraison";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Date dateLivraison = rs.getDate("datelivraison");
                String status = rs.getString("status");
                int commande_id = rs.getInt("commande_id");
                int fournisseur_id = rs.getInt("fournisseur_id");
                livraisons.add(new Livraison(id, dateLivraison, status, commande_id, fournisseur_id));
            }
        }
        return livraisons;
    }
/*
    public static void main(String[] args) {
        try {
            // Création d'une commande factice
            Pharmacien pharmacien = new Pharmacien(1, "pharma_test", "pass123");
            Fournisseur fournisseur = new Fournisseur(1, "Test Fournisseur");
            // Pour le test, on crée une commande avec un ID fictif (supposé déjà inséré en base)
            Commande commande = new Commande(1, 0.0, pharmacien, fournisseur, new ArrayList<>(), "En attente");

            // Instanciation du service de livraison
            LivraisonService livraisonService = new LivraisonService();

            // Création de la livraison via la commande
            int livraisonId = livraisonService.creerLivraison(commande);
            System.out.println("Livraison créée avec succès, ID : " + livraisonId);

            // Récupérer la livraison via le DAO pour vérifier son insertion
            LivraisonDao livraisonDao = new LivraisonDao();
            Livraison livraisonRecuperee = livraisonDao.recupererLivraisonParCommandeId(commande.getId());
            if (livraisonRecuperee != null) {
                System.out.println("Livraison récupérée : ID = " + livraisonRecuperee.getId() +
                        ", Date = " + livraisonRecuperee.getDateLivraison() +
                        ", Status = " + livraisonRecuperee.getStatus());
            } else {
                System.out.println("Aucune livraison trouvée pour la commande ID " + commande.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du test de création de livraison : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors du test de livraison : " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    /*
    public static void main(String[] args) {
        try {
            // Pour ce test, on suppose que la commande associée à la livraison a l'ID 1
            int commandeId = 5;

            // Récupérer la livraison associée à cette commande
            LivraisonDao livraisonDao = new LivraisonDao();
            Livraison livraison = livraisonDao.recupererLivraisonParCommandeId(commandeId);

            if (livraison != null) {
                System.out.println("Livraison trouvée :");
                System.out.println("  ID         : " + livraison.getId());
                System.out.println("  Date       : " + livraison.getDateLivraison());
                System.out.println("  Status     : " + livraison.getStatus());
                System.out.println("  CommandeId : " + livraison.getCommandeId());
                System.out.println("  FournisseurId : " + livraison.getFournisseurId());
            } else {
                System.out.println("Aucune livraison trouvée pour la commande ID " + commandeId);
            }

            // Récupérer les lignes de commande associées à cette commande
            LigneDeCommandeDao ligneDao = new LigneDeCommandeDao();
            List<LigneDeCommande> lignes = ligneDao.recupererLignesParCommande(commandeId);

            if (lignes.isEmpty()) {
                System.out.println("Aucune ligne de commande trouvée pour la commande ID " + commandeId);
            } else {
                System.out.println("Lignes de commande associées à la livraison (commande ID " + commandeId + ") :");
                for (LigneDeCommande ligne : lignes) {
                    System.out.println(ligne);
                }
            }
        } catch (Exception e) {
            System.err.println("Une erreur est survenue lors du test : " + e.getMessage());
            e.printStackTrace();
        }
    }*/
}
