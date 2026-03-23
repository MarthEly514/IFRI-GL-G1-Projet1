/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package com.campusdocs.client.controller;

import com.campusdocs.client.SessionManager;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.Demande;
import com.campusdocs.client.service.DemandeService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DemandeFormViewController implements Initializable {

    @FXML private VBox rootPane;

    // ── Stepper — 5 steps ──
    @FXML private Circle stepCircle1, stepCircle2, stepCircle3, stepCircle4;
    @FXML private Label  stepLabel1,  stepLabel2,  stepLabel3,  stepLabel4;
    @FXML private Line   stepLine1,   stepLine2,   stepLine3;

    // ── Step panes ──
    @FXML private ScrollPane step1, step2, step3, step4;

    // ── Step 1 ──
    @FXML private GridPane docTypeGrid;

    // ── Step 2 ──
    @FXML private Label step2Subtitle;
    @FXML private VBox dynamicFormContainer;

    // ── Step 3 — Justificatifs ──
    @FXML private Label justifSubtitle;
    @FXML private VBox justifContainer;
    @FXML private Label justifErrorLabel;

    // ── Step 4 — Overview ──
    @FXML private Label overviewDocType, overviewAmount;
    @FXML private VBox overviewFields;
    @FXML private Button btnDownloadPdf;

    // ── Footer ──
    @FXML private Button btnPrecedent, btnSuivant;

    // ── State ──
    private int currentStep = 1;
    private String selectedDocType = null;
    private File selectedPaymentFile = null;
    private final Map<String, String> formValues    = new LinkedHashMap<>();
    private final Map<String, File>   uploadedFiles = new LinkedHashMap<>();

    // ── Doc types ──
    private static final List<DocType> DOC_TYPES = Arrays.asList(
        new DocType("/icons/badge_black.png",      "Attestation de scolarité", "Justificatif d'inscription",    "attestation"),
        new DocType("/icons/license_black.png",    "Relevé de notes",          "Notes par semestre",            "releve"),
        new DocType("/icons/school_black.png",     "Diplôme",                  "Copie officielle du diplôme",   "diplome"),
        new DocType("/icons/home_black.png",       "Autorisation d'absence",   "Justificatif en cas d'absence", "justificatif"),
        new DocType("/icons/folder_black.png",     "Dossier d'admission",      "Demande d'inscription",         "admission"),
        new DocType("/icons/assignment_black.png", "Rapport de stage",         "Validation de stage",           "rapport")
    );

    // ── Required justificatifs per doc type ──
    private static final Map<String, List<Justificatif>> JUSTIFICATIFS = new HashMap<>();
    static {
        JUSTIFICATIFS.put("attestation", Arrays.asList(
            new Justificatif("cni",           "Carte Nationale d'Identité",    true),
            new Justificatif("photo",         "Photo d'identité (JPG/PNG)",    true),
            new Justificatif("recu_paiement", "Reçu de paiement des frais",   true)
        ));
        JUSTIFICATIFS.put("releve", Arrays.asList(
            new Justificatif("cni",           "Carte Nationale d'Identité",    true),
            new Justificatif("recu_paiement", "Reçu de paiement des frais",   true)
        ));
        JUSTIFICATIFS.put("diplome", Arrays.asList(
            new Justificatif("cni",           "Carte Nationale d'Identité",    true),
            new Justificatif("acte_naissance","Acte de naissance",             true),
            new Justificatif("photo",         "Photo d'identité",              true),
            new Justificatif("recu_paiement", "Reçu de paiement des frais",   true)
        ));
        JUSTIFICATIFS.put("justificatif", Arrays.asList(
            new Justificatif("cni",            "Carte Nationale d'Identité",   true),
            new Justificatif("justif_absence", "Justificatif d'absence",       true)
        ));
        JUSTIFICATIFS.put("admission", Arrays.asList(
            new Justificatif("cni",           "Carte Nationale d'Identité",    true),
            new Justificatif("releve_bac",    "Relevé du Baccalauréat",        true),
            new Justificatif("photo",         "Photo d'identité",              true),
            new Justificatif("acte_naissance","Acte de naissance",             false)
        ));
        JUSTIFICATIFS.put("rapport", Arrays.asList(
            new Justificatif("rapport_pdf",   "Rapport de stage (PDF)",        true),
            new Justificatif("convention",    "Convention de stage signée",    true),
            new Justificatif("cni",           "Carte Nationale d'Identité",    false)
        ));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CssLoader.loadCssFiles(rootPane, "demandeformview", "globalStyles");
        buildDocTypeGrid();
        updateStepperUI();
        updateFooterButtons();
    }

    // ═════════════════════════════════════════
    // STEP 1 — Doc type
    // ═════════════════════════════════════════
    private void buildDocTypeGrid() {
        docTypeGrid.getChildren().clear();
        int col = 0, row = 0;
        for (DocType dt : DOC_TYPES) {
            VBox card = buildDocTypeCard(dt);
            docTypeGrid.add(card, col, row);
            col++;
            if (col == 3) { col = 0; row++; }
        }
    }

    private VBox buildDocTypeCard(DocType dt) {
        VBox card = new VBox(8);
        card.getStyleClass().add("doc-type-card");
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setMaxWidth(Double.MAX_VALUE);

        ImageView iconView = loadDocTypeIcon(dt.iconPath);
        if (iconView != null) {
            card.getChildren().add(iconView);
        } else {
            Label fallback = new Label("📄");
            fallback.getStyleClass().add("doc-type-icon");
            card.getChildren().add(fallback);
        }

        Label name = new Label(dt.name);
        name.getStyleClass().add("doc-type-name");
        name.setWrapText(true);
        name.setAlignment(javafx.geometry.Pos.CENTER);

        Label desc = new Label(dt.description);
        desc.getStyleClass().add("doc-type-desc");
        desc.setWrapText(true);
        desc.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(name, desc);
        card.setOnMouseClicked(e -> selectDocType(dt, card));
        return card;
    }

    private void selectDocType(DocType dt, VBox card) {
        docTypeGrid.getChildren().forEach(n ->
            n.getStyleClass().remove("doc-type-card-selected"));
        card.getStyleClass().add("doc-type-card-selected");
        selectedDocType = dt.key;

        javafx.animation.PauseTransition pause =
            new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
        pause.setOnFinished(e -> goToStep(2));
        pause.play();
    }

    // ═════════════════════════════════════════
    // STEP 2 — Informations
    // ═════════════════════════════════════════
    private void buildDynamicForm() {
        dynamicFormContainer.getChildren().clear();
        formValues.clear();

        String docName = DOC_TYPES.stream()
            .filter(d -> d.key.equals(selectedDocType))
            .findFirst().map(d -> d.name).orElse("");
        step2Subtitle.setText("Complétez les informations pour : " + docName);

        for (FormField f : getFieldsForDocType(selectedDocType)) {
            VBox group = new VBox(6);
            Label label = new Label(f.label);
            label.getStyleClass().add("field-label");
            TextField tf = new TextField();
            tf.setPromptText(f.placeholder);
            tf.getStyleClass().add("input-field");
            tf.textProperty().addListener((obs, o, n) -> formValues.put(f.key, n));
            group.getChildren().addAll(label, tf);
            dynamicFormContainer.getChildren().add(group);
        }
    }

    private List<FormField> getFieldsForDocType(String key) {
        switch (key) {
            case "attestation":
                return Arrays.asList(
                    new FormField("Numéro étudiant",  "ex: 20210001",    "num_etudiant", "text"),
                    new FormField("Année académique", "ex: 2025-2026",   "annee",        "text"),
                    new FormField("Niveau",           "ex: Licence 2",   "niveau",       "text"),
                    new FormField("Motif",            "ex: Banque, Visa","motif",        "text")
                );
            case "releve":
                return Arrays.asList(
                    new FormField("Numéro étudiant",  "ex: 20210001",    "num_etudiant", "text"),
                    new FormField("Semestre",         "ex: S5",          "semestre",     "text"),
                    new FormField("Année académique", "ex: 2025-2026",   "annee",        "text")
                );
            case "diplome":
                return Arrays.asList(
                    new FormField("Numéro étudiant",  "ex: 20210001",    "num_etudiant", "text"),
                    new FormField("Année d'obtention","ex: 2024",        "annee",        "text"),
                    new FormField("Intitulé",         "ex: Licence Info","intitule",     "text")
                );
            default:
                return Arrays.asList(
                    new FormField("Numéro étudiant",  "ex: 20210001",    "num_etudiant", "text"),
                    new FormField("Motif",            "Précisez…",       "motif",        "text")
                );
        }
    }

    // ═════════════════════════════════════════
    // STEP 3 — Justificatifs
    // ═════════════════════════════════════════
    private void buildJustificatifsForm() {
        justifContainer.getChildren().clear();
        uploadedFiles.clear();
        hideJustifError();

        String docName = DOC_TYPES.stream()
            .filter(d -> d.key.equals(selectedDocType))
            .findFirst().map(d -> d.name).orElse("");
        justifSubtitle.setText("Documents requis pour : " + docName);

        List<Justificatif> list = JUSTIFICATIFS.getOrDefault(
            selectedDocType, new ArrayList<>());

        if (list.isEmpty()) {
            Label none = new Label("Aucun document justificatif requis pour ce type.");
            none.getStyleClass().add("activity-sub");
            justifContainer.getChildren().add(none);
            return;
        }

        for (Justificatif j : list) {
            justifContainer.getChildren().add(buildUploadSlot(j));
        }
    }

    private VBox buildUploadSlot(Justificatif j) {
        VBox slot = new VBox(10);
        slot.setStyle(
            "-fx-background-color:#ffffff;" +
            "-fx-background-radius:10;" +
            "-fx-border-color:#d8eeec;" +
            "-fx-border-width:1;" +
            "-fx-border-radius:10;" +
            "-fx-padding:14;"
        );

        // Title + badge
        HBox header = new HBox(8);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label title = new Label(j.label);
        title.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#0d2b2a;");
        HBox.setHgrow(title, Priority.ALWAYS);
        Label badge = new Label(j.required ? "Obligatoire" : "Optionnel");
        badge.getStyleClass().add(j.required ? "badge-rejected" : "badge-pending");
        header.getChildren().addAll(title, badge);

        // Accepted formats hint
        Label hint = new Label("Formats acceptés : PDF, JPG, PNG — max 5 Mo");
        hint.setStyle("-fx-font-size:11px;-fx-text-fill:#a0bab8;");

        // File picker row
        HBox fileRow = new HBox(10);
        fileRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label fileNameLabel = new Label("Aucun fichier sélectionné");
        fileNameLabel.setStyle("-fx-font-size:12px;-fx-text-fill:#7a9e9c;");
        HBox.setHgrow(fileNameLabel, Priority.ALWAYS);

        Button chooseBtn = new Button("📎  Parcourir");
        chooseBtn.getStyleClass().add("choose-file-btn");
        chooseBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choisir : " + j.label);
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                    "Documents", "*.pdf", "*.jpg", "*.jpeg", "*.png"));
            File file = fc.showOpenDialog(null);
            if (file != null) {
                uploadedFiles.put(j.key, file);
                fileNameLabel.setText("✓  " + file.getName());
                fileNameLabel.setStyle(
                    "-fx-font-size:12px;-fx-text-fill:#3cc3bd;-fx-font-weight:bold;");
                chooseBtn.setText("🔄  Modifier");
                hideJustifError();
            }
        });

        fileRow.getChildren().addAll(fileNameLabel, chooseBtn);
        slot.getChildren().addAll(header, hint, fileRow);
        return slot;
    }

    private boolean validateJustificatifs() {
        List<Justificatif> list = JUSTIFICATIFS.getOrDefault(
            selectedDocType, new ArrayList<>());
        List<String> missing = new ArrayList<>();
        for (Justificatif j : list) {
            if (j.required && !uploadedFiles.containsKey(j.key)) {
                missing.add(j.label);
            }
        }
        if (!missing.isEmpty()) {
            showJustifError("Documents manquants :\n• " + String.join("\n• ", missing));
            return false;
        }
        return true;
    }

    private void showJustifError(String msg) {
        if (justifErrorLabel != null) {
            justifErrorLabel.setText(msg);
            justifErrorLabel.setVisible(true);
            justifErrorLabel.setManaged(true);
        }
        System.err.println("Justificatifs: " + msg);
    }

    private void hideJustifError() {
        if (justifErrorLabel != null) {
            justifErrorLabel.setVisible(false);
            justifErrorLabel.setManaged(false);
        }
    }

    // ═════════════════════════════════════════
    // STEP 4 — Overview
    // ═════════════════════════════════════════
    private void buildOverview() {
        String docName = DOC_TYPES.stream()
            .filter(d -> d.key.equals(selectedDocType))
            .findFirst().map(d -> d.name).orElse("—");
        overviewDocType.setText(docName);
        overviewAmount.setText("500 FCFA");
        overviewFields.getChildren().clear();

        formValues.forEach((key, val) -> {
            HBox row = new HBox();
            Label k = new Label(key);
            k.getStyleClass().add("overview-field-key");
            HBox.setHgrow(k, Priority.ALWAYS);
            Label v = new Label(val == null || val.isEmpty() ? "—" : val);
            v.getStyleClass().add("overview-field-val");
            row.getChildren().addAll(k, v);
            overviewFields.getChildren().add(row);
        });

        if (!uploadedFiles.isEmpty()) {
            Label sep = new Label("Pièces jointes");
            sep.setStyle("-fx-font-size:12px;-fx-font-weight:bold;" +
                         "-fx-text-fill:#4a7a78;-fx-padding:8 0 4 0;");
            overviewFields.getChildren().add(sep);
            uploadedFiles.forEach((key, file) -> {
                HBox row = new HBox();
                Label k = new Label("📎  " + key);
                k.getStyleClass().add("overview-field-key");
                HBox.setHgrow(k, Priority.ALWAYS);
                Label v = new Label(file.getName());
                v.getStyleClass().add("overview-field-val");
                row.getChildren().addAll(k, v);
                overviewFields.getChildren().add(row);
            });
        }
    }

    @FXML
    private void handleDownloadPdf() {
        // Ask user where to save
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le récapitulatif");
        fc.setInitialFileName("demande_" + selectedDocType + "_"
            + java.time.LocalDate.now() + ".pdf");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fc.showSaveDialog(rootPane.getScene().getWindow());

        if (file == null) return; // user cancelled

        btnDownloadPdf.setDisable(true);

        TaskRunner.run(
            () -> { generatePdf(file); return null; },
            ignored -> {
                btnDownloadPdf.setDisable(false);
                
            },
            ex -> {
                btnDownloadPdf.setDisable(false);
                System.err.println("PDF error: " + ex.getMessage());
            }
        );
    }

    private void generatePdf(File file) throws Exception {
        String docName = DOC_TYPES.stream()
            .filter(d -> d.key.equals(selectedDocType))
            .findFirst().map(d -> d.name).orElse("Document");

        String studentName = SessionManager.getInstance().getFullName();
        String today = java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter
                .ofPattern("dd MMMM yyyy", java.util.Locale.FRENCH));

        // ── iText setup ──────────────────────────────────────────
        com.itextpdf.text.Document pdf =
            new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4);
        com.itextpdf.text.pdf.PdfWriter.getInstance(pdf,
            new java.io.FileOutputStream(file));
        pdf.open();

        // ── Fonts ────────────────────────────────────────────────
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 20,
            com.itextpdf.text.Font.BOLD,
            new com.itextpdf.text.BaseColor(0x3C, 0xC3, 0xBD));

        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 13,
            com.itextpdf.text.Font.BOLD,
            com.itextpdf.text.BaseColor.WHITE);

        com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 10,
            com.itextpdf.text.Font.BOLD,
            new com.itextpdf.text.BaseColor(0x4A, 0x7A, 0x78));

        com.itextpdf.text.Font valueFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 11,
            com.itextpdf.text.Font.NORMAL,
            new com.itextpdf.text.BaseColor(0x0D, 0x2B, 0x2A));

        com.itextpdf.text.Font smallFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 9,
            com.itextpdf.text.Font.NORMAL,
            com.itextpdf.text.BaseColor.GRAY);

        com.itextpdf.text.BaseColor teal =
            new com.itextpdf.text.BaseColor(0x3C, 0xC3, 0xBD);
        com.itextpdf.text.BaseColor tealLight =
            new com.itextpdf.text.BaseColor(0xE6, 0xF7, 0xF6);
        com.itextpdf.text.BaseColor darkBg =
            new com.itextpdf.text.BaseColor(0x0D, 0x2B, 0x2A);

        // ── Header band ──────────────────────────────────────────
        com.itextpdf.text.pdf.PdfPTable headerTable =
            new com.itextpdf.text.pdf.PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20);

        com.itextpdf.text.pdf.PdfPCell headerCell =
            new com.itextpdf.text.pdf.PdfPCell();
        headerCell.setBackgroundColor(darkBg);
        headerCell.setPadding(20);
        headerCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);

        com.itextpdf.text.Paragraph appName =
            new com.itextpdf.text.Paragraph("CAMPUSDOCS",
                new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 10,
                    com.itextpdf.text.Font.BOLD,
                    teal));
        appName.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);

        com.itextpdf.text.Paragraph recapTitle =
            new com.itextpdf.text.Paragraph(
                "RÉCAPITULATIF DE DEMANDE", headerFont);
        recapTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);

        headerCell.addElement(appName);
        headerCell.addElement(recapTitle);
        headerTable.addCell(headerCell);
        pdf.add(headerTable);

        // ── Doc type title ───────────────────────────────────────
        com.itextpdf.text.Paragraph docTitle =
            new com.itextpdf.text.Paragraph(docName, titleFont);
        docTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        docTitle.setSpacingAfter(6);
        pdf.add(docTitle);

        com.itextpdf.text.Paragraph dateP =
            new com.itextpdf.text.Paragraph(
                "Soumis le " + today + "  •  Statut : En attente", smallFont);
        dateP.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        dateP.setSpacingAfter(20);
        pdf.add(dateP);

        // ── Separator line ───────────────────────────────────────
        com.itextpdf.text.pdf.draw.LineSeparator separator =
            new com.itextpdf.text.pdf.draw.LineSeparator(
                1, 100, teal,
                com.itextpdf.text.Element.ALIGN_CENTER, -2);
        pdf.add(new com.itextpdf.text.Chunk(separator));
        pdf.add(com.itextpdf.text.Chunk.NEWLINE);

        // ── Student info section ─────────────────────────────────
        addSectionTitle(pdf, "INFORMATIONS DE L'ÉTUDIANT", teal, headerFont);

        com.itextpdf.text.pdf.PdfPTable infoTable =
            new com.itextpdf.text.pdf.PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f, 2f});
        infoTable.setSpacingAfter(16);

        addTableRow(infoTable, "Nom complet",
            studentName != null ? studentName : "—",
            labelFont, valueFont, tealLight);
        addTableRow(infoTable, "Email",
            SessionManager.getInstance().getEmail() != null
                ? SessionManager.getInstance().getEmail() : "—",
            labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE);
        addTableRow(infoTable, "Matricule",
            SessionManager.getInstance().getMatricule() != null
                ? SessionManager.getInstance().getMatricule() : "—",
            labelFont, valueFont, tealLight);
        pdf.add(infoTable);

        // ── Form fields section ──────────────────────────────────
        addSectionTitle(pdf, "DÉTAILS DE LA DEMANDE", teal, headerFont);

        com.itextpdf.text.pdf.PdfPTable fieldsTable =
            new com.itextpdf.text.pdf.PdfPTable(2);
        fieldsTable.setWidthPercentage(100);
        fieldsTable.setWidths(new float[]{1f, 2f});
        fieldsTable.setSpacingAfter(16);

        boolean alternate = false;
        for (Map.Entry<String, String> entry : formValues.entrySet()) {
            String val = entry.getValue();
            addTableRow(fieldsTable,
                entry.getKey(),
                (val == null || val.isEmpty()) ? "—" : val,
                labelFont, valueFont,
                alternate ? tealLight : com.itextpdf.text.BaseColor.WHITE);
            alternate = !alternate;
        }
        pdf.add(fieldsTable);

        // ── Uploaded files section ───────────────────────────────
        if (!uploadedFiles.isEmpty()) {
            addSectionTitle(pdf, "PIÈCES JOINTES", teal, headerFont);

            com.itextpdf.text.pdf.PdfPTable filesTable =
                new com.itextpdf.text.pdf.PdfPTable(2);
            filesTable.setWidthPercentage(100);
            filesTable.setWidths(new float[]{1f, 2f});
            filesTable.setSpacingAfter(16);

            alternate = false;
            for (Map.Entry<String, File> entry : uploadedFiles.entrySet()) {
                addTableRow(filesTable,
                    entry.getKey(),
                    entry.getValue().getName(),
                    labelFont, valueFont,
                    alternate ? tealLight : com.itextpdf.text.BaseColor.WHITE);
                alternate = !alternate;
            }
            pdf.add(filesTable);
        }

        // ── Footer ───────────────────────────────────────────────
        pdf.add(com.itextpdf.text.Chunk.NEWLINE);
        pdf.add(new com.itextpdf.text.Chunk(separator));
        pdf.add(com.itextpdf.text.Chunk.NEWLINE);

        com.itextpdf.text.Paragraph footer =
            new com.itextpdf.text.Paragraph(
                "Document généré automatiquement par CampusDocs  •  " + today,
                smallFont);
        footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        pdf.add(footer);

        pdf.close();
    }

    // ── PDF helpers ──────────────────────────────────────────────────────
    private void addSectionTitle(
            com.itextpdf.text.Document pdf,
            String text,
            com.itextpdf.text.BaseColor bg,
            com.itextpdf.text.Font font) throws Exception {

        com.itextpdf.text.pdf.PdfPTable t =
            new com.itextpdf.text.pdf.PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(10);
        t.setSpacingAfter(6);

        com.itextpdf.text.pdf.PdfPCell c =
            new com.itextpdf.text.pdf.PdfPCell(
                new com.itextpdf.text.Phrase(text, font));
        c.setBackgroundColor(bg);
        c.setPadding(8);
        c.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        t.addCell(c);
        pdf.add(t);
    }

    private void addTableRow(
            com.itextpdf.text.pdf.PdfPTable table,
            String label,
            String value,
            com.itextpdf.text.Font labelFont,
            com.itextpdf.text.Font valueFont,
            com.itextpdf.text.BaseColor bg) {

        com.itextpdf.text.pdf.PdfPCell labelCell =
            new com.itextpdf.text.pdf.PdfPCell(
                new com.itextpdf.text.Phrase(label, labelFont));
        labelCell.setBackgroundColor(bg);
        labelCell.setPadding(8);
        labelCell.setBorderColor(
            new com.itextpdf.text.BaseColor(0xD8, 0xEE, 0xEC));

        com.itextpdf.text.pdf.PdfPCell valueCell =
            new com.itextpdf.text.pdf.PdfPCell(
                new com.itextpdf.text.Phrase(value, valueFont));
        valueCell.setBackgroundColor(bg);
        valueCell.setPadding(8);
        valueCell.setBorderColor(
            new com.itextpdf.text.BaseColor(0xD8, 0xEE, 0xEC));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    // ═════════════════════════════════════════
    // NAVIGATION
    // ═════════════════════════════════════════
    @FXML private void handleBack() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("DemandeView", "Mes demandes");
    }

    @FXML private void handlePrecedent() {
        if (currentStep > 1) goToStep(currentStep - 1);
    }

    @FXML private void handleSuivant() {
        if (currentStep == 3 && !validateJustificatifs()) return;
        if (currentStep == 4) { submitDemande(); return; } // was 5
        goToStep(currentStep + 1);
    }

   private void goToStep(int step) {
        currentStep = step;

        step1.setVisible(step == 1); step1.setManaged(step == 1);
        step2.setVisible(step == 2); step2.setManaged(step == 2);
        step3.setVisible(step == 3); step3.setManaged(step == 3);
        step4.setVisible(step == 4); step4.setManaged(step == 4);

        if (step == 2) buildDynamicForm();
        if (step == 3) buildJustificatifsForm();
        if (step == 4) buildOverview();   // was step 5

        updateStepperUI();
        updateFooterButtons();
    }
    private void updateStepperUI() {
       List<Circle> circles = Arrays.asList(
           stepCircle1, stepCircle2, stepCircle3, stepCircle4);
       List<Label>  labels  = Arrays.asList(
           stepLabel1, stepLabel2, stepLabel3, stepLabel4);
       List<Line>   lines   = Arrays.asList(
           stepLine1, stepLine2, stepLine3);

       for (int i = 0; i < 4; i++) {
           circles.get(i).getStyleClass().removeAll("step-active", "step-done");
           labels.get(i).getStyleClass().removeAll("step-label-active", "step-label-done");
           if      (i + 1 < currentStep)  { circles.get(i).getStyleClass().add("step-done");  labels.get(i).getStyleClass().add("step-label-done"); }
           else if (i + 1 == currentStep) { circles.get(i).getStyleClass().add("step-active"); labels.get(i).getStyleClass().add("step-label-active"); }
       }
       for (int i = 0; i < 3; i++) {
           lines.get(i).getStyleClass().remove("step-line-done");
           if (i + 1 < currentStep) lines.get(i).getStyleClass().add("step-line-done");
       }
   }

    private void updateFooterButtons() {
        btnPrecedent.setDisable(currentStep == 1);
        btnSuivant.setDisable(currentStep == 1 && selectedDocType == null);
        btnSuivant.setText(currentStep == 4 ? "Valider ✓" : "Suivant →"); // was 5
    }

    // ═════════════════════════════════════════
    // SUBMIT
    // ═════════════════════════════════════════
    private void submitDemande() {
        setLoading(true);
        DemandeService.DemandeRequest request = new DemandeService.DemandeRequest();
        request.type = DOC_TYPES.stream()
            .filter(d -> d.key.equals(selectedDocType))
            .findFirst().map(d -> d.name).orElse("");
        request.motif = formValues.getOrDefault("motif", "");
        request.annee = formValues.getOrDefault("annee", "");
        request.details = "Non spécifié";

        TaskRunner.run(
            () -> {
                try { return DemandeService.submitDemande(request); }
                catch (ApiException e) { throw new RuntimeException(e); }
            },
            demande -> {
            try {
                setLoading(false);
                DashboardViewController db = DashboardViewControllerRegistry.getInstance();
                if (db != null) {
                    db.loadSubView("DemandeView", "Mes demandes");
                    db.getDemandeViewController().onDemandeSubmitted(demande);
                }
                
                UsagerViewController usvc = new UsagerViewController();
                usvc.loadDemandes();
            } catch (Exception ex) {
                Logger.getLogger(DemandeFormViewController.class.getName()).log(Level.SEVERE, null, ex);
            }

            },
            ex -> {
                setLoading(false);
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                System.err.println("Submit error: " + cause.getMessage());
            }
        );
    }

    private void setLoading(boolean loading) {
        btnSuivant.setDisable(loading);
        btnSuivant.setText(loading ? "Envoi en cours..." : "Valider ✓");
    }

    private ImageView loadDocTypeIcon(String path) {
        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream == null) return null;
            ImageView iv = new ImageView(new Image(stream));
            iv.setFitWidth(40); iv.setFitHeight(40); iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) { return null; }
    }

    // ═════════════════════════════════════════
    // INNER CLASSES
    // ═════════════════════════════════════════
    private static class DocType {
        final String iconPath, name, description, key;
        DocType(String iconPath, String name, String description, String key) {
            this.iconPath = iconPath; this.name = name;
            this.description = description; this.key = key;
        }
    }

    private static class FormField {
        final String label, placeholder, key, type;
        FormField(String label, String placeholder, String key, String type) {
            this.label = label; this.placeholder = placeholder;
            this.key = key; this.type = type;
        }
    }

    private static class Justificatif {
        final String key, label;
        final boolean required;
        Justificatif(String key, String label, boolean required) {
            this.key = key; this.label = label; this.required = required;
        }
    }
}
