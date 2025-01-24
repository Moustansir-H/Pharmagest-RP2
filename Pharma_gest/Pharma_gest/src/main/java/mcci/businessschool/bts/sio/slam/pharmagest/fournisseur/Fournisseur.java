package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur;

import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;

public class Fournisseur {
    private int id; // Identifiant unique
    private String nom;
    private String adresse;
    private String contact;

    // Constructeurs
    public Fournisseur(int id, String nom, String adresse, String contact) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.contact = contact;
    }

    public Fournisseur(String nom, String adresse, String contact) {
        this.nom = nom;
        this.adresse = adresse;
        this.contact = contact;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    // Méthode pour récupérer le prix d'un médicament (optionnel)
    public double getPrix(Medicament medicament) {
        // TODO Implémentation réelle ou suppression si non nécessaire
        throw new UnsupportedOperationException("Cette méthode n'est pas encore implémentée.");
    }

    @Override
    public String toString() {
        return "Fournisseur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
