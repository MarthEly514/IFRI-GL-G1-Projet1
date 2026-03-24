/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;
 
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.SystemLog;
import com.campusdocs.client.service.StatsService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
 
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
 
public class StatsViewController implements Initializable {
 
    @FXML private Label statTotalUsers, statStudents, statAgents, statAdmins;
    @FXML private Label statTotalDemands, statPendingDemands, statApprovedDemands;
    @FXML private Label statTotalActes, statApprovalRate;
    @FXML private ComboBox<String> logFilterCombo;
    @FXML private VBox rootPane, logList, logEmptyState;
 
    // Simulated data — replace with real DB/API calls
    private final int totalStudents = 142, totalAgents = 8, totalAdmins = 2;
    private final int totalDemands  = 218, pendingDemands = 34, approvedDemands = 171;
    private final int totalActes    = 171;
 
    private List<SystemLog> allLogs = new ArrayList<>();
    private int loadedCount = 0;
    private static final int PAGE_SIZE = 20;
 
    @Override
public void initialize(URL url, ResourceBundle rb) {
    //load css
    CssLoader.loadCssFiles(rootPane, "adminshared", "globalStyles", "statsview");
    
    setupLogFilter();
 
    // Load stats from API
    TaskRunner.run(
        () ->  StatsService.getAdminStats(),
            
        stats -> {
            statTotalUsers.setText(String.valueOf(stats.statTotalUsers));
            statStudents.setText(String.valueOf(stats.statStudents));
            statAgents.setText(String.valueOf(stats.statAgents));
            statAdmins.setText(String.valueOf(stats.statAdmins));
            statTotalDemands.setText(String.valueOf(stats.statTotalDemands));
            statPendingDemands.setText(stats.statPendingDemands + " en attente");
            statApprovedDemands.setText(stats.statApprovedDemands + " approuvées");
            statTotalActes.setText(String.valueOf(stats.statTotalActes));
            statApprovalRate.setText(stats.statApprovalRate);
        },
        ex -> System.err.println("Stats load error: " + ex.getMessage())
    );
 
    loadLogs(false);
}
 
    private void populateStats() {
        statTotalUsers.setText(String.valueOf(totalStudents + totalAgents + totalAdmins));
        statStudents.setText(String.valueOf(totalStudents));
        statAgents.setText(String.valueOf(totalAgents));
        statAdmins.setText(String.valueOf(totalAdmins));
        statTotalDemands.setText(String.valueOf(totalDemands));
        statPendingDemands.setText(pendingDemands + " en attente");
        statApprovedDemands.setText(approvedDemands + " approuvées");
        statTotalActes.setText(String.valueOf(totalActes));
        int rate = totalDemands > 0 ? (approvedDemands * 100 / totalDemands) : 0;
        statApprovalRate.setText(rate + "%");
    }
 
    private void setupLogFilter() {
        logFilterCombo.setItems(FXCollections.observableArrayList(
            "Toutes les actions", "Demandes", "Approbations", "Rejets", "Connexions", "Créations"
        ));
        logFilterCombo.getSelectionModel().selectFirst();
        logFilterCombo.valueProperty().addListener((obs, o, n) -> {
            loadedCount = 0;
            renderLogs(false);
        });
    }
 
    private void renderLogs(boolean append) {
        String filter = logFilterCombo.getValue();
        List<SystemLog> filtered = allLogs.stream().filter(l -> {
            if (filter == null || filter.equals("Toutes les actions")) return true;
            if (filter.equals("Demandes"))     return l.getType().equals("demand");
            if (filter.equals("Approbations")) return l.getType().equals("approve");
            if (filter.equals("Rejets"))       return l.getType().equals("reject");
            if (filter.equals("Connexions"))   return l.getType().equals("login");
            if (filter.equals("Créations"))    return l.getType().equals("create");
            return true;
        }).collect(Collectors.toList());
 
        if (!append) { logList.getChildren().clear(); loadedCount = 0; }
 
        int end = Math.min(loadedCount + PAGE_SIZE, filtered.size());
        for (int i = loadedCount; i < end; i++) {
            logList.getChildren().add(buildLogRow(filtered.get(i)));
        }
        loadedCount = end;
 
        logEmptyState.setVisible(filtered.isEmpty());
        logEmptyState.setManaged(filtered.isEmpty());
    }
    
    private void loadLogs(boolean append) {
        int page = append ? loadedCount / PAGE_SIZE : 0;
        TaskRunner.run(
            () -> {
            try {
                return StatsService.getLogs(page, PAGE_SIZE);
            } catch (ApiException ex) {
                throw new RuntimeException(ex);
            }
        },
            logs -> {
                if (!append) { logList.getChildren().clear(); loadedCount = 0; allLogs.clear(); }
                allLogs.addAll(Arrays.asList(logs));
                renderLogs(append);
            },
            ex -> System.err.println("Logs load error: " + ex.getMessage())
        );
    }
 
    private HBox buildLogRow(SystemLog log) {
        HBox row = new HBox(0);
        row.getStyleClass().add("log-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
 
        Circle dot = new Circle(5);
        dot.getStyleClass().add("log-dot-" + log.getType());
        HBox.setMargin(dot, new javafx.geometry.Insets(0, 12, 0, 0));
 
        VBox actionBox = new VBox(2);
        actionBox.setPrefWidth(220);
        Label action = new Label(log.getAction());
        action.getStyleClass().add("log-action");
        Label user = new Label(log.getUser());
        user.getStyleClass().add("log-detail");
        actionBox.getChildren().addAll(action, user);
 
        Label detail = new Label(log.getDetail());
        detail.getStyleClass().add("log-detail");
        HBox.setHgrow(detail, Priority.ALWAYS);
 
        Label time = new Label(log.getTime());
        time.getStyleClass().add("log-time");
        time.setPrefWidth(160);
 
        row.getChildren().addAll(dot, actionBox, detail, time);
        return row;
    }
 
    @FXML private void handleClearFilter() {
        logFilterCombo.getSelectionModel().selectFirst();
    }
 
    @FXML private void handleLoadMore() {
        renderLogs(true);
    }
}
