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
}
