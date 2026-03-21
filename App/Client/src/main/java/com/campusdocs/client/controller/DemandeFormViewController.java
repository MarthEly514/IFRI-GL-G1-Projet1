/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package com.campusdocs.client.controller;

import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.Demande;
import com.campusdocs.client.service.DemandeService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DemandeFormViewController implements Initializable {
    
    @FXML private VBox rootPane;

    // ── Stepper UI ──
    @FXML private Circle stepCircle1, stepCircle2, stepCircle3, stepCircle4;
    @FXML private Label  stepLabel1,  stepLabel2,  stepLabel3,  stepLabel4;
    @FXML private Line   stepLine1,   stepLine2,   stepLine3;

    // ── Step content panes ──
    @FXML private ScrollPane step1, step2, step3, step4;

    // ── Step 1 ──
    @FXML private GridPane docTypeGrid;

    // ── Step 2 ──
    @FXML private Label step2Subtitle;
    @FXML private VBox dynamicFormContainer;

    // ── Step 3 ──
    @FXML private Button btnPayInApp, btnPayTicket;
    @FXML private VBox payInAppForm, payTicketForm;
    @FXML private TextField cardHolder, cardNumber, cardExpiry, cardCvc;
    @FXML private VBox dropZone;
    @FXML private Label selectedFileName, summaryDocType, summaryFee, summaryTotal;

    // ── Step 4 ──
    @FXML private Label overviewDocType, overviewAmount;
    @FXML private VBox overviewFields;
    @FXML private Button btnDownloadPdf;

    // ── Footer ──
    @FXML private Button btnPrecedent, btnSuivant;

    // ── State ──
    private int currentStep = 1;
    private String selectedDocType = null;
    private File selectedPaymentFile = null;
    private final Map<String, String> formValues = new LinkedHashMap<>();

    // Document types catalog
    private static final List<DocType> DOC_TYPES = Arrays.asList(
        new DocType("📄", "Attestation de scolarité",  "Justificatif d'inscription",  "attestation"),
        new DocType("📋", "Relevé de notes",           "Notes par semestre",          "releve"),
        new DocType("🎓", "Diplôme",                   "Copie officielle du diplôme", "diplome"),
        new DocType("📝", "Certificat de résidence",   "Résidence universitaire",     "certificat"),
        new DocType("🗂️", "Dossier d'admission",       "Demande d'inscription",       "admission"),
        new DocType("📊", "Rapport de stage",          "Validation de stage",         "rapport")
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //load CSS
        CssLoader.loadCssFiles(rootPane, "demandeformview", "globalStyles");
        
        buildDocTypeGrid();
        updateStepperUI();
        updateFooterButtons();
    }

    // ─────────────────────────────────────────
    // STEP 1 — Doc type grid
    // ─────────────────────────────────────────
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

        Label icon = new Label(dt.icon);
        icon.getStyleClass().add("doc-type-icon");

        Label name = new Label(dt.name);
        name.getStyleClass().add("doc-type-name");
        name.setWrapText(true);
        name.setAlignment(javafx.geometry.Pos.CENTER);

        Label desc = new Label(dt.description);
        desc.getStyleClass().add("doc-type-desc");
        desc.setWrapText(true);
        desc.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(icon, name, desc);

        card.setOnMouseClicked(e -> selectDocType(dt, card));
        return card;
    }

    private void selectDocType(DocType dt, VBox card) {
        // Deselect all
        docTypeGrid.getChildren().forEach(n ->
            n.getStyleClass().remove("doc-type-card-selected")
        );
        // Select clicked
        card.getStyleClass().add("doc-type-card-selected");
        selectedDocType = dt.key;

        // Auto advance after short delay
        javafx.animation.PauseTransition pause =
            new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
        pause.setOnFinished(e -> goToStep(2));
        pause.play();
    }

    // ─────────────────────────────────────────
    // STEP 2 — Dynamic form
    // ─────────────────────────────────────────
    private void buildDynamicForm() {
        dynamicFormContainer.getChildren().clear();
        formValues.clear();

        List<FormField> fields = getFieldsForDocType(selectedDocType);
        step2Subtitle.setText("Complétez les informations pour : " +
            DOC_TYPES.stream().filter(d -> d.key.equals(selectedDocType))
                .findFirst().map(d -> d.name).orElse(""));

        for (FormField f : fields) {
            VBox group = new VBox(6);
            Label label = new Label(f.label);
            label.getStyleClass().add("field-label");
            group.getChildren().add(label);

            if (f.type.equals("file")) {
                HBox fileRow = new HBox(10);
                fileRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                TextField fileField = new TextField();
                fileField.setPromptText("Aucun fichier sélectionné");
                fileField.setEditable(false);
                fileField.getStyleClass().add("input-field");
                HBox.setHgrow(fileField, Priority.ALWAYS);
                Button chooseBtn = new Button("Parcourir");
                chooseBtn.getStyleClass().add("choose-file-btn");
                chooseBtn.setOnAction(e -> {
                    FileChooser fc = new FileChooser();
                    fc.setTitle("Choisir un fichier");
                    File file = fc.showOpenDialog(null);
                    if (file != null) {
                        fileField.setText(file.getName());
                        formValues.put(f.key, file.getAbsolutePath());
                    }
                });
                fileRow.getChildren().addAll(fileField, chooseBtn);
                group.getChildren().add(fileRow);
            } else {
                TextField tf = new TextField();
                tf.setPromptText(f.placeholder);
                tf.getStyleClass().add("input-field");
                tf.textProperty().addListener((obs, o, n) -> formValues.put(f.key, n));
                group.getChildren().add(tf);
            }
            dynamicFormContainer.getChildren().add(group);
        }
    }

    private List<FormField> getFieldsForDocType(String key) {
        switch (key) {
            case "attestation":
                return List.of(
                        new FormField("Numéro étudiant",  "ex: 20210001",    "num_etudiant", "text"),
                        new FormField("Année académique", "ex: 2025-2026",   "annee",        "text"),
                        new FormField("Motif",            "ex: Banque, Visa","motif",        "text")
                );
            case "releve":
                return List.of(
                        new FormField("Numéro étudiant",  "ex: 20210001",    "num_etudiant", "text"),
                        new FormField("Semestre",         "ex: S5",          "semestre",     "text")
                );
            case "diplome":
                return List.of(
                        new FormField("Numéro étudiant",  "ex: 20210001",    "num_etudiant", "text"),
                        new FormField("Année d'obtention","ex: 2024",        "annee",        "text"),
                        new FormField("Copie CNI",        "",                "cni",          "file")
                );
            default:
                return List.of(
                        new FormField("Numéro étudiant",  "ex: 20210001",    "num_etudiant", "text"),
                        new FormField("Motif",            "Précisez…",       "motif",        "text")
                );
        }
    }

    // ─────────────────────────────────────────
    // STEP 3 — Payment
    // ─────────────────────────────────────────
    @FXML
    private void handlePayInApp() {
        btnPayInApp.getStyleClass().add("pay-method-active");
        btnPayTicket.getStyleClass().remove("pay-method-active");
        payInAppForm.setVisible(true);  payInAppForm.setManaged(true);
        payTicketForm.setVisible(false); payTicketForm.setManaged(false);
    }

    @FXML
    private void handlePayTicket() {
        btnPayTicket.getStyleClass().add("pay-method-active");
        btnPayInApp.getStyleClass().remove("pay-method-active");
        payTicketForm.setVisible(true);  payTicketForm.setManaged(true);
        payInAppForm.setVisible(false);  payInAppForm.setManaged(false);
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir votre reçu");
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images / PDF", "*.png", "*.jpg", "*.jpeg", "*.pdf")
        );
        File file = fc.showOpenDialog(null);
        if (file != null) {
            selectedPaymentFile = file;
            selectedFileName.setText("✓ " + file.getName());
        }
    }

    // ─────────────────────────────────────────
    // STEP 4 — Overview
    // ─────────────────────────────────────────
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
            Label v = new Label(val.isEmpty() ? "—" : val);
            v.getStyleClass().add("overview-field-val");
            row.getChildren().addAll(k, v);
            overviewFields.getChildren().add(row);
        });
    }

    @FXML
    private void handleDownloadPdf() {
        // PDF generation hook — wire to your PDF library
        System.out.println("TODO: generate PDF for " + selectedDocType);
    }

    // ─────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────
    @FXML
    private void handleBack() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("DemandeView", "Mes demandes");
    }

    @FXML
    private void handlePrecedent() {
        if (currentStep > 1) goToStep(currentStep - 1);
    }

    @FXML
    private void handleSuivant() {
        if (currentStep == 4) {
            submitDemande();
        } else {
            goToStep(currentStep + 1);
        }
    }

    private void goToStep(int step) {
        currentStep = step;

        // Show correct pane
        step1.setVisible(step == 1); step1.setManaged(step == 1);
        step2.setVisible(step == 2); step2.setManaged(step == 2);
        step3.setVisible(step == 3); step3.setManaged(step == 3);
        step4.setVisible(step == 4); step4.setManaged(step == 4);

        // Build content for entering steps
        if (step == 2) buildDynamicForm();
        if (step == 3) {
            summaryDocType.setText(DOC_TYPES.stream()
                .filter(d -> d.key.equals(selectedDocType))
                .findFirst().map(d -> d.name).orElse("—"));
        }
        if (step == 4) buildOverview();

        updateStepperUI();
        updateFooterButtons();
    }

    private void updateStepperUI() {
        List<Circle> circles = List.of(stepCircle1, stepCircle2, stepCircle3, stepCircle4);
        List<Label>  labels  = List.of(stepLabel1,  stepLabel2,  stepLabel3,  stepLabel4);
        List<Line>   lines   = List.of(stepLine1,   stepLine2,   stepLine3);

        for (int i = 0; i < 4; i++) {
            Circle c = circles.get(i);
            Label  l = labels.get(i);
            c.getStyleClass().removeAll("step-active", "step-done");
            l.getStyleClass().removeAll("step-label-active", "step-label-done");

            if (i + 1 < currentStep) {
                c.getStyleClass().add("step-done");
                l.getStyleClass().add("step-label-done");
            } else if (i + 1 == currentStep) {
                c.getStyleClass().add("step-active");
                l.getStyleClass().add("step-label-active");
            }
        }

        for (int i = 0; i < 3; i++) {
            Line line = lines.get(i);
            line.getStyleClass().remove("step-line-done");
            if (i + 1 < currentStep) line.getStyleClass().add("step-line-done");
        }
    }

    private void updateFooterButtons() {
        btnPrecedent.setDisable(currentStep == 1);
        if (currentStep == 4) {
            btnSuivant.setText("Valider ✓");
        } else {
            btnSuivant.setText("Suivant →");
        }
        // Disable next on step 1 if no doc type selected
        btnSuivant.setDisable(currentStep == 1 && selectedDocType == null);
    }

    private void submitDemande() {
        setLoading(true);

        // Build request from collected form values
        DemandeService.DemandeRequest request = new DemandeService.DemandeRequest();
        request.documentType = DOC_TYPES.stream()
            .filter(d -> d.key.equals(selectedDocType))
            .findFirst().map(d -> d.name).orElse("");
        request.motif = formValues.getOrDefault("motif", "");
        request.annee = formValues.getOrDefault("annee", "");

        TaskRunner.run(
            () -> {
                try{
                    return DemandeService.submitDemande(request);
                }
                catch(ApiException e){
                    throw new RuntimeException(e);
                }
            },

            demande -> {
                setLoading(false);
                DashboardViewController dashboard = DashboardViewControllerRegistry.getInstance();
                if (dashboard != null) {
                    dashboard.loadSubView("DemandeView", "Mes demandes");
                    dashboard.getDemandeViewController().onDemandeSubmitted(demande);
                }
            },

            ex -> {
                setLoading(false);
                // Show error — stay on current step
                if (ex instanceof ApiException) {
                    showStepError(((ApiException) ex).getMessage());
                } else {
                    showStepError("Erreur réseau. Réessayez.");
                }
            }
        );
    }

    private void setLoading(boolean loading) {
        btnSuivant.setDisable(loading);
        btnSuivant.setText(loading ? "Envoi en cours..." : "Valider ✓");
    }

    private void showStepError(String message) {
        // I will add an error label in step 4 FXML with fx:id="submitErrorLabel"
        // For now, print to console — replace with a proper label later
        System.err.println("Submit error: " + message);
    }

    // ─────────────────────────────────────────
    // Inner model classes
    // ─────────────────────────────────────────
    private static class DocType {
        final String icon;
        final String name;
        final String description;
        final String key;

        DocType(String icon, String name, String description, String key) {
            this.icon        = icon;
            this.name        = name;
            this.description = description;
            this.key         = key;
        }
    }
    
    private static class FormField {
        final String label;
        final String placeholder;
        final String key;
        final String type;

        FormField(String label, String placeholder, String key, String type) {
            this.label       = label;
            this.placeholder = placeholder;
            this.key         = key;
            this.type        = type;
        }
    }
}
