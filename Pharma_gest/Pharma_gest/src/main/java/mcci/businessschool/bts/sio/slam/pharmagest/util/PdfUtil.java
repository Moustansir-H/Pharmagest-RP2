package mcci.businessschool.bts.sio.slam.pharmagest.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import mcci.businessschool.bts.sio.slam.pharmagest.commande.LigneDeCommande;
import mcci.businessschool.bts.sio.slam.pharmagest.fournisseur.Fournisseur;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PdfUtil {

    /**
     * Génère un PDF contenant le bon de commande.
     *
     * @param fournisseur    le fournisseur concerné.
     * @param lignesCommande la liste des lignes de commande.
     * @return le fichier PDF généré.
     * @throws Exception en cas d'erreur lors de la création du PDF.
     */
    public static File genererBonDeCommandePDF(Fournisseur fournisseur, List<LigneDeCommande> lignesCommande) throws Exception {
        Document document = new Document();
        File fichierPdf = new File("Commande_" + fournisseur.getNom() + ".pdf");
        PdfWriter.getInstance(document, new FileOutputStream(fichierPdf));
        document.open();

        document.add(new Paragraph("Bon de Commande\n\n"));

        PdfPTable table = new PdfPTable(4);
        table.addCell("Médicament");
        table.addCell("Quantité");
        table.addCell("Prix Unitaire (€)");
        table.addCell("Total (€)");

        for (LigneDeCommande ligne : lignesCommande) {
            table.addCell(ligne.getMedicament().getNom());
            table.addCell(String.valueOf(ligne.getQuantiteVendu()));
            table.addCell(String.valueOf(ligne.getPrixUnitaire()));
            table.addCell(String.valueOf(ligne.getQuantiteVendu() * ligne.getPrixUnitaire()));
        }

        document.add(table);
        document.close();

        return fichierPdf;
    }
}
