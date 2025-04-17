package mcci.businessschool.bts.sio.slam.pharmagest.vente.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LigneVenteDao {
    private Connection connection;
    private MedicamentDao medicamentDao;

    public LigneVenteDao() throws Exception {
        this.connection = DatabaseConnection.getConnexion();
        this.medicamentDao = new MedicamentDao();
    }

    public Integer ajouterLigneVente(LigneVente ligne) throws SQLException {
        String insertSQL = "INSERT INTO lignevente (quantitevendu, prixunitaire, vente_id, medicament_id) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setInt(1, ligne.getQuantiteVendu());
            stmt.setDouble(2, ligne.getPrixUnitaire());
            stmt.setInt(3, ligne.getVenteId());
            stmt.setInt(4, ligne.getMedicament().getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    return id;
                } else {
                    throw new SQLException("Aucun ID retourné lors de l'ajout de la ligne de vente.");
                }
            }
        }
    }

    public List<LigneVente> recupererLignesParVente(int venteId) throws SQLException {
        String selectSQL = "SELECT id, quantitevendu, prixunitaire, vente_id, medicament_id FROM lignevente WHERE vente_id = ?";
        List<LigneVente> lignes = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, venteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int quantite = rs.getInt("quantitevendu");
                    double prix = rs.getDouble("prixunitaire");
                    int medId = rs.getInt("medicament_id");
                    // Récupérer l'objet Médicament via MedicamentDao
                    Medicament medicament = medicamentDao.recupererMedicamentParId(medId);
                    if (medicament == null) {
                        System.err.println("Erreur : Aucun médicament trouvé pour l'ID " + medId);
                    }
                    LigneVente ligne = new LigneVente(quantite, prix, medicament);
                    ligne.setVenteId(venteId);
                    lignes.add(ligne);
                }
            }
        }
        return lignes;
    }
}
