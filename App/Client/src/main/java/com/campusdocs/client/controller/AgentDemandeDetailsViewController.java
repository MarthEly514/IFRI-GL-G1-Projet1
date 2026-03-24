/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;
 
import com.campusdocs.client.model.AgentDemande;
import com.campusdocs.client.model.Piece;
import com.campusdocs.client.service.DemandeService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import java.net.URI;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
 
import java.net.URL;
import java.awt.Desktop;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
 
public class AgentDemandeDetailsViewController implements Initializable {
 
    // ── Top bar ──
    @FXML private Label topTitle;
    @FXML private Label statusBadge;
 
    // ── Student info ──
    @FXML private Label studentInitials;
    @FXML private Label studentName;
    @FXML private Label studentEmail;
    @FXML private Label studentMatricule;
    @FXML private Label studentFiliere;
 
    // ── Demand details ──
    @FXML private Label docType;
    @FXML private Label demandRef;
    @FXML private Label demandDate;
    @FXML private Label demandPayment;
    @FXML private Label previewDocType;
    @FXML private VBox demandFieldsContainer;
 
    // ── Pièces justificatives ──
    @FXML private VBox piecesContainer;
    @FXML private VBox piecesEmptyState;
    @FXML private Label piecesCountBadge;
 
    // ── Action panel ──
    @FXML private TextArea agentNote;
    @FXML private TextField rejectReason;
    @FXML private Button btnValidate;
    @FXML private Button btnReject;
    @FXML private VBox timelineContainer;
 
    // ── Toast ──
    @FXML private StackPane toastContainer;
    @FXML private Label toastLabel;
 
    @FXML private VBox rootPane;
 
