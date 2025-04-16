package mcci.businessschool.bts.sio.slam.pharmagest.patient;

import java.util.Date;

public class Patient {
    private Integer id;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String adresse;
    private String contact;

    // Constructeur complet
    public Patient(String nom, String prenom, Date dateNaissance, String adresse, String contact) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.contact = contact;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
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

    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}

