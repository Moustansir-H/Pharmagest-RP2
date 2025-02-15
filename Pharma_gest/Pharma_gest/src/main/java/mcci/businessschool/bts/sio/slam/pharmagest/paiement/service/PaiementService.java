package mcci.businessschool.bts.sio.slam.pharmagest.paiement.service;

import mcci.businessschool.bts.sio.slam.pharmagest.paiement.Paiement;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.StatutPaiement;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.dao.PaiementDao;

import java.sql.SQLException;
import java.util.List;

public class PaiementService {
    private PaiementDao paiementDAO;

    public PaiementService() throws Exception {
        this.paiementDAO = new PaiementDao();
    }

    /**
     * Ajouter un paiement
     */
    public Integer ajouterPaiement(Paiement paiement) {
        try {
            return paiementDAO.ajouterPaiement(paiement);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du paiement : " + e.getMessage());
            return null;
        }
    }

    /**
     * Récupérer un paiement par ID
     */
    public Paiement getPaiementById(int id) {
        return paiementDAO.getPaiementById(id);
    }

    /**
     * Récupérer tous les paiements
     */
    public List<Paiement> getAllPaiements() {
        return paiementDAO.getAllPaiements();
    }

    /**
     * Mettre à jour le statut d'un paiement
     */
    public boolean mettreAJourStatutPaiement(int id, StatutPaiement statut) {
        return paiementDAO.mettreAJourStatutPaiement(id, statut);
    }
}
