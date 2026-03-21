/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package com.campusdocs.client.controller;
 
import com.campusdocs.client.App;
import com.campusdocs.client.model.AgentDemande;
import com.campusdocs.client.util.CssLoader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
 
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
 
public class AgentDemandeViewController implements Initializable {
 
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter, typeFilter;
    @FXML private Label totalLabel, pendingLabel, approvedLabel, rejectedLabel;
    @FXML private VBox rootPane, demandeList, emptyState;
 
    private List<AgentDemande> allDemandes;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //loading css files
        CssLoader.loadCssFiles(rootPane, "agentdemanddetailsview", "adminshared","globalStyles");

        
        // Sample data — replace with API call
        allDemandes = Arrays.asList(
            new AgentDemande("DEM-001", "Dupont Jean",   "jean@test.com", "Attestation de scolarité", "19 Mar 2026", "EN_ATTENTE"),
            new AgentDemande("DEM-002", "Koné Awa",      "awa@test.com",  "Relevé de notes S5",       "18 Mar 2026", "EN_ATTENTE"),
            new AgentDemande("DEM-003", "Mbaye Fatou",   "f@test.com",    "Certificat de résidence",  "17 Mar 2026", "APPROUVEE"),
            new AgentDemande("DEM-004", "Traoré Moussa", "m@test.com",    "Diplôme",                  "16 Mar 2026", "REJETEE"),
            new AgentDemande("DEM-005", "Sow Ibrahim",   "i@test.com",    "Attestation de scolarité", "15 Mar 2026", "EN_ATTENTE")
        );
 
        statusFilter.setItems(FXCollections.observableArrayList("Tous les statuts", "En attente", "Approuvées", "Rejetées"));
        statusFilter.getSelectionModel().selectFirst();
        typeFilter.setItems(FXCollections.observableArrayList("Tous les types", "Attestation de scolarité", "Relevé de notes", "Diplôme", "Certificat de résidence"));
        typeFilter.getSelectionModel().selectFirst();
 
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        typeFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
 
        applyFilters();
    }
 
    private void applyFilters() {
        String query  = searchField.getText().trim().toLowerCase();
        String status = statusFilter.getValue();
        String type   = typeFilter.getValue();
 
        List<AgentDemande> filtered = allDemandes.stream().filter(d -> {
            boolean matchQuery  = d.getStudentName().toLowerCase().contains(query) || d.getDocType().toLowerCase().contains(query);
            boolean matchStatus = status == null || status.equals("Tous les statuts")
                || (status.equals("En attente")  && d.getStatus().equals("EN_ATTENTE"))
                || (status.equals("Approuvées")  && d.getStatus().equals("APPROUVEE"))
                || (status.equals("Rejetées")    && d.getStatus().equals("REJETEE"));
            boolean matchType   = type == null || type.equals("Tous les types") || d.getDocType().contains(type);
            return matchQuery && matchStatus && matchType;
        }).collect(Collectors.toList());
 
        long pending  = filtered.stream().filter(d -> d.getStatus().equals("EN_ATTENTE")).count();
        long approved = filtered.stream().filter(d -> d.getStatus().equals("APPROUVEE")).count();
        long rejected = filtered.stream().filter(d -> d.getStatus().equals("REJETEE")).count();
 
        totalLabel.setText(filtered.size() + " demandes");
        pendingLabel.setText(pending + " en attente");
        approvedLabel.setText(approved + " approuvées");
        rejectedLabel.setText(rejected + " rejetées");
 
        demandeList.getChildren().clear();
        if (filtered.isEmpty()) {
            emptyState.setVisible(true); emptyState.setManaged(true);
        } else {
            emptyState.setVisible(false); emptyState.setManaged(false);
            for (AgentDemande d : filtered) demandeList.getChildren().add(buildRow(d));
        }
    }
 
    private HBox buildRow(AgentDemande d) {
        HBox row = new HBox(0);
        row.getStyleClass().add("table-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
 
        Label name = new Label(d.getStudentName()); name.getStyleClass().add("table-cell"); name.setPrefWidth(200);
        Label type = new Label(d.getDocType());     type.getStyleClass().add("table-cell"); type.setPrefWidth(200);
        Label date = new Label(d.getDate());         date.getStyleClass().add("table-cell-muted"); date.setPrefWidth(130);
 
        Label badge = new Label(d.getStatusLabel());
        badge.getStyleClass().add(d.getStatusBadgeClass());
        badge.setPrefWidth(120);
 
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
 
        javafx.scene.control.Button btnVoir = new javafx.scene.control.Button("Voir");
        btnVoir.getStyleClass().add("btn-small");
        btnVoir.setOnAction(e -> openDetail(d));
 
        row.getChildren().addAll(name, type, date, badge, spacer, btnVoir);
        return row;
    }
 
    private void openDetail(AgentDemande demande) {
        DashboardViewController dashboard = getDashboardController();
        if (dashboard != null) {
            dashboard.loadSubView("AgentDemandeDetailsView", "Demandes à traiter");
        }
    }

    private DashboardViewController getDashboardController() {
        return DashboardViewControllerRegistry.getInstance();
    }
}