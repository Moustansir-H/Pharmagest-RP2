package mcci.businessschool.bts.sio.slam.pharmagest.utilisateur;

public abstract class Utilisateur {
    private Integer id;
    private String identifiant;
    private String motDePasse;
    private Role role;


    public Utilisateur(Integer id, String identifiant, String motDePasse) {
        this.id = id;
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
        this.role = Role.PHARMACIEN;
    }

    public Utilisateur(String identifiant, String motDePasse) {
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
        this.role = Role.PHARMACIEN;
    }

    public Utilisateur(String identifiant, String motDePasse, Role role) {
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public Utilisateur() {
        this.id = 0;
        this.identifiant = null;
        this.motDePasse = null;
        this.role = Role.PHARMACIEN;
    }

    public Utilisateur(Integer id, String identifiant, String motDePasse, Role role) {
        this.id = id;
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void seConnecter() {

        // TODO Implémentation de la connexion

    }

    public void deconnecter() {
        // TODO Implémentation de la déconnexion
    }


}
