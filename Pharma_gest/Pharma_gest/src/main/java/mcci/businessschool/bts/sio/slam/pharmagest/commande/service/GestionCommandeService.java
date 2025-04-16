package mcci.businessschool.bts.sio.slam.pharmagest.commande.service;

import mcci.businessschool.bts.sio.slam.pharmagest.commande.Commande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.CommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.dao.LigneDeCommandeDao;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestionCommandeService {
    private final CommandeDao commandeDao;
    private final LigneDeCommandeDao ligneDeCommandeDao;
    private final MedicamentDao medicamentDao;

    public GestionCommandeService() throws Exception {
        this.commandeDao = new CommandeDao();
        this.ligneDeCommandeDao = new LigneDeCommandeDao();
        this.medicamentDao = new MedicamentDao();
    }

    /**
     * Récupère toutes les commandes avec leurs lignes
     * @return Liste des commandes complètes
     */
    public List<Commande> recupererToutesLesCommandes() throws SQLException {
        List<Commande> commandes = commandeDao.recupererToutesLesCommandes();

        // Pour chaque commande, récupérer ses lignes
        for (Commande commande : commandes) {
            List<LigneDeCommande> lignes = ligneDeCommandeDao.recupererLignesParCommande(commande.getId());
            commande.setLignesDeCommande(lignes);
        }

        return commandes;
    }

    /**
     * Récupère les commandes en attente de confirmation avec leurs lignes
     * @return Liste des commandes en attente
     */
    public List<Commande> recupererCommandesEnAttente() throws SQLException {
        List<Commande> commandes = commandeDao.recupererCommandesEnAttente();

        // Pour chaque commande, récupérer ses lignes
        for (Commande commande : commandes) {
            List<LigneDeCommande> lignes = ligneDeCommandeDao.recupererLignesParCommande(commande.getId());
            commande.setLignesDeCommande(lignes);
        }

        return commandes;
    }

    /**
     * Récupère une commande complète par son ID
     * @param commandeId ID de la commande
     * @return La commande avec ses lignes ou null si non trouvée
     */
    public Commande recupererCommandeComplete(int commandeId) throws SQLException {
        Commande commande = commandeDao.recupererCommandeParId(commandeId);
        if (commande != null) {
            List<LigneDeCommande> lignes = ligneDeCommandeDao.recupererLignesParCommande(commandeId);
            commande.setLignesDeCommande(lignes);
        }
        return commande;
    }


    /**
     * Confirme une commande et met à jour les stocks
     * @param commandeId ID de la commande à confirmer
     */
    public void confirmerCommande(int commandeId) throws SQLException {
        try {
            // Appeler la fonction PostgreSQL pour valider la commande
            commandeDao.confirmerCommande(commandeId);
            System.out.println("✅ Commande #" + commandeId + " confirmée avec succès");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la confirmation de la commande: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Supprime une commande et ses lignes
     * @param commandeId ID de la commande à supprimer
     */
    public void supprimerCommande(int commandeId) throws SQLException {
        try {
            commandeDao.supprimerCommande(commandeId);
            System.out.println("✅ Commande #" + commandeId + " supprimée avec succès");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la commande: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Récupère les statistiques des commandes automatiques
     * @return Map contenant diverses statistiques
     */
    public Map<String, Object> getStatistiquesCommandesAutomatiques() throws SQLException {
        List<Commande> commandes = recupererToutesLesCommandes();

        // Nombre total de commandes automatiques
        long nombreTotal = commandes.size();

        // Nombre de commandes en attente
        long nombreEnAttente = commandes.stream()
                .filter(c -> "En attente de confirmation".equals(c.getStatut()))
                .count();

        // Nombre de commandes validées
        long nombreValidees = commandes.stream()
                .filter(c -> "Validée".equals(c.getStatut()))
                .count();

        // Montant total des commandes
        double montantTotal = commandes.stream()
                .mapToDouble(Commande::getMontantTotal)
                .sum();

        // Médicaments les plus commandés
        Map<Medicament, Long> medicamentsParFrequence = commandes.stream()
                .flatMap(c -> c.getLignesDeCommande().stream())
                .collect(Collectors.groupingBy(
                        LigneDeCommande::getMedicament,
                        Collectors.counting()
                ));

        return Map.of(
                "nombreTotal", nombreTotal,
                "nombreEnAttente", nombreEnAttente,
                "nombreValidees", nombreValidees,
                "montantTotal", montantTotal,
                "medicamentsParFrequence", medicamentsParFrequence
        );
    }
}