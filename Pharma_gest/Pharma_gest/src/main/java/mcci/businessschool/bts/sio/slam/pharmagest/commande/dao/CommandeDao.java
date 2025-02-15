package mcci.businessschool.bts.sio.slam.pharmagest.commande.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.dao.PharmacienDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDao {
    private final Connection baseDeDonneeConnexion;
    private final PharmacienDao pharmacienDao;

    public CommandeDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
        this.pharmacienDao = new PharmacienDao();
    }

    // 🔹 Récupérer toutes les commandes
    public List<Commande> recupererToutesLesCommandes() {
        String sql = "SELECT id, montant, pharmacien_id FROM commande";
        List<Commande> commandes = new ArrayList<>();

        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                double montant = rs.getDouble("montant");
                int pharmacienId = rs.getInt("pharmacien_id");

                // Récupérer le pharmacien correspondant
                Pharmacien pharmacien = pharmacienDao.recupererPharmacienParId(pharmacienId);

                Commande commande = new Commande(id, montant, pharmacien);
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }

        return commandes;
    }

    // 🔹 Récupérer une commande par ID
    public Commande recupererCommandeParId(int idCommande) {
        String sql = "SELECT id, montant, pharmacien_id FROM commande WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double montant = rs.getDouble("montant");
                int pharmacienId = rs.getInt("pharmacien_id");

                Pharmacien pharmacien = pharmacienDao.recupererPharmacienParId(pharmacienId);
                return new Commande(idCommande, montant, pharmacien);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la commande : " + e.getMessage());
        }

        return null;
    }

    // 🔹 Ajouter une commande
    public Integer ajouterCommande(Commande commande) {
        String sql = """
                    INSERT INTO commande (montant, pharmacien_id) 
                    VALUES (?, ?) 
                    RETURNING id
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setDouble(1, commande.getMontant());
            stmt.setInt(2, commande.getPharmacien().getId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("Commande ajoutée avec succès !");
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }

        return null;
    }

    // 🔹 Modifier une commande
    public void modifierCommande(Commande commande) {
        String sql = """
                    UPDATE commande 
                    SET montant = ?, pharmacien_id = ? 
                    WHERE id = ?
                """;

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setDouble(1, commande.getMontant());
            stmt.setInt(2, commande.getPharmacien().getId());
            stmt.setInt(3, commande.getId());

            int lignesModifiees = stmt.executeUpdate();
            if (lignesModifiees > 0) {
                System.out.println("Commande modifiée avec succès !");
            } else {
                System.out.println("Aucune commande trouvée avec id = " + commande.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la commande : " + e.getMessage());
        }
    }

    // 🔹 Supprimer une commande
    public void supprimerCommande(int idCommande) {
        String sql = "DELETE FROM commande WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);

            int lignesSupprimees = stmt.executeUpdate();
            if (lignesSupprimees > 0) {
                System.out.println("Commande supprimée avec succès !");
            } else {
                System.out.println("Aucune commande trouvée avec id = " + idCommande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la commande : " + e.getMessage());
        }
    }
}
