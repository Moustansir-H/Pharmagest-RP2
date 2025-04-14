package mcci.businessschool.bts.sio.slam.pharmagest.vente.service;

import mcci.businessschool.bts.sio.slam.pharmagest.medicament.dao.MedicamentDao;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.Paiement;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.StatutPaiement;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.service.PaiementService;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.Vendeur;
import mcci.businessschool.bts.sio.slam.pharmagest.vendeur.service.VendeurService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.dao.VenteDao;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;

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

    public void validerPaiementEtMettreAJourStock(int venteId, int vendeurId) {
        try {
            // 1. Vérifier s'il existe un paiement pour cette vente
            Paiement paiement = paiementService.getPaiementByVenteId(venteId);
            if (paiement == null) {
                System.err.println("❌ Aucun paiement trouvé pour la vente ID : " + venteId);
                return;
            }

            // 2. Mettre à jour le statut à VALIDE
            boolean statutMisAJour = paiementService.mettreAJourStatutPaiement(paiement.getId(), StatutPaiement.VALIDE);
            if (!statutMisAJour) {
                System.err.println("❌ Impossible de valider le paiement pour la vente ID : " + venteId);
                return;
            }

            System.out.println("✅ Paiement validé pour la vente ID : " + venteId);

            // 3. Récupérer les lignes de vente associées
            LigneVenteService ligneVenteService = new LigneVenteService();
            List<LigneVente> lignes = ligneVenteService.recupererLignesParVente(venteId);
            if (lignes == null || lignes.isEmpty()) {
                System.out.println("⚠️ Aucune ligne de vente trouvée pour la vente ID : " + venteId);
                return;
            }

            // 4. Mettre à jour le stock des médicaments
            MedicamentDao medicamentDao = new MedicamentDao();
            for (LigneVente ligne : lignes) {
                int medicamentId = ligne.getMedicament().getId();
                int quantiteVendue = ligne.getQuantiteVendu();

                // Décrémenter le stock (quantité vendue => stock - quantite)
                medicamentDao.mettreAJourStock(medicamentId, -quantiteVendue);
            }

            System.out.println("📦 Stock des médicaments mis à jour après validation du paiement !");

            // 5. Vérifier si le vendeur existe avec l'ID saisi
            VendeurService vendeurService = new VendeurService(); // Création de l'instance
            if (!vendeurService.verifierExistenceVendeur(vendeurId)) {
                throw new Exception("❌ L'ID du vendeur n'est pas valide ! ID : " + vendeurId);
            }

            // 6. Ajouter l'ID du vendeur dans la vente (mise à jour)
            VenteDao venteDao = new VenteDao();
            Vente vente = venteDao.recupererVenteParId(venteId);
            if (vente != null) {
                Vendeur vendeur = new Vendeur(vendeurId, "Vendeur", "Inconnu");  // Création du vendeur avec l'ID validé
                vente.setVendeur(vendeur);  // Affecter le vendeur à la vente
                venteDao.modifierVente(vente);  // Mettre à jour la vente avec l'ID du vendeur
                System.out.println("✅ Vendeur mis à jour pour la vente ID : " + venteId);
            } else {
                System.err.println("❌ Vente non trouvée pour l'ID : " + venteId);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la validation du paiement et mise à jour du stock : " + e.getMessage());
        }
    }


}
