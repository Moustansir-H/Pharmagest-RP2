package mcci.businessschool.bts.sio.slam.pharmagest.vente.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.Vendeur;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.TypeVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteDao {
    private final Connection baseDeDonneeConnexion;

    public VenteDao() throws Exception {
        this.baseDeDonneeConnexion = DatabaseConnection.getConnexion();
    }

    public List<Vente> recupererVentes() {
        String selectSQL = "SELECT id, datevente, montanttotal, typevente, vendeur_id FROM vente";
        List<Vente> ventes = new ArrayList<>();

        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                Date dateVente = rs.getDate("datevente");
                double montantTotal = rs.getDouble("montanttotal");
                TypeVente typeVente = TypeVente.valueOf(rs.getString("typevente"));
                Vendeur vendeur = new Vendeur(rs.getInt("vendeur_id"), "", "");

                Vente vente = new Vente(dateVente, montantTotal, typeVente, vendeur);
                vente.setId(id);
                ventes.add(vente);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des ventes : " + e.getMessage());
        }

        return ventes;
    }

    public Vente recupererVenteParId(int id) throws SQLException {
        String selectSQL = "SELECT id, datevente, montanttotal, typevente, vendeur_id FROM vente WHERE id = ?";
        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int venteId = rs.getInt("id");
                    Date dateVente = rs.getDate("datevente");
                    double montantTotal = rs.getDouble("montanttotal");
                    // Convertir la valeur récupérée en type enum
                    TypeVente typeVente = TypeVente.valueOf(rs.getString("typevente"));
                    // Création d'un objet Vendeur avec l'ID récupéré (les autres attributs peuvent être complétés ultérieurement)
                    int vendeurId = rs.getInt("vendeur_id");
                    Vendeur vendeur = new Vendeur(vendeurId, "", "");

                    Vente vente = new Vente(dateVente, montantTotal, typeVente, vendeur);
                    vente.setId(venteId);
                    return vente;
                }
            }
        }
        return null; // Si aucune vente n'est trouvée pour cet ID
    }


    public Integer ajouterVente(Vente vente) throws SQLException {
        String insertSQL = "INSERT INTO vente (datevente, montanttotal, typevente, vendeur_id) VALUES (?, ?, ?::typevente, ?) RETURNING id";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(insertSQL)) {
            stmt.setDate(1, new java.sql.Date(vente.getDateVente().getTime()));
            stmt.setDouble(2, vente.getMontantTotal());
            stmt.setString(3, vente.getTypeVente().name());

            // Si le vendeur est null, on envoie une valeur SQL NULL
            if (vente.getVendeur() != null) {
                stmt.setInt(4, vente.getVendeur().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    vente.setId(id);
                    System.out.println("Vente ajoutée avec succès !");
                    return id;
                } else {
                    throw new SQLException("Erreur lors de l'ajout de la vente, aucun ID retourné.");
                }
            }
        }
    }


    public boolean supprimerVenteParId(Integer id) {
        String deleteSQL = "DELETE FROM vente WHERE id = ?";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);

            int ligneSupprimee = stmt.executeUpdate();

            if (ligneSupprimee > 0) {
                System.out.println("Vente supprimée avec succès !");
            } else {
                System.out.println("Aucune vente trouvée avec id = " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la vente : " + e.getMessage());
        }
        return false;
    }

    public void modifierVente(Vente vente) {
        String updateSQL = "UPDATE vente SET datevente = ?, montanttotal = ?, typevente = ?::typevente, vendeur_id = ? WHERE id = ?";


        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(updateSQL)) {
            stmt.setDate(1, new java.sql.Date(vente.getDateVente().getTime()));
            stmt.setDouble(2, vente.getMontantTotal());
            stmt.setString(3, vente.getTypeVente().name());
            stmt.setInt(4, vente.getVendeur().getId());
            stmt.setInt(5, vente.getId());

            int lignesModifiees = stmt.executeUpdate();

            if (lignesModifiees > 0) {
                System.out.println("Vente modifiée avec succès !");
            } else {
                System.out.println("Aucune vente trouvée avec id = " + vente.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la vente : " + e.getMessage());
        }
    }

    public List<Vente> recupererVentesEnAttente() {
        List<Vente> ventesEnAttente = new ArrayList<>();
        String sql = """
                    SELECT id, datevente, montanttotal, typevente, vendeur_id
                    FROM vente
                    WHERE vendeur_id IS NULL
                """;

        try (Statement stmt = baseDeDonneeConnexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                Date dateVente = rs.getDate("datevente");  // ou getTimestamp si tu veux l'heure
                double montantTotal = rs.getDouble("montanttotal");
                TypeVente typeVente = TypeVente.valueOf(rs.getString("typevente"));
                int vendeurId = rs.getInt("vendeur_id"); // ce sera 0 si NULL, selon la config

                // Créer l'objet Vente
                // Pour l'instant, on ne récupère pas la facture ni le paiement
                // On suppose qu'on aura un setFacture(...) plus tard si besoin
                Vente vente = new Vente(dateVente, montantTotal, typeVente, null);
                vente.setId(id);

                // On pourrait récupérer la facture, etc., si nécessaire
                ventesEnAttente.add(vente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventesEnAttente;
    }

/*
    public static void main(String[] args) {
        try {
            // Initialisation des services d'intégration et de médicament
            VenteIntegrationService integrationService = new VenteIntegrationService();
            MedicamentService medicamentService = new MedicamentService();

            // Simuler la phase Pharmacien :
            // On suppose que le médicament avec ID 1 existe dans la base
            Medicament med = medicamentService.recupererMedicamentParId(32);
            if (med == null) {
                System.out.println("Médicament non trouvé, test annulé.");
                return;
            }

            // Créer une ligne de vente pour 2 unités de ce médicament
            LigneVente ligne = new LigneVente(2, med.getPrixVente(), med);
            // On ajoute cette ligne à une liste
            List<LigneVente> lignes = new ArrayList<>();
            lignes.add(ligne);

            // Log : le Pharmacien (ID 10) initie la création de la vente
            System.out.println("Pharmacien avec ID 10 crée la vente.");
            // Ici, la vente est créée sans vendeur (le champ vendeur sera null)
            Vente vente = integrationService.creerVentePharmacien(lignes, TypeVente.LIBRE);
            System.out.println("Vente créée avec ID : " + vente.getId() +
                    " | Facture générée : " + vente.getFacture().getNumeroFacture());

            // Simuler la phase Vendeur (Caissier) :
            // Log : le Vendeur (ID 4) finalise le paiement
            System.out.println("Vendeur avec ID 4 finalise le paiement.");
            // Création d'un paiement pour la vente
            Paiement paiement = new Paiement(vente.getMontantTotal(), "Espèces", StatutPaiement.VALIDE);
            // Finaliser le paiement en associant le vendeur avec l'ID 4
            integrationService.finaliserPaiementVendeur(vente.getId(), paiement, 4);
            System.out.println("Paiement validé et vente finalisée pour la vente ID : " + vente.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
