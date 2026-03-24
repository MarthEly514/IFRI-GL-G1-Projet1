package com.campusdocs.server.services;

import com.campusdocs.server.config.StorageConfig;
import com.campusdocs.server.models.ActeAdministratif;
import com.campusdocs.server.models.Usager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Service
public class pdfService {
    @Autowired
    private StorageConfig storageConfig;

    private static final DeviceRgb BLEU_FONCE = new DeviceRgb(26, 58, 92);
    private static final DeviceRgb BLEU_CLAIR = new DeviceRgb(184, 204, 228);

    // ── Créer le dossier de sortie si inexistant ──
    private void ensureOutputDir() throws IOException {
        Files.createDirectories(Paths.get(storageConfig.getPdfsDir()));
    }

    // ── Générer QR code en bytes ──
    public byte[] generateQRBytes(String data) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 80, 80);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
        return baos.toByteArray();
    }

    // ── Générer QR code en base64 ──
    private String generateQRBase64(String data) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 80, 80);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return "data:image/png;base64," +
                    Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }

    private String loadImageAsBase64(String relativePath) {
        try {
            Path path = Paths.get(relativePath);
            if (!Files.exists(path)) return null;
            byte[] bytes = Files.readAllBytes(path);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Calculer la cote ──
    private String getCote(double note) {
        if (note >= 16) return "A+";
        if (note >= 15) return "A";
        if (note >= 14) return "A-";
        if (note >= 13) return "B+";
        if (note >= 12) return "B-";
        if (note >= 11) return "C+";
        if (note >= 10) return "C";
        if (note >= 9)  return "C-";
        if (note >= 8)  return "D+";
        if (note >= 5)  return "D";
        return "F";
    }

    // ── Structure des UEs par semestre ──
    private List<Map<String, Object>> getUEStructure(int semestre) {
        List<Map<String, Object>> ues = new ArrayList<>();
        if (semestre == 1) {
            ues.add(createUE("MTH1121", "LOGIQUE, ARITHMETIQUE ET SES APPLICATIONS", 5,
                    List.of(createECU("1MTH1121", "Logique, arithmetique et applications"))));
            ues.add(createUE("MTH1122", "MATHEMATIQUES FONDAMENTALES", 5,
                    List.of(createECU("1MTH1122", "Algebre lineaire et applications"),
                            createECU("2MTH1122", "Analyse et applications"))));
            ues.add(createUE("INF1124", "ARCHITECTURE ET TOPOLOGIE DES RESEAUX", 4,
                    List.of(createECU("1INF1124", "Architecture et topologie des reseaux informatiques"))));
            ues.add(createUE("INF1125", "SYSTEME D'EXPLOITATION ET OUTILS DE BASE", 4,
                    List.of(createECU("1INF1125", "Utilisation et administration sous Windows/Linux"),
                            createECU("2INF1125", "Outils de base en informatique"))));
            ues.add(createUE("INF1126", "BASE DE LA PROGRAMMATION", 4,
                    List.of(createECU("1INF1126", "Algorithmique"),
                            createECU("2INF1126", "Langages C"))));
            ues.add(createUE("DRP1127", "DEONTOLOGIE ET DROIT LIES AUX TIC", 2,
                    List.of(createECU("1DRP1127", "Deontologie et droit lies aux TIC"))));
        } else {
            ues.add(createUE("INF1222", "PROGRAMMATION ORIENTEE OBJET", 5,
                    List.of(createECU("1INF1222", "Concepts POO et Java"),
                            createECU("2INF1222", "Structures de donnees et algorithmes avances"))));
            ues.add(createUE("INF1223", "BASE DE DONNEES", 4,
                    List.of(createECU("1INF1223", "Modelisation et conception de BDD"),
                            createECU("2INF1223", "SQL et administration"))));
            ues.add(createUE("INF1224", "SYSTEMES D'INFORMATION", 4,
                    List.of(createECU("1INF1224", "Analyse et conception des SI"),
                            createECU("2INF1224", "UML et methodes agiles"))));
            ues.add(createUE("INF1225", "RESEAUX INFORMATIQUES", 4,
                    List.of(createECU("1INF1225", "Protocoles reseau TCP/IP"),
                            createECU("2INF1225", "Administration reseau"))));
            ues.add(createUE("DRP1226", "DROIT DU NUMERIQUE", 2,
                    List.of(createECU("1DRP1226", "Cybercriminalite et protection des donnees"))));
        }
        return ues;
    }

    private Map<String, Object> createUE(String code, String intitule, int credits,
                                         List<Map<String, Object>> ecus) {
        Map<String, Object> ue = new HashMap<>();
        ue.put("code", code);
        ue.put("intitule", intitule);
        ue.put("credits", credits);
        ue.put("ecus", ecus);
        return ue;
    }

    private Map<String, Object> createECU(String code, String intitule) {
        Map<String, Object> ecu = new HashMap<>();
        ecu.put("code", code);
        ecu.put("intitule", intitule);
        ecu.put("note", 8 + Math.random() * 11);
        return ecu;
    }

    // Champs temporaires pour les résultats globaux
    private double lastMoyenne;
    private double lastCreditsCapitalises;
    private String lastDecision;

    // ── Générer les lignes du tableau HTML ──
    @SuppressWarnings("unchecked")
    private String buildTableRows(List<Map<String, Object>> ues, int annee) {
        StringBuilder sb = new StringBuilder();
        double totalCredits = 0;
        double totalCreditsValides = 0;
        double sommePonderee = 0;

        for (Map<String, Object> ue : ues) {
            List<Map<String, Object>> ecus = (List<Map<String, Object>>) ue.get("ecus");
            int credits = (int) ue.get("credits");

            double moyUE = ecus.stream()
                    .mapToDouble(e -> (double) e.get("note"))
                    .average().orElse(0);
            moyUE = Math.round(moyUE * 100.0) / 100.0;

            boolean valide = moyUE >= 10;
            String sessionValidation = valide
                    ? "VALIDE EN Fevr " + annee
                    : "NON VALIDE - Sept " + annee;

            totalCredits += credits;
            if (valide) totalCreditsValides += credits;
            sommePonderee += moyUE * credits;

            sb.append("<tr class='row-ue'>")
                    .append("<td class='cell-code'>").append(ue.get("code")).append("</td>")
                    .append("<td class='cell-intitule'>").append(ue.get("intitule")).append("</td>")
                    .append("<td class='cell-num'>").append(credits).append("</td>")
                    .append("<td class='cell-num'>").append(String.format("%.2f", moyUE)).append("/20</td>")
                    .append("<td class='cell-cote'>").append(getCote(moyUE)).append("</td>")
                    .append("<td class='cell-result'>").append(sessionValidation).append("</td>")
                    .append("</tr>");

            for (Map<String, Object> ecu : ecus) {
                double note = (double) ecu.get("note");
                sb.append("<tr class='row-ecu'>")
                        .append("<td class='cell-code-ecu'>").append(ecu.get("code")).append("</td>")
                        .append("<td class='cell-intitule ecu-indent'>").append(ecu.get("intitule")).append("</td>")
                        .append("<td class='cell-empty'></td>")
                        .append("<td class='cell-num-ecu'>").append(String.format("%.2f", note)).append("/20</td>")
                        .append("<td class='cell-empty'></td>")
                        .append("<td class='cell-empty'></td>")
                        .append("</tr>");
            }
        }

        this.lastMoyenne = Math.round((sommePonderee / totalCredits) * 100.0) / 100.0;
        this.lastCreditsCapitalises = Math.round((totalCreditsValides / totalCredits) * 10000.0) / 100.0;
        this.lastDecision = this.lastMoyenne >= 10 ? "Continue" : "Redouble";

        return sb.toString();
    }

    // ── Construire le HTML du bulletin / relevé de notes ──
    private String buildBulletinHtml(Usager etudiant, String reference, int semestre,
                                     String anneeAcademique, String qrBase64,
                                     String directeurNom, String directeurAdjointNom,
                                     String directeurAdjointTitre) {
        int annee = LocalDate.now().getYear();
        List<Map<String, Object>> ues = getUEStructure(semestre);
        String tableRows = buildTableRows(ues, annee);
        String dateGeneration = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String semestreLabel = switch (semestre) {
            case 1 -> "Premier";
            case 2 -> "Deuxieme";
            case 3 -> "Troisieme";
            case 4 -> "Quatrieme";
            case 5 -> "Cinquieme";
            default -> "Sixieme";
        };

        String qrSrc = qrBase64 != null
                ? "<img src='" + qrBase64 + "' class='qr-img' alt='QR Code'/>"
                : "<div class='qr-placeholder'>QR Code</div>";

        // ── Charger les tampons depuis le dossier assets ──
        String daBase64 = loadImageAsBase64("assets/DA.png");
        String dirBase64 = loadImageAsBase64("assets/DIR.png");

        String tampanDA = daBase64 != null
                ? "<img src='data:image/png;base64," + daBase64 + "' class='stamp-img' alt='Tampon DA'/>"
                : "";
        String tampanDIR = dirBase64 != null
                ? "<img src='data:image/png;base64," + dirBase64 + "' class='stamp-img' alt='Tampon DIR'/>"
                : "";

        // ── Charger le logo IFRI ──
        String ifriBase64 = loadImageAsBase64("assets/IFRI.png");
        String logoIfri = ifriBase64 != null
                ? "<img src='data:image/png;base64," + ifriBase64 + "' class='logo-img' alt='IFRI'/>"
                : "<div class='header-left-text'>IFRI</div>";

        return "<?xml version='1.0' encoding='UTF-8'?>" +
                "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' " +
                "'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>" +
                "<html xmlns='http://www.w3.org/1999/xhtml' lang='fr'>" +
                "<head><meta charset='UTF-8'/><style>" +
                "* { margin: 0; padding: 0; }" +
                "body { width: 210mm; font-family: Times New Roman, serif; color: #111; font-size: 8.5pt; background: #ffc8c8; }" +
                ".page { width: 210mm; min-height: 297mm; padding: 5mm 7mm 4mm 7mm; background: #ffc8c8; }" +

                // Header
                ".header { display: table; width: 100%; margin-bottom: 2mm; }" +
                ".header-left, .header-right { display: table-cell; width: 22mm; vertical-align: middle; text-align: center; }" +
                ".header-center { display: table-cell; text-align: center; vertical-align: middle; padding: 0 3mm; }" +
                ".logo-img { width: 20mm; height: 20mm; object-fit: contain; }" +
                ".header-left-text { font-size: 8pt; font-weight: bold; }" +
                ".univ-name { font-size: 13pt; font-weight: bold; text-transform: uppercase; }" +
                ".ifri-name { font-size: 9pt; font-weight: bold; text-transform: uppercase; margin-top: 1mm; }" +
                ".header-addr { font-size: 7.5pt; margin-top: 1mm; }" +

                // Document number
                ".doc-number-line { text-align: center; font-size: 8pt; margin: 2mm 0 1.5mm 0; font-style: italic; }" +
                ".doc-ref { font-weight: bold; border: 1px solid #555; padding: 0.3mm 2.5mm; font-size: 8.5pt; }" +

                // Student block
                ".student-block { display: table; width: 100%; margin: 1.5mm 0; padding: 2mm 2.5mm; border: 1px solid #ccc; }" +
                ".qr-box { display: table-cell; width: 20mm; vertical-align: middle; text-align: center; }" +
                ".qr-img { width: 20mm; height: 20mm; }" +
                ".qr-placeholder { width: 20mm; height: 20mm; border: 1px solid #aaa; font-size: 6pt; text-align: center; color: #666; }" +
                ".info-table { display: table-cell; vertical-align: top; padding-left: 3mm; }" +
                ".info-row { display: table; width: 100%; margin-bottom: 0.9mm; font-size: 8pt; }" +
                ".info-lbl { display: table-cell; width: 46mm; color: #222; }" +
                ".info-sep { display: table-cell; width: 5mm; text-align: center; }" +
                ".info-val { display: table-cell; color: #111; }" +

                // Title
                ".releve-title { text-align: center; font-size: 10.5pt; font-weight: bold; font-style: italic; text-decoration: underline; margin: 2.5mm 0 1.5mm 0; }" +

                // Table - CORRECTION COUPURE DROITE (10+38+8+12+6+26=100%)
                ".notes { width: 100%; border-collapse: collapse; font-size: 7pt; table-layout: fixed; word-wrap: break-word; }" +
                ".notes thead tr { background-color: #1a3a5c; color: #ffffff; }" +
                ".notes thead th { padding: 1.2mm 0.5mm; text-align: center; font-weight: bold; border: 0.5px solid #4a6a8c; font-size: 6.8pt; overflow: hidden; word-wrap: break-word; }" +
                ".row-ue td { background-color: #b8cce4; font-weight: bold; padding: 1mm 0.5mm; border: 0.5px solid #7a9ec0; font-size: 6.8pt; overflow: hidden; word-wrap: break-word; }" +
                ".row-ecu td { background-color: #dce6f1; padding: 0.8mm 0.5mm; border: 0.5px solid #aac4e0; font-size: 6.5pt; overflow: hidden; word-wrap: break-word; }" +
                ".cell-code { text-align: center; width: 10%; }" +
                ".cell-code-ecu { text-align: center; width: 10%; background-color: #dce6f1; border: 0.5px solid #aac4e0; padding: 0.8mm; }" +
                ".cell-intitule { text-align: left; width: 38%; }" +
                ".ecu-indent { padding-left: 4mm; font-style: italic; }" +
                ".cell-num { text-align: center; width: 8%; }" +
                ".cell-num-ecu { text-align: center; width: 8%; background-color: #dce6f1; border: 0.5px solid #aac4e0; padding: 0.8mm; }" +
                ".cell-cote { text-align: center; width: 6%; font-weight: bold; }" +
                ".cell-result { text-align: center; width: 26%; font-size: 6.5pt; word-wrap: break-word; overflow: hidden; }" +
                ".cell-empty { background-color: #dce6f1; border: 0.5px solid #aac4e0; }" +

                // Summary bar
                ".summary-bar { display: table; width: 100%; background-color: #1a3a5c; color: #ffffff; padding: 2mm 3mm; }" +
                ".sum-item { display: table-cell; text-align: center; padding: 1mm; }" +
                ".sum-label { font-size: 6.8pt; display: block; }" +
                ".sum-val { font-size: 9pt; font-weight: bold; display: block; }" +

                // Legend
                ".legend { font-size: 5.5pt; color: #333; margin-top: 1.5mm; line-height: 1.5; }" +

                // Date
                ".date-line { text-align: right; font-size: 8pt; margin: 2mm 0 1mm 0; font-style: italic; }" +

                // Signatures
                ".signatures { display: table; width: 100%; margin-top: 1mm; }" +
                ".sig-block { display: table-cell; text-align: center; width: 50%; vertical-align: top; }" +
                ".sig-title { font-size: 8.5pt; font-weight: bold; }" +
                ".sig-subtitle { font-size: 7.5pt; font-style: italic; color: #444; margin-bottom: 1mm; }" +
                ".sig-name { font-size: 8pt; font-weight: bold; margin-top: 0.5mm; }" +
                ".stamp-img { width: 28mm; height: 28mm; object-fit: contain; margin: 1mm auto; display: block; }" +
                "</style></head><body>" +
                "<div class='page'>" +

                // ── En-tête avec logo IFRI ──
                "<div class='header'>" +
                "<div class='header-left'>" + logoIfri + "</div>" +
                "<div class='header-center'>" +
                "<div class='univ-name'>Universite d'Abomey-Calavi</div>" +
                "<div class='ifri-name'>Institut de Formation et de Recherche en Informatique</div>" +
                "<div class='header-addr'>BP: 526 COTONOU - TEL : (+229) 55-028-070</div>" +
                "<div class='header-addr'>Site web : https://www.ifri-uac.bj - Courriel : contact@ifri.uac.bj</div>" +
                "</div>" +
                "<div class='header-right'>UAC</div>" +
                "</div>" +

                // Numéro document
                "<div class='doc-number-line'>N : <span class='doc-ref'>" + reference + "</span></div>" +

                // Bloc étudiant
                "<div class='student-block'>" +
                "<div class='qr-box'>" + qrSrc + "</div>" +
                "<div class='info-table'>" +
                buildInfoRow("Annee academique", anneeAcademique) +
                buildInfoRow("Domaine", "Sciences et Technologies") +
                buildInfoRow("Grade", "Licence") +
                buildInfoRow("Mention", "Informatique") +
                buildInfoRow("Specialite", etudiant.getFiliere() != null ? etudiant.getFiliere() : "-") +
                buildInfoRow("Nom et Prenoms", etudiant.getNom() + " " + etudiant.getPrenom()) +
                buildInfoRow("Numero matricule", String.valueOf(etudiant.getMatricule())) +
                buildInfoRow("Niveau", etudiant.getNiveau() != null ? etudiant.getNiveau() : "-") +
                "</div>" +
                "</div>" +

                // Titre
                "<div class='releve-title'>Releve de notes du " + semestreLabel + " semestre</div>" +

                // Tableau
                "<table class='notes'>" +
                "<thead><tr>" +
                "<th style='width:10%'>Code UE</th>" +
                "<th style='width:38%'>Intitule UE/ECU</th>" +
                "<th style='width:8%'>Credit</th>" +
                "<th style='width:12%'>Moy. UE/ECU</th>" +
                "<th style='width:6%'>Cote</th>" +
                "<th style='width:26%'>Resultat</th>" +
                "</tr></thead>" +
                "<tbody>" + tableRows + "</tbody>" +
                "</table>" +

                // Barre récapitulative
                "<div class='summary-bar'>" +
                "<div class='sum-item'><span class='sum-label'>Credits capitalises</span><span class='sum-val'>" + String.format("%.2f", lastCreditsCapitalises) + " %</span></div>" +
                "<div class='sum-item'><span class='sum-label'>Moyenne semestrielle ponderee</span><span class='sum-val'>" + String.format("%.2f", lastMoyenne) + " / 20</span></div>" +
                "<div class='sum-item'><span class='sum-label'>Decision du jury</span><span class='sum-val'>" + lastDecision + "</span></div>" +
                "</div>" +

                // Légende
                "<div class='legend'>" +
                "(UE = Unite d Enseignement) et (ECU = Element Constitutif d Unite d Enseignement) " +
                "|16,20|=A+ / 16=A / |15,16|=A- / 14=B+ / |13,14|=B- / |12,13|=C+ / 12=C / |11,12|=C- / |10,11|=D+ / |05,10|=D / |00,05|=F" +
                "</div>" +

                // Date
                "<div class='date-line'>Abomey-Calavi, le <b>" + dateGeneration + "</b></div>" +

                // ── Signatures avec tampons ──
                "<div class='signatures'>" +
                "<div class='sig-block'>" +
                "<div class='sig-title'>Le Directeur-Adjoint,</div>" +
                "<div class='sig-subtitle'>" + (directeurAdjointTitre != null ? directeurAdjointTitre : "Charge des affaires academiques") + "</div>" +
                tampanDA +
                "</div>" +
                "<div class='sig-block'>" +
                "<div class='sig-title'>Le Directeur,</div>" +
                tampanDIR +
                "</div>" +
                "</div>" +

                "</div></body></html>";
    }

    private String buildInfoRow(String label, String valeur) {
        return "<div class='info-row'>" +
                "<span class='info-lbl'>" + label + "</span>" +
                "<span class='info-sep'>:</span>" +
                "<span class='info-val'>" + valeur + "</span>" +
                "</div>";
    }

    // ── Générer bulletin PDF ──
    public String generateBulletin(Usager etudiant, String reference, int semestre,
                                   String directeurNom, String directeurAdjointNom,
                                   String directeurAdjointTitre) throws IOException, DocumentException {
        ensureOutputDir();

        int annee = LocalDate.now().getYear();
        String anneeAcademique = (annee - 1) + "-" + annee;
        String qrBase64 = generateQRBase64("https://campusdocs.com/verifier/" + reference);

        String html = buildBulletinHtml(etudiant, reference, semestre, anneeAcademique,
                qrBase64, directeurNom, directeurAdjointNom, directeurAdjointTitre);

        String outputPath = storageConfig.getPdfsDir() + "/" + reference + ".pdf";

        try (OutputStream os = new FileOutputStream(outputPath)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(os);
        } catch (com.lowagie.text.DocumentException e) {
            throw new RuntimeException(e);
        }

        return outputPath;
    }

    // ── Générer relevé de notes — identique au bulletin (logo IFRI + tampons + coupure corrigée) ──
    public String generateReleveDeNotes(Usager etudiant, ActeAdministratif acte, String reference)
            throws IOException, WriterException {
        ensureOutputDir();

        int annee = LocalDate.now().getYear();
        String anneeAcademique = (annee - 1) + "-" + annee;
        String qrBase64 = generateQRBase64("https://campusdocs.com/verifier/" + reference);

        // Semestre par défaut : 1 (peut être adapté si tu stockes le semestre dans ActeAdministratif)
        int semestre = 1;

        String html = buildBulletinHtml(etudiant, reference, semestre, anneeAcademique,
                qrBase64, "Le Directeur", "Le Directeur Adjoint", "Charge des affaires academiques");

        String outputPath = storageConfig.getPdfsDir() + "/" + reference + ".pdf";

        try (OutputStream os = new FileOutputStream(outputPath)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(os);
        } catch (com.lowagie.text.DocumentException e) {
            throw new RuntimeException(e);
        }

        return outputPath;
    }

    // ── Générer attestation d'inscription ──
    public String generateAttestationInscription(Usager etudiant, String reference, String anneeAcademique)
            throws IOException, WriterException {
        ensureOutputDir();

        String outputPath = storageConfig.getPdfsDir() + "/" + reference + ".pdf";
        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale.FRENCH);
        String dateGeneration = LocalDate.now().format(formatter);

        document.add(new Paragraph("UNIVERSITE D'ABOMEY CALAVI")
                .setBold().setFontSize(13).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Vice-Rectorat\nCharge des affaires academiques")
                .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
        document.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine()));
        document.add(new Paragraph("ATTESTATION D'INSCRIPTION")
                .setBold().setFontSize(14).setUnderline().setTextAlignment(TextAlignment.CENTER));

        String nomComplet = etudiant.getNom().toUpperCase() + " " + etudiant.getPrenom();
        document.add(new Paragraph(
                "Le Vice-Recteur charge des Affaires Academiques de l'Universite d'Abomey-Calavi atteste " +
                        "que le nomme " + nomComplet + ", est inscrit(e) sous le numero matricule " +
                        etudiant.getMatricule() + " en " + etudiant.getFiliere() +
                        " au titre de l'annee academique : " + anneeAcademique + ".")
                .setFontSize(10.5f).setTextAlignment(TextAlignment.JUSTIFIED));

        document.add(new Paragraph(
                "Cette attestation a ete delivree a l'interesse(e) pour servir et valoir ce que de droit.")
                .setFontSize(10.5f));

        byte[] qrBytes = generateQRBytes("https://campusdocs.com/verifier/" + reference);
        Image qrImage = new Image(ImageDataFactory.create(qrBytes));
        qrImage.setWidth(80).setHeight(80);
        document.add(qrImage);

        document.add(new Paragraph("Fait a Abomey-Calavi le " + dateGeneration)
                .setItalic().setFontSize(10).setTextAlignment(TextAlignment.RIGHT));
        document.add(new Paragraph("Professeur Tahirou DJARA")
                .setBold().setFontSize(9.5f).setTextAlignment(TextAlignment.RIGHT));

        document.close();
        return outputPath;
    }
}