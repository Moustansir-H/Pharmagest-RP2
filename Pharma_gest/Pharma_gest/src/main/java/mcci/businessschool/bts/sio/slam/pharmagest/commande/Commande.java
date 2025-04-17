package mcci.businessschool.bts.sio.slam.pharmagest.commande;

import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private double montantTotal;
    private Fournisseur fournisseur;
    private List<LigneDeCommande> lignesDeCommande;
    private String statut;
    private LocalDateTime dateCreation; // Nouveau champ pour la date de création

    // Constructeur avec ID
    public Commande(int id, double montantTotal, Fournisseur fournisseur, List<LigneDeCommande> lignesDeCommande, String statut) {
        this.id = id;
        this.montantTotal = montantTotal;
        this.fournisseur = fournisseur;
        this.lignesDeCommande = lignesDeCommande;
        this.statut = statut;
        this.dateCreation = LocalDateTime.now(); // Par défaut, date actuelle
    }

    // Constructeur avec ID et date de création
    public Commande(int id, double montantTotal, Fournisseur fournisseur, List<LigneDeCommande> lignesDeCommande, String statut, LocalDateTime dateCreation) {
        this.id = id;
        this.montantTotal = montantTotal;
        this.fournisseur = fournisseur;
        this.lignesDeCommande = lignesDeCommande;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    // Constructeur sans ID (pour les nouvelles commandes)
    public Commande(double montantTotal, Fournisseur fournisseur, List<LigneDeCommande> lignesDeCommande) {
        this.id = -1; // Valeur par défaut pour une nouvelle commande
        this.montantTotal = montantTotal;
        this.fournisseur = fournisseur;
        this.lignesDeCommande = lignesDeCommande;
        this.statut = "En attente"; // Statut par défaut
        this.dateCreation = LocalDateTime.now(); // Date actuelle
    }

    public Commande(Integer id) {
        this.id = id;
        this.montantTotal = 0.0;
        this.fournisseur = null;
        this.lignesDeCommande = new ArrayList<>();
        this.statut = "En attente";
        this.dateCreation = LocalDateTime.now(); // Date actuelle
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

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setLignesDeCommande(List<LigneDeCommande> lignesDeCommande) {
        this.lignesDeCommande = lignesDeCommande;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    // Méthode utilitaire pour obtenir la date de création formatée
    public String getDateCreationFormatee() {
        if (dateCreation == null) {
            return "Non définie";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateCreation.format(formatter);
    }


    public String getFournisseurNom() {
        return fournisseur != null ? fournisseur.getNom() : "Inconnu";
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", montantTotal=" + montantTotal +
                ", fournisseur=" + (fournisseur != null ? fournisseur.getNom() : "Aucun") +
                ", lignesDeCommande=" + (lignesDeCommande != null ? lignesDeCommande.size() : "Aucune") +
                ", statut=" + statut +
                ", dateCreation=" + (dateCreation != null ? dateCreation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "Non définie") +
                '}';
    }
}