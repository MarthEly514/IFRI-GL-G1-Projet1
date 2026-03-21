/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;

import com.campusdocs.client.App;
import com.campusdocs.client.SessionManager;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.ActeAdministratif;
import com.campusdocs.client.service.ActeService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ActeViewController implements Initializable {

    @FXML private VBox rootPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCombo;
    @FXML private Label resultsLabel;
    @FXML private Label activeFilterLabel;
    @FXML private FlowPane acteGrid;
    @FXML private VBox emptyState;

    // Loaded from API — starts empty
    private List<ActeAdministratif> allActes = new ArrayList<>();
    private List<ActeAdministratif> filteredActes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CssLoader.loadCssFiles(rootPane, "acteview", "globalStyles");

        filterCombo.setItems(FXCollections.observableArrayList(
            "Date (récent → ancien)",
            "Date (ancien → récent)",
            "Nom (A → Z)",
            "Nom (Z → A)",
            "Type"
        ));
        filterCombo.getSelectionModel().selectFirst();

        // Listeners — only fire after data is loaded
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Show loading state then fetch
        showLoading();
        loadActes();
    }

    // ─────────────────────────────────────────
    // API CALL
    // ─────────────────────────────────────────

    private void loadActes() {
        TaskRunner.run(
            () -> ActeService.getMyActes(),

            actes -> {
                // Back on JavaFX thread
                allActes = new ArrayList<>(Arrays.asList(actes));
                hideLoading();
                applyFilters();
            },

            ex -> {
                hideLoading();
                Throwable cause = ex.getCause();
                String message = (cause instanceof ApiException)
                    ? ((ApiException) cause).getMessage()
                    : "Erreur de chargement des actes.";
                showError(message);
            }
        );
    }

    // ─────────────────────────────────────────
    // FILTER & SORT
    // ─────────────────────────────────────────

    private void applyFilters() {
        String query  = searchField.getText().trim().toLowerCase();
        String filter = filterCombo.getValue();

        filteredActes = allActes.stream()
            .filter(a ->
                a.getName().toLowerCase().contains(query) ||
                a.getType().toLowerCase().contains(query) ||
                a.getRef().toLowerCase().contains(query)
            )
            .collect(Collectors.toList());

        if (filter != null) {
            switch (filter) {
                case "Date (récent → ancien)":
                    filteredActes.sort(Comparator.comparing(ActeAdministratif::getDate).reversed());
                    break;
                case "Date (ancien → récent)":
                    filteredActes.sort(Comparator.comparing(ActeAdministratif::getDate));
                    break;
                case "Nom (A → Z)":
                    filteredActes.sort(Comparator.comparing(ActeAdministratif::getName));
                    break;
                case "Nom (Z → A)":
                    filteredActes.sort(Comparator.comparing(ActeAdministratif::getName).reversed());
                    break;
                case "Type":
                    filteredActes.sort(Comparator.comparing(ActeAdministratif::getType));
                    break;
                default:
                    break;
            }
        }

        if (!query.isEmpty()) {
            activeFilterLabel.setText("Recherche : \"" + query + "\"");
            activeFilterLabel.setVisible(true);
            activeFilterLabel.setManaged(true);
        } else {
            activeFilterLabel.setVisible(false);
            activeFilterLabel.setManaged(false);
        }

        rebuildGrid();
    }

    // ─────────────────────────────────────────
    // GRID
    // ─────────────────────────────────────────

    private void rebuildGrid() {
        acteGrid.getChildren().clear();

        if (filteredActes.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
            resultsLabel.setText(allActes.isEmpty() ? "Aucun acte disponible" : "Aucun résultat");
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            resultsLabel.setText(filteredActes.size() + " acte(s) trouvé(s)");
            for (ActeAdministratif acte : filteredActes) {
                acteGrid.getChildren().add(buildActeCard(acte));
            }
        }
    }

    private VBox buildActeCard(ActeAdministratif acte) {
        VBox card = new VBox(0);
        card.getStyleClass().add("acte-card");

        // ── Icon area ──
        VBox imageArea = new VBox();
        imageArea.getStyleClass().add("acte-card-image-area");
        imageArea.setAlignment(javafx.geometry.Pos.CENTER);
        imageArea.setSpacing(8);

        Label icon = new Label(acte.getIcon());
        icon.getStyleClass().add("acte-card-icon");

        Label typeBadge = new Label(acte.getType());
        typeBadge.getStyleClass().add("acte-card-type-badge");

        imageArea.getChildren().addAll(icon, typeBadge);

        // ── Body ──
        VBox body = new VBox(4);
        body.getStyleClass().add("acte-card-body");

        Label title = new Label(acte.getName());
        title.getStyleClass().add("acte-card-title");
        title.setWrapText(true);
        title.setMaxWidth(192);

        Label date = new Label(acte.getFormattedDate());
        date.getStyleClass().add("acte-card-date");

        body.getChildren().addAll(title, date);

        // ── Footer ──
        HBox footer = new HBox(8);
        footer.getStyleClass().add("acte-card-footer");
        footer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button btnView = new Button("Voir");
        btnView.getStyleClass().add("acte-card-btn-view");
        HBox.setHgrow(btnView, Priority.ALWAYS);
        btnView.setMaxWidth(Double.MAX_VALUE);
        btnView.setOnAction(e -> openActeDetail(acte));
//        btnView.setVisible(SessionManager.getInstance().getRole().equalsIgnoreCase("Agent"));
        btnView.setVisible(false);

        Button btnDownload = new Button("↓");
        btnDownload.getStyleClass().add("acte-card-btn-download");
        btnDownload.setOnAction(e -> downloadActe(acte));

        footer.getChildren().addAll(btnView, btnDownload);
        card.getChildren().addAll(imageArea, body, footer);
        card.setOnMouseClicked(e -> openActeDetail(acte));

        return card;
    }

    // ─────────────────────────────────────────
    // ACTIONS
    // ─────────────────────────────────────────

    private void openActeDetail(ActeAdministratif acte) {
        try {
            FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/fxml/ActeDetailView.fxml")
            );
            Parent view = loader.load();
            ActeDetailsViewController controller = loader.getController();
            controller.setActe(acte);

            DashboardViewController dashboard = DashboardViewControllerRegistry.getInstance();
            if (dashboard != null) {
                dashboard.loadRawView(view, "Aperçu — " + acte.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadActe(ActeAdministratif acte) {
        // TODO: call ActeService.downloadActe(acte.getRef()) when server supports it
        System.out.println("Download requested for: " + acte.getRef());
    }

    // ─────────────────────────────────────────
    // LOADING / ERROR STATES
    // ─────────────────────────────────────────

    private void showLoading() {
        acteGrid.getChildren().clear();
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        resultsLabel.setText("Chargement...");
    }

    private void hideLoading() {
        // applyFilters() will repopulate the grid
    }

    private void showError(String message) {
        acteGrid.getChildren().clear();
        emptyState.setVisible(true);
        emptyState.setManaged(true);
        resultsLabel.setText("Erreur");

        // Reuse emptyState labels if accessible, or add a temporary label
        Label errLabel = new Label(message);
        errLabel.getStyleClass().add("empty-sub");

        // Add a retry button
        Button retry = new Button("Réessayer");
        retry.getStyleClass().add("btn-small");
        retry.setOnAction(e -> {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            showLoading();
            loadActes();
        });

        acteGrid.getChildren().addAll(errLabel, retry);
    }
}
