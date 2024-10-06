package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur;

import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;

public class Fournisseur {
    private String nom;
    private String adresse;
    private String contact;

    public Fournisseur(String nom, String adresse, String contact) {
        this.nom = nom;
        this.adresse = adresse;
        this.contact = contact;
    }

    public double getPrix(Medicament medicament) {
        // TODO Implémentation pour obtenir le prix d'un médicament
        return 0.0;
    }

    // Getters et setters

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
