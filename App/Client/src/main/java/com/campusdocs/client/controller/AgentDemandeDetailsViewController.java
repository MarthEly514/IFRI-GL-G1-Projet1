/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;
 
import com.campusdocs.client.model.AgentDemande;
import com.campusdocs.client.util.CssLoader;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
 
public class AgentDemandeDetailsViewController implements Initializable {
 
    @FXML private Label topTitle, statusBadge;
    @FXML private Label studentName, studentInitials, studentEmail, studentMatricule, studentFiliere;
    @FXML private Label docType, demandRef, demandDate, demandPayment, previewDocType;
    @FXML private VBox rootPane, demandFieldsContainer, timelineContainer;
    @FXML private TextArea agentNote;
    @FXML private TextField rejectReason;
    @FXML private StackPane toastContainer;
    @FXML private Label toastLabel;
    
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
        addFieldRow("Motif", "Banque / Visa");
        addFieldRow("Année académique", "2025 - 2026");
 
        // Timeline
        addTimelineItem("Demande soumise", d.getDate() + " — " + d.getStudentName());
 
        // Disable buttons if already processed
        if (!d.getStatus().equals("EN_ATTENTE")) {
            // visually handled by opacity via CSS :disabled
        }
    }
 
    private void addFieldRow(String key, String value) {
        HBox row = new HBox(12);
        Label k = new Label(key); k.getStyleClass().add("field-label"); k.setPrefWidth(160);
        Label v = new Label(value); v.getStyleClass().add("table-cell"); HBox.setHgrow(v, Priority.ALWAYS);
        row.getChildren().addAll(k, v);
        demandFieldsContainer.getChildren().add(row);
    }
 
    private void addTimelineItem(String action, String detail) {
        VBox item = new VBox(2);
        item.getStyleClass().add("timeline-item");
        Label a = new Label(action); a.getStyleClass().add("timeline-action");
        Label d = new Label(detail); d.getStyleClass().add("timeline-time");
        item.getChildren().addAll(a, d);
        timelineContainer.getChildren().add(item);
    }
 
    @FXML
    private void handleValidate() {
        if (currentDemande == null) return;
 
        // ── GENERATION LOGIC (simulation) ──
        // 1. Mark demand as APPROUVEE in the DB (API call here)
        // 2. Create a new Acte record linked to this demand:
        //    String ref = "ACT-" + System.currentTimeMillis();
        //    Acte acte = new Acte(ref, currentDemande.getDocType(), "📄",
        //                         LocalDate.now().toString(), "Scolarité", "Disponible");
        //    ActeRepository.save(acte);  <-- your persistence layer
        //    StudentActeStore.addActe(currentDemande.getStudentEmail(), acte); <-- notify student
        // 3. Log the action in SystemLog:
        //    SystemLogRepository.log("APPROBATION", agentName, "Acte généré: " + ref, now);
        // 4. Navigate back
 
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
        addTimelineItem("Validé par l'agent", now + " — " + com.campusdocs.client.SessionManager.getInstance().getFullName());
        addTimelineItem("Acte généré", "Référence ACT-" + System.currentTimeMillis());
 
        statusBadge.setText("Approuvée");
        statusBadge.getStyleClass().setAll("badge-approved");
 
        showToast("Demande validée — acte généré avec succès !");
    }
 
    @FXML
    private void handleReject() {
        String reason = rejectReason.getText().trim();
        if (reason.isEmpty()) {
            showToast("Veuillez saisir un motif de rejet.");
            return;
        }
        // API call: mark demand as REJETEE with reason
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
        addTimelineItem("Rejetée", now + " — Motif : " + reason);
 
        statusBadge.setText("Rejetée");
        statusBadge.getStyleClass().setAll("badge-rejected");
        showToast("Demande rejetée.");
    }
 
    @FXML
    private void handleBack() {
        DashboardViewControllerRegistry.getInstance().loadSubView("AgentDemandeView", "Gestion des demandes");
    }
 
    private void showToast(String msg) {
        toastLabel.setText(msg);
        toastContainer.setVisible(true); toastContainer.setManaged(true);
        FadeTransition fi = new FadeTransition(Duration.millis(250), toastContainer); fi.setFromValue(0); fi.setToValue(1);
        PauseTransition hold = new PauseTransition(Duration.seconds(3));
        FadeTransition fo = new FadeTransition(Duration.millis(400), toastContainer); fo.setFromValue(1); fo.setToValue(0);
        fo.setOnFinished(e -> { toastContainer.setVisible(false); toastContainer.setManaged(false); });
        new SequentialTransition(fi, hold, fo).play();
    }
}
