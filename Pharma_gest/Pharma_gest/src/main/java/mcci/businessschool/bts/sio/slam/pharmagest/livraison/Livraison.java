package mcci.businessschool.bts.sio.slam.pharmagest.livraison;

import java.util.Date;

public class Livraison {
    private int id;
    private Date dateLivraison;
    private String status;
    private int commandeId;
    private int fournisseurId;

    // Constructeur vide
    public Livraison() {
    }

    // Constructeur avec tous les attributs (avec id)
    public Livraison(int id, Date dateLivraison, String status, int commandeId, int fournisseurId) {
        this.id = id;
        this.dateLivraison = dateLivraison;
        this.status = status;
        this.commandeId = commandeId;
        this.fournisseurId = fournisseurId;
    }

    // Constructeur sans id (pour les nouvelles insertions)
    public Livraison(Date dateLivraison, String status, int commandeId, int fournisseurId) {
        this.dateLivraison = dateLivraison;
        this.status = status;
        this.commandeId = commandeId;
        this.fournisseurId = fournisseurId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }

    public int getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(int fournisseurId) {
        this.fournisseurId = fournisseurId;
    }
}
