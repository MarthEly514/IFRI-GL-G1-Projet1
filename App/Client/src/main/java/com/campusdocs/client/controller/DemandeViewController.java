/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package com.campusdocs.client.controller;

import com.campusdocs.client.App;
import com.campusdocs.client.model.Demande;
import com.campusdocs.client.service.DemandeService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.layout.StackPane;

public class DemandeViewController implements Initializable {

    @FXML private Button btnFaireDemande;
    @FXML private VBox rootPane, emptyState;
    @FXML private VBox demandeList;
    @FXML private StackPane toastContainer;
    @FXML private Label toastLabel;

    // Holds demands loaded from backend (or local state for now)
    private final List<Demande> demandesArray = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //loadin css files
        CssLoader.loadCssFiles(rootPane, "demandeview", "globalStyles");

//        refreshList();
        loadDemandes();
    }
    
    private void loadDemandes(){
                // Show loading placeholder
        demandeList.getChildren().clear();
        demandeList.getChildren().add(buildLoadingLabel("Chargement..."));

        TaskRunner.run(
            () -> DemandeService.getMyDemandes(),
            demandes -> {
                demandeList.getChildren().clear();

                if (demandes == null || demandes.length == 0) {
                    emptyState.setVisible(true);
                    emptyState.setManaged(true);
                    demandeList.setVisible(false);
                    demandeList.setManaged(false);
//                    demandeList.getChildren()
//                        .add(buildEmptyLabel("Aucune demande récente."));
                    return;
                }
                
                emptyState.setVisible(false);
                emptyState.setManaged(false);
                demandeList.setVisible(true);
                demandeList.setManaged(true);
                demandeList.getChildren().clear();

                int count = demandes.length;
                for (int i = 0; i < count; i++) {
                    demandesArray.add(demandes[i]);
                    demandeList.getChildren()
                        .add(buildDemandCard(demandes[i]));
                }
            },
            ex -> {
                demandeList.getChildren().clear();
                demandeList.getChildren()
                    .add(buildEmptyLabel("Erreur de chargement."));
                System.err.println("Recent demandes error: " + ex.getMessage());
            }
        );
    }

    @FXML
    private void handleFaireDemande() {
        // Navigate to form view inside the dashboard's content area
        DashboardViewController dashboard = getDashboardController();
        if (dashboard != null) {
            dashboard.loadSubView("DemandeFormView", "Nouvelle demande");
        }
    }

    // Called by DashboardViewController after a successful form submission
    public void onDemandeSubmitted(Demande demande) {
        demandesArray.add(demande);
        refreshList();
        showToast("Demande envoyée avec succès !");
    }

    private void refreshList() {
        if (demandesArray.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
            demandeList.setVisible(false);
            demandeList.setManaged(false);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            demandeList.setVisible(true);
            demandeList.setManaged(true);
            demandeList.getChildren().clear();
            for (Demande d : demandesArray) {
                demandeList.getChildren().add(buildDemandCard(d));
            }
        }
    }
    
    private Label buildLoadingLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("activity-sub");
        l.setStyle("-fx-opacity: 0.6;");
        return l;
    }
    
    private Label buildEmptyLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("activity-sub");
        return l;
    }

    private HBox buildDemandCard(Demande d) {
        HBox card = new HBox(12);
        card.getStyleClass().add("demand-card");
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Circle dot = new Circle(6);
        dot.getStyleClass().add("activity-dot-teal");

        VBox info = new VBox(3);
        info.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);
        Label name = new Label(d.getDocumentType());
        name.getStyleClass().add("demand-doc-type");
        Label date = new Label(d.getDate());
        date.getStyleClass().add("demand-date");
        info.getChildren().addAll(name, date);

        Label statusBadge = new Label(d.getStatusLabel());
        statusBadge.getStyleClass().add(d.getStatusStyleClass());

        card.getChildren().addAll(dot, info, statusBadge);
        return card;
    }

    public void showToast(String message) {
        toastLabel.setText(message);
        toastContainer.setVisible(true);
        toastContainer.setManaged(true);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toastContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Hold then fade out
        PauseTransition hold = new PauseTransition(Duration.seconds(3));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toastContainer);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            toastContainer.setVisible(false);
            toastContainer.setManaged(false);
        });

        new SequentialTransition(fadeIn, hold, fadeOut).play();
    }

    private DashboardViewController getDashboardController() {
        // Access dashboard controller via a shared reference or singleton
        return DashboardViewControllerRegistry.getInstance();
    }
}
