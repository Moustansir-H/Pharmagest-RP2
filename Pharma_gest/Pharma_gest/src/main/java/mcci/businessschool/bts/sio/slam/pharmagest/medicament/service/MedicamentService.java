package mcci.businessschool.bts.sio.slam.pharmagest.medicament.service;

import mcci.businessschool.bts.sio.slam.pharmagest.famille.dao.FamilleDao;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;
import mcci.businessschool.bts.sio.slam.pharmagest.unite.dao.UniteDao;

import java.util.List;

public class MedicamentService {
    private MedicamentDao medicamentDAO;
    private FamilleDao familleDAO;
    private FournisseurDao fournisseurDao;
    private UniteDao uniteDao;

    public MedicamentService() throws Exception {
        this.medicamentDAO = new MedicamentDao();
        this.familleDAO = new FamilleDao();
        this.fournisseurDao = new FournisseurDao();
        this.uniteDao = new UniteDao();
    }

    public List<Medicament> recupererMedicaments() {
        return medicamentDAO.recupererMedicaments();
    }

    // Ajout de la méthode pour récupérer un médicament par son ID
    public Medicament recupererMedicamentParId(int id) {
        return medicamentDAO.recupererMedicamentParId(id);
    }

    public void ajouterMedicament(Medicament medicament) {
        // Implémentation à ajouter
    }

    public void modifierMedicament(Medicament medicament) {
        // Implémentation à ajouter
    }

    public void supprimerMedicamentParId(Integer id) {
        // Implémentation à ajouter
    }
}
