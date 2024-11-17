package mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.service;

import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.service.PharmacienService;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Role;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.Utilisateur;
import mcci.businessschool.bts.sio.slam.pharmagest.utilisateur.dao.UtilisateurDao;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.service.VendeurService;

import java.sql.SQLException;
import java.util.List;

public class UtilisateurService {

    private UtilisateurDao utilisateurDao;
    private PharmacienService pharmacienService;
    private VendeurService vendeurService;


    public UtilisateurService() throws Exception {
        this.utilisateurDao = new UtilisateurDao();
        this.pharmacienService = new PharmacienService();
        this.vendeurService = new VendeurService();
    }


    // Méthode pour charger les utilisateurs depuis la base de données
    public List<Utilisateur> recupererUtilisateurs() {

        return utilisateurDao.recupererUtilisateurs();


    }

    public void ajouterUtilisateur(Utilisateur utilisateur) {
        try {
            Integer idUtilisateur = utilisateurDao.ajouterUtilisateur(utilisateur);
            if (utilisateur.getRole().equals(Role.PHARMACIEN)) {
                pharmacienService.ajouterPharmacien(idUtilisateur);
            } else {
                vendeurService.ajouterVendeur(idUtilisateur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'utilisateur : " + e.getMessage());
        }
    }

    public void supprimerUtilisateurParId(Integer id) {
        pharmacienService.supprimerPharmacien(id);
        vendeurService.supprimerVendeur(id);
        utilisateurDao.supprimerUtilisateurParId(id);
    }

    public void modifierUtilisateur(Utilisateur utilisateur) {
        Integer idUtilisateur = utilisateur.getId();

        pharmacienService.supprimerPharmacien(idUtilisateur);
        vendeurService.supprimerVendeur(idUtilisateur);
        utilisateurDao.modifierUtilisateur(utilisateur);

        if (utilisateur.getRole().equals(Role.PHARMACIEN)) {
            pharmacienService.ajouterPharmacien(idUtilisateur);
        } else {
            vendeurService.ajouterVendeur(idUtilisateur);
        }
    }


}
