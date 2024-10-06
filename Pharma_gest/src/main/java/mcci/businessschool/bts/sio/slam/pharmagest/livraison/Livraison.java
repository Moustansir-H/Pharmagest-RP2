package mcci.businessschool.bts.sio.slam.pharmagest.livraison;

import java.util.Date;

public class Livraison {
    private Date dateLivraison;
    private String status;

    public Livraison(Date dateLivraison, String status) {
        this.dateLivraison = dateLivraison;
        this.status = status;
    }

    public void suivreLivraison() {
        // TODO Implémentation du suivi de la livraison
    }

    public void recevoirCommande() {
        // TODO Implémentation de la réception de la commande
    }

    public void mettreAJourStock() {
        // TODO Implémentation de la mise à jour du stock
    }

    // Getters et setters


    public Date getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(Date dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
