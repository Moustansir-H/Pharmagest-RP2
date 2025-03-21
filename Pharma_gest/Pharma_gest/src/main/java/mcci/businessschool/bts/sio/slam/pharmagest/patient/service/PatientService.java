package mcci.businessschool.bts.sio.slam.pharmagest.patient.service;

import mcci.businessschool.bts.sio.slam.pharmagest.patient.Patient;
import mcci.businessschool.bts.sio.slam.pharmagest.patient.dao.PatientDao;

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

}
