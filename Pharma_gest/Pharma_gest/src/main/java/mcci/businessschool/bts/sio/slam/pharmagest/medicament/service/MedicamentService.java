package mcci.businessschool.bts.sio.slam.pharmagest.medicament.service;

import mcci.businessschool.bts.sio.slam.pharmagest.famille.dao.FamilleDao;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDAO;
import mcci.businessschool.bts.sio.slam.pharmagest.unite.dao.UniteDao;

import java.util.List;

public class MedicamentService {
    private MedicamentDAO medicamentDAO;
    private FamilleDao familleDAO;
    private FournisseurDao fournisseurDao;
    private UniteDao uniteDao;

    public MedicamentService() throws Exception {
        this.medicamentDAO = new MedicamentDAO();
        this.familleDAO = new FamilleDao();
        this.fournisseurDao = new FournisseurDao();
        this.uniteDao = new UniteDao();
    }

    public List<Medicament> recupererMedicaments() {

        return medicamentDAO.recupererMedicaments();
    }

    public void ajouterMedicament(Medicament medicament) {

    }

    public void modifierMedicament(Medicament medicament) {

    }

    public void supprimerMedicamentParId(Integer id) {

    }
}
