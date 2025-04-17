package mcci.businessschool.bts.sio.slam.pharmagest.patient.service;

import mcci.businessschool.bts.sio.slam.pharmagest.patient.Patient;
import mcci.businessschool.bts.sio.slam.pharmagest.patient.dao.PatientDao;

import java.util.List;

public class PatientService {
    private PatientDao patientDao;

    public PatientService() throws Exception {
        this.patientDao = new PatientDao();
    }

    public Integer ajouterPatient(Patient patient) {
        try {
            return patientDao.ajouterPatient(patient);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du patient : " + e.getMessage());
            return null;
        }
    }

    public Patient rechercherPatientParNom(String nom) {
        return patientDao.getPatientByNom(nom);
    }

    // Nouvelles méthodes pour l'autocomplétion
    public List<String> recupererTousLesNomsPatients() {
        return patientDao.getAllPatientNames();
    }

    public List<String> recupererNomsPatientsFiltres(String filtre) {
        return patientDao.getFilteredPatientNames(filtre);
    }

    public List<Patient> rechercherPatientsParNomOuPrenom(String recherche) {
        return patientDao.getPatientsByNomOrPrenom(recherche);
    }
}

