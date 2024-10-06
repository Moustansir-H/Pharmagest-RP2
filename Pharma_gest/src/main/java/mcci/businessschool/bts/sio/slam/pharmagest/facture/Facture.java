package mcci.businessschool.bts.sio.slam.pharmagest.facture;

import java.util.Date;

public class Facture {
    private Date dateEmission;
    private double montantTotal;
    private String numeroFacture;

    public Facture(Date dateEmission, double montantTotal, String numeroFacture) {
        this.dateEmission = dateEmission;
        this.montantTotal = montantTotal;
        this.numeroFacture = numeroFacture;
    }

    public void genererFacture() {
        // TODO Implémentation de la génération de facture
    }

    // Getters et setters


    public Date getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(Date dateEmission) {
        this.dateEmission = dateEmission;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }
}
