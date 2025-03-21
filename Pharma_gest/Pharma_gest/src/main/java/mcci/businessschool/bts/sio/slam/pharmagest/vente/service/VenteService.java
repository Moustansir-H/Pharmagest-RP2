package mcci.businessschool.bts.sio.slam.pharmagest.vente.service;

import mcci.businessschool.bts.sio.slam.pharmagest.paiement.Paiement;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.StatutPaiement;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.service.PaiementService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.dao.VenteDao;

import java.sql.SQLException;
import java.util.List;

public class VenteService {

    private VenteDao venteDAO;
    private PaiementService paiementService;

    public VenteService() throws Exception {
        this.venteDAO = new VenteDao();
        this.paiementService = new PaiementService();
    }

    /**
     * ✅ Récupère toutes les ventes enregistrées dans la base de données.
     */
    public List<Vente> recupererVentes() {
        return venteDAO.recupererVentes();
    }

    /**
     * ✅ Ajoute une nouvelle vente et retourne son ID.
     */
    public Integer ajouterVente(Vente vente) {
        try {
            Integer idVente = venteDAO.ajouterVente(vente);
            vente.setId(idVente);
            System.out.println("✅ Vente ajoutée avec succès !");
            return idVente;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la vente : " + e.getMessage());
            return null;
        }
    }

    /**
     * ✅ Récupère une vente par son ID.
     */
    public Vente recupererVenteParId(int id) {
        try {
            return venteDAO.recupererVenteParId(id);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la vente : " + e.getMessage());
            return null;
        }
    }

    /**
     * ✅ Récupère les ventes en attente de validation.
     */
    public List<Vente> recupererVentesEnAttente() {
        return venteDAO.recupererVentesEnAttente();
    }

    /**
     * ✅ Associe un paiement à une vente.
     */
    public void associerPaiementAVente(int venteId, int paiementId) {
        try {
            Paiement paiement = paiementService.getPaiementById(paiementId);
            if (paiement != null) {
                paiementService.mettreAJourStatutPaiement(paiementId, StatutPaiement.VALIDE);
                System.out.println("✅ Paiement ID : " + paiementId + " associé à la vente ID : " + venteId);
            } else {
                System.err.println("❌ Aucun paiement trouvé avec l'ID : " + paiementId);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'association du paiement : " + e.getMessage());
        }
    }

    /**
     * ✅ Met à jour une vente existante.
     */
    public void modifierVente(Vente vente) {
        try {
            venteDAO.modifierVente(vente);
            System.out.println("✅ Vente modifiée avec succès !");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la modification de la vente : " + e.getMessage());
        }
    }

    /**
     * ✅ Supprime une vente par son ID.
     */
    public void supprimerVenteParId(Integer id) {
        try {
            boolean suppression = venteDAO.supprimerVenteParId(id);
            if (suppression) {
                System.out.println("✅ Vente supprimée avec succès !");
            } else {
                System.out.println("❌ Aucune vente trouvée avec l'ID " + id);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression de la vente : " + e.getMessage());
        }
    }

    /**
     * ✅ Valide le paiement d'une vente et met à jour son statut.
     */
    public void validerPaiement(int venteId) {
        Paiement paiement = paiementService.getPaiementByVenteId(venteId);
        if (paiement != null) {
            boolean paiementValide = paiementService.mettreAJourStatutPaiement(paiement.getId(), StatutPaiement.VALIDE);
            if (paiementValide) {
                System.out.println("✅ Paiement validé pour la vente ID : " + venteId);
            } else {
                System.err.println("❌ Échec de la validation du paiement pour la vente ID : " + venteId);
            }
        } else {
            System.err.println("❌ Aucun paiement trouvé pour la vente ID : " + venteId);
        }
    }

    /**
     * ✅ Rejette un paiement pour une vente.
     */
    public void rejeterPaiement(int venteId) {
        Paiement paiement = paiementService.getPaiementByVenteId(venteId);
        if (paiement != null) {
            boolean paiementRejete = paiementService.mettreAJourStatutPaiement(paiement.getId(), StatutPaiement.REJETE);
            if (paiementRejete) {
                System.out.println("❌ Paiement rejeté pour la vente ID : " + venteId);
            } else {
                System.err.println("⚠️ Impossible de rejeter le paiement pour la vente ID : " + venteId);
            }
        } else {
            System.err.println("❌ Aucun paiement trouvé pour la vente ID : " + venteId);
        }
    }

    public StatutPaiement getStatutPaiementByVenteId(int venteId) {
        try {
            return paiementService.getPaiementByVenteId(venteId).getStatut();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération du statut du paiement pour la vente ID : " + venteId);
            return StatutPaiement.EN_ATTENTE; // Retourner "EN_ATTENTE" par défaut si problème
        }
    }

}
