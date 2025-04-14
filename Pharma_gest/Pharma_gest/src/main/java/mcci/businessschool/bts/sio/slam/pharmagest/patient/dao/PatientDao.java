package mcci.businessschool.bts.sio.slam.pharmagest.patient.dao;

import mcci.businessschool.bts.sio.slam.pharmagest.database.DatabaseConnection;
import mcci.businessschool.bts.sio.slam.pharmagest.patient.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDao {
    private Connection connection;

    public PatientDao() throws Exception {
        this.connection = DatabaseConnection.getConnexion();
    }

    public Patient getPatientByNom(String nom) {
        String query = "SELECT * FROM patient WHERE LOWER(nom) = LOWER(?) LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Récupérer les valeurs du ResultSet
                String patientNom = rs.getString("nom");
                String patientPrenom = rs.getString("prenom");
                java.sql.Date patientDateNaissance = rs.getDate("datenaissance");
                String patientAdresse = rs.getString("adresse");
                String patientContact = rs.getString("contact");

                // Afficher les valeurs pour le débogage
                System.out.println("Patient trouvé dans la base de données:");
                System.out.println("Nom: " + patientNom);
                System.out.println("Prénom: " + patientPrenom);
                System.out.println("Date naissance: " + patientDateNaissance);
                System.out.println("Adresse: " + patientAdresse);
                System.out.println("Contact: " + patientContact);

                // Créer et retourner l'objet Patient
                return new Patient(
                        patientNom,
                        patientPrenom,
                        patientDateNaissance,
                        patientAdresse,
                        patientContact
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du patient : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
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

    // Nouvelles méthodes pour l'autocomplétion
    public List<String> getAllPatientNames() {
        List<String> noms = new ArrayList<>();
        String query = "SELECT nom FROM patient ORDER BY nom";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                noms.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des noms de patients : " + e.getMessage());
        }

        return noms;
    }

    public List<String> getFilteredPatientNames(String filtre) {
        List<String> noms = new ArrayList<>();
        String query = "SELECT nom FROM patient WHERE LOWER(nom) LIKE LOWER(?) ORDER BY nom";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + filtre + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    noms.add(rs.getString("nom"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des noms de patients filtrés : " + e.getMessage());
        }

        return noms;
    }

    public List<Patient> getPatientsByNomOrPrenom(String recherche) {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patient WHERE LOWER(nom) LIKE LOWER(?) OR LOWER(prenom) LIKE LOWER(?) ORDER BY nom, prenom";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String param = "%" + recherche + "%";
            stmt.setString(1, param);
            stmt.setString(2, param);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = new Patient(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getDate("datenaissance"),
                            rs.getString("adresse"),
                            rs.getString("contact")
                    );
                    patients.add(patient);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de patients : " + e.getMessage());
        }

        return patients;
    }
}

