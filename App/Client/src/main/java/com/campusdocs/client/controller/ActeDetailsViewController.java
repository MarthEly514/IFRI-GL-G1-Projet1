/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.controller;
 
import com.campusdocs.client.model.ActeAdministratif;
import com.campusdocs.client.util.CssLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
 
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.layout.VBox;
 
public class ActeDetailsViewController implements Initializable {
 
    //rootPane
    @FXML private VBox rootPane;
    
    // Top bar
    @FXML private Label topBarTitle;
 
    // Preview labels (document placeholder)
    @FXML private Label docTypeLabel;
    @FXML private Label docStudentName;
    @FXML private Label docStudentId;
    @FXML private Label docFiliere;
    @FXML private Label docAnnee;
    @FXML private Label docDate;
 
    // Meta panel
    @FXML private Label metaDocType;
    @FXML private Label metaDate;
    @FXML private Label metaStatus;
    @FXML private Label metaRef;
 
    private ActeAdministratif currentActe;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CssLoader.loadCssFiles(rootPane, "acteview", "globalStyles");

    }
 
    public void setActe(ActeAdministratif acte) {
        this.currentActe = acte;
 
        // Top bar
        topBarTitle.setText("Aperçu — " + acte.getName());
 
        // Document placeholder content
        docTypeLabel.setText(acte.getName().toUpperCase());
        docStudentName.setText(com.campusdocs.client.SessionManager.getInstance().getFullName());
        docStudentId.setText("20210001");   // replace with real session data
        docFiliere.setText("Licence 3 - Informatique");
        docAnnee.setText("2025 - 2026");
        docDate.setText("Fait le : " + acte.getFormattedDate());
 
        // Meta panel
        metaDocType.setText(acte.getName());
        metaDate.setText(acte.getFormattedDate());
        metaRef.setText(acte.getRef());
    }
 
    @FXML
    private void handleBack() {
        DashboardViewController dashboard = DashboardViewControllerRegistry.getInstance();
        if (dashboard != null) {
            dashboard.loadSubView("ActeView", "Mes actes");
        }
    }
 
    @FXML
    private void handleDownload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le document");
        fileChooser.setInitialFileName(currentActe.getRef() + ".pdf");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            // TODO: wire to real PDF generation/download
            System.out.println("Save to: " + file.getAbsolutePath());
        }
    }
 
    @FXML
    private void handleShare() {
        // TODO: implement share logic
        System.out.println("Share: " + currentActe.getRef());
    }
}