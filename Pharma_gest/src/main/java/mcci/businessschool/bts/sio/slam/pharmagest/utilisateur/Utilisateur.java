package mcci.businessschool.bts.sio.slam.pharmagest.utilisateur;

public abstract class Utilisateur {
    private String identifiant;
    private String motDePasse;

    public Utilisateur(String identifiant, String motDePasse) {
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
    }

    public Utilisateur() {
        this.identifiant = null;
        this.motDePasse = null;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public void seConnecter() {

        // TODO Implémentation de la connexion

    }

    public void deconnecter() {
        // TODO Implémentation de la déconnexion
    }
}
