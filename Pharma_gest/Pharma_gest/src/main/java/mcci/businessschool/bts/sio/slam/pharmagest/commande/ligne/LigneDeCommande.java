package mcci.businessschool.bts.sio.slam.pharmagest.commande.ligne;

import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;

public class LigneDeCommande {
    private int id; // ✅ Ajout de l'ID correctement placé
    private int quantiteVendu;
    private double prixUnitaire;
    private Medicament medicament;

    // ✅ Constructeur sans ID (utile pour l'ajout d'une nouvelle ligne)
    public LigneDeCommande(int quantiteVendu, double prixUnitaire, Medicament medicament) {
        this.quantiteVendu = quantiteVendu;
        this.prixUnitaire = prixUnitaire;
        this.medicament = medicament;
    }

    // ✅ Constructeur avec ID (utile pour la récupération depuis la base de données)
    public LigneDeCommande(int id, int quantiteVendu, double prixUnitaire, Medicament medicament) {
        this.id = id;
        this.quantiteVendu = quantiteVendu;
        this.prixUnitaire = prixUnitaire;
        this.medicament = medicament;
    }

    // ✅ Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Medicament getMedicament() {
        return medicament;
    }

    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
    }
}
