package mcci.businessschool.bts.sio.slam.pharmagest.livraison;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;

import java.time.LocalDate;

public class Livraison {
    private int id;
    private LocalDate dateLivraison;
    private String status;
    private Commande commande;
    private Fournisseur fournisseur;

    // ✅ Constructeurs
    public Livraison(int id, LocalDate dateLivraison, String status, Commande commande, Fournisseur fournisseur) {
        this.id = id;
        this.dateLivraison = dateLivraison;
        this.status = status;
        this.commande = commande;
        this.fournisseur = fournisseur;
    }

    public Livraison(LocalDate dateLivraison, String status, Commande commande, Fournisseur fournisseur) {
        this.dateLivraison = dateLivraison;
        this.status = status;
        this.commande = commande;
        this.fournisseur = fournisseur;
    }

    // ✅ Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(LocalDate dateLivraison) {
        this.dateLivraison = dateLivraison;
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

    // ✅ toString()
    @Override
    public String toString() {
        return "Livraison{" +
                "id=" + id +
                ", dateLivraison=" + dateLivraison +
                ", status='" + status + '\'' +
                ", commandeId=" + (commande != null ? commande.getId() : "Inconnue") +
                ", fournisseur=" + (fournisseur != null ? fournisseur.getNom() : "Inconnu") +
                '}';
    }
}
