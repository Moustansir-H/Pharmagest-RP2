package mcci.businessschool.bts.sio.slam.pharmagest.patient.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.patient.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatientDao {
    private Connection connection;

    public PatientDao() throws Exception {
        this.connection = DatabaseConnection.getConnexion();
    }

    public Integer ajouterPatient(Patient patient) throws SQLException {
        String insertSQL = "INSERT INTO patient (nom, prenom, datenaissance, adresse, contact) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, patient.getNom());
            stmt.setString(2, patient.getPrenom());
            stmt.setDate(3, new java.sql.Date(patient.getDateNaissance().getTime()));
            stmt.setString(4, patient.getAdresse());
            stmt.setString(5, patient.getContact());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Aucun ID retourné lors de l'ajout du patient.");
                }
            }
        }
    }

    // Ajoutez les méthodes de récupération, mise à jour, suppression si nécessaire
}
