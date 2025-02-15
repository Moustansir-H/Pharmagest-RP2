package mcci.businessschool.bts.sio.slam.pharmagest.commande.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.ligne.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LigneDeCommandeDao {
    private final Connection baseDeDonneeConnexion;
    private final MedicamentDao medicamentDao;

    public LigneDeCommandeDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
        this.medicamentDao = new MedicamentDao();
    }

    // 🔹 Recuperer les lignes d'une commande
    public List<LigneDeCommande> recupererLignesParCommande(int idCommande) {
        String sql = "SELECT id, quantitevendu, prixunitaire, medicament_id FROM lignedecommande WHERE commande_id = ?";
        List<LigneDeCommande> lignes = new ArrayList<>();

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int quantiteVendu = rs.getInt("quantite");
                double prixUnitaire = rs.getDouble("prixunitaire");
                int medicamentId = rs.getInt("medicament_id");

                Medicament medicament = medicamentDao.recupererMedicamentParId(medicamentId);
                LigneDeCommande ligne = new LigneDeCommande(id, quantiteVendu, prixUnitaire, medicament);
                lignes.add(ligne);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des lignes de commande : " + e.getMessage());
        }

        return lignes;
    }

    // 🔹 Recuperer une ligne de commande par ID
    public LigneDeCommande recupererLigneParId(int idLigneCommande) {
        String sql = "SELECT id, quantitevendu, prixunitaire, medicament_id FROM lignedecommande WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, idLigneCommande);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                int quantiteVendu = rs.getInt("quantitevendu");
                double prixUnitaire = rs.getDouble("prixunitaire");
                int medicamentId = rs.getInt("medicament_id");

                Medicament medicament = medicamentDao.recupererMedicamentParId(medicamentId);
                return new LigneDeCommande(id, quantiteVendu, prixUnitaire, medicament);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la ligne de commande : " + e.getMessage());
        }

        return null;
    }

    public void ajouterLigneDeCommande(int commandeId, LigneDeCommande ligne) {
        String sql = "INSERT INTO lignedecommande (commande_id, quantitevendu, prixunitaire, medicament_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            stmt.setInt(2, ligne.getQuantiteVendu());
            stmt.setDouble(3, ligne.getPrixUnitaire());
            stmt.setInt(4, ligne.getMedicament().getId());

            stmt.executeUpdate();
            System.out.println("✅ Ligne de commande ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la ligne de commande : " + e.getMessage());
        }
    }


    // 🔹 Mettre a jour la quantite recue
    public void mettreAJourQuantiteRecue(int idLigneCommande, int quantiteRecue) {
        String sql = "UPDATE lignedecommande SET quantitevendu = ? WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(sql)) {
            stmt.setInt(1, quantiteRecue);
            stmt.setInt(2, idLigneCommande);

            int lignesModifiees = stmt.executeUpdate();
            if (lignesModifiees > 0) {
                System.out.println("✅ Quantite mise a jour avec succes !");
            } else {
                System.out.println("❌ Aucune ligne de commande trouvee avec id = " + idLigneCommande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la quantite de la ligne de commande : " + e.getMessage());
        }
    }
}
