package mcci.businessschool.bts.sio.slam.pharmagest.commande.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.dao.PharmacienDao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeDao {
    private Connection baseDeDonneeConnexion;
    private FournisseurDao fournisseurDao;
    private PharmacienDao pharmacienDao;

    public CommandeDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
        this.fournisseurDao = new FournisseurDao();
        this.pharmacienDao = new PharmacienDao();
    }

    /**
     * Récupère toutes les commandes
     * @return Liste des commandes
     */
    public List<Commande> recupererToutesLesCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = """
                SELECT id, montant, pharmacien_id, fournisseur_id, 
                       date_creation, statut
                FROM commande
                ORDER BY date_creation DESC
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Commande commande = extraireCommandeDeResultSet(rs);
                commandes.add(commande);
            }
        }

        return commandes;
    }

    /**
     * Récupère les commandes en attente de confirmation
     * @return Liste des commandes en attente
     */
    public List<Commande> recupererCommandesEnAttente() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = """
                SELECT id, montant, pharmacien_id, fournisseur_id, 
                       date_creation, statut
                FROM commande
                WHERE statut = 'En attente de confirmation'
                ORDER BY date_creation DESC
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Commande commande = extraireCommandeDeResultSet(rs);
                commandes.add(commande);
            }
        }

        return commandes;
    }

    /**
     * Récupère une commande par son ID
     * @param id ID de la commande
     * @return La commande ou null si non trouvée
     */
    public Commande recupererCommandeParId(int id) throws SQLException {
        String sql = """
                SELECT id, montant, pharmacien_id, fournisseur_id, 
                       date_creation, statut
                FROM commande
                WHERE id = ?
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extraireCommandeDeResultSet(rs);
                }
            }
        }

        return null;
    }

    /**
     * Ajoute une nouvelle commande
     * @param commande La commande à ajouter
     * @return L'ID de la commande créée
     */
    public int ajouterCommande(Commande commande) throws SQLException {
        String sql = """
                INSERT INTO commande (montant, pharmacien_id, fournisseur_id, date_creation, statut)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setDouble(1, commande.getMontantTotal());
            stmt.setInt(2, commande.getPharmacien().getId());
            stmt.setInt(3, commande.getFournisseur().getId());

            // Utiliser la date de création de la commande ou la date actuelle
            Timestamp dateCreation = commande.getDateCreation() != null
                    ? Timestamp.valueOf(commande.getDateCreation())
                    : Timestamp.valueOf(LocalDateTime.now());
            stmt.setTimestamp(4, dateCreation);

            stmt.setString(5, commande.getStatut() != null ? commande.getStatut() : "En attente de confirmation");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Échec de l'ajout de la commande");
    }

    /**
     * Met à jour une commande existante
     * @param commande La commande à mettre à jour
     */
    public void mettreAJourCommande(Commande commande) throws SQLException {
        String sql = """
                UPDATE commande
                SET montant = ?, pharmacien_id = ?, fournisseur_id = ?, statut = ?
                WHERE id = ?
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setDouble(1, commande.getMontantTotal());
            stmt.setInt(2, commande.getPharmacien().getId());
            stmt.setInt(3, commande.getFournisseur().getId());
            stmt.setString(4, commande.getStatut());
            stmt.setInt(5, commande.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La mise à jour de la commande a échoué, aucune ligne affectée");
            }
        }
    }

    /**
     * Confirme une commande en appelant la fonction PostgreSQL
     * @param commandeId ID de la commande à confirmer
     */
    public void confirmerCommande(int commandeId) throws SQLException {
        String sql = "SELECT valider_commande(?)";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la confirmation de la commande: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime une commande
     * @param commandeId ID de la commande à supprimer
     */
    public void supprimerCommande(int commandeId) throws SQLException {
        // D'abord supprimer les lignes de commande associées
        String sqlLignes = "DELETE FROM lignedecommande WHERE commande_id = ?";
        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sqlLignes)) {
            stmt.setInt(1, commandeId);
            stmt.executeUpdate();
        }

        // Ensuite supprimer la commande
        String sqlCommande = "DELETE FROM commande WHERE id = ?";
        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sqlCommande)) {
            stmt.setInt(1, commandeId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La suppression de la commande a échoué, aucune ligne affectée");
            }
        }
    }

    /**
     * Extrait une commande d'un ResultSet
     */
    private Commande extraireCommandeDeResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        double montant = rs.getDouble("montant");
        int pharmacienId = rs.getInt("pharmacien_id");
        int fournisseurId = rs.getInt("fournisseur_id");

        Timestamp dateCreationTimestamp = rs.getTimestamp("date_creation");
        LocalDateTime dateCreation = dateCreationTimestamp != null
                ? dateCreationTimestamp.toLocalDateTime()
                : null;

        String statut = rs.getString("statut");

        // Récupérer le pharmacien et le fournisseur
        Pharmacien pharmacien = pharmacienDao.recupererPharmacienParId(pharmacienId);
        Fournisseur fournisseur = fournisseurDao.getFournisseurById(fournisseurId);

        // Créer la commande avec tous les détails
        Commande commande = new Commande(id, montant, pharmacien, fournisseur, new ArrayList<>(), statut, dateCreation);

        return commande;
    }
}