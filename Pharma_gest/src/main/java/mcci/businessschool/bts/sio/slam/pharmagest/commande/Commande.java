package mcci.businessschool.bts.sio.slam.pharmagest.commande;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.ligne.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;

import java.util.List;

public class Commande {
    private double montant;
    private List<LigneDeCommande> lignesDeCommande;
    private Fournisseur fournisseur;

    public Commande(double montant, Fournisseur fournisseur) {
        this.montant = montant;
        this.fournisseur = fournisseur;
    }

    public void genererCommande() {
        // TODO Implémentation de la génération de commande
    }

    public void validerCommande() {
        // TODO Implémentation de la validation de commande
    }

    public void envoyerCommandePDF() {
        // TODO Implémentation pour envoyer la commande en PDF
    }

    // Getters et setters

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public List<LigneDeCommande> getLignesDeCommande() {
        return lignesDeCommande;
    }

    public void setLignesDeCommande(List<LigneDeCommande> lignesDeCommande) {
        this.lignesDeCommande = lignesDeCommande;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }
}
