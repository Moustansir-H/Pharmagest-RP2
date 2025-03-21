package mcci.businessschool.bts.sio.slam.pharmagest.commande.service;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.CommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.LigneDeCommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;
import mcci.businessschool.bts.sio.slam.pharmagest.livraison.service.LivraisonService;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;
import mcci.businessschool.bts.sio.slam.pharmagest.pharmacien.Pharmacien;

import java.sql.SQLException;
import java.util.*;

public class CommandeService {
    private CommandeDao commandeDao;
    private LigneDeCommandeDao ligneDeCommandeDao;
    private MedicamentDao medicamentDao;
    private LivraisonService livraisonService;

    public CommandeService() throws Exception {
        this.commandeDao = new CommandeDao();
        this.ligneDeCommandeDao = new LigneDeCommandeDao();
        this.medicamentDao = new MedicamentDao();
        this.livraisonService = new LivraisonService();
    }

    public int ajouterCommande(Commande commande) throws SQLException {
        if (commande.getMontantTotal() < 0) {
            throw new IllegalArgumentException("Le montant total doit être positif.");
        }
        return commandeDao.ajouterCommande(commande);
    }

    public List<Commande> recupererToutesLesCommandes() throws SQLException {
        return commandeDao.recupererToutesLesCommandes();
    }

    public void validerCommande(int commandeId) throws SQLException {
        List<LigneDeCommande> lignes = ligneDeCommandeDao.recupererLignesParCommande(commandeId);
        if (lignes.isEmpty()) {
            throw new SQLException("Impossible de valider une commande sans lignes de commande.");
        }

        double montantTotal = lignes.stream()
                .mapToDouble(ligne -> ligne.getQuantiteVendu() * ligne.getPrixUnitaire())
                .sum();

        commandeDao.mettreAJourMontantCommande(commandeId, montantTotal);

        Commande commande = commandeDao.recupererCommandeParId(commandeId);
        if (commande.getFournisseur() == null) {
            throw new SQLException("Erreur : Le fournisseur de la commande ID " + commandeId + " est introuvable.");
        }
        livraisonService.creerLivraison(commande);
        System.out.println("Commande ID " + commandeId + " validée avec un montant total de " + montantTotal + " €.");
    }

    public Map<Fournisseur, List<LigneDeCommande>> preparerNouvelleCommande() throws SQLException {
        Map<Fournisseur, List<LigneDeCommande>> commandesParFournisseur = new HashMap<>();
        List<Fournisseur> fournisseurs = commandeDao.recupererFournisseursAvecMedicamentsSousSeuil();

        for (Fournisseur fournisseur : fournisseurs) {
            List<LigneDeCommande> lignes = medicamentDao.recupererMedicamentsSousSeuilParFournisseur(fournisseur.getId());
            commandesParFournisseur.put(fournisseur, lignes);
        }
        return commandesParFournisseur;
    }

    public int ajouterCommandeAvecLignes(Pharmacien pharmacien, Fournisseur fournisseur, List<LigneDeCommande> lignes) throws SQLException {
        if (lignes.isEmpty()) {
            throw new SQLException("Impossible de créer une commande sans lignes de commande.");
        }

        Commande nouvelleCommande = new Commande(0.0, pharmacien, fournisseur, new ArrayList<>());
        int commandeId = commandeDao.ajouterCommande(nouvelleCommande);

        for (LigneDeCommande ligne : lignes) {
            ligne.setCommande(new Commande(commandeId));
            ligneDeCommandeDao.ajouterLigneDeCommande(ligne);
        }
        return commandeId;
    }
}
