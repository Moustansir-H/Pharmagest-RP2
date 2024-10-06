package mcci.businessschool.bts.sio.slam.pharmagest.prescription;

import java.util.Date;

public class Prescription {
    private String nomMedecin;
    private Date datePrescription;

    public Prescription(String nomMedecin, Date datePrescription) {
        this.nomMedecin = nomMedecin;
        this.datePrescription = datePrescription;
    }

    public String getNumeroPrescription() {
        // TODO Implémentation pour obtenir le numéro de prescription
        return "Numero de prescription";
    }

    // Getters et setters


    public String getNomMedecin() {
        return nomMedecin;
    }

    public void setNomMedecin(String nomMedecin) {
        this.nomMedecin = nomMedecin;
    }

    public Date getDatePrescription() {
        return datePrescription;
    }

    public void setDatePrescription(Date datePrescription) {
        this.datePrescription = datePrescription;
    }
}
