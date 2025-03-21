package mcci.businessschool.bts.sio.slam.pharmagest.vente.service;

import mcci.businessschool.bts.sio.slam.pharmagest.facture.Facture;
import mcci.businessschool.bts.sio.slam.pharmagest.facture.service.FactureService;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.Medicament;
import mcci.businessschool.bts.sio.slam.pharmagest.medicament.service.MedicamentService;
import mcci.businessschool.bts.sio.slam.pharmagest.paiement.service.PaiementService;
import mcci.businessschool.bts.sio.slam.pharmagest.patient.Patient;
import mcci.businessschool.bts.sio.slam.pharmagest.patient.service.PatientService;
import mcci.businessschool.bts.sio.slam.pharmagest.prescription.Prescription;
import mcci.businessschool.bts.sio.slam.pharmagest.prescription.service.PrescriptionService;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.TypeVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.service.LigneVenteService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class VenteIntegrationService {

    private VenteService venteService;
    private LigneVenteService ligneVenteService;
    private FactureService factureService;
    private PaiementService paiementService;
    private MedicamentService medicamentService;
    private PatientService patientService;
    private PrescriptionService prescriptionService;

    public VenteIntegrationService() throws Exception {
        venteService = new VenteService();
        ligneVenteService = new LigneVenteService();
        factureService = new FactureService();
        paiementService = new PaiementService();
        medicamentService = new MedicamentService();
        patientService = new PatientService();
        prescriptionService = new PrescriptionService();
    }

    /**
     * Création de la vente par le pharmacien.
     * Si la vente est prescrite, on enregistre d'abord le patient et la prescription.
     * Ensuite, la vente est enregistrée normalement.
     *
     * @param lignes       La liste des lignes de vente sélectionnées.
     * @param type         Le type de vente (LIBRE ou PRESCRITE).
     * @param patient      Les informations du patient (si vente prescrite).
     * @param prescription Les informations de la prescription (si vente prescrite).
     * @return L'objet Vente créé.
     */
    public Vente creerVentePharmacien(List<LigneVente> lignes, TypeVente type, Patient patient, Prescription prescription) {
        // Calcul du montant total de la vente
        double montantTotal = lignes.stream()
                .mapToDouble(ligne -> ligne.getQuantiteVendu() * ligne.getPrixUnitaire())
                .sum();

        // Déclaration des variables pour stocker l'ID du patient et de la prescription
        Integer prescriptionId = null;

        // Gestion de la vente prescrite
        if (type == TypeVente.PRESCRITE) {
            if (patient == null || prescription == null) {
                throw new IllegalArgumentException("⚠️ Erreur : Les informations du patient et de la prescription sont requises pour une vente prescrite.");
            }

            try {
                // 1️⃣ Enregistrer le patient et récupérer son ID
                Integer patientId = patientService.ajouterPatient(patient);
                if (patientId == null) {
                    throw new RuntimeException("❌ Erreur lors de l'ajout du patient !");
                }
                System.out.println("✅ Patient ajouté avec succès - ID : " + patientId);

                // 2️⃣ Enregistrer la prescription associée au patient
                prescriptionId = prescriptionService.ajouterPrescription(prescription, patientId);
                if (prescriptionId == null) {
                    throw new RuntimeException("❌ Erreur lors de l'ajout de la prescription !");
                }
                System.out.println("✅ Prescription ajoutée avec succès - ID : " + prescriptionId);

            } catch (Exception e) {
                throw new RuntimeException("❌ Erreur lors de l'ajout du patient ou de la prescription : " + e.getMessage());
            }
        }

        // Création de l'objet Vente
        Vente vente = new Vente(new Date(), montantTotal, type, null);
        if (prescriptionId != null) {
            vente.setPrescriptionId(prescriptionId);
        }

        // Insérer la vente dans la base et récupérer son ID
        venteService.ajouterVente(vente);
        if (vente.getId() <= 0) {
            throw new RuntimeException("❌ L'ID de la vente n'a pas été correctement mis à jour !");
        }
        System.out.println("✅ Vente ajoutée avec succès - ID : " + vente.getId());

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

        System.out.println("✅ Vente créée avec ID : " + vente.getId() + " | Facture générée : " + facture.getNumeroFacture());

        return vente;
    }

    public Vente creerVentePharmacien(List<LigneVente> lignes, TypeVente type) {
        return creerVentePharmacien(lignes, type, null, null);
    }


    public static void main(String[] args) {
        try {

            // Initialisation des services
            VenteIntegrationService integrationService = new VenteIntegrationService();

// 📅 Initialisation correcte de la date de naissance
            LocalDate dateNaissance = LocalDate.of(1985, 6, 10); // Année, Mois, Jour
            Date utilDate = Date.from(dateNaissance.atStartOfDay(ZoneId.systemDefault()).toInstant()); // Conversion vers java.util.Date

// 🔹 Création d'un patient avec la bonne date
            Patient patient = new Patient("Dupont", "Jean", utilDate, "15 rue de Paris", "0601234567");


            // 🔹 Création d'une prescription
            Prescription prescription = new Prescription("Dr. Martin", new Date());

            // 🔹 Création de lignes de vente
            MedicamentService medicamentService = new MedicamentService();
            Medicament medicament = medicamentService.recupererMedicamentParId(3); // Remplace 1 par un ID existant en base

            if (medicament == null) {
                System.out.println("❌ Médicament non trouvé !");
                return;
            }

            LigneVente ligne = new LigneVente(2, medicament.getPrixVente(), medicament);
            List<LigneVente> lignes = List.of(ligne);

            // 🔹 Création d'une vente prescrite
            Vente venteCree = integrationService.creerVentePharmacien(
                    new ArrayList<>(lignes),  // ✅ Conversion ObservableList -> ArrayList
                    TypeVente.PRESCRITE,
                    patient,
                    prescription
            );


            // ✅ Vérification de la création
            System.out.println("\n✅ Vente PRESCRITE créée avec succès !");
            System.out.println("🆔 ID Vente : " + venteCree.getId());
            System.out.println("📜 ID Prescription : " + venteCree.getPrescriptionId());
            System.out.println("💰 Montant Total : " + venteCree.getMontantTotal());
            System.out.println("📄 Facture : " + (venteCree.getFacture() != null ? venteCree.getFacture().getNumeroFacture() : "Non générée"));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du test de la vente prescrite !");
        }
    }
}
