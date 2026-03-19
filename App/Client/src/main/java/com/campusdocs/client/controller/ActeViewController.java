/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;

import com.campusdocs.client.App;
import com.campusdocs.client.model.ActeAdministratif;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCombo;
    @FXML private Label resultsLabel;
    @FXML private Label activeFilterLabel;
    @FXML private FlowPane acteGrid;
    @FXML private VBox emptyState;

    // Sample data — replace with real API/DB call
    private final List<ActeAdministratif> allActes = Arrays.asList(
        new ActeAdministratif("ACT-2024-001", "Attestation de scolarité",   "📄", "2024-03-10", "Scolarité",    "Disponible"),
        new ActeAdministratif("ACT-2024-002", "Relevé de notes S5",         "📋", "2024-02-28", "Notes",        "Disponible"),
        new ActeAdministratif("ACT-2024-003", "Certificat de résidence",    "📝", "2024-01-15", "Résidence",    "Disponible"),
        new ActeAdministratif("ACT-2023-004", "Attestation de scolarité",   "📄", "2023-11-05", "Scolarité",    "Disponible"),
        new ActeAdministratif("ACT-2023-005", "Relevé de notes S4",         "📋", "2023-09-20", "Notes",        "Disponible"),
        new ActeAdministratif("ACT-2024-006", "Rapport de stage validé",    "📊", "2024-03-01", "Stage",        "Disponible")
    );

    private List<ActeAdministratif> filteredActes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup filter combo
        filterCombo.setItems(FXCollections.observableArrayList(
            "Date (récent → ancien)",
            "Date (ancien → récent)",
            "Nom (A → Z)",
            "Nom (Z → A)",
            "Type"
        ));
        filterCombo.getSelectionModel().selectFirst();

        // Live search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Filter change listener
        filterCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        applyFilters();
    }

    private void applyFilters() {
        String query  = searchField.getText().trim().toLowerCase();
        String filter = filterCombo.getValue();

        // Filter by search query
        filteredActes = allActes.stream()
            .filter(a ->
                a.getName().toLowerCase().contains(query) ||
                a.getType().toLowerCase().contains(query) ||
                a.getRef().toLowerCase().contains(query)
            )
            .collect(Collectors.toList());

        // Sort
        if (filter != null) {
            if (filter.equals("Date (récent → ancien)")) {
                filteredActes.sort(Comparator.comparing(ActeAdministratif::getDate).reversed());
            } else if (filter.equals("Date (ancien → récent)")) {
                filteredActes.sort(Comparator.comparing(ActeAdministratif::getDate));
            } else if (filter.equals("Nom (A → Z)")) {
                filteredActes.sort(Comparator.comparing(ActeAdministratif::getName));
            } else if (filter.equals("Nom (Z → A)")) {
                filteredActes.sort(Comparator.comparing(ActeAdministratif::getName).reversed());
            } else if (filter.equals("Type")) {
                filteredActes.sort(Comparator.comparing(ActeAdministratif::getType));
            }
        }

        // Update active filter tag
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

    private void rebuildGrid() {
        acteGrid.getChildren().clear();

        if (filteredActes.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
            resultsLabel.setText("Aucun résultat");
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

        // ── Image / icon area ──
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

        // ── Footer buttons ──
        HBox footer = new HBox(8);
        footer.getStyleClass().add("acte-card-footer");
        footer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        javafx.scene.control.Button btnView = new javafx.scene.control.Button("Voir");
        btnView.getStyleClass().add("acte-card-btn-view");
        HBox.setHgrow(btnView, Priority.ALWAYS);
        btnView.setMaxWidth(Double.MAX_VALUE);
        btnView.setOnAction(e -> openActeDetail(acte));

        javafx.scene.control.Button btnDownload = new javafx.scene.control.Button("↓");
        btnDownload.getStyleClass().add("acte-card-btn-download");
        btnDownload.setOnAction(e -> downloadActe(acte));

        footer.getChildren().addAll(btnView, btnDownload);

        card.getChildren().addAll(imageArea, body, footer);

        // Click on card also opens detail
        card.setOnMouseClicked(e -> openActeDetail(acte));

        return card;
    }

    private void openActeDetail(ActeAdministratif acte) {
        try {
            FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/fxml/ActeDetailView.fxml")
            );
            Parent view = loader.load();
            ActeDetailViewController controller = loader.getController();
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
        // TODO: wire to real file download
        System.out.println("Downloading: " + acte.getName());
    }
}
