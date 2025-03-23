package mcci.businessschool.bts.sio.slam.pharmagest.facture.service;

import mcci.businessschool.bts.sio.slam.pharmagest.facture.Facture;
import mcci.businessschool.bts.sio.slam.pharmagest.facture.dao.FactureDao;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;

public class FactureService {
    private FactureDao factureDao;

    public FactureService() throws Exception {
        this.factureDao = new FactureDao();
    }

    public Integer genererEtEnregistrerFacture(Vente vente, Facture facture) {
        try {
            return factureDao.ajouterFacture(facture, vente);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la facture : " + e.getMessage());
            return null;
        }
    }
}
