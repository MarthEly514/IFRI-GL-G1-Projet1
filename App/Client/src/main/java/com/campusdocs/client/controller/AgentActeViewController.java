/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;
 
import com.campusdocs.client.SessionManager;
import com.campusdocs.client.model.ActeAdministratif;
import com.campusdocs.client.service.ActeService;
import com.campusdocs.client.service.DemandeService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
 
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
 
public class AgentActeViewController implements Initializable {
 
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter, sortFilter;
    @FXML private VBox actList, emptyState;
    @FXML private VBox rootPane;

 
    private List<ActeAdministratif> allActes;

 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //load css files
        CssLoader.loadCssFiles(rootPane, "adminshared", "globalStyles");
 
        typeFilter.setItems(FXCollections.observableArrayList("Tous les types", "Scolarité", "Notes", "Résidence", "Diplôme"));
        typeFilter.getSelectionModel().selectFirst();
        sortFilter.setItems(FXCollections.observableArrayList("Date (récent → ancien)", "Date (ancien → récent)", "Nom A→Z"));
        sortFilter.getSelectionModel().selectFirst();
 
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        typeFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        sortFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        
        loadActes();

    }
 
    private void applyFilters() {
        String q = searchField.getText().trim().toLowerCase();
        String type = typeFilter.getValue();
 
        List<ActeAdministratif> filtered = allActes.stream().filter(a ->
            (a.getName().toLowerCase().contains(q) || a.getRef().toLowerCase().contains(q)) &&
            (type == null || type.equals("Tous les types") || a.getType().equals(type))
        ).collect(Collectors.toList());
 
        actList.getChildren().clear();
        if (filtered.isEmpty()) {
            emptyState.setVisible(true); emptyState.setManaged(true);
        } else {
            emptyState.setVisible(false); emptyState.setManaged(false);
            for (ActeAdministratif a : filtered) actList.getChildren().add(buildActRow(a));
        }
    }
 
    private HBox buildActRow(ActeAdministratif a) {
        HBox row = new HBox(0);
        row.getStyleClass().add("table-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
 
        Label ref    = new Label(a.getRef());             ref.getStyleClass().add("table-cell-muted");  ref.setPrefWidth(160);
        Label student= new Label("Dupont Jean");          student.getStyleClass().add("table-cell");     student.setPrefWidth(200);
        Label type   = new Label(a.getName());            type.getStyleClass().add("table-cell");        type.setPrefWidth(200);
        Label date   = new Label(a.getFormattedDate());  date.getStyleClass().add("table-cell-muted");  date.setPrefWidth(150);
        Label agent  = new Label("Agent Martin");         agent.getStyleClass().add("table-cell-muted"); agent.setPrefWidth(150);
 
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
 
        javafx.scene.control.Button btnView = new javafx.scene.control.Button("Voir");
        btnView.getStyleClass().add("btn-small");
        btnView.setVisible(SessionManager.getInstance().getRole().equalsIgnoreCase("Agent"));
        // No download button for agent/admin — view only
 
        row.getChildren().addAll(ref, student, type, date, agent, spacer, btnView);
        return row;
    }
    
    private void loadActes() {
        // Show loading state
        actList.getChildren().clear();
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        Label loading = new Label("Chargement des demandes...");
        loading.getStyleClass().add("table-cell-muted");
        actList.getChildren().add(loading);

        TaskRunner.run(
            () -> ActeService.getAllActes(),
            actes -> {
                allActes = Arrays.asList(actes);
                applyFilters();
            },
            ex -> {
                actList.getChildren().clear();
                Label error = new Label("Erreur de chargement : " + ex.getMessage());
                error.getStyleClass().add("badge-rejected");
                actList.getChildren().add(error);
                System.err.println("Demandes load error: " + ex.getMessage());
            }
        );
    }
}

