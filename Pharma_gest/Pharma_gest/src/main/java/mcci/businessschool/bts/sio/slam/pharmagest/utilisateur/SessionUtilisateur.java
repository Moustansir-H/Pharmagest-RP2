package mcci.businessschool.bts.sio.slam.pharmagest.utilisateur;

public class SessionUtilisateur {
    private static SessionUtilisateur instance;
    private Utilisateur utilisateurConnecte;

    private SessionUtilisateur() {
    }

    public static SessionUtilisateur getInstance() {
        if (instance == null) {
            instance = new SessionUtilisateur();
        }
        return instance;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void setUtilisateurConnecte(Utilisateur utilisateurConnecte) {
        this.utilisateurConnecte = utilisateurConnecte;
    }
}
