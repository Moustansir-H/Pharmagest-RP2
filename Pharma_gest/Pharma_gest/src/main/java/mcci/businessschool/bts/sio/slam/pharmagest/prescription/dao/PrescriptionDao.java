package mcci.businessschool.bts.sio.slam.pharmagest.prescription.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.prescription.Prescription;

import java.sql.*;

public class PrescriptionDao {
    private Connection connection;

    public PrescriptionDao() throws Exception {
        this.connection = DatabaseConnection.getConnexion();
    }

    public Integer ajouterPrescription(Prescription prescription, int patientId) throws SQLException {
        String insertSQL = "INSERT INTO prescription (nommedecin, dateprescription, patient_id) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, prescription.getNomMedecin());
            stmt.setTimestamp(2, new Timestamp(prescription.getDatePrescription().getTime()));
            stmt.setInt(3, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Aucun ID retourné lors de l'ajout de la prescription.");
                }
            }
        }
    }

    // Ajoutez d'autres méthodes de récupération et de mise à jour si nécessaire
}
