package mcci.businessschool.bts.sio.slam.pharmagest.vendeur;

import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Utilisateur;

public class Vendeur extends Utilisateur {

    public Vendeur(String identifiant, String motDePasse) {
        super(identifiant, motDePasse);
    }

    public Vendeur() {
    }

    @Override
    public String toString() {
        return "Vendeur{" +
                "identifiant='" + super.getIdentifiant() + '\'' +
                ", motDePasse='" + super.getMotDePasse() + '\'' +
                '}';
    }

    public void effectuerVente() {
        // TODO Implémentation de l'effectuation de vente
    }

    public void gererPaiement() {
        // TODO Implémentation de la gestion du paiement
    }
}
