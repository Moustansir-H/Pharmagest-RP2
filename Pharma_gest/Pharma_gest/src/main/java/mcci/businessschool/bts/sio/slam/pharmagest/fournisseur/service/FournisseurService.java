package mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.service;

import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.dao.FournisseurDao;

import java.util.List;

public class FournisseurService {
    private final FournisseurDao fournisseurDao;

    public FournisseurService() throws Exception {
        this.fournisseurDao = new FournisseurDao();
    }

    // 🔹 Récupérer tous les fournisseurs
    public List<Fournisseur> recupererTousLesFournisseurs() {
        return fournisseurDao.recupererTousLesFournisseurs();
    }

    // 🔹 Ajouter un fournisseur
    public void ajouterFournisseur(Fournisseur fournisseur) {
        try {
            fournisseurDao.ajouterFournisseur(fournisseur);
            System.out.println("✅ Fournisseur ajouté avec succès !");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout du fournisseur : " + e.getMessage());
        }
    }
}
