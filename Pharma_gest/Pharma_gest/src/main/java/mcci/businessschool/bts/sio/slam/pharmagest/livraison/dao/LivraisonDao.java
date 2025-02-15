package mcci.businessschool.bts.sio.slam.pharmagest.livraison.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.CommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.Livraison;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivraisonDao {
    private final Connection baseDeDonneeConnexion;
    private final CommandeDao commandeDao;
    private final FournisseurDao fournisseurDao;

    public LivraisonDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
        this.commandeDao = new CommandeDao();
        this.fournisseurDao = new FournisseurDao();
    }

    // 🔹 Ajouter une livraison avec un fournisseur
    public Integer ajouterLivraison(Livraison livraison, int commandeId, int fournisseurId) {
        String sql = """
                    INSERT INTO livraison (datelivraison, status, commande_id, fournisseur_id) 
                    VALUES (?, ?, ?, ?) 
                    RETURNING id
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(livraison.getDatelivraison().getTime()));
            stmt.setString(2, livraison.getStatus());
            stmt.setInt(3, commandeId);
            stmt.setInt(4, fournisseurId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("Livraison ajoutée avec succès !");
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la livraison : " + e.getMessage());
        }

        return null;
    }

    // 🔹 Récupérer une livraison par ID
    public Livraison recupererLivraisonParId(int idLivraison) {
        String sql = "SELECT datelivraison, status, commande_id, fournisseur_id FROM livraison WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, idLivraison);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Date dateLivraison = rs.getDate("datelivraison");
                String status = rs.getString("status");
                int commandeId = rs.getInt("commande_id");
                int fournisseurId = rs.getInt("fournisseur_id");

                Commande commande = commandeDao.recupererCommandeParId(commandeId);
                Fournisseur fournisseur = fournisseurDao.getFournisseurById(fournisseurId);

                return new Livraison(idLivraison, dateLivraison, status, commande, fournisseur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la livraison : " + e.getMessage());
        }

        return null;
    }

    // 🔹 Mettre à jour le statut de la livraison
    public void mettreAJourStatutLivraison(int idLivraison, String status) {
        String sql = "UPDATE livraison SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, idLivraison);

            int lignesModifiees = stmt.executeUpdate();
            if (lignesModifiees > 0) {
                System.out.println("Statut de livraison mis à jour avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }

    // 🔹 Récupérer toutes les livraisons
    public List<Livraison> recupererToutesLesLivraisons() {
        String sql = "SELECT id, datelivraison, status, commande_id, fournisseur_id FROM livraison";
        List<Livraison> livraisons = new ArrayList<>();

        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                Date dateLivraison = rs.getDate("datelivraison");
                String status = rs.getString("status");
                int commandeId = rs.getInt("commande_id");
                int fournisseurId = rs.getInt("fournisseur_id");

                Commande commande = commandeDao.recupererCommandeParId(commandeId);
                Fournisseur fournisseur = fournisseurDao.getFournisseurById(fournisseurId);

                Livraison livraison = new Livraison(id, dateLivraison, status, commande, fournisseur);
                livraisons.add(livraison);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des livraisons : " + e.getMessage());
        }

        return livraisons;
    }

    // 🔹 Supprimer une livraison
    public void supprimerLivraison(int idLivraison) {
        String sql = "DELETE FROM livraison WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, idLivraison);

            int lignesSupprimees = stmt.executeUpdate();
            if (lignesSupprimees > 0) {
                System.out.println("Livraison supprimée avec succès !");
            } else {
                System.out.println("Aucune livraison trouvée avec id = " + idLivraison);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la livraison : " + e.getMessage());
        }
    }
}
