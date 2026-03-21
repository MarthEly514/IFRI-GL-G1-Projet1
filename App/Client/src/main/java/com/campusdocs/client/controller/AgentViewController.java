/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;

import com.campusdocs.client.util.CssLoader;


 
import com.campusdocs.client.SessionManager;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.AgentDemande;
import com.campusdocs.client.model.ActeAdministratif;
import com.campusdocs.client.service.DemandeService;
import com.campusdocs.client.service.ActeService;
import com.campusdocs.client.service.StatsService;
import com.campusdocs.client.util.TaskRunner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
 
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
 
public class AgentViewController implements Initializable {
        
    @FXML private VBox rootPane;
 
    @FXML private Label welcomeTitle;
    @FXML private Label currentDateLabel;
 
    // KPI labels
    @FXML private Label statPendingDemands;
    @FXML private Label statTotalDemands;
    @FXML private Label statActes;
    @FXML private Label statApprovalRate;
    @FXML private Label statTodayProcessed;
    @FXML private Label statTodayRejected;
    @FXML private Label statMonthApproved;
 
    // Recent lists
    @FXML private VBox recentDemandesContainer;
    @FXML private VBox recentActesContainer;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CssLoader.loadCssFiles(rootPane, "adminshared", "globalStyles", "dashboardview", "agentview");
        // Welcome
        String name = SessionManager.getInstance().getFullName();
        welcomeTitle.setText("Bonjour, " + (name != null ? name : "Agent") + " !");
        currentDateLabel.setText(
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy",
                java.util.Locale.FRENCH))
        );
 
        loadStats();
        loadRecentDemandes();
        loadRecentActes();
    }
 
    private void loadStats() {
        TaskRunner.run(
            () -> StatsService.getStats(),
            stats -> {
                statTotalDemands.setText(String.valueOf(stats.totalDemandes));
                statPendingDemands.setText(String.valueOf(stats.pendingDemandes));
                statActes.setText(String.valueOf(stats.totalActes));
                statApprovalRate.setText(stats.approvalRate + "%");
                // Simulated today/month stats — replace with dedicated API fields
                statTodayProcessed.setText("—");
                statTodayRejected.setText("—");
                statMonthApproved.setText(String.valueOf(stats.approvedDemandes));
            },
            ex -> System.err.println("Agent stats error: " + ex.getMessage())
        );
    }
 
    private void loadRecentDemandes() {
        TaskRunner.run(
            () -> DemandeService.getAllDemandes(),
            demandes -> {
                recentDemandesContainer.getChildren().clear();
                // Show only first 4
                int count = Math.min(demandes.length, 4);
                for (int i = 0; i < count; i++) {
                    recentDemandesContainer.getChildren().add(buildDemandeRow(demandes[i]));
                }
                if (demandes.length == 0) {
                    recentDemandesContainer.getChildren().add(buildEmptyLabel("Aucune demande récente."));
                }
            },
            ex -> System.err.println("Recent demandes error: " + ex.getMessage())
        );
    }
 
    private void loadRecentActes() {
        TaskRunner.run(
            () -> ActeService.getAllActes(),
            actes -> {
                recentActesContainer.getChildren().clear();
                int count = Math.min(actes.length, 4);
                for (int i = 0; i < count; i++) {
                    recentActesContainer.getChildren().add(buildActeRow(actes[i]));
                }
                if (actes.length == 0) {
                    recentActesContainer.getChildren().add(buildEmptyLabel("Aucun acte récent."));
                }
            },
            ex -> System.err.println("Recent actes error: " + ex.getMessage())
        );
    }
 
    private HBox buildDemandeRow(AgentDemande d) {
        HBox row = new HBox(12);
        row.getStyleClass().add("activity-row");
        row.setAlignment(Pos.CENTER_LEFT);
 
        Circle dot = new Circle(6);
        dot.getStyleClass().add(
            d.getStatus().equals("EN_ATTENTE") ? "activity-dot-amber" :
            d.getStatus().equals("APPROUVEE")  ? "activity-dot-green" : "activity-dot-red"
        );
 
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(d.getStudentName() + " — " + d.getDocType());
        name.getStyleClass().add("activity-name");
        Label date = new Label(d.getDate());
        date.getStyleClass().add("activity-sub");
        info.getChildren().addAll(name, date);
 
        Label badge = new Label(d.getStatusLabel());
        badge.getStyleClass().add(d.getStatusBadgeClass());
 
        row.getChildren().addAll(dot, info, badge);
        return row;
    }
 
    private HBox buildActeRow(ActeAdministratif a) {
        HBox row = new HBox(12);
        row.getStyleClass().add("activity-row");
        row.setAlignment(Pos.CENTER_LEFT);
 
        Circle dot = new Circle(6);
        dot.getStyleClass().add("activity-dot-green");
 
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(a.getName());
        name.getStyleClass().add("activity-name");
        Label date = new Label(a.getFormattedDate() + "  •  " + a.getRef());
        date.getStyleClass().add("activity-sub");
        info.getChildren().addAll(name, date);
 
        row.getChildren().addAll(dot, info);
        return row;
    }
 
    private Label buildEmptyLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("activity-sub");
        return l;
    }
 
    @FXML
    private void handleViewAllDemandes() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("AgentDemandeView", "Gestion des demandes");
    }
 
    @FXML
    private void handleViewAllActes() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("AgentActView", "Actes générés");
    }
    
}

