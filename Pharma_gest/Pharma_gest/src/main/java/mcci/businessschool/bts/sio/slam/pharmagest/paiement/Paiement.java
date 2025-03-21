package mcci.businessschool.bts.sio.slam.pharmagest.paiement;

public class Paiement {
    private int id; // ✅ Ajout de l'attribut ID
    private double montant;
    private String modePaiement;
    private StatutPaiement statut;
    private int venteId;
    private int vendeurId;

    // ✅ Nouveau constructeur complet avec tous les attributs
    public Paiement(int id, double montant, String modePaiement, StatutPaiement statut, int venteId, int vendeurId) {
        this.id = id;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.venteId = venteId;
        this.vendeurId = vendeurId;
    }

    // ✅ Surcharge du constructeur sans ID (utile pour la création avant insertion en base)
    public Paiement(double montant, String modePaiement, StatutPaiement statut, int venteId, int vendeurId) {
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.venteId = venteId;
        this.vendeurId = vendeurId;
    }

    // ✅ Ancien constructeur (pour compatibilité avec du code existant)
    public Paiement(double montant, String modePaiement, StatutPaiement statut) {
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.statut = statut;
    }

    // ✅ Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
