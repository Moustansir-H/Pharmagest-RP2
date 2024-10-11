package mcci.businessschool.bts.sio.slam.pharmagest.vente;

import mcci.businessschool.bts.sio.slam.pharmagest.facture.Facture;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.Paiement;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.Vendeur;

import java.util.Date;

public class Vente {
    private Date dateVente;
    private double montantTotal;
    private TypeVente typeVente;
    private Vendeur vendeur;
    private Facture facture;
    private Paiement paiement;

    public Vente(Date dateVente, double montantTotal, TypeVente typeVente, Vendeur vendeur) {
        this.dateVente = dateVente;
        this.montantTotal = montantTotal;
        this.typeVente = typeVente;
        this.vendeur = vendeur;
    }

    public void effectuerVente() {
        // TODO Implémentation de l'effectuation de la vente
    }

    // Getters et setters


    public Date getDateVente() {
        return dateVente;
    }

    public void setDateVente(Date dateVente) {
        this.dateVente = dateVente;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public TypeVente getTypeVente() {
        return typeVente;
    }

    public void setTypeVente(TypeVente typeVente) {
        this.typeVente = typeVente;
    }

    public Vendeur getVendeur() {
        return vendeur;
    }

    public void setVendeur(Vendeur vendeur) {
        this.vendeur = vendeur;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }
}
