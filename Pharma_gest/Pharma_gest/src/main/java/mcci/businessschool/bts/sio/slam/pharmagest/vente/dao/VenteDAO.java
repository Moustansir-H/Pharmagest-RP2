package mcci.businessschool.bts.sio.slam.pharmagest.vente.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.Vendeur;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.TypeVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteDAO {
    private Connection baseDeDonneeConnexion;

    public VenteDAO() throws Exception {
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

    public Integer ajouterVente(Vente vente) throws SQLException {
        String insertSQL = "INSERT INTO vente (datevente, montanttotal, typevente, vendeur_id) VALUES (?, ?, ?::typevente, ?) RETURNING id";

        try (PreparedStatement stmt = baseDeDonneeConnexion.prepareStatement(insertSQL)) {
            stmt.setDate(1, new java.sql.Date(vente.getDateVente().getTime()));
            stmt.setDouble(2, vente.getMontantTotal());
            stmt.setString(3, vente.getTypeVente().name()); // Convertit l'énumération en String pour l'insertion
            stmt.setInt(4, vente.getVendeur().getId());

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
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
        String updateSQL = "UPDATE vente SET datevente = ?, montanttotal = ?, typevente = ?, vendeur_id = ? WHERE id = ?";

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
}
