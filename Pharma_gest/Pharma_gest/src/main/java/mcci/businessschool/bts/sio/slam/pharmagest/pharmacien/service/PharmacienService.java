package mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.service;

import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.dao.PharmacienDao;

public class PharmacienService {

    private PharmacienDao pharmacienDao;

    public PharmacienService() throws Exception {
        this.pharmacienDao = new PharmacienDao();
    }


    public void ajouterPharmacien(Integer idUtilisateur) {
        pharmacienDao.ajouterPharmacien(idUtilisateur);
    }

    public void supprimerPharmacien(Integer idUtilisateur) {
        pharmacienDao.supprimerPharmacien(idUtilisateur);
    }


}
