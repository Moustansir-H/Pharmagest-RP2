package mcci.businessschool.bts.sio.slam.pharmagest.medicament;

import mcci.businessschool.bts.sio.slam.pharmagest.famille.Famille;
import mcci.businessschool.bts.sio.slam.pharmagest.unite.Unite;

public class Medicament {
    private String nom;
    private String forme;
    private double prixAchat;
    private double prixVente;
    private int stock;
    private int seuilCommande;
    private int qteMax;
    private Famille famille;
    private Unite unite;

    // Constructeur et getters/setters

    public Medicament(String nom, String forme, double prixAchat, double prixVente, int stock,
                      int seuilCommande, int qteMax, Famille famille, Unite unite) {
        this.nom = nom;
        this.forme = forme;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.stock = stock;
        this.seuilCommande = seuilCommande;
        this.qteMax = qteMax;
        this.famille = famille;
        this.unite = unite;
    }

    // Getters et setters pour chaque attribut

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getForme() {
        return forme;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getSeuilCommande() {
        return seuilCommande;
    }

    public void setSeuilCommande(int seuilCommande) {
        this.seuilCommande = seuilCommande;
    }

    public int getQteMax() {
        return qteMax;
    }

    public void setQteMax(int qteMax) {
        this.qteMax = qteMax;
    }

    public Famille getFamille() {
        return famille;
    }

    public void setFamille(Famille famille) {
        this.famille = famille;
    }

    public Unite getUnite() {
        return unite;
    }

    public void setUnite(Unite unite) {
        this.unite = unite;
    }
}
