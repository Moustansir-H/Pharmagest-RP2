package mcci.businessschool.bts.sio.slam.pharmagest.livraison;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;

import java.util.Date;

public class Livraison {
    // ✅ Ajouter les attributs manquants
    private int id;  // ID de la livraison
    private Date datelivraison;
    private String status;
    private Commande commande;
    private Fournisseur fournisseur;

    // ✅ Constructeur simple
    public Livraison(Date datelivraison, String status) {
        this.datelivraison = datelivraison;
        this.status = status;
    }

    // ✅ Constructeur complet avec ID, Commande et Fournisseur
    public Livraison(int id, Date datelivraison, String status, Commande commande, Fournisseur fournisseur) {
        this.id = id;
        this.datelivraison = datelivraison;
        this.status = status;
        this.commande = commande;
        this.fournisseur = fournisseur;
    }

    // ✅ Méthodes pour gérer la livraison
    public void suivreLivraison() {
        // TODO Implémentation du suivi de la livraison
    }

    public void recevoirCommande() {
        // TODO Implémentation de la réception de la commande
    }

    public void mettreAJourStock() {
        // TODO Implémentation de la mise à jour du stock
    }

    // ✅ Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDatelivraison() {
        return datelivraison;
    }

    public void setDatelivraison(Date datelivraison) {
        this.datelivraison = datelivraison;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }
}
