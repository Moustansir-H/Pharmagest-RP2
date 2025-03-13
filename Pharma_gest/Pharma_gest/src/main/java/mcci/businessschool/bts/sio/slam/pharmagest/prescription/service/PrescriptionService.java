package mcci.businessschool.bts.sio.slam.pharmagest.prescription.service;

import mcci.businessschool.bts.sio.slam.pharmagest.prescription.Prescription;
import mcci.businessschool.bts.sio.slam.pharmagest.prescription.dao.PrescriptionDao;

public class PrescriptionService {
    private PrescriptionDao prescriptionDao;

    public PrescriptionService() throws Exception {
        this.prescriptionDao = new PrescriptionDao();
    }

    public Integer ajouterPrescription(Prescription prescription, int patientId) {
        try {
            return prescriptionDao.ajouterPrescription(prescription, patientId);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de la prescription : " + e.getMessage());
            return null;
        }
    }
}
