package mcci.businessschool.bts.sio.slam.pharmagest.medicament.service;

import mcci.businessschool.bts.sio.slam.pharmagest.famille.dao.FamilleDao;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;

import java.sql.SQLException;
import java.util.List;

public class MedicamentService {
    private MedicamentDao medicamentDAO;
    private FamilleDao familleDAO;
    private FournisseurDao fournisseurDao;

    public MedicamentService() throws Exception {
        this.medicamentDAO = new MedicamentDao();
        this.familleDAO = new FamilleDao();
        this.fournisseurDao = new FournisseurDao();
    }

    public List<Medicament> recupererMedicaments() {
        return medicamentDAO.recupererMedicaments();
    }

    // Ajout de la méthode pour récupérer un médicament par son ID
    public Medicament recupererMedicamentParId(int id) {
        return medicamentDAO.recupererMedicamentParId(id);
    }

    public Integer ajouterMedicament(Medicament medicament) throws SQLException {
        Integer idGenere = medicamentDAO.ajouterMedicament(medicament);
        System.out.println("✅ Médicament ajouté avec ID : " + idGenere);
        return idGenere;
    }

    public void modifierMedicament(Medicament medicament) throws SQLException {
        System.out.println("🔄 Modification du médicament avec ID : " + medicament.getId());

        // 📌 Vérification avant modification
        System.out.println("🆕 Modification - Nouveau Nom : " + medicament.getNom());
        System.out.println("🆕 Modification - Nouvelle Forme : " + medicament.getForme());
        System.out.println("🆕 Modification - Nouveau Prix Achat : " + medicament.getPrixAchat());
        System.out.println("🆕 Modification - Nouveau Prix Vente : " + medicament.getPrixVente());
        System.out.println("🆕 Modification - Nouveau Stock : " + medicament.getStock());
        System.out.println("🆕 Modification - Nouveau Seuil Commande : " + medicament.getSeuilCommande());
        System.out.println("🆕 Modification - Nouvelle Quantité Max : " + medicament.getQteMax());
        System.out.println("📌 ID Famille récupéré : " + medicament.getFamille().getId());
        System.out.println("📌 ID Fournisseur récupéré : " + medicament.getFournisseur().getId());

        medicamentDAO.modifierMedicament(medicament);

        System.out.println("✅ Modification envoyée à la base de données !");
    }


    public void supprimerMedicamentParId(Integer id) throws SQLException {
        System.out.println("🗑️ Suppression du médicament avec ID : " + id);
        medicamentDAO.supprimerMedicamentParId(id);
        System.out.println("✅ Médicament supprimé avec succès !");
    }

}
