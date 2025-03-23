package mcci.businessschool.bts.sio.slam.pharmagest.vente.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.Paiement;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.StatutPaiement;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.dao.PaiementDao;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.service.PaiementService;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.Vendeur;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.TypeVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.service.VenteService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteDao {
    private final Connection baseDeDonneeConnexion;
    private final PaiementDao paiementDao;

    public VenteDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
        this.paiementDao = new PaiementDao();
    }

    /**
     * ✅ Récupère toutes les ventes et leur paiement associé.
     */
    public List<Vente> recupererVentes() {
        String selectSQL = "SELECT id, datevente, montanttotal, typevente, vendeur_id FROM vente";
        List<Vente> ventes = new ArrayList<>();

        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                Vente vente = extraireVenteDepuisResultSet(rs);
                ventes.add(vente);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des ventes : " + e.getMessage());
        }
        return ventes;
    }

    /**
     * ✅ Récupère une vente par son ID avec son paiement associé.
     */
    public Vente recupererVenteParId(int id) throws SQLException {
        String selectSQL = "SELECT id, datevente, montanttotal, typevente, vendeur_id FROM vente WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extraireVenteDepuisResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * ✅ Ajoute une nouvelle vente dans la base de données et retourne son ID.
     */
    public Integer ajouterVente(Vente vente) throws SQLException {
        String insertSQL = "INSERT INTO vente (datevente, montanttotal, typevente, vendeur_id) VALUES (?, ?, ?::typevente, ?) RETURNING id";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(insertSQL)) {
            stmt.setDate(1, new java.sql.Date(vente.getDateVente().getTime()));
            stmt.setDouble(2, vente.getMontantTotal());
            stmt.setString(3, vente.getTypeVente().name());

            // ✅ Vérifier si le vendeur est présent avant d'insérer son ID
            if (vente.getVendeur() != null) {
                stmt.setInt(4, vente.getVendeur().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    vente.setId(id);
                    System.out.println("✅ Vente ajoutée avec succès, ID : " + id);
                    return id;
                } else {
                    throw new SQLException("❌ Erreur : Aucun ID retourné lors de l'ajout de la vente.");
                }
            }
        }
    }


    /**
     * ✅ Met à jour une vente existante.
     */
    public void modifierVente(Vente vente) throws SQLException {
        String updateSQL = "UPDATE vente SET datevente = ?, montanttotal = ?, typevente = ?::typevente, vendeur_id = ? WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(updateSQL)) {
            stmt.setDate(1, new java.sql.Date(vente.getDateVente().getTime()));
            stmt.setDouble(2, vente.getMontantTotal());
            stmt.setString(3, vente.getTypeVente().name());
            stmt.setObject(4, (vente.getVendeur() != null) ? vente.getVendeur().getId() : null, Types.INTEGER);
            stmt.setInt(5, vente.getId());

            int lignesModifiees = stmt.executeUpdate();
            if (lignesModifiees > 0) {
                System.out.println("✅ Vente modifiée avec succès !");
            } else {
                System.out.println("❌ Aucune vente trouvée avec ID : " + vente.getId());
            }
        }
    }

    /**
     * ✅ Supprime une vente par son ID.
     */
    public boolean supprimerVenteParId(Integer id) {
        String deleteSQL = "DELETE FROM vente WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);

            int ligneSupprimee = stmt.executeUpdate();
            if (ligneSupprimee > 0) {
                System.out.println("✅ Vente supprimée avec succès !");
                return true;
            } else {
                System.out.println("❌ Aucune vente trouvée avec ID = " + id);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la vente : " + e.getMessage());
        }
        return false;
    }

    /**
     * ✅ Récupère les ventes en attente de validation (paiement non validé).
     */
    public List<Vente> recupererVentesEnAttente() {
        List<Vente> ventesEnAttente = new ArrayList<>();
        String sql = """
                SELECT v.id, v.datevente, v.montanttotal, v.typevente, v.vendeur_id
                FROM vente v
                LEFT JOIN paiement p ON v.id = p.vente_id
                WHERE p.statut IS NULL OR p.statut = 'EN_ATTENTE'
                """;

        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vente vente = extraireVenteDepuisResultSet(rs);
                ventesEnAttente.add(vente);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des ventes en attente : " + e.getMessage());
        }
        return ventesEnAttente;
    }

    /**
     * ✅ Fonction utilitaire pour extraire une vente d'un ResultSet.
     */
    private Vente extraireVenteDepuisResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Date dateVente = rs.getDate("datevente");
        double montantTotal = rs.getDouble("montanttotal");
        TypeVente typeVente = TypeVente.valueOf(rs.getString("typevente"));
        Vendeur vendeur = (rs.getInt("vendeur_id") != 0) ? new Vendeur(rs.getInt("vendeur_id"), "Inconnu", "Inconnu") : null;

        Vente vente = new Vente(dateVente, montantTotal, typeVente, vendeur);
        vente.setId(id);

        // ✅ Associer le paiement via PaiementDao
        Paiement paiement = paiementDao.getPaiementByVenteId(id);
        vente.setPaiement(paiement);

        return vente;
    }

    public static void main(String[] args) {
        try {
            VenteService venteService = new VenteService();
            PaiementService paiementService = new PaiementService(); // ✅ Ajout du service paiement

            // ✅ ID de la vente à vérifier (modifie si nécessaire)
            int idVente = 32;

            // ✅ Récupération de la vente
            Vente vente = venteService.recupererVenteParId(idVente);

            if (vente != null) {
                System.out.println("\n✅ Vente trouvée !");
                System.out.println("🆔 ID Vente : " + vente.getId());
                System.out.println("💰 Montant Total : " + vente.getMontantTotal());
                System.out.println("📌 Type Vente : " + vente.getTypeVente());
                System.out.println("👤 Vendeur ID : " + (vente.getVendeur() != null ? vente.getVendeur().getId() : "Non attribué"));

                // ✅ Récupération du paiement associé
                Paiement paiement = paiementService.getPaiementByVenteId(idVente);

                if (paiement != null) {
                    System.out.println("💳 Paiement trouvé !");
                    System.out.println("🆔 ID Paiement : " + paiement.getId());
                    System.out.println("💰 Montant payé : " + paiement.getMontant() + "€");
                    System.out.println("📌 Mode : " + paiement.getModePaiement());
                    System.out.println("🕒 Statut Paiement : " + paiement.getStatut());

                    if (paiement.getStatut() == StatutPaiement.VALIDE) {
                        System.out.println("🎉 ✅ La vente est bien associée à un paiement validé !");
                    } else {
                        System.out.println("⚠️ ❌ Attention : Le statut du paiement n'est pas encore mis à jour !");
                    }
                } else {
                    System.out.println("⚠️ Aucun paiement trouvé pour cette vente !");
                }

            } else {
                System.out.println("❌ Vente introuvable !");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération de la vente et du paiement : " + e.getMessage());
        }
    }

}
