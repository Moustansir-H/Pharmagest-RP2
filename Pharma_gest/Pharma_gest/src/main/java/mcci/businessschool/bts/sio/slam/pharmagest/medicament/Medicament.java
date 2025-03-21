package mcci.businessschool.bts.sio.slam.pharmagest.medicament;

import mcci.businessschool.bts.sio.slam.pharmagest.famille.Famille;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;

public class Medicament {
    private int id;
    private String nom;
    private String forme;
    private double prixAchat;
    private double prixVente;
    private int stock;
    private int seuilCommande;
    private int qteMax;
    private Famille famille;
    private Fournisseur fournisseur;

    // Constructeur sans Unite
    public Medicament(int id, String nom, String forme, double prixAchat, double prixVente, int stock,
                      int seuilCommande, int qteMax, Famille famille, Fournisseur fournisseur) {
        this.id = id;
        this.nom = nom;
        this.forme = forme;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.stock = stock;
        this.seuilCommande = seuilCommande;
        this.qteMax = qteMax;
        this.famille = famille;
        this.fournisseur = fournisseur;
    }

    public Medicament(String nom, String forme, double prixAchat, double prixVente, int stock,
                      int seuilCommande, int qteMax, Famille famille, Fournisseur fournisseur) {
        this.id = -1;
        this.nom = nom;
        this.forme = forme;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.stock = stock;
        this.seuilCommande = seuilCommande;
        this.qteMax = qteMax;
        this.famille = famille;
        this.fournisseur = fournisseur;
    }

    // Suppression des références à Unite

    // Getters et setters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getForme() {
        return forme;
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public int getStock() {
        return stock;
    }

    public int getSeuilCommande() {
        return seuilCommande;
    }

    public int getQteMax() {
        return qteMax;
    }

    public Famille getFamille() {
        return famille;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }

    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setSeuilCommande(int seuilCommande) {
        this.seuilCommande = seuilCommande;
    }

    public void setQteMax(int qteMax) {
        this.qteMax = qteMax;
    }

    public void setFamille(Famille famille) {
        this.famille = famille;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public String getFamilleNom() {
        return (famille != null) ? famille.getNom() : "Aucune";
    }

    public String getFournisseurNom() {
        return (fournisseur != null) ? fournisseur.getNom() : "Aucun";
    }


    @Override
    public String toString() {
        return "Medicament{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", forme='" + forme + '\'' +
                ", prixAchat=" + prixAchat +
                ", prixVente=" + prixVente +
                ", stock=" + stock +
                ", seuilCommande=" + seuilCommande +
                ", qteMax=" + qteMax +
                ", famille=" + (famille != null ? famille.getNom() : "Aucune") +
                ", fournisseur=" + (fournisseur != null ? fournisseur.getNom() : "Aucun") +
                '}';
    }
}
