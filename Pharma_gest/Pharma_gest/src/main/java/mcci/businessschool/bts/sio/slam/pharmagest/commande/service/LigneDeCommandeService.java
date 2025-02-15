package mcci.businessschool.bts.sio.slam.pharmagest.commande.service;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.LigneDeCommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.ligne.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;

import java.sql.SQLException;
import java.util.List;

public class LigneDeCommandeService {
    private final LigneDeCommandeDao ligneDeCommandeDao;
    private final MedicamentDao medicamentDao;

    public LigneDeCommandeService() throws Exception {
        this.ligneDeCommandeDao = new LigneDeCommandeDao();
        this.medicamentDao = new MedicamentDao();
    }

    // 🔹 Recuperer les lignes d'une commande
    public List<LigneDeCommande> recupererLignesParCommande(int idCommande) {
        return ligneDeCommandeDao.recupererLignesParCommande(idCommande);
    }

    public void ajouterLigneDeCommande(int commandeId, LigneDeCommande ligne) {
        ligneDeCommandeDao.ajouterLigneDeCommande(commandeId, ligne);
    }


    // 🔹 Mettre a jour la quantite recue et les prix si besoin
    public void mettreAJourQuantiteRecue(int idLigneCommande, int quantiteRecue, double prixAchat, double prixVente) {
        try {
            // Recuperer la ligne de commande
            LigneDeCommande ligne = ligneDeCommandeDao.recupererLigneParId(idLigneCommande);
            if (ligne == null) {
                System.out.println("❌ Ligne de commande introuvable !");
                return;
            }

            // Recuperer le medicament associe
            Medicament medicament = ligne.getMedicament();
            if (medicament == null) {
                System.out.println("❌ Medicament introuvable !");
                return;
            }

            // Ajouter la quantite recue au stock du medicament
            int nouveauStock = medicament.getStock() + quantiteRecue;
            medicament.setStock(nouveauStock);

            // Mettre a jour le prix d'achat et de vente si necessaire
            if (prixAchat > 0) medicament.setPrixAchat(prixAchat);
            if (prixVente > 0) medicament.setPrixVente(prixVente);

            // Enregistrer les modifications en base
            medicamentDao.modifierMedicament(medicament);
            ligneDeCommandeDao.mettreAJourQuantiteRecue(idLigneCommande, quantiteRecue);

            System.out.println("✅ Quantite et prix mis a jour avec succes !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la ligne de commande : " + e.getMessage());
        }
    }
}
