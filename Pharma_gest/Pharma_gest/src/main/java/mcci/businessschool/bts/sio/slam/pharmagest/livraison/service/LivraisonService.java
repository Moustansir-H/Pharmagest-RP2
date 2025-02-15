package mcci.businessschool.bts.sio.slam.pharmagest.livraison.service;

import mcci.businessschool.bts.sio.slam.pharmagest.livraison.Livraison;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.dao.LivraisonDao;

import java.util.List;

public class LivraisonService {
    private final LivraisonDao livraisonDao;

    public LivraisonService() throws Exception {
        this.livraisonDao = new LivraisonDao();
    }

    // 🔹 Récupérer toutes les livraisons
    public List<Livraison> recupererToutesLesLivraisons() {
        return livraisonDao.recupererToutesLesLivraisons();
    }

    // 🔹 Récupérer une livraison par son ID
    public Livraison recupererLivraisonParId(int idLivraison) {
        return livraisonDao.recupererLivraisonParId(idLivraison);
    }

    // 🔹 Ajouter une nouvelle livraison avec une commande et un fournisseur
    public Integer ajouterLivraison(Livraison livraison, int commandeId, int fournisseurId) {
        return livraisonDao.ajouterLivraison(livraison, commandeId, fournisseurId);
    }

    // 🔹 Mettre à jour le statut d'une livraison
    public void mettreAJourStatutLivraison(int idLivraison, String nouveauStatut) {
        livraisonDao.mettreAJourStatutLivraison(idLivraison, nouveauStatut);
    }

    
}
