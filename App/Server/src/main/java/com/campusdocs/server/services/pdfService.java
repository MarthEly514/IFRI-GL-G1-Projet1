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

        // ── QR Code ──
        String qrSrc = qrBase64 != null
                ? "<img src='" + qrBase64 + "' style='width:18mm;height:18mm;' alt='QR'/>"
                : "<div style='width:18mm;height:18mm;border:1px solid #aaa;font-size:5pt;text-align:center;color:#666;'>QR</div>";

        // ── Assets (tampons + logos) ──
        String daBase64  = loadImageAsBase64("assets/DA.png");
        String dirBase64 = loadImageAsBase64("assets/DIR.png");
        String ifriBase64= loadImageAsBase64("assets/IFRI.png");
        String uacBase64 = loadImageAsBase64("assets/UAC.png");

        String tampanDA = daBase64 != null
                ? "<img src='data:image/png;base64," + daBase64 + "' style='width:26mm;height:26mm;display:block;margin:1mm auto;' alt='Tampon DA'/>"
                : "<div style='width:26mm;height:26mm;border:1px dashed #999;margin:1mm auto;font-size:6pt;color:#888;text-align:center;'>Tampon DA</div>";

        String tampanDIR = dirBase64 != null
                ? "<img src='data:image/png;base64," + dirBase64 + "' style='width:26mm;height:26mm;display:block;margin:1mm auto;' alt='Tampon DIR'/>"
                : "<div style='width:26mm;height:26mm;border:1px dashed #999;margin:1mm auto;font-size:6pt;color:#888;text-align:center;'>Tampon DIR</div>";

        // Logo gauche : IFRI
        String logoLeft = ifriBase64 != null
                ? "<img src='data:image/png;base64," + ifriBase64 + "' style='width:18mm;height:18mm;object-fit:contain;' alt='IFRI'/>"
                : "<span style='font-size:8pt;font-weight:bold;'>IFRI</span>";

        // Logo droite : UAC
        String logoRight = uacBase64 != null
                ? "<img src='data:image/png;base64," + uacBase64 + "' style='width:18mm;height:18mm;object-fit:contain;' alt='UAC'/>"
                : "<span style='font-size:8pt;font-weight:bold;'>UAC</span>";

        // ────────────────────────────────────────────────────────────────
        // CSS — Flying Saucer (ITextRenderer) compatible
        // • @page supprime toutes les marges du moteur PDF
        // • .page occupe exactement 210mm × 297mm, padding interne 5mm/6mm
        // • table-layout:fixed + word-wrap:break-word évite les débordements
        // ────────────────────────────────────────────────────────────────
        String css =
                "@page { size: 210mm 297mm; margin: 0; }" +
                        "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                        "html, body { width: 210mm; margin: 0; padding: 0; font-family: 'Times New Roman', Times, serif; color: #111; font-size: 8pt; background: #ffc8c8; }" +
                        ".page { width: 210mm; padding: 5mm 6mm 5mm 6mm; background: #ffc8c8; }" +

                        /* ── En-tête ── */
                        ".hdr { width: 100%; border-collapse: collapse; margin-bottom: 2mm; }" +
                        ".hdr td { vertical-align: middle; }" +
                        ".hdr-left  { width: 22mm; text-align: center; }" +
                        ".hdr-center { text-align: center; padding: 0 2mm; }" +
                        ".hdr-right { width: 22mm; text-align: center; }" +
                        ".univ-name { font-size: 11pt; font-weight: bold; text-transform: uppercase; }" +
                        ".ifri-name { font-size: 8pt; font-weight: bold; text-transform: uppercase; margin-top: 1mm; }" +
                        ".hdr-addr  { font-size: 6.5pt; margin-top: 0.5mm; }" +

                        /* ── Numéro document ── */
                        ".doc-num { text-align: center; font-size: 7.5pt; margin: 1.5mm 0; font-style: italic; }" +
                        ".doc-ref  { font-weight: bold; font-style: normal; border: 1px solid #555; padding: 0.2mm 2mm; font-size: 7.5pt; font-family: 'Courier New', Courier, monospace; }" +

                        /* ── Bloc étudiant ── */
                        ".stud { width: 100%; border-collapse: collapse; border: 1px solid #ccc; margin: 1.5mm 0; }" +
                        ".stud td { vertical-align: top; padding: 1.5mm; }" +
                        ".stud-qr  { width: 22mm; vertical-align: middle; text-align: center; }" +
                        ".stud-info { }" +
                        ".info-row { margin-bottom: 0.8mm; font-size: 7.5pt; }" +
                        ".info-lbl { display: inline-block; width: 46mm; color: #222; }" +
                        ".info-val { color: #111; }" +
                        ".info-val-bold { color: #111; font-weight: bold; }" +

                        /* ── Titre relevé ── */
                        ".releve-title { text-align: center; font-size: 9.5pt; font-weight: bold; font-style: italic; text-decoration: underline; margin: 2mm 0 1.5mm 0; }" +

            /* ── Tableau des notes ──
               CLEF : table-layout fixed + word-wrap + overflow hidden
               Les largeurs de colonnes sont exprimées en % et totalisent 100 % ── */
                        ".notes { width: 100%; border-collapse: collapse; font-size: 6.8pt; table-layout: fixed; }" +
                        ".notes thead tr { background-color: #1a3a5c; color: #fff; }" +
                        ".notes th { padding: 1mm 0.5mm; text-align: center; font-weight: bold; border: 0.5px solid #4a6a8c; font-size: 6.5pt; overflow: hidden; word-wrap: break-word; }" +
                        ".row-ue td { background-color: #b8cce4; font-weight: bold; padding: 1mm 0.5mm; border: 0.5px solid #7a9ec0; font-size: 6.8pt; overflow: hidden; word-wrap: break-word; }" +
                        ".row-ecu td { background-color: #dce6f1; padding: 0.7mm 0.5mm; border: 0.5px solid #aac4e0; font-size: 6.3pt; overflow: hidden; word-wrap: break-word; }" +

                        /* Colonnes — total = 100 % */
                        ".c-code { text-align: center; width: 11%; }" +
                        ".c-intit { text-align: left;   width: 36%; }" +
                        ".c-cred  { text-align: center; width:  7%; }" +
                        ".c-moy   { text-align: center; width: 12%; }" +
                        ".c-cote  { text-align: center; width:  6%; font-weight: bold; }" +
                        ".c-res   { text-align: center; width: 28%; font-size: 6.3pt; word-wrap: break-word; }" +

                        /* Cellules ECU (alignement intitulé + colonnes vides) */
                        ".cell-code     { text-align: center; overflow: hidden; word-wrap: break-word; }" +
                        ".cell-code-ecu { text-align: center; overflow: hidden; word-wrap: break-word; font-style: italic; }" +
                        ".cell-intitule { text-align: left;   overflow: hidden; word-wrap: break-word; }" +
                        ".ecu-indent    { padding-left: 3mm !important; font-style: italic; }" +
                        ".cell-num      { text-align: center; overflow: hidden; word-wrap: break-word; }" +
                        ".cell-num-ecu  { text-align: center; overflow: hidden; word-wrap: break-word; }" +
                        ".cell-cote     { text-align: center; font-weight: bold; overflow: hidden; }" +
                        ".cell-result   { text-align: center; overflow: hidden; word-wrap: break-word; font-size: 6.3pt; }" +
                        ".cell-empty    { background-color: #dce6f1; border: 0.5px solid #aac4e0; }" +

                        /* ── Barre récapitulative ── */
                        ".sum-bar { width: 100%; border-collapse: collapse; background-color: #1a3a5c; color: #fff; }" +
                        ".sum-bar td { text-align: center; padding: 1.5mm 1mm; width: 33%; }" +
                        ".sum-label { font-size: 6pt; display: block; }" +
                        ".sum-val   { font-size: 9pt; font-weight: bold; display: block; }" +

                        /* ── Légende ── */
                        ".legend { font-size: 5pt; color: #333; margin-top: 1.5mm; line-height: 1.4; }" +

                        /* ── Date ── */
                        ".date-line { text-align: right; font-size: 7pt; margin: 1.5mm 0 1mm 0; font-style: italic; }" +

                        /* ── Signatures ── */
                        ".sigs { width: 100%; border-collapse: collapse; margin-top: 1mm; }" +
                        ".sigs td { text-align: center; vertical-align: top; width: 50%; padding: 0 2mm; }" +
                        ".sig-title    { font-size: 8pt; font-weight: bold; }" +
                        ".sig-subtitle { font-size: 7pt; font-style: italic; color: #444; margin-bottom: 1mm; }" +
                        ".sig-name     { font-size: 7.5pt; font-weight: bold; margin-top: 0.5mm; }";

        return "<?xml version='1.0' encoding='UTF-8'?>" +
                "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>" +
                "<html xmlns='http://www.w3.org/1999/xhtml' lang='fr'>" +
                "<head><meta charset='UTF-8'/><style>" + css + "</style></head>" +
                "<body>" +
                "<div class='page'>" +

                /* ── En-tête : IFRI | Texte centré | UAC ── */
                "<table class='hdr'><tr>" +
                "<td class='hdr-left'>" + logoLeft + "</td>" +
                "<td class='hdr-center'>" +
                "<div class='univ-name'>Universite d'Abomey-Calavi</div>" +
                "<div class='ifri-name'>Institut de Formation et de Recherche en Informatique</div>" +
                "<div class='hdr-addr'>BP: 526 COTONOU - TEL : (+229) 55-028-070</div>" +
                "<div class='hdr-addr'>Site web : https://www.ifri-uac.bj - Courriel : contact@ifri.uac.bj</div>" +
                "</td>" +
                "<td class='hdr-right'>" + logoRight + "</td>" +
                "</tr></table>" +

                /* ── Numéro document ── */
                "<div class='doc-num'>N : <span class='doc-ref'>" + reference + "</span></div>" +

                /* ── Bloc étudiant ── */
                "<table class='stud'><tr>" +
                "<td class='stud-qr'>" + qrSrc + "</td>" +
                "<td class='stud-info'>" +
                "<div class='info-row'><span class='info-lbl'>Annee academique</span>: <span class='info-val'>" + anneeAcademique + "</span></div>" +
                "<div class='info-row'><span class='info-lbl'>Domaine</span>: <span class='info-val'>Sciences et Technologies</span></div>" +
                "<div class='info-row'><span class='info-lbl'>Grade</span>: <span class='info-val'>Licence</span></div>" +
                "<div class='info-row'><span class='info-lbl'>Mention</span>: <span class='info-val'>Informatique</span></div>" +
                "<div class='info-row'><span class='info-lbl'>Specialite</span>: <span class='info-val'>" + (etudiant.getFiliere() != null ? etudiant.getFiliere() : "Non renseignee") + "</span></div>" +
                "<div class='info-row'><span class='info-lbl'>Nom et Prenoms</span>: <span class='info-val-bold'>" + etudiant.getNom().toUpperCase() + " " + etudiant.getPrenom() + "</span></div>" +
                "<div class='info-row'><span class='info-lbl'>Numero matricule</span>: <span class='info-val'>" + (String.valueOf(etudiant.getMatricule())) + "</span></div>" +
                "<div class='info-row'><span class='info-lbl'>Niveau</span>: <span class='info-val'>" + (etudiant.getNiveau() != null ? etudiant.getNiveau() : "Non renseignee") + "</span></div>" +
                "</td>" +
                "</tr></table>" +

                /* ── Titre ── */
                "<div class='releve-title'>Releve de notes du " + semestreLabel + " semestre</div>" +

                /* ── Tableau des notes ── */
                "<table class='notes'>" +
                "<thead><tr>" +
                "<th class='c-code'>Code UE</th>" +
                "<th class='c-intit'>Intitule UE/ECU</th>" +
                "<th class='c-cred'>Credit</th>" +
                "<th class='c-moy'>Moy. UE/ECU</th>" +
                "<th class='c-cote'>Cote</th>" +
                "<th class='c-res'>Resultat</th>" +
                "</tr></thead>" +
                "<tbody>" + tableRows + "</tbody>" +
                "</table>" +

                /* ── Barre récapitulative ── */
                "<table class='sum-bar'><tr>" +
                "<td><span class='sum-label'>Credits capitalises</span><span class='sum-val'>" + String.format("%.2f", lastCreditsCapitalises) + " %</span></td>" +
                "<td><span class='sum-label'>Moyenne semestrielle ponderee</span><span class='sum-val'>" + String.format("%.2f", lastMoyenne) + " / 20</span></td>" +
                "<td><span class='sum-label'>Decision du jury</span><span class='sum-val'>" + lastDecision + "</span></td>" +
                "</tr></table>" +

                /* ── Légende ── */
                "<div class='legend'>" +
                "(UE = Unite d Enseignement) et (ECU = Element Constitutif d Unite d Enseignement) " +
                "|16,20|=A+ / 16=A / |15,16|=A- / 14=B+ / |13,14|=B- / |12,13|=C+ / 12=C / |11,12|=C- / |10,11|=D+ / |05,10|=D / |00,05|=F" +
                "</div>" +

                /* ── Date ── */
                "<div class='date-line'>Abomey-Calavi, le <b>" + dateGeneration + "</b></div>" +

                /* ── Signatures ── */
                "<table class='sigs'><tr>" +
                "<td>" +
                "<div class='sig-title'>Le Directeur-Adjoint,</div>" +
                "<div class='sig-subtitle'>" + (directeurAdjointTitre != null ? directeurAdjointTitre : "Charge des affaires academiques") + "</div>" +
                tampanDA +
                "<div class='sig-name'>" + (directeurAdjointNom != null ? directeurAdjointNom : "Le Directeur Adjoint") + "</div>" +
                "</td>" +
                "<td>" +
                "<div class='sig-title'>Le Directeur,</div>" +
                "<div class='sig-subtitle'>&#160;</div>" +
                tampanDIR +
                "<div class='sig-name'>" + (directeurNom != null ? directeurNom : "Le Directeur") + "</div>" +
                "</td>" +
                "</tr></table>" +

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