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

    @Override
    public String toString() {
        return "Pharmacien{" +
                "identifiant='" + super.getIdentifiant() + '\'' +
                ", motDePasse='" + super.getMotDePasse() + '\'' +
                '}';
    }

    public void validerVente() {
        // TODO Implémentation de la validation de vente
    }

    public void validerCommande() {
        // TODO Implémentation de la validation de commande
    }

    public void verifierApprovisionnement() {
        // TODO Implémentation de la vérification d'approvisionnement
    }

    public void modifierQuantiteCommande() {
        // TODO Implémentation de la modification de la quantité de commande
    }
}
