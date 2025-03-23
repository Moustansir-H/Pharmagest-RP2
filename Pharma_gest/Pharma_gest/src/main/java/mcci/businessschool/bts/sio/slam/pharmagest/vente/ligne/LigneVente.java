package mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne;

import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;

public class LigneVente {
    private int quantiteVendu;
    private double prixUnitaire;
    private Medicament medicament;
    // Ajout de l'attribut pour stocker l'ID de la vente associée
    private int venteId;

    public LigneVente(int quantiteVendu, double prixUnitaire, Medicament medicament) {
        this.quantiteVendu = quantiteVendu;
        this.prixUnitaire = prixUnitaire;
        this.medicament = medicament;
    }

    // Getters et setters existants
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

    // Nouveaux accesseurs pour venteId
    public int getVenteId() {
        return venteId;
    }

    public void setVenteId(int venteId) {
        this.venteId = venteId;
    }
}
