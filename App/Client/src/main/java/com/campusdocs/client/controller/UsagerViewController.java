package com.campusdocs.client.controller;

import com.campusdocs.client.SessionManager;
import com.campusdocs.client.model.ActeAdministratif;
import com.campusdocs.client.model.Demande;
import com.campusdocs.client.service.ActeService;
import com.campusdocs.client.service.DemandeService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import java.lang.reflect.Method;

public class UsagerViewController implements Initializable {

    @FXML private Label welcomeTitle;
    @FXML private VBox usagerView;
    @FXML private ImageView bannerImage;

    // Stats
    @FXML private Label statDemandesEnCours;
    @FXML private Label statActesDisponibles;
    @FXML private Label statDemandesTotales;
    @FXML private Label statActesTotaux;

    // Activity list
    @FXML private VBox activityContainer;

    // Hold loaded data to merge into activity list
    private Demande[] loadedDemandes = null;
    private ActeAdministratif[]    loadedActes    = null;
    SessionManager user = SessionManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CssLoader.loadCssFiles(usagerView, "adminshared", "globalStyles", "usagerview");

        String name = user.getFullName();
        if (name != null) {
            welcomeTitle.setText("Bonjour, " + name + " !");
        }

        Image image = new Image(
            getClass().getResourceAsStream("/images/mobile-user.png"));
        bannerImage.setImage(image);

        loadDemandes();
        loadActes();
    }

    public void setWelcomeTitle(String name) {
        welcomeTitle.setText("Bonjour, " + name + " !");
    }

    public void loadDemandes() { ///should be private
        statDemandesEnCours.setText("...");
        statDemandesTotales.setText("...");

        TaskRunner.run(
            () -> DemandeService.getMyDemandes(),
            demandes -> {
                loadedDemandes = demandes;

                long enCours = 0;
                for (Demande d : demandes) {
                    if ("EN_ATTENTE".equalsIgnoreCase(d.getStatut())) enCours++;
                }

                statDemandesEnCours.setText(String.valueOf(enCours));
                statDemandesTotales.setText(String.valueOf(demandes.length));

                tryBuildActivityList();
            },
            ex -> {
                statDemandesEnCours.setText("—");
                statDemandesTotales.setText("—");
                System.err.println("Demandes error: " + ex.getMessage());
                tryBuildActivityList();
            }
        );
    }

    private void loadActes() {
        statActesDisponibles.setText("...");
        statActesTotaux.setText("...");

        TaskRunner.run(
            () -> ActeService.getActesByUsager(user.getUserId()),
            actes -> {
                loadedActes = actes;
                statActesDisponibles.setText(String.valueOf(actes.length));
                statActesTotaux.setText(String.valueOf(actes.length));
                tryBuildActivityList();
            },
            ex -> {
                statActesDisponibles.setText("—");
                statActesTotaux.setText("—");
                System.err.println("Actes error: " + ex.getMessage());
                tryBuildActivityList();
            }
        );
    }

    private void tryBuildActivityList() {
        // Wait until both requests have completed (success or error)
        if (loadedDemandes == null && loadedActes == null) return;

        activityContainer.getChildren().clear();

        List<HBox> rows = new ArrayList<>();

        // Actes — show as completed (green dot)
        if (loadedActes != null) {
            for (int i = loadedActes.length - 1; i >= 0 && rows.size() < 3; i--) {
                ActeAdministratif a = loadedActes[i];
                rows.add(buildRow(
                    a.getName(),
                    "Disponible au téléchargement",
                    a.getFormattedDate(),
                    "activity-dot-green"
                ));
            }
        }

        // Demandes — color based on status
        if (loadedDemandes != null) {
            for (int i = loadedDemandes.length - 1; i >= 0 && rows.size() < 6; i--) {
                Demande d = loadedDemandes[i];
                String dot;
                if ("APPROUVEE".equalsIgnoreCase(d.getStatut())) {
                    dot = "activity-dot-green";
                } else if ("REJETEE".equalsIgnoreCase(d.getStatut())) {
                    dot = "activity-dot-red";
                } else {
                    dot = "activity-dot-amber";
                }
                rows.add(buildRow(
                    d.getDocumentType(),
                    d.getStatusLabel(),
                    d.getDate(),
                    dot
                ));
            }
        }

        if (rows.isEmpty()) {
            Label empty = new Label("Aucune activité récente.");
            empty.getStyleClass().add("activity-sub");
            activityContainer.getChildren().add(empty);
        } else {
            activityContainer.getChildren().addAll(rows);
        }
    }

    private HBox buildRow(String title, String subtitle,
                          String date, String dotStyle) {
        HBox row = new HBox(12);
        row.getStyleClass().add("activity-row");
        row.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(6);
        dot.getStyleClass().add(dotStyle);

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(title != null ? title : "—");
        name.getStyleClass().add("activity-name");
        Label sub = new Label(subtitle != null ? subtitle : "");
        sub.getStyleClass().add("activity-sub");
        info.getChildren().addAll(name, sub);

        Label dateLabel = new Label(date != null ? date : "—");
        dateLabel.getStyleClass().add("activity-date");

        row.getChildren().addAll(dot, info, dateLabel);
        return row;
    }
}