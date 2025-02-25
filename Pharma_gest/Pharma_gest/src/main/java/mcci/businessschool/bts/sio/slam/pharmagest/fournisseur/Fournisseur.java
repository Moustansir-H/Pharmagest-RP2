package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur;

import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;

public class Fournisseur {
    private int id; // Identifiant unique
    private String nom;
    private String adresse;
    private String contact;
    private String email; // ✅ Ajout de l'email

    // ✅ Constructeur complet avec email
    public Fournisseur(int id, String nom, String adresse, String contact, String email) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.contact = contact;
        this.email = email;
    }

    public Fournisseur(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.adresse = "Adresse inconnue";
        this.contact = "Contact inconnu";
        this.email = "Email inconnu";
    }

    // ✅ Constructeur sans id (utile pour les nouvelles insertions)
    public Fournisseur(String nom, String adresse, String contact, String email) {
        this.nom = nom;
        this.adresse = adresse;
        this.contact = contact;
        this.email = email;
    }


    // ✅ Getters et Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ✅ Méthode pour récupérer le prix d'un médicament (optionnel)
    public double getPrix(Medicament medicament) {
        throw new UnsupportedOperationException("Cette méthode n'est pas encore implémentée.");
    }

    @Override
    public String toString() {
        return "Fournisseur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' + // ✅ Ajout de l'email dans l'affichage
                '}';
    }
}
