package mcci.businessschool.bts.sio.slam.pharmagest.vente.ticket;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import mcci.businessschool.bts.sio.slam.pharmagest.patient.Patient;
import mcci.businessschool.bts.sio.slam.pharmagest.prescription.Prescription;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class GenerateurPDF {

    /**
     * Génère automatiquement un ticket PDF à partir d'une vente.
     * Le fichier sera enregistré dans le dossier "tickets/" sous le nom : ticket_vente_{ID}.pdf
     *
     * @param vente        La vente concernée
     * @param lignes       Les lignes de vente associées
     * @param patient      Le patient (si vente prescrite)
     * @param prescription La prescription (si vente prescrite)
     */
    public static File genererTicketPDF(Vente vente, List<LigneVente> lignes, Patient patient, Prescription prescription) {
        File fichierPDF = null;

        try {
            // ✅ 1. Créer le dossier "tickets" s’il n’existe pas
            File dossier = new File("tickets");
            if (!dossier.exists()) {
                dossier.mkdirs();
            }

            // ✅ 2. Construire le fichier de destination
            fichierPDF = new File(dossier, "ticket_vente_" + vente.getId() + ".pdf");

            // ✅ 3. Générer le contenu texte du ticket
            String contenuTicket = GenerateurTicket.genererContenu(vente, lignes, patient, prescription);

            // ✅ 4. Écrire dans le fichier PDF
            Document document = new Document(PageSize.A6);
            PdfWriter.getInstance(document, new FileOutputStream(fichierPDF));
            document.open();

            Font font = new Font(Font.COURIER, 9);
            Paragraph paragraph = new Paragraph(contenuTicket, font);
            document.add(paragraph);
            document.close();

            System.out.println("✅ Ticket PDF sauvegardé dans : " + fichierPDF.getAbsolutePath());
            return fichierPDF;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération du ticket PDF : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


}
