package mcci.businessschool.bts.sio.slam.pharmagest.commande;

import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;

public class LigneDeCommande {
    private int id;
    private Commande commande;
    private Medicament medicament;
    private int quantiteVendu;
    private double prixUnitaire;
    private int quantiteRecue;
    private double prixAchatReel;
    private double prixVenteReel;

    // Constructeur avec ID (pour récupération depuis la base de données)
    public LigneDeCommande(int id, Commande commande, Medicament medicament, int quantiteVendu, double prixUnitaire, int quantiteRecue, double prixAchatReel, double prixVenteReel) {
        this.id = id;
        this.commande = commande;
        this.medicament = medicament;
        this.quantiteVendu = quantiteVendu;
        this.prixUnitaire = prixUnitaire;
        this.quantiteRecue = quantiteRecue;
        this.prixAchatReel = prixAchatReel;
        this.prixVenteReel = prixVenteReel;
    }

    // Constructeur sans ID (pour création de nouvelles lignes de commande)
    public LigneDeCommande(Commande commande, Medicament medicament, int quantiteVendu, double prixUnitaire, int quantiteRecue, double prixAchatReel, double prixVenteReel) {
        this.id = -1; // Valeur par défaut pour une nouvelle ligne
        this.commande = commande;
        this.medicament = medicament;
        this.quantiteVendu = quantiteVendu;
        this.prixUnitaire = prixUnitaire;
        this.quantiteRecue = quantiteRecue;
        this.prixAchatReel = prixAchatReel;
        this.prixVenteReel = prixVenteReel;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
    }

    public int getQuantiteVendu() {
        return quantiteVendu;
    }

    public void setQuantiteVendu(int quantiteVendu) {
        this.quantiteVendu = quantiteVendu;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public int getQuantiteRecue() {
        return quantiteRecue;
    }

    public void setQuantiteRecue(int quantiteRecue) {
        this.quantiteRecue = quantiteRecue;
    }

    public double getPrixAchatReel() {
        return prixAchatReel;
    }

    public void setPrixAchatReel(double prixAchatReel) {
        this.prixAchatReel = prixAchatReel;
    }

    public double getPrixVenteReel() {
        return prixVenteReel;
    }

    public void setPrixVenteReel(double prixVenteReel) {
        this.prixVenteReel = prixVenteReel;
    }

    @Override
    public String toString() {
        return "LigneDeCommande{" +
                "id=" + id +
                ", commandeId=" + (commande != null ? commande.getId() : "Aucune") +
                ", medicament=" + (medicament != null ? medicament.getNom() : "Aucun") +
                ", quantiteVendu=" + quantiteVendu +
                ", prixUnitaire=" + prixUnitaire +
                ", quantiteRecue=" + quantiteRecue +
                ", prixAchatReel=" + prixAchatReel +
                ", prixVenteReel=" + prixVenteReel +
                '}';
    }
}
