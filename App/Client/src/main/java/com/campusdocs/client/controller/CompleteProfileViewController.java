/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;

import com.campusdocs.client.SessionManager;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.service.UserService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CompleteProfileViewController extends BaseViewController {

    @FXML private AnchorPane leftPane, rootPane;
    @FXML private ImageView leftPaneImage;

    @FXML private TextField matriculeField;
    @FXML private ComboBox<String> filiereCombo;
    @FXML private ComboBox<String> niveauCombo;
    @FXML private TextField anneeField;

    @FXML private Button submitBtn;
    @FXML private Hyperlink skipLink;
    @FXML private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        CssLoader.loadCssFiles(rootPane, "completeprofileview", "globalStyles");

        // Banner image
        try {
            Image img = new Image(
                getClass().getResourceAsStream("/images/girl_holding_document.png"));
            leftPaneImage.setImage(img);
            leftPaneImage.fitWidthProperty().bind(leftPane.widthProperty());
        } catch (Exception e) {
            System.err.println("Banner image not found.");
        }

        // Populate filière options — adjust to match your institution
        filiereCombo.setItems(FXCollections.observableArrayList(
            "Génie Logiciel",
            "Réseaux et Télécommunications",
            "Systèmes Embarqués",
            "Intelligence Artificielle",
            "Cybersécurité",
            "Sciences des Données",
            "Autre"
        ));

        // Populate niveau options
        niveauCombo.setItems(FXCollections.observableArrayList(
            "Licence 1",
            "Licence 2",
            "Licence 3",
            "Master 1",
            "Master 2",
            "Doctorat"
        ));

        // Default année
        anneeField.setText("2025-2026");
    }

    @FXML
    private void handleSubmit() {
        String matricule = matriculeField.getText().trim();
        String filiere   = filiereCombo.getValue();
        String niveau    = niveauCombo.getValue();
        String annee     = anneeField.getText().trim();

        // Validation
        if (matricule.isEmpty()) {
            showError("Veuillez entrer votre numéro matricule.");
            return;
        }
        if (filiere == null || filiere.isEmpty()) {
            showError("Veuillez sélectionner votre filière.");
            return;
        }
        if (niveau == null || niveau.isEmpty()) {
            showError("Veuillez sélectionner votre niveau.");
            return;
        }
        if (annee.isEmpty()) {
            showError("Veuillez entrer l'année académique.");
            return;
        }

        setLoading(true);

        // Build the request
        UserService.CompleteProfileRequest request =
            new UserService.CompleteProfileRequest(matricule, filiere, niveau, annee);

        TaskRunner.run(
            () -> { UserService.completeProfile(request); return null; },

            ignored -> {
                setLoading(false);

                // Save to session for immediate use
                SessionManager.getInstance().setMatricule(matricule);
                SessionManager.getInstance().setFiliere(filiere);
                SessionManager.getInstance().setNiveau(niveau);
                SessionManager.getInstance().setAnnee(annee);

                // Navigate to login
                try {
                    navigate("LoginView", "CampusDocs - Connexion");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            },

            ex -> {
                setLoading(false);
                if (ex instanceof ApiException) {
                    ApiException apiEx = (ApiException) ex;
                    if (apiEx.isNetworkError()) {
                        showError("Impossible de joindre le serveur. Vérifiez votre connexion.");
                    } else {
                        showError(apiEx.getMessage());
                    }
                } else {
                    showError("Une erreur inattendue s'est produite.");
                }
            }
        );
    }

    @FXML
    private void handleSkip() {
        // Allow user to skip and go directly to login
        try {
            navigate("LoginView", "CampusDocs - Connexion");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setLoading(boolean loading) {
        submitBtn.setDisable(loading);
        submitBtn.setText(loading ? "Enregistrement..." : "Finaliser mon inscription");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}