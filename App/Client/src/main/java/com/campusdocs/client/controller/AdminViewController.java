package com.campusdocs.client.controller;

import com.campusdocs.client.SessionManager;
import com.campusdocs.client.service.StatsService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminViewController implements Initializable {

    @FXML private ScrollPane rootPane;
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

    // ─────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CssLoader.loadCssFiles(rootPane,
            "adminshared", "globalStyles", "dashboardview", "adminview");

        String name = SessionManager.getInstance().getFullName();
        welcomeTitle.setText("Bonjour, " + (name != null ? name : "Administrateur") + " !");
        currentDateLabel.setText(LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH)));

        // Single API call — stats response contains everything we need
        loadStats();
    }

    // ─────────────────────────────────────────
    // LOAD STATS (includes newUsers + recentActivity)
    // ─────────────────────────────────────────
    private void loadStats() {
        TaskRunner.run(
            () -> StatsService.getAdminStats(),

            stats -> {
                populateStatLabels(stats);
                populateNewUsers(stats);
                populateRecentActivity(stats);
            },

            ex -> System.err.println("Admin stats error: " + ex.getMessage())
        );
    }

    // ─────────────────────────────────────────
    // POPULATE LABELS
    // ─────────────────────────────────────────
    private void populateStatLabels(StatsService.SystemStats stats) {
        // Users
        statTotalUsers.setText(String.valueOf(stats.statTotalUsers));
        statStudents.setText(String.valueOf(stats.statStudents));
        statStudentsActive.setText(stats.statStudentsActive + " actifs");
        statAgents.setText(String.valueOf(stats.statAgents));
        statAgentsActive.setText(stats.statAgentsActive + " actifs");
        statAdmins.setText(String.valueOf(stats.statAdmins));
        statInactiveUsers.setText(String.valueOf(stats.statInactiveUsers));

        // Demands
        statTotalDemands.setText(String.valueOf(stats.statTotalDemands));
        statPendingDemands.setText(String.valueOf(stats.statPendingDemands));
        statApprovedDemands.setText(String.valueOf(stats.statApprovedDemands));
        statRejectedDemands.setText(String.valueOf(stats.statRejectedDemands));
        statApprovalRate.setText(
            stats.statApprovalRate != null ? stats.statApprovalRate : "0%");

        // Actes
        statTotalActes.setText(String.valueOf(stats.statTotalActes));
        statActesThisMonth.setText(String.valueOf(stats.statActesThisMonth));
        statDownloads.setText(String.valueOf(stats.statDownloads));
    }

    // ─────────────────────────────────────────
    // POPULATE NEW USERS
    // ─────────────────────────────────────────
    private void populateNewUsers(StatsService.SystemStats stats) {
        newUsersContainer.getChildren().clear();

        if (stats.newUsers == null || stats.newUsers.length == 0) {
            newUsersContainer.getChildren().add(
                buildEmptyLabel("Aucun nouveau compte."));
            return;
        }

        for (StatsService.NewUser u : stats.newUsers) {
            newUsersContainer.getChildren().add(buildNewUserRow(u));
        }
    }

    // ─────────────────────────────────────────
    // POPULATE RECENT ACTIVITY
    // ─────────────────────────────────────────
    private void populateRecentActivity(StatsService.SystemStats stats) {
        recentLogsContainer.getChildren().clear();

        if (stats.recentActivity == null || stats.recentActivity.length == 0) {
            recentLogsContainer.getChildren().add(
                buildEmptyLabel("Aucune activité récente."));
            return;
        }

        for (StatsService.RecentActivity a : stats.recentActivity) {
            recentLogsContainer.getChildren().add(buildActivityRow(a));
        }
    }

    // ─────────────────────────────────────────
    // ROW BUILDERS
    // ─────────────────────────────────────────
    private HBox buildNewUserRow(StatsService.NewUser u) {
        HBox row = new HBox(12);
        row.getStyleClass().add("table-row");
        row.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        StackPane avatar = new StackPane();
        Circle c = new Circle(16);
        c.getStyleClass().add("stat-card-accent");

        String name = (u.nom != null) ? u.nom : "?";
        String initials = Arrays.stream(name.split(" "))
            .filter(p -> !p.isEmpty() && !p.equals("null"))
            .map(p -> String.valueOf(p.charAt(0)))
            .collect(Collectors.joining())
            .toUpperCase();
        if (initials.length() > 2) initials = initials.substring(0, 2);

        Label ini = new Label(initials);
        ini.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:white;");
        avatar.getChildren().addAll(c, ini);

        // Info
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        String displayName = name.contains("null") ? "Nom non renseigné" : name;
        Label nm = new Label(displayName);
        nm.getStyleClass().add("table-cell");
        Label role = new Label(u.role != null ? u.role : "—");
        role.getStyleClass().add("table-cell-muted");
        info.getChildren().addAll(nm, role);

        // Badge
        Label badge = new Label(u.actif ? "Actif" : "Inactif");
        badge.getStyleClass().add(u.actif ? "badge-active" : "badge-inactive");

        row.getChildren().addAll(avatar, info, badge);
        return row;
    }

    private HBox buildActivityRow(StatsService.RecentActivity a) {
        HBox row = new HBox(12);
        row.getStyleClass().add("log-row");
        row.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(5);
        dot.getStyleClass().add("log-dot-approve");

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label title = new Label(a.title != null ? a.title : "—");
        title.getStyleClass().add("log-action");
        Label sub = new Label(a.sub != null ? a.sub : "");
        sub.getStyleClass().add("log-detail");
        info.getChildren().addAll(title, sub);

        row.getChildren().addAll(dot, info);
        return row;
    }

    private Label buildEmptyLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("table-cell-muted");
        return l;
    }

    // ─────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────
    @FXML private void handleViewUsers() {
        DashboardViewControllerRegistry.getInstance().handleNavUsers();
    }

    @FXML private void handleViewDemandes() {
        DashboardViewControllerRegistry.getInstance().handleNavDemandes();
    }

    @FXML private void handleViewActes() {
        DashboardViewControllerRegistry.getInstance().handleNavActes();
    }

    @FXML private void handleViewStats() {
        DashboardViewControllerRegistry.getInstance().handleNavStats();
    }

    @FXML private void handleCreateAgent() {
        DashboardViewControllerRegistry.getInstance()
            .loadSubView("UsersManagementView", "Gestion des utilisateurs");
    }
}