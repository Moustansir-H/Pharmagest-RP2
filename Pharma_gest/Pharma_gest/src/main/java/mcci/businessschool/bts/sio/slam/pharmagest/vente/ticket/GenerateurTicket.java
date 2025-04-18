package mcci.businessschool.bts.sio.slam.pharmagest.vente.ticket;

import mcci.businessschool.bts.sio.slam.pharmagest.patient.Patient;
import mcci.businessschool.bts.sio.slam.pharmagest.prescription.Prescription;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.Vente;
import mcci.businessschool.bts.sio.slam.pharmagest.vente.ligne.LigneVente;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Classe pour générer des tickets PDF et leur contenu textuel
 */
public class GenerateurTicket {

    private static final String RESOURCES_PATH = "src/main/resources/";
    private static final String LOGO_PATH = RESOURCES_PATH + "images/medicament.png";
    private static final String DOWNLOADS_PATH = System.getProperty("user.home") + File.separator + "Downloads";
    private static final String FONTS_DIR = "fonts";

    /**
     * Génère le contenu textuel du ticket
     * @param vente La vente concernée
     * @param lignes Les lignes de vente
     * @param patient Le patient (peut être null pour une vente non prescrite)
     * @param prescription La prescription (peut être null pour une vente non prescrite)
     * @return Le contenu textuel formaté du ticket
     */
    public static String genererContenu(Vente vente, List<LigneVente> lignes, Patient patient, Prescription prescription) {
        StringBuilder sb = new StringBuilder();

        // En-tête
        sb.append("==================================\n");
        sb.append("         PHARMACIE PHARMAGEST    \n");
        sb.append("==================================\n\n");

        // Informations de la vente
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sb.append("Date: ").append(dateFormat.format(vente.getDateVente())).append("\n");
        sb.append("Facture N°: ").append(vente.getFacture().getNumeroFacture()).append("\n");
        sb.append("Type: ").append(vente.getTypeVente().name()).append("\n\n");

        // Détails des médicaments
        sb.append("----------------------------------\n");
        sb.append(String.format("%-20s %5s %8s %8s\n", "PRODUIT", "QTÉ", "PRIX", "TOTAL"));
        sb.append("----------------------------------\n");

        for (LigneVente ligne : lignes) {
            String nom = ligne.getMedicament().getNom();
            // Tronquer le nom s'il est trop long
            if (nom.length() > 20) {
                nom = nom.substring(0, 17) + "...";
            }

            int qte = ligne.getQuantiteVendu();
            double prix = ligne.getPrixUnitaire();
            double total = prix * qte;

            sb.append(String.format("%-20s %5d %8.2f€ %8.2f€\n",
                    nom, qte, prix, total));
        }

        sb.append("----------------------------------\n");
        sb.append(String.format("%34s %8.2f€\n", "TOTAL:", vente.getMontantTotal()));
        sb.append("----------------------------------\n\n");

        // Informations patient et prescription si vente prescrite
        if (vente.getTypeVente().name().equals("PRESCRITE") && patient != null && prescription != null) {
            sb.append("PATIENT: ").append(patient.getPrenom()).append(" ").append(patient.getNom()).append("\n");
            sb.append("MÉDECIN: ").append(prescription.getNomMedecin()).append("\n\n");
        }

        // Pied de page
        sb.append("Merci pour votre visite!\n");
        sb.append("Conservez ce ticket pour tout échange.\n");
        sb.append("==================================\n");

        return sb.toString();
    }

    /**
     * Génère un PDF pour une vente donnée
     * @param vente La vente pour laquelle générer le ticket
     * @param lignes Les lignes de vente
     * @param patient Le patient (peut être null pour une vente non prescrite)
     * @param prescription La prescription (peut être null pour une vente non prescrite)
     * @return Le fichier PDF généré
     * @throws IOException Si une erreur survient lors de la génération du PDF
     */
    public static File genererTicketPDF(Vente vente, List<LigneVente> lignes, Patient patient, Prescription prescription) throws IOException {
        // Créer le nom du fichier
        String fileName = "ticket_" + vente.getFacture().getNumeroFacture() + ".pdf";
        String filePath = DOWNLOADS_PATH + File.separator + fileName;

        // Vérifier et créer le dossier de téléchargement si nécessaire
        ensureDownloadDirectoryExists();

        // Générer le PDF
        genererTicketPDF(vente, lignes, patient, prescription, filePath);

        return new File(filePath);
    }

    /**
     * Génère un PDF pour une vente donnée avec un chemin de fichier spécifié
     * @param vente La vente pour laquelle générer le ticket
     * @param lignes Les lignes de vente
     * @param patient Le patient (peut être null pour une vente non prescrite)
     * @param prescription La prescription (peut être null pour une vente non prescrite)
     * @param outputPath Le chemin où sauvegarder le fichier PDF
     * @throws IOException Si une erreur survient lors de la génération du PDF
     */
    public static void genererTicketPDF(Vente vente, List<LigneVente> lignes, Patient patient, Prescription prescription, String outputPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            // Créer une page au format A5 (plus petit que A4, adapté pour un ticket)
            PDPage page = new PDPage(new PDRectangle(PDRectangle.A5.getHeight(), PDRectangle.A5.getWidth()));
            document.addPage(page);

            // Charger les polices personnalisées avec fallback sur les polices standard
            PDFont fontRegular = loadFont(document, "Helvetica.ttf", getHelveticaFont());
            PDFont fontBold = loadFont(document, "Helvetica-Bold.ttf", getHelveticaBoldFont());
            PDFont fontItalic = loadFont(document, "Helvetica-Oblique.ttf", getHelveticaObliqueFont());

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Dimensions de la page
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float margin = 30;
                float yPosition = pageHeight - margin;
                float lineHeight = 15;

                // Ajouter le logo PHARMAGEST
                PDImageXObject logo;
                try {
                    File logoFile = new File(LOGO_PATH);
                    if (logoFile.exists()) {
                        logo = PDImageXObject.createFromFile(LOGO_PATH, document);
                    } else {
                        // Créer le dossier images s'il n'existe pas
                        File imagesDir = new File(RESOURCES_PATH + "images");
                        if (!imagesDir.exists()) {
                            imagesDir.mkdirs();
                        }
                        // Créer un logo de remplacement
                        logo = createPlaceholderLogo(document, "PHARMAGEST");
                    }
                } catch (IOException e) {
                    System.err.println("Impossible de charger le logo: " + e.getMessage());
                    // Créer un logo de remplacement
                    logo = createPlaceholderLogo(document, "PHARMAGEST");
                }

                // Centrer le logo en haut de la page
                float logoWidth = 150;
                float logoHeight = logoWidth * logo.getHeight() / logo.getWidth();
                contentStream.drawImage(logo, (pageWidth - logoWidth) / 2, yPosition - logoHeight, logoWidth, logoHeight);

                yPosition -= logoHeight + 20;

                // Titre
                drawCenteredText(contentStream, "PHARMACIE PHARMAGEST", pageWidth / 2, yPosition, fontBold, 14);
                yPosition -= lineHeight * 1.5f;

                // Informations de la facture dans un cadre
                contentStream.setNonStrokingColor(240, 240, 240); // Gris très clair
                contentStream.addRect(margin, yPosition - lineHeight * 3, pageWidth - 2 * margin, lineHeight * 3);
                contentStream.fill();
                contentStream.setNonStrokingColor(0, 0, 0); // Noir

                // Bordure du cadre
                contentStream.setStrokingColor(100, 100, 100);
                contentStream.addRect(margin, yPosition - lineHeight * 3, pageWidth - 2 * margin, lineHeight * 3);
                contentStream.stroke();

                // Informations de la facture
                SimpleDateFormat dateHeureFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                drawText(contentStream, "Facture N° : " + vente.getFacture().getNumeroFacture(), margin + 10, yPosition - lineHeight, fontBold, 10);
                drawText(contentStream, "Date : " + dateHeureFormat.format(vente.getDateVente()), margin + 10, yPosition - lineHeight * 2, fontRegular, 10);
                drawText(contentStream, "Type : " + vente.getTypeVente().name(), margin + 10, yPosition - lineHeight * 3, fontRegular, 10);

                yPosition -= lineHeight * 4;

                // Titre de la section médicaments
                contentStream.setNonStrokingColor(200, 230, 255); // Bleu clair
                contentStream.addRect(margin, yPosition - lineHeight, pageWidth - 2 * margin, lineHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(0, 0, 0); // Noir

                drawCenteredText(contentStream, "MÉDICAMENTS", pageWidth / 2, yPosition - lineHeight + 3, fontBold, 12);
                yPosition -= lineHeight * 1.5f;

                // En-têtes du tableau
                float[] columnWidths = {pageWidth - 2 * margin - 180, 60, 60, 60};
                float tableX = margin;

                // Fond gris pour l'en-tête
                contentStream.setNonStrokingColor(220, 220, 220);
                contentStream.addRect(tableX, yPosition - lineHeight, pageWidth - 2 * margin, lineHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(0, 0, 0);

                // Bordure de l'en-tête
                contentStream.setStrokingColor(100, 100, 100);
                contentStream.addRect(tableX, yPosition - lineHeight, pageWidth - 2 * margin, lineHeight);
                contentStream.stroke();

                // Texte de l'en-tête
                drawText(contentStream, "Nom", tableX + 5, yPosition - lineHeight + 3, fontBold, 10);
                drawText(contentStream, "Qté", tableX + columnWidths[0] + 5, yPosition - lineHeight + 3, fontBold, 10);
                drawText(contentStream, "P.U.", tableX + columnWidths[0] + columnWidths[1] + 5, yPosition - lineHeight + 3, fontBold, 10);
                drawText(contentStream, "Total", tableX + columnWidths[0] + columnWidths[1] + columnWidths[2] + 5, yPosition - lineHeight + 3, fontBold, 10);

                yPosition -= lineHeight;

                // Lignes du tableau
                for (LigneVente ligne : lignes) {
                    String nom = ligne.getMedicament().getNom();
                    int qte = ligne.getQuantiteVendu();
                    double pu = ligne.getPrixUnitaire();
                    double sousTotal = qte * pu;

                    // Limiter le nom à 25 caractères pour l'alignement
                    if (nom.length() > 25) {
                        nom = nom.substring(0, 22) + "...";
                    }

                    // Alternance de couleurs pour les lignes
                    if (lignes.indexOf(ligne) % 2 == 0) {
                        contentStream.setNonStrokingColor(245, 245, 245);
                        contentStream.addRect(tableX, yPosition - lineHeight, pageWidth - 2 * margin, lineHeight);
                        contentStream.fill();
                        contentStream.setNonStrokingColor(0, 0, 0);
                    }

                    // Bordure de la ligne
                    contentStream.setStrokingColor(200, 200, 200);
                    contentStream.addRect(tableX, yPosition - lineHeight, pageWidth - 2 * margin, lineHeight);
                    contentStream.stroke();

                    drawText(contentStream, nom, tableX + 5, yPosition - lineHeight + 3, fontRegular, 9);
                    drawText(contentStream, String.valueOf(qte), tableX + columnWidths[0] + 5, yPosition - lineHeight + 3, fontRegular, 9);
                    drawText(contentStream, String.format("%.2f€", pu), tableX + columnWidths[0] + columnWidths[1] + 5, yPosition - lineHeight + 3, fontRegular, 9);
                    drawText(contentStream, String.format("%.2f€", sousTotal), tableX + columnWidths[0] + columnWidths[1] + columnWidths[2] + 5, yPosition - lineHeight + 3, fontRegular, 9);

                    yPosition -= lineHeight;
                }

                // Total
                yPosition -= lineHeight;

                contentStream.setNonStrokingColor(230, 230, 255); // Bleu très clair
                contentStream.addRect(tableX + columnWidths[0] + columnWidths[1], yPosition - lineHeight, columnWidths[2] + columnWidths[3], lineHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(0, 0, 0);

                contentStream.setStrokingColor(100, 100, 100);
                contentStream.addRect(tableX + columnWidths[0] + columnWidths[1], yPosition - lineHeight, columnWidths[2] + columnWidths[3], lineHeight);
                contentStream.stroke();

                drawText(contentStream, "Total à régler :", tableX + columnWidths[0] + columnWidths[1] + 5, yPosition - lineHeight + 3, fontBold, 10);
                drawText(contentStream, String.format("%.2f€", vente.getMontantTotal()), tableX + columnWidths[0] + columnWidths[1] + columnWidths[2] + 5, yPosition - lineHeight + 3, fontBold, 10);

                yPosition -= lineHeight * 2;

                // Infos patient et médecin (si vente prescrite)
                if (vente.getTypeVente().name().equals("PRESCRITE") && patient != null && prescription != null) {
                    contentStream.setNonStrokingColor(230, 255, 230); // Vert très clair
                    contentStream.addRect(margin, yPosition - lineHeight * 2, pageWidth - 2 * margin, lineHeight * 2);
                    contentStream.fill();
                    contentStream.setNonStrokingColor(0, 0, 0);

                    contentStream.setStrokingColor(100, 100, 100);
                    contentStream.addRect(margin, yPosition - lineHeight * 2, pageWidth - 2 * margin, lineHeight * 2);
                    contentStream.stroke();

                    String nomComplet = patient.getPrenom() + " " + patient.getNom();
                    drawText(contentStream, "Client : " + nomComplet, margin + 10, yPosition - lineHeight + 3, fontBold, 10);
                    drawText(contentStream, "Médecin : " + prescription.getNomMedecin(), margin + 10, yPosition - lineHeight * 2 + 3, fontBold, 10);

                    yPosition -= lineHeight * 3;
                }

                // Message de remerciement
                drawCenteredText(contentStream, "✓ Merci pour votre visite!", pageWidth / 2, yPosition - lineHeight, fontItalic, 11);
                yPosition -= lineHeight;
                drawCenteredText(contentStream, "🧾 Conservez ce ticket pour tout échange", pageWidth / 2, yPosition - lineHeight, fontItalic, 9);
            }

            // Sauvegarder le document au chemin spécifié
            File outputFile = new File(outputPath);
            // Créer les répertoires parents si nécessaires
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            document.save(outputPath);
            System.out.println("PDF généré avec succès: " + outputPath);

        } catch (Exception e) {
            throw new IOException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Vérifie et crée le dossier de téléchargement si nécessaire
     * @return true si le dossier existe ou a été créé avec succès
     */
    private static boolean ensureDownloadDirectoryExists() {
        try {
            File path = new File(DOWNLOADS_PATH);
            if (!path.exists()) {
                path.mkdirs();
                System.out.println("Dossier de téléchargement créé: " + DOWNLOADS_PATH);
            }
            return path.isDirectory() && path.canWrite();
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du dossier de téléchargement: " + e.getMessage());
            return false;
        }
    }

    /**
     * Méthode pour obtenir la police Helvetica de manière compatible avec différentes versions de PDFBox
     */
    public static PDFont getHelveticaFont() {
        try {
            // Essayer d'abord avec la méthode moderne (PDFBox 2.0.0+)
            try {
                // Utiliser la réflexion pour vérifier si la méthode existe
                Class.forName("org.apache.pdfbox.pdmodel.font.Standard14Fonts");

                // Si nous sommes ici, la classe existe, essayons d'utiliser la méthode moderne
                Class<?> fontNameEnum = Class.forName("org.apache.pdfbox.pdmodel.font.Standard14Fonts$FontName");
                Object helveticaEnum = Enum.valueOf((Class<Enum>) fontNameEnum, "HELVETICA");

                // Appeler PDType1Font.createFromStandard14Font via réflexion
                java.lang.reflect.Method createMethod = PDType1Font.class.getMethod("createFromStandard14Font", fontNameEnum);
                return (PDFont) createMethod.invoke(null, helveticaEnum);
            } catch (Exception e) {
                // La méthode moderne n'existe pas, utiliser l'ancienne méthode
                return PDType1Font.HELVETICA;
            }
        } catch (Exception e) {
            // En cas d'erreur, retourner la police par défaut
            System.err.println("Erreur lors de l'obtention de la police Helvetica: " + e.getMessage());
            return PDType1Font.HELVETICA;
        }
    }

    /**
     * Méthode pour obtenir la police Helvetica Bold de manière compatible avec différentes versions de PDFBox
     */
    public static PDFont getHelveticaBoldFont() {
        try {
            // Essayer d'abord avec la méthode moderne (PDFBox 2.0.0+)
            try {
                // Utiliser la réflexion pour vérifier si la méthode existe
                Class.forName("org.apache.pdfbox.pdmodel.font.Standard14Fonts");

                // Si nous sommes ici, la classe existe, essayons d'utiliser la méthode moderne
                Class<?> fontNameEnum = Class.forName("org.apache.pdfbox.pdmodel.font.Standard14Fonts$FontName");
                Object helveticaBoldEnum = Enum.valueOf((Class<Enum>) fontNameEnum, "HELVETICA_BOLD");

                // Appeler PDType1Font.createFromStandard14Font via réflexion
                java.lang.reflect.Method createMethod = PDType1Font.class.getMethod("createFromStandard14Font", fontNameEnum);
                return (PDFont) createMethod.invoke(null, helveticaBoldEnum);
            } catch (Exception e) {
                // La méthode moderne n'existe pas, utiliser l'ancienne méthode
                return PDType1Font.HELVETICA_BOLD;
            }
        } catch (Exception e) {
            // En cas d'erreur, retourner la police par défaut
            System.err.println("Erreur lors de l'obtention de la police Helvetica Bold: " + e.getMessage());
            return PDType1Font.HELVETICA_BOLD;
        }
    }

    /**
     * Méthode pour obtenir la police Helvetica Oblique de manière compatible avec différentes versions de PDFBox
     */
    public static PDFont getHelveticaObliqueFont() {
        try {
            // Essayer d'abord avec la méthode moderne (PDFBox 2.0.0+)
            try {
                // Utiliser la réflexion pour vérifier si la méthode existe
                Class.forName("org.apache.pdfbox.pdmodel.font.Standard14Fonts");

                // Si nous sommes ici, la classe existe, essayons d'utiliser la méthode moderne
                Class<?> fontNameEnum = Class.forName("org.apache.pdfbox.pdmodel.font.Standard14Fonts$FontName");
                Object helveticaObliqueEnum = Enum.valueOf((Class<Enum>) fontNameEnum, "HELVETICA_OBLIQUE");

                // Appeler PDType1Font.createFromStandard14Font via réflexion
                java.lang.reflect.Method createMethod = PDType1Font.class.getMethod("createFromStandard14Font", fontNameEnum);
                return (PDFont) createMethod.invoke(null, helveticaObliqueEnum);
            } catch (Exception e) {
                // La méthode moderne n'existe pas, utiliser l'ancienne méthode
                return PDType1Font.HELVETICA_OBLIQUE;
            }
        } catch (Exception e) {
            // En cas d'erreur, retourner la police par défaut
            System.err.println("Erreur lors de l'obtention de la police Helvetica Oblique: " + e.getMessage());
            return PDType1Font.HELVETICA_OBLIQUE;
        }
    }

    /**
     * Charge une police à partir du dossier fonts
     * @param document Le document PDF
     * @param fontFileName Le nom du fichier de police
     * @param fallbackFont La police de secours à utiliser en cas d'erreur
     * @return La police chargée ou la police de secours
     */
    private static PDFont loadFont(PDDocument document, String fontFileName, PDFont fallbackFont) {
        // Essayer plusieurs chemins possibles pour trouver la police
        String[] possiblePaths = {
                FONTS_DIR + File.separator + fontFileName,
                "src" + File.separator + "main" + File.separator + "resources" + File.separator + FONTS_DIR + File.separator + fontFileName,
                System.getProperty("user.dir") + File.separator + FONTS_DIR + File.separator + fontFileName,
                System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + FONTS_DIR + File.separator + fontFileName,
                System.getProperty("user.home") + File.separator + ".fonts" + File.separator + fontFileName,
                "C:" + File.separator + "Windows" + File.separator + "Fonts" + File.separator + fontFileName
        };

        for (String path : possiblePaths) {
            try {
                File fontFile = new File(path);
                if (fontFile.exists()) {
                    System.out.println("Police trouvée: " + path);
                    try (FileInputStream fis = new FileInputStream(fontFile)) {
                        return PDType0Font.load(document, fis);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la police " + path + ": " + e.getMessage());
            }
        }

        // Si on arrive ici, aucune des polices n'a pu être chargée
        System.err.println("Impossible de charger la police " + fontFileName + ". Utilisation de la police de secours.");
        return fallbackFont;
    }

    /**
     * Crée un logo de remplacement avec du texte
     */
    private static PDImageXObject createPlaceholderLogo(PDDocument document, String text) throws IOException {
        BufferedImage image = new BufferedImage(300, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Fond blanc
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 300, 100);

        // Bordure bleue
        g2d.setColor(new Color(0, 102, 204));
        g2d.drawRect(0, 0, 299, 99);

        // Texte centré
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.setColor(new Color(0, 102, 204));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.drawString(text, (300 - textWidth) / 2, (100 + textHeight / 2) / 2);

        // Ajouter un symbole de pharmacie
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("⚕", 10, 50);
        g2d.drawString("⚕", 275, 50);

        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        return PDImageXObject.createFromByteArray(document, baos.toByteArray(), "Placeholder Logo");
    }

    /**
     * Dessine du texte centré
     */
    private static void drawCenteredText(PDPageContentStream contentStream, String text, float x, float y, PDFont font, float fontSize) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(0, 0, 0); // Noir
        contentStream.newLineAtOffset(x - textWidth / 2, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * Dessine du texte simple
     */
    private static void drawText(PDPageContentStream contentStream, String text, float x, float y, PDFont font, float fontSize) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(0, 0, 0); // Noir
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}
