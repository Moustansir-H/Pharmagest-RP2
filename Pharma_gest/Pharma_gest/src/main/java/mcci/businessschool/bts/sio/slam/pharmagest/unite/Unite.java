package mcci.businessschool.bts.sio.slam.pharmagest.unite;

public class Unite {
    private int id; // Identifiant unique
    private String nomUnite;

    // Constructeurs
    public Unite(int id, String nomUnite) {
        this.id = id;
        this.nomUnite = nomUnite;
    }

    public Unite(String nomUnite) {
        this.nomUnite = nomUnite;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomUnite() {
        return nomUnite;
    }

    public void setNomUnite(String nomUnite) {
        this.nomUnite = nomUnite;
    }

    @Override
    public String toString() {
        return "Unite{" +
                "id=" + id +
                ", nomUnite='" + nomUnite + '\'' +
                '}';
    }
}
