package mcci.businessschool.bts.sio.slam.pharmagest.vente.service;

import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.dao.LigneVenteDao;

import java.sql.SQLException;
import java.util.List;

public class LigneVenteService {
    private LigneVenteDao ligneVenteDao;

    public LigneVenteService() throws Exception {
        this.ligneVenteDao = new LigneVenteDao();
    }

    public Integer ajouterLigneVente(LigneVente ligne) {
        try {
            return ligneVenteDao.ajouterLigneVente(ligne);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la ligne de vente : " + e.getMessage());
            return null;
        }
    }

    public List<LigneVente> recupererLignesParVente(int venteId) {
        try {
            return ligneVenteDao.recupererLignesParVente(venteId);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des lignes de vente : " + e.getMessage());
            return null;
        }
    }

    // Ajoutez d'autres méthodes de logique métier au besoin
}
