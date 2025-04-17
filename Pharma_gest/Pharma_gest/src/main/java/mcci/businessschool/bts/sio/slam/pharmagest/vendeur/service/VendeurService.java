package mcci.businessschool.bts.sio.slam.pharmagest.vendeur.service;

import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.dao.VendeurDao;

public class VendeurService {

    private VendeurDao vendeurDao;

    public VendeurService() throws Exception {
        this.vendeurDao = new VendeurDao();
    }


    public void ajouterVendeur(Integer idUtilisateur) {
        vendeurDao.ajouterVendeur(idUtilisateur);
    }

    public void supprimerVendeur(Integer idUtilisateur) {
        vendeurDao.supprimerVendeur(idUtilisateur);
    }

    public boolean verifierExistenceVendeur(Integer idUtilisateur) {
        return vendeurDao.existeVendeur(idUtilisateur);
    }

    /*
    public Vendeur recupererVendeurParUtilisateurId(Integer idUtilisateur) {
        return vendeurDao.recupererVendeurParUtilisateurId(idUtilisateur);
    }*/

}


