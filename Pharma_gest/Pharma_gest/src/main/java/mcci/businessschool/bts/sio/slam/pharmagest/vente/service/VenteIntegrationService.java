package mcci.businessschool.bts.sio.slam.pharmagest.vente.service;

import mcci.businessschool.bts.sio.slam.pharmagest.facture.Facture;
import mcci.businessschool.bts.sio.slam.pharmagest.facture.service.FactureService;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.service.MedicamentService;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.service.PaiementService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.TypeVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;

import java.util.Date;
import java.util.List;

public class VenteIntegrationService {

    private VenteService venteService;
    private mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.service.LigneVenteService ligneVenteService;
    private FactureService factureService;
    private PaiementService paiementService;
    private MedicamentService medicamentService;

    public VenteIntegrationService() throws Exception {
        venteService = new VenteService();
        ligneVenteService = new mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.service.LigneVenteService();
        factureService = new FactureService();
        paiementService = new PaiementService();
        medicamentService = new MedicamentService();
    }

    /**
     * Création de la vente par le pharmacien.
     * Cette méthode crée la vente (sans vendeur), insère les lignes de vente,
     * et génère la facture associée.
     *
     * @param lignes La liste des lignes de vente sélectionnées.
     * @param type   Le type de vente (LIBRE ou PRESCRITE).
     * @return L'objet Vente créé.
     */
    public Vente creerVentePharmacien(List<LigneVente> lignes, TypeVente type) {
        // Calcul du montant total de la vente
        double montantTotal = 0.0;
        for (LigneVente ligne : lignes) {
            montantTotal += ligne.getQuantiteVendu() * ligne.getPrixUnitaire();
        }

        // Création de l'objet Vente sans vendeur (null)
        Vente vente = new Vente(new Date(), montantTotal, type, null);

        // Insérer la vente dans la base et mettre à jour son ID
        venteService.ajouterVente(vente);
        System.out.println("Vente ajoutée, ID: " + vente.getId());

        if (vente.getId() <= 0) {
            throw new RuntimeException("L'ID de la vente n'a pas été correctement mis à jour !");
        }

        // Affecter l'ID de la vente à chaque LigneVente et les insérer
        for (LigneVente ligne : lignes) {
            ligne.setVenteId(vente.getId());
            ligneVenteService.ajouterLigneVente(ligne);
        }

        // Générer un numéro de facture et créer la facture associée
        String numeroFacture = "FAC-" + vente.getId() + "-" + System.currentTimeMillis();
        Facture facture = new Facture(new Date(), montantTotal, numeroFacture);
        factureService.genererEtEnregistrerFacture(vente, facture);
        vente.setFacture(facture);

        System.out.println("Vente créée avec ID : " + vente.getId() + " | Facture générée : " + facture.getNumeroFacture());
        return vente;
    }

    /**
     * Finalisation du paiement par le vendeur (caissier).
     * Cette méthode enregistre le paiement, associe le vendeur à la vente,
     * et décrémente le stock de chaque médicament vendu.
     *
     * @param venteId   L'ID de la vente à finaliser.
     * @param paiement  L'objet Paiement renseigné par le vendeur.
     * @param vendeurId L'ID du vendeur (caissier) finalisant la transaction.
     */
  /*  public void finaliserPaiementVendeur(int venteId, Paiement paiement, int vendeurId) {
        // Enregistrer le paiement
        paiement.setVenteId(venteId);
        paiementService.ajouterPaiement(paiement);
        System.out.println("Paiement ajouté avec succès !");

        // Récupérer la vente et associer le vendeur
        Vente vente = venteService.recupererVenteParId(venteId);
        vente.setVendeur(new Vendeur(vendeurId, "identifiant", "motDePasse"));
        venteService.modifierVente(vente);
        System.out.println("Vente modifiée avec succès (vendeur associé) !");

        // Récupérer les lignes de vente et mettre à jour le stock des médicaments
        List<LigneVente> lignes = ligneVenteService.recupererLignesParVente(venteId);
        for (LigneVente ligne : lignes) {
            if (ligne.getMedicament() == null) {
                System.err.println("Erreur : Ligne de vente n'a pas de médicament associé pour vente ID " + venteId);
                continue;
            }
            int medId = ligne.getMedicament().getId();
            Medicament medicament = medicamentService.recupererMedicamentParId(medId);
            if (medicament == null) {
                System.err.println("Erreur : Médicament avec ID " + medId + " non trouvé.");
                continue;
            }
            int nouveauStock = medicament.getStock() - ligne.getQuantiteVendu();
            medicament.setStock(nouveauStock);
            medicamentService.modifierMedicament(medicament);
        }

        System.out.println("Paiement validé et vente finalisée pour la vente ID : " + venteId);
    }*/
}
