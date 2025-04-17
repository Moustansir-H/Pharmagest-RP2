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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
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

    public static File genererFacturePDF(Vente vente, List<LigneVente> lignes, double montantRecu) {
        if (vente == null || vente.getFacture() == null) {
            System.err.println("❌ Vente ou facture manquante !");
            return null;
        }

        Path dossier = Path.of("factures");
        if (!Files.exists(dossier)) {
            try {
                Files.createDirectories(dossier);
            } catch (IOException e) {
                System.err.println("Erreur création dossier facture : " + e.getMessage());
                return null;
            }
        }

        File fichierPDF = new File(dossier.toFile(), "facture_vente_" + vente.getId() + ".pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A5);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.setLeading(14f);
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 50);

                contentStream.showText("PHARMACIE PHARMAGEST");
                contentStream.newLine();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.showText("FACTURE N° : " + vente.getFacture().getNumeroFacture());
                contentStream.newLine();
                contentStream.showText("Date émission : " + new SimpleDateFormat("dd/MM/yyyy").format(vente.getFacture().getDateEmission()));
                contentStream.newLine();
                contentStream.showText("Type de vente : " + vente.getTypeVente().name());
                contentStream.newLine();
                contentStream.newLine();

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.showText("DÉTAILS DES MÉDICAMENTS :");
                contentStream.newLine();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.showText(String.format("%-20s %-6s %-10s %-10s", "Nom", "Qté", "P.U (€)", "Total (€)"));
                contentStream.newLine();

                for (LigneVente ligne : lignes) {
                    String nom = ligne.getMedicament().getNom();
                    int qte = ligne.getQuantiteVendu();
                    double pu = ligne.getPrixUnitaire();
                    double total = pu * qte;

                    contentStream.showText(String.format("%-20s x%-4d %-10.2f %-10.2f", nom, qte, pu, total));
                    contentStream.newLine();
                }

                contentStream.newLine();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.showText(String.format("Montant total  : %.2f €", vente.getMontantTotal()));
                contentStream.newLine();
                contentStream.showText(String.format("Montant reçu   : %.2f €", montantRecu));
                contentStream.newLine();
                double monnaie = montantRecu - vente.getMontantTotal();
                contentStream.showText(String.format("Monnaie rendue : %.2f €", monnaie));
                contentStream.newLine();
                contentStream.newLine();

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 9);
                contentStream.showText("Merci pour votre achat 🙏. Conservez cette facture.");
                contentStream.endText();
            }

            document.save(fichierPDF);
            System.out.println("✅ Facture PDF générée dans : " + fichierPDF.getAbsolutePath());
            return fichierPDF;

        } catch (IOException e) {
            System.err.println("Erreur génération facture PDF : " + e.getMessage());
            return null;
        }
    }



}
