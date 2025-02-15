package mcci.businessschool.bts.sio.slam.pharmagest.medicament;

import mcci.businessschool.bts.sio.slam.pharmagest.famille.Famille;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.unite.Unite;

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
    private Unite unite;

    // Constructeur avec id
    public Medicament(int id, String nom, String forme, double prixAchat, double prixVente, int stock,
                      int seuilCommande, int qteMax, Famille famille, Fournisseur fournisseur, Unite unite) {
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
        this.unite = unite;
    }

    // Constructeur et getters/setters=

    public Medicament(String nom, String forme, double prixAchat, double prixVente, int stock,
                      int seuilCommande, int qteMax, Famille famille, Fournisseur fournisseur, Unite unite) {
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
        this.unite = unite;
    }

    // Getters et setters pour chaque attribut

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


    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public Unite getUnite() {
        return unite;
    }

    public void setUnite(Unite unite) {
        this.unite = unite;
    }

    @Override
    public String toString() {
        return "Medicament{" +
                "id=" + id + // Ajout de l'id dans le toString
                ", nom='" + nom + '\'' +
                ", forme='" + forme + '\'' +
                ", prixAchat=" + prixAchat +
                ", prixVente=" + prixVente +
                ", stock=" + stock +
                ", seuilCommande=" + seuilCommande +
                ", qteMax=" + qteMax +
                ", famille=" + (famille != null ? famille.getNom() : "Aucune") +
                ", fournisseur=" + (fournisseur != null ? fournisseur.getNom() : "Aucun") +
                ", unite=" + (unite != null ? unite.getNomUnite() : "Aucune") +
                '}';
    }
}
