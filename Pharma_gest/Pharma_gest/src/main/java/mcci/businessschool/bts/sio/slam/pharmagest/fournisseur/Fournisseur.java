package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur;

import javafx.beans.property.*;

public class Fournisseur {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty adresse;
    private final StringProperty contact;
    private final StringProperty email;

    // ✅ Constructeur principal
    public Fournisseur(int id, String nom, String adresse, String contact, String email) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.adresse = new SimpleStringProperty(adresse);
        this.contact = new SimpleStringProperty(contact);
        this.email = new SimpleStringProperty(email);
    }

    // ✅ Constructeur sans ID (utile pour les nouvelles insertions)
    public Fournisseur(String nom, String adresse, String contact, String email) {
        this.id = new SimpleIntegerProperty(0); // Valeur temporaire
        this.nom = new SimpleStringProperty(nom);
        this.adresse = new SimpleStringProperty(adresse);
        this.contact = new SimpleStringProperty(contact);
        this.email = new SimpleStringProperty(email);
    }

    // ✅ Constructeur simplifié avec ID et nom
    public Fournisseur(int id, String nom) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.adresse = new SimpleStringProperty("Adresse inconnue");
        this.contact = new SimpleStringProperty("Contact inconnu");
        this.email = new SimpleStringProperty("Email inconnu");
    }

    // ✅ Getters et Setters avec JavaFX Properties
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public String getAdresse() {
        return adresse.get();
    }

    public void setAdresse(String adresse) {
        this.adresse.set(adresse);
    }

    public StringProperty adresseProperty() {
        return adresse;
    }

    public String getContact() {
        return contact.get();
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    // ✅ Affichage pour debugging
    @Override
    public String toString() {
        return "Fournisseur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", adresse='" + getAdresse() + '\'' +
                ", contact='" + getContact() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
