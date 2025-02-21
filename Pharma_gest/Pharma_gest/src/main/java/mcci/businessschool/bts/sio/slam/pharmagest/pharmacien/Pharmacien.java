package mcci.businessschool.bts.sio.slam.pharmagest.pharmacien;

import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Utilisateur;

public class Pharmacien extends Utilisateur {

    public Pharmacien(Integer id, String identifiant, String motDePasse) {
        super(id, identifiant, motDePasse);
    }

    public Pharmacien() {
    }

    public Pharmacien(String identifiant, String motDePasse) {
        super(identifiant, motDePasse);
    }

    public Pharmacien(Integer id) {
        super(id, "Inconnu", "Inconnu");
    }

    public Pharmacien(Integer id, String identifiant) {
        super(id, identifiant, "Inconnu"); // Mot de passe par défaut
    }


    @Override
    public String toString() {
        return "Pharmacien{" +
                "id=" + super.getId() +  // Ajout de l'ID
                ", identifiant='" + super.getIdentifiant() + '\'' +
                ", motDePasse='" + super.getMotDePasse() + '\'' +
                '}';
    }

    // 🚀 Implémentation potentielle des méthodes
    public void validerVente(int venteId) {
        System.out.println("Vente " + venteId + " validée par le pharmacien " + getIdentifiant());
        // TODO : Ajouter l'interaction avec la classe Vente si existante
    }

    public void validerCommande(int commandeId) {
        System.out.println("Commande " + commandeId + " validée par le pharmacien " + getIdentifiant());
        // TODO : Ajouter l'interaction avec la classe Commande
    }

    public void verifierApprovisionnement() {
        System.out.println("Vérification de l'approvisionnement en cours...");
        // TODO : Ajouter une interaction avec les stocks de médicaments
    }

    public void modifierQuantiteCommande(int commandeId, int nouvelleQuantite) {
        System.out.println("Commande " + commandeId + " modifiée avec la nouvelle quantité : " + nouvelleQuantite);
        // TODO : Ajouter l'interaction avec la commande pour modifier sa quantité
    }
}
