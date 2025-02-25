package mcci.businessschool.bts.sio.slam.pharmagest.commande;

import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;

import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private double montantTotal;
    private Pharmacien pharmacien;
    private Fournisseur fournisseur;
    private List<LigneDeCommande> lignesDeCommande;
    private String statut;

    // Constructeur avec ID
    public Commande(int id, double montantTotal, Pharmacien pharmacien, Fournisseur fournisseur, List<LigneDeCommande> lignesDeCommande, String statut) {
        this.id = id;
        this.montantTotal = montantTotal;
        this.pharmacien = pharmacien;
        this.fournisseur = fournisseur;
        this.lignesDeCommande = lignesDeCommande;
        this.statut = statut;
    }

    // Constructeur sans ID (pour les nouvelles commandes)
    public Commande(double montantTotal, Pharmacien pharmacien, Fournisseur fournisseur, List<LigneDeCommande> lignesDeCommande) {
        this.id = -1; // Valeur par défaut pour une nouvelle commande
        this.montantTotal = montantTotal;
        this.pharmacien = pharmacien;
        this.fournisseur = fournisseur;
        this.lignesDeCommande = lignesDeCommande;

    }

    public Commande(Integer id) {
        this.id = id;
        this.montantTotal = 0.0;
        this.pharmacien = null;
        this.fournisseur = null;
        this.lignesDeCommande = new ArrayList<>();
        this.statut = "En attente";
    }


    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Pharmacien getPharmacien() {
        return pharmacien;
    }

    public void setPharmacien(Pharmacien pharmacien) {
        this.pharmacien = pharmacien;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public List<LigneDeCommande> getLignesDeCommande() {
        return lignesDeCommande;
    }

    public String getStatut() {
        return statut;
    }

    public void setLignesDeCommande(List<LigneDeCommande> lignesDeCommande) {
        this.lignesDeCommande = lignesDeCommande;
    }

    public String getPharmacienNom() {
        return pharmacien != null ? pharmacien.getIdentifiant() : "Inconnu";
    }

    public String getFournisseurNom() {
        return fournisseur != null ? fournisseur.getNom() : "Inconnu";
    }


    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", montantTotal=" + montantTotal +
                ", pharmacien=" + (pharmacien != null ? pharmacien.getIdentifiant() : "Aucun") +
                ", fournisseur=" + (fournisseur != null ? fournisseur.getNom() : "Aucun") +
                ", lignesDeCommande=" + (lignesDeCommande != null ? lignesDeCommande.size() : "Aucune") +
                '}';
    }
}
