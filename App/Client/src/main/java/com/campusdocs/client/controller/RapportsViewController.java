/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;
 
import com.campusdocs.client.util.CssLoader;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
 
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
 
public class RapportsViewController implements Initializable {
 
    @FXML private DatePicker dateFrom, dateTo;
    @FXML private ComboBox<String> roleFilter;
    @FXML private CheckBox includeStats, includeLog, includeUsers, includeActes;
    @FXML private VBox rootPane ,previewSection, reportLogContainer, recentExports, noExports;
    @FXML private Label reportPeriodLabel, reportGenDate, rptUsers, rptDemands, rptActes, rptRate;
    @FXML private StackPane toastContainer;
    @FXML private Label toastLabel;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //load css
        CssLoader.loadCssFiles(rootPane, "adminshared", "globalStyles", "rapportsview");

        
        roleFilter.getItems().addAll("Tous les rôles", "Étudiant", "Agent", "Admin");
        roleFilter.getSelectionModel().selectFirst();
        dateFrom.setValue(LocalDate.now().minusMonths(1));
        dateTo.setValue(LocalDate.now());
        noExports.setVisible(true); noExports.setManaged(true);
    }
 
    @FXML
    private void handlePreview() {
        // Populate preview with simulated data
        String from = dateFrom.getValue() != null ? dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "—";
        String to   = dateTo.getValue()   != null ? dateTo.getValue().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))   : "—";
        reportPeriodLabel.setText("Période : " + from + " → " + to);
        reportGenDate.setText("Généré le : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
 
        rptUsers.setText("152");
        rptDemands.setText("218");
        rptActes.setText("171");
        rptRate.setText("78%");
 
        // Sample log rows
        reportLogContainer.getChildren().clear();
        addReportLogRow("APPROBATION", "Agent Martin", "REF-2026-001 validé", "19 Mar 2026 09:47");
        addReportLogRow("DEMANDE",     "Dupont Jean",  "Attestation soumise", "19 Mar 2026 09:14");
        addReportLogRow("REJET",       "Agent Dubois", "Dossier incomplet",   "19 Mar 2026 10:31");
 
        previewSection.setVisible(true); previewSection.setManaged(true);
    }
 
    private void addReportLogRow(String action, String user, String detail, String time) {
        HBox row = new HBox(0);
        row.getStyleClass().add("log-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label a = new Label(action); a.getStyleClass().add("log-action"); a.setPrefWidth(140);
        Label u = new Label(user);   u.getStyleClass().add("log-detail");  u.setPrefWidth(160);
        Label d = new Label(detail); d.getStyleClass().add("log-detail");  HBox.setHgrow(d, Priority.ALWAYS);
        Label t = new Label(time);   t.getStyleClass().add("log-time");    t.setPrefWidth(160);
        row.getChildren().addAll(a, u, d, t);
        reportLogContainer.getChildren().add(row);
    }
 
    @FXML
    private void handleExportPdf() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le rapport PDF");
        fc.setInitialFileName("rapport-campusdocs-" + LocalDate.now() + ".pdf");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fc.showSaveDialog(null);
        if (file != null) {
            // TODO: wire to PDF generation library (e.g. iText, Apache PDFBox)
            addExportRow(file.getName(), "PDF");
            showToast("Rapport PDF exporté : " + file.getName());
        }
    }
 
    @FXML
    private void handleExportCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le rapport CSV");
        fc.setInitialFileName("rapport-campusdocs-" + LocalDate.now() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fc.showSaveDialog(null);
        if (file != null) {
            // TODO: write CSV using your data
            addExportRow(file.getName(), "CSV");
            showToast("Rapport CSV exporté : " + file.getName());
        }
    }
 
    private void addExportRow(String fileName, String format) {
        noExports.setVisible(false); noExports.setManaged(false);
        HBox row = new HBox(12);
        row.getStyleClass().add("export-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label icon = new Label("CSV".equals(format) ? "📊" : "📄");
        VBox info = new VBox(2); HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(fileName); name.getStyleClass().add("export-name");
        Label meta = new Label(format + " — " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
        meta.getStyleClass().add("export-meta");
        info.getChildren().addAll(name, meta);
        Label badge = new Label(format); badge.getStyleClass().add("badge-approved");
        row.getChildren().addAll(icon, info, badge);
        recentExports.getChildren().add(0, row);
    }
 
    @FXML private void handleClosePreview() {
        previewSection.setVisible(false); previewSection.setManaged(false);
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