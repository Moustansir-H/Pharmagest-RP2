package mcci.businessschool.bts.sio.slam.pharmagest.paiement;

public class Paiement {
    private double montant;
    private String modePaiement;
    private StatutPaiement statut;
    // Ajout de l'attribut pour lier le paiement à une vente
    private int venteId;
    private int vendeurId;

    public Paiement(double montant, String modePaiement, StatutPaiement statut) {
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.statut = statut;
    }

    public void effectuerPaiement() {
        // TODO Implémentation de l'effectuation du paiement
    }

    // Getters et setters existants
    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    // Nouveaux accesseurs pour venteId
    public int getVenteId() {
        return venteId;
    }

    public void setVenteId(int venteId) {
        this.venteId = venteId;
    }

    public int getVendeurId() {
        return vendeurId;
    }

    public void setVendeurId(int vendeurId) {
        this.vendeurId = vendeurId;
    }
}
