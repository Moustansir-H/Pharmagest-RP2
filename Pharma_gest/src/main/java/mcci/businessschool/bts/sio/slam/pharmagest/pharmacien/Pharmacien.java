package mcci.businessschool.bts.sio.slam.pharmagest.pharmacien;

import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Utilisateur;

public class Pharmacien extends Utilisateur {
    public Pharmacien(String identifiant, String motDePasse) {
        super(identifiant, motDePasse);
    }

    public Pharmacien() {
    }

    @Override
    public String toString() {
        return "Pharmacien{" +
                "identifiant='" + super.getIdentifiant() + '\'' +
                ", motDePasse='" + super.getMotDePasse() + '\'' +
                '}';
    }

}