    private AgentDemande currentDemande;
    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //loading css files
        CssLoader.loadCssFiles(rootPane, "agentdemanddetailsview", "adminshared","globalStyles");
    }
 
    public void setDemande(AgentDemande d) {
        this.currentDemande = d;
 
        topTitle.setText("Demande — " + d.getRef());
        statusBadge.setText(d.getStatusLabel());
        statusBadge.getStyleClass().setAll(d.getStatusBadgeClass());
 
        // Student info
        String[] parts = d.getStudentName().split(" ", 2);
        String initials = (parts[0].isEmpty() ? "?" : String.valueOf(parts[0].charAt(0)))
                        + (parts.length > 1 && !parts[1].isEmpty() ? String.valueOf(parts[1].charAt(0)) : "");
        studentInitials.setText(initials.toUpperCase());
        studentName.setText(d.getStudentName());
        studentEmail.setText(d.getStudentEmail());
        studentMatricule.setText("20210001");
        studentFiliere.setText("Licence 3 - Informatique");
 
        // Demand info
        docType.setText(d.getDocType());
        demandRef.setText(d.getRef());
        demandDate.setText(d.getDate());
        demandPayment.setText("500 FCFA — Payé");
        previewDocType.setText(d.getDocType().toUpperCase());
 
        // Sample fields
        demandFieldsContainer.getChildren().clear();
        addFieldRow("Motif", d.getMotif());
        addFieldRow("Année académique", "2025 - 2026");
 
        // Timeline
        addTimelineItem("Demande soumise", d.getDate() + " — " + d.getStudentName(), "approve");
 
        // Disable buttons if already processed
        if (!d.getStatus().equals("EN_ATTENTE")) {
            btnValidate.setDisable(true);
            btnReject.setDisable(true);
            rejectReason.setDisable(true);
            agentNote.setDisable(true);
        }
        
        loadPieces(d.getId());
    }
 
    private void addFieldRow(String key, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label k = new Label(key);
        k.getStyleClass().add("field-label");
        k.setPrefWidth(160);
        Label v = new Label(value);
        v.getStyleClass().add("table-cell");
        HBox.setHgrow(v, Priority.ALWAYS);
        row.getChildren().addAll(k, v);
        demandFieldsContainer.getChildren().add(row);
    }
 
    private void addTimelineItem(String action, String detail, String type) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color:#f4faf9;-fx-background-radius:8;" +
                      "-fx-border-color:#d8eeec;-fx-border-width:1;" +
                      "-fx-border-radius:8;-fx-padding:10 12 10 12;");
 
        Circle dot = new Circle(5);
        dot.getStyleClass().add("log-dot-" + type);
 
        VBox info = new VBox(2);
        Label a = new Label(action);
        a.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:#0d2b2a;");
        Label d = new Label(detail);
        d.setStyle("-fx-font-size:11px;-fx-text-fill:#a0bab8;");
        info.getChildren().addAll(a, d);
 
        item.getChildren().addAll(dot, info);
        timelineContainer.getChildren().add(item);
    }
    
    private void loadPieces(int demandeId) {
        piecesEmptyState.setVisible(false);
        piecesEmptyState.setManaged(false);
        piecesContainer.setVisible(false);
        piecesContainer.setManaged(false);
        piecesCountBadge.setText("Chargement...");
 
        TaskRunner.run(
            () -> DemandeService.getPieces(demandeId),
            pieces -> {
                piecesContainer.getChildren().clear();
 
                if (pieces == null || pieces.length == 0) {
                    piecesCountBadge.setText("0 fichier(s)");
                    piecesEmptyState.setVisible(true);
                    piecesEmptyState.setManaged(true);
                } else {
                    piecesCountBadge.setText(pieces.length + " fichier(s)");
                    piecesContainer.setVisible(true);
                    piecesContainer.setManaged(true);
                    for (Piece p : pieces) {
                        piecesContainer.getChildren().add(buildPieceRow(p));
                    }
                }
            },
            ex -> {
                piecesCountBadge.setText("Erreur");
                piecesEmptyState.setVisible(true);
                piecesEmptyState.setManaged(true);
                System.err.println("Pieces load error: " + ex.getMessage());
            }
        );
    }
    
    private HBox buildPieceRow(Piece piece) {
        HBox row = new HBox(12);
        row.getStyleClass().add("piece-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10 14 10 14; -fx-background-color: #f4faf9;" +
                     "-fx-background-radius: 8; -fx-border-color: #d8eeec;" +
                     "-fx-border-width: 1; -fx-border-radius: 8;");
  
        // File info
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(piece.getNomFichier() != null
            ? piece.getNomFichier() : piece.getType());
        name.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#0d2b2a;");
        Label type = new Label(piece.getType() + "  •  " + 
            (piece.getDateUpload() != null ? piece.getDateUpload() : ""));
        type.setStyle("-fx-font-size:11px;-fx-text-fill:#7a9e9c;");
        info.getChildren().addAll(name, type);
 
 
        // View button — opens the file URL in browser
        Button btnVoir = new Button("Voir");
        btnVoir.getStyleClass().add("btn-small");
        btnVoir.setOnAction(e -> openFile(piece.getCheminFichier()));
 
        row.getChildren().addAll(info, btnVoir);
        return row;
    }
    
    private void openFile(String url) {
        if (url == null || url.isEmpty()) {
            showToast("Lien du fichier indisponible.");
            return;
        }
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            showToast("Impossible d'ouvrir le fichier.");
            System.err.println("Open file error: " + e.getMessage());
        }
    }

 
    @FXML
    private void handleValidate() {
        btnValidate.setDisable(true);
        btnValidate.setText("Traitement...");
 
        TaskRunner.run(
            () -> {
                DemandeService.validateDemande(
                    currentDemande.getId(), agentNote.getText().trim());
                return null;
            },
            ignored -> {
                String now = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
                addTimelineItem("Validée par l'agent", now, "approve");
                statusBadge.setText("Approuvée");
                statusBadge.getStyleClass().setAll("badge-approved");
                btnValidate.setText("✓  Validée");
                btnReject.setDisable(true);
                rejectReason.setDisable(true);
                showToast("Demande validée — acte généré avec succès !");
            },
            ex -> {
                btnValidate.setDisable(false);
                btnValidate.setText("✓  Valider et générer l'acte");
                showToast("Erreur : " + ex.getMessage());
            }
        );
    }
 
   @FXML
    private void handleReject() {
        String reason = rejectReason.getText().trim();
        if (reason.isEmpty()) {
            showToast("Veuillez saisir un motif de rejet.");
            return;
        }
 
        btnReject.setDisable(true);
        btnReject.setText("Traitement...");
 
        TaskRunner.run(
            () -> { DemandeService.rejectDemande(currentDemande.getId(), reason); return null; },
            ignored -> {
                String now = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
                addTimelineItem("Rejetée — " + reason, now, "reject");
                statusBadge.setText("Rejetée");
                statusBadge.getStyleClass().setAll("badge-rejected");
                btnReject.setText("✕  Rejetée");
                btnValidate.setDisable(true);
                agentNote.setDisable(true);
                showToast("Demande rejetée.");
            },
            ex -> {
                btnReject.setDisable(false);
                btnReject.setText("✕  Rejeter la demande");
                showToast("Erreur : " + ex.getMessage());
            }
        );
    }
 
    @FXML
    private void handleBack() {
        DashboardViewControllerRegistry.getInstance().loadSubView("AgentDemandeView", "Gestion des demandes");
    }
    
    @FXML
    private void handleViewPayment(){
        System.out.println("Raven paid");
    }
 
    private void showToast(String msg) {
        toastLabel.setText(msg);
        toastContainer.setVisible(true);
        toastContainer.setManaged(true);
        FadeTransition fi = new FadeTransition(Duration.millis(250), toastContainer);
        fi.setFromValue(0); fi.setToValue(1);
        PauseTransition hold = new PauseTransition(Duration.seconds(3));
        FadeTransition fo = new FadeTransition(Duration.millis(400), toastContainer);
        fo.setFromValue(1); fo.setToValue(0);
        fo.setOnFinished(e -> {
            toastContainer.setVisible(false);
            toastContainer.setManaged(false);
        });
        new SequentialTransition(fi, hold, fo).play();
    }
}
