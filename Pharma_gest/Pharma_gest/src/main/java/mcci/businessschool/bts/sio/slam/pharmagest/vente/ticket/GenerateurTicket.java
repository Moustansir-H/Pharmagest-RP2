package mcci.businessschool.bts.sio.slam.pharmagest.vente.ticket;

import mcci.businessschool.bts.sio.slam.pharmagest.patient.Patient;
import mcci.businessschool.bts.sio.slam.pharmagest.prescription.Prescription;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;

import java.text.SimpleDateFormat;
import java.util.List;

public class GenerateurTicket {

    public static String genererContenu(Vente vente, List<LigneVente> lignes, Patient patient, Prescription prescription) {
        StringBuilder sb = new StringBuilder();

        SimpleDateFormat dateHeureFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        // En-tête
        sb.append("╔══════════════════════════════════╗\n");
        sb.append("║      PHARMACIE PHARMAGEST       ║\n");
        sb.append("╚══════════════════════════════════╝\n\n");

        sb.append("Facture N° : ").append(vente.getFacture().getNumeroFacture()).append("\n");
        sb.append("Date       : ").append(dateHeureFormat.format(vente.getDateVente())).append("\n");
        sb.append("Type       : ").append(vente.getTypeVente().name()).append("\n\n");

        // Médicaments
        sb.append("--------------------------------------\n");
        sb.append("Médicaments :\n");
        for (LigneVente ligne : lignes) {
            String nom = ligne.getMedicament().getNom();
            int qte = ligne.getQuantiteVendu();
            double pu = ligne.getPrixUnitaire();
            double sousTotal = qte * pu;

            sb.append(String.format("- %-18s x%-2d  %5.2f€  %6.2f€\n", nom, qte, pu, sousTotal));
        }
        sb.append("--------------------------------------\n");
        sb.append(String.format("Total à régler : %.2f € 💰\n\n", vente.getMontantTotal()));

        // Infos patient et médecin (si vente prescrite)
        if (vente.getTypeVente().name().equals("PRESCRITE") && patient != null && prescription != null) {
            String nomComplet = patient.getPrenom() + " " + patient.getNom();
            sb.append("Client  : ").append(nomComplet).append("\n");
            sb.append("Médecin : ").append(prescription.getNomMedecin()).append("\n\n");
        }

        sb.append("🧾 Merci de remettre ce ticket au caissier\n");

        return sb.toString();
    }
}
