package mcci.businessschool.bts.sio.slam.pharmagest.vente.ticket;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

/**
 * Classe de base pour la génération de documents PDF
 * Contient les méthodes communes à tous les types de documents
 */
public abstract class PDFGenerator {

    protected static final String LOGO_PATH = "images/medicament.png";
    protected static final Color HEADER_COLOR = new Color(41, 128, 185); // Bleu pharmaceutique
    protected static final Color ACCENT_COLOR = new Color(46, 204, 113); // Vert médical

    /**
     * Crée un dossier s'il n'existe pas
     * @param cheminDossier Chemin du dossier à créer
     * @return Le dossier créé
     */
    protected File creerDossier(String cheminDossier) {
        File dossier = new File(cheminDossier);
        if (!dossier.exists()) {
            dossier.mkdirs();
        }
        return dossier;
    }

    /**
     * Ajoute le logo de la pharmacie au document
     * @param document Document PDF
     */
    protected void ajouterLogo(Document document) throws Exception {
        try {
            Image logo = Image.getInstance(LOGO_PATH);
            logo.scaleToFit(80, 80);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
            document.add(new Paragraph(" ")); // Espace après le logo
        } catch (Exception e) {
            System.err.println("⚠️ Logo non trouvé : " + e.getMessage());
            // Continuer sans logo
        }
    }

    /**
     * Ajoute l'en-tête du document
     * @param document Document PDF
     * @param titre Titre du document
     * @param sousTitre Sous-titre du document
     */
    protected void ajouterEntete(Document document, String titre, String sousTitre) throws Exception {
        Font fontTitre = new Font(Font.HELVETICA, 16, Font.BOLD, HEADER_COLOR);
        Font fontSousTitre = new Font(Font.HELVETICA, 12, Font.NORMAL);

        Paragraph paragrapheTitre = new Paragraph(titre, fontTitre);
        paragrapheTitre.setAlignment(Element.ALIGN_CENTER);
        document.add(paragrapheTitre);

        Paragraph paragrapheSousTitre = new Paragraph(sousTitre, fontSousTitre);
        paragrapheSousTitre.setAlignment(Element.ALIGN_CENTER);
        document.add(paragrapheSousTitre);

        document.add(new Paragraph(" "));
    }

    /**
     * Ajoute une ligne de séparation
     * @param document Document PDF
     */
    protected void ajouterLigneSeparation(Document document) throws Exception {
        LineSeparator line = new LineSeparator();
        line.setLineColor(ACCENT_COLOR);
        document.add(line);
        document.add(new Paragraph(" "));
    }

    /**
     * Ajoute un titre de section
     * @param document Document PDF
     * @param titre Titre de la section
     */
    protected void ajouterTitreSection(Document document, String titre) throws Exception {
        Font fontTitre = new Font(Font.HELVETICA, 12, Font.BOLD);
        Paragraph titreSection = new Paragraph(titre, fontTitre);
        document.add(titreSection);
        document.add(new Paragraph(" "));
    }

    /**
     * Ajoute le pied de page
     * @param document Document PDF
     * @param texte Texte du pied de page
     */
    protected void ajouterPiedDePage(Document document, String texte) throws Exception {
        Font fontPied = new Font(Font.HELVETICA, 8, Font.ITALIC);
        Paragraph pied = new Paragraph(texte, fontPied);
        pied.setAlignment(Element.ALIGN_CENTER);
        document.add(pied);
    }

    /**
     * Formate une date selon le format spécifié
     * @param date Date à formater
     * @param format Format de date (ex: "dd/MM/yyyy HH:mm")
     * @return Date formatée
     */
    protected String formaterDate(java.util.Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}