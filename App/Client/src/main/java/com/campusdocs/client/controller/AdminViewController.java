/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;

import com.campusdocs.client.util.CssLoader;
 
import com.campusdocs.client.SessionManager;
import com.campusdocs.client.model.User;
import com.campusdocs.client.model.SystemLog;
import com.campusdocs.client.service.StatsService;
import com.campusdocs.client.service.UserService;
import com.campusdocs.client.service.StatsService.SystemStats;
import com.campusdocs.client.util.TaskRunner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
 
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
 
public class AdminViewController implements Initializable {
 
    @FXML private VBox rootPane;
    
    @FXML private Label welcomeTitle;
    @FXML private Label currentDateLabel;
 
    // User stats
    @FXML private Label statTotalUsers;
    @FXML private Label statStudents;
    @FXML private Label statStudentsActive;
    @FXML private Label statAgents;
    @FXML private Label statAgentsActive;
    @FXML private Label statAdmins;
    @FXML private Label statInactiveUsers;
 
    // Demand stats
    @FXML private Label statTotalDemands;
    @FXML private Label statPendingDemands;
    @FXML private Label statApprovedDemands;
    @FXML private Label statRejectedDemands;
    @FXML private Label statApprovalRate;
 
    // Acte stats
    @FXML private Label statTotalActes;
    @FXML private Label statActesThisMonth;
    @FXML private Label statDownloads;
 
    // Containers
    @FXML private VBox recentLogsContainer;
    @FXML private VBox newUsersContainer;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CssLoader.loadCssFiles(rootPane, "adminshared", "globalStyles", "dashboardview", "adminview");
        String name = SessionManager.getInstance().getFullName();
        welcomeTitle.setText("Bonjour, " + (name != null ? name : "Administrateur") + " !");
        currentDateLabel.setText(
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy",
                java.util.Locale.FRENCH))
        );
 
        loadStats();
        loadRecentLogs();
        loadNewUsers();
    }
 
    private void loadStats() {
        TaskRunner.run(
            () -> StatsService.getStats(),
            stats -> {
                // Users
                statTotalUsers.setText(String.valueOf(stats.totalUsers));
                statStudents.setText(String.valueOf(stats.totalStudents));
                statStudentsActive.setText(stats.totalStudents + " actifs");
                statAgents.setText(String.valueOf(stats.totalAgents));
                statAgentsActive.setText(stats.totalAgents + " actifs");
                statAdmins.setText(String.valueOf(stats.totalAdmins));
                // Inactive — derived if not from API directly
                statInactiveUsers.setText("—");
 
                // Demands
                statTotalDemands.setText(String.valueOf(stats.totalDemandes));
                statPendingDemands.setText(String.valueOf(stats.pendingDemandes));
                statApprovedDemands.setText(String.valueOf(stats.approvedDemandes));
                statRejectedDemands.setText(String.valueOf(stats.rejectedDemandes));
                statApprovalRate.setText(stats.approvalRate + "%");
 
                // Actes
                statTotalActes.setText(String.valueOf(stats.totalActes));
                statActesThisMonth.setText("—"); // add field to StatsService if needed
                statDownloads.setText("—");       // add field to StatsService if needed
            },
            ex -> System.err.println("Admin stats error: " + ex.getMessage())
        );
    }
 
    private void loadRecentLogs() {
        TaskRunner.run(
            () -> StatsService.getLogs(0, 6),
            logs -> {
                recentLogsContainer.getChildren().clear();
                if (logs.length == 0) {
                    recentLogsContainer.getChildren().add(buildEmptyLabel("Aucune activité récente."));
                    return;
                }
                for (SystemLog log : logs) {
                    recentLogsContainer.getChildren().add(buildLogRow(log));
                }
            },
            ex -> System.err.println("Recent logs error: " + ex.getMessage())
        );
    }
 
    private void loadNewUsers() {
        TaskRunner.run(
            () -> UserService.getAllUsers(),
            users -> {
                newUsersContainer.getChildren().clear();
                // Show last 4 users (most recently added)
                User[] recent = users.length > 4
                    ? Arrays.copyOfRange(users, users.length - 4, users.length)
                    : users;
                if (recent.length == 0) {
                    newUsersContainer.getChildren().add(buildEmptyLabel("Aucun nouveau compte."));
                    return;
                }
                for (User u : recent) {
                    newUsersContainer.getChildren().add(buildUserRow(u));
                }
            },
            ex -> System.err.println("New users error: " + ex.getMessage())
        );
    }
 
    // ── Row builders ──────────────────────────────────────────────────
 
    private HBox buildLogRow(SystemLog log) {
        HBox row = new HBox(12);
        row.getStyleClass().add("log-row");
        row.setAlignment(Pos.CENTER_LEFT);
 
        Circle dot = new Circle(5);
        dot.getStyleClass().add("log-dot-" + log.getType());
 
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label action = new Label(log.getAction() + " — " + log.getUser());
        action.getStyleClass().add("log-action");
        Label detail = new Label(log.getDetail());
        detail.getStyleClass().add("log-detail");
        info.getChildren().addAll(action, detail);
 
        Label time = new Label(log.getTime());
        time.getStyleClass().add("log-time");
 
        row.getChildren().addAll(dot, info, time);
        return row;
    }
 
    private HBox buildUserRow(User u) {
        HBox row = new HBox(12);
        row.getStyleClass().add("table-row");
        row.setAlignment(Pos.CENTER_LEFT);
 
        // Avatar initials
        StackPane avatar = new StackPane();
        Circle c = new Circle(16);
        c.getStyleClass().add("stat-card-accent");
        String initials = Arrays.stream(u.getName().split(" "))
            .filter(p -> !p.isEmpty())
            .map(p -> String.valueOf(p.charAt(0)))
            .collect(Collectors.joining())
            .toUpperCase();
        if (initials.length() > 2) initials = initials.substring(0, 2);
        Label ini = new Label(initials);
        ini.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:white;");
        avatar.getChildren().addAll(c, ini);
 
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(u.getName());
        name.getStyleClass().add("table-cell");
        Label email = new Label(u.getEmail());
        email.getStyleClass().add("table-cell-muted");
        info.getChildren().addAll(name, email);
 
        Label badge = new Label(u.getRole().toString());
        badge.getStyleClass().add(u.getRoleBadgeClass());
 
        row.getChildren().addAll(avatar, info, badge);
        return row;
    }
 
    private Label buildEmptyLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("table-cell-muted");
        return l;
    }
 
    // ── Navigation handlers ───────────────────────────────────────────
 
    @FXML
    private void handleViewUsers() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("UserManagementView", "Gestion des utilisateurs");
    }
 
    @FXML
    private void handleViewDemandes() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("AgentDemandeView", "Gestion des demandes");
    }
 
    @FXML
    private void handleViewActes() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("AgentActView", "Actes générés");
    }
 
    @FXML
    private void handleViewStats() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("StatsView", "Statistiques système");
    }
 
    @FXML
    private void handleCreateAgent() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("UserManagementView", "Gestion des utilisateurs");
    }
}
