package mcci.businessschool.bts.sio.slam.pharmagest.commande;

import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;

public class Commande {
    private int id;
    private double montant;
    private Pharmacien pharmacien;

    public Commande(int id, double montant, Pharmacien pharmacien) {
        this.id = id;
        this.montant = montant;
        this.pharmacien = pharmacien;
    }

    public Commande(double montant, Pharmacien pharmacien) {
        this.montant = montant;
        this.pharmacien = pharmacien;
    }

    public int getId() {
        return id;
    }

    public double getMontant() {
        return montant;
    }

    public Pharmacien getPharmacien() {
        return pharmacien;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public void setPharmacien(Pharmacien pharmacien) {
        this.pharmacien = pharmacien;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", montant=" + montant +
                ", pharmacien=" + pharmacien +
                '}';
    }
}
