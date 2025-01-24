package mcci.businessschool.bts.sio.slam.pharmagest.famille;

public class Famille {
    private int id; // ID unique
    private String nom; // Nom de la famille

    // Constructeurs
    public Famille(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Famille(String nom) {
        this.nom = nom;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Famille{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
