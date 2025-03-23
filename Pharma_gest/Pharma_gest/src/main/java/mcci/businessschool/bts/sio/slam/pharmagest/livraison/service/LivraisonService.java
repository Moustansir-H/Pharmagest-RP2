package mcci.businessschool.bts.sio.slam.pharmagest.livraison.service;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.Livraison;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.dao.LivraisonDao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class LivraisonService {
    private LivraisonDao livraisonDao;

    public LivraisonService() throws Exception {
        this.livraisonDao = new LivraisonDao();
    }

    public List<Livraison> recupererToutesLesLivraisons() throws SQLException {
        return livraisonDao.recupererToutesLesLivraisons();
    }


    /**
     * Crée une livraison pour la commande validée.
     * La livraison est initialisée avec la date actuelle et un statut "En attente".
     *
     * @param commande La commande validée.
     * @return L'ID de la livraison créée.
     * @throws SQLException Si une erreur survient lors de l'insertion en base.
     */
    public int creerLivraison(Commande commande) throws SQLException {
        if (commande == null) {
            throw new IllegalArgumentException("La commande ne doit pas être null.");
        }
        if (commande.getId() <= 0) {
            throw new IllegalArgumentException("La commande doit avoir un ID valide.");
        }
        // Récupérer l'ID du fournisseur depuis la commande
        int fournisseurId = commande.getFournisseur() != null ? commande.getFournisseur().getId() : -1;
        if (fournisseurId <= 0) {
            throw new SQLException("Fournisseur invalide pour la commande " + commande.getId());
        }
        // Créer une nouvelle livraison avec la date actuelle et le statut "En attente"
        Livraison livraison = new Livraison(new Date(), "En attente", commande.getId(), fournisseurId);
        return livraisonDao.creerLivraison(livraison);
    }

    // Méthode pour mettre à jour le statut d'une livraison
    public void updateStatutLivraison(int livraisonId, String nouveauStatut) throws SQLException {
        livraisonDao.mettreAJourStatutLivraison(livraisonId, nouveauStatut);
    }
}
