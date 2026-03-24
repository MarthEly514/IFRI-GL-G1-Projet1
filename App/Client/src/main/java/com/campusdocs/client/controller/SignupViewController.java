/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;
/**
 * FXML Controller class
 *
 * @author ely
 */
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration; 
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.service.AuthService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
 
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.AnchorPane;
 
public class SignupViewController extends BaseViewController {
 
    @FXML private AnchorPane rootPane;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signupBtn;
    @FXML private Hyperlink loginLink;
    @FXML private Label errorLabel;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //loading the stylesheets in the controller
        CssLoader.loadCssFiles(rootPane, "signupview", "globalStyles");

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
 
        signupBtn.setOnAction(e -> handleSignup());
 
        loginLink.setOnAction(e -> {
            try {
                navigate("LoginView", "CampusDocs - Connexion");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
 
    private void handleSignup() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();
        String password  = passwordField.getText();
        String confirm   = confirmPasswordField.getText();
 
        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }
        if (password.length() < 8) {
            showError("Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            return;
        }
        if (!email.contains("@")) {
            showError("Adresse email invalide.");
            return;
        }
 
        setLoading(true);
 
        TaskRunner.run(
            () -> {
                AuthService.signup(firstName, lastName, email, password);
                return null; 
            },
 
            ignored -> {
                setLoading(false);
                // Signup successful — redirect to completing page
                try {
                    navigate("CompleteProfileView", "CampusDocs - Compléter votre profil");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            },
 
            ex -> {
                setLoading(false);
                if (ex instanceof ApiException) {
                    ApiException apiEx = (ApiException) ex;
                    if (apiEx.getStatusCode() == 409) {
                        showError("Cette adresse email est déjà utilisée.");
                    } else if (apiEx.isNetworkError()) {
                        showError("Impossible de joindre le serveur.");
                    } else {
                        showError(apiEx.getMessage());
                    }
                } else {
                    showError("Une erreur inattendue s'est produite.");
                }
            }
        );
    }
 
    private void setLoading(boolean loading) {
        signupBtn.setDisable(loading);
        signupBtn.setText(loading ? "Création du compte..." : "S'inscrire");
    }
 
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
    
    private void addScaleAnimation(Button btn) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(500), btn);
        scaleIn.setToX(1.001);
        scaleIn.setToY(1.001);
        scaleIn.setInterpolator(Interpolator.EASE_BOTH) ;

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(500), btn);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        scaleOut.setInterpolator(Interpolator.EASE_BOTH) ;

        btn.setOnMouseEntered(e -> {
            scaleOut.stop();
            scaleIn.play();
        });

        btn.setOnMouseExited(e -> {
            scaleIn.stop();
            scaleOut.play();
        });
    }
    
    public void addShadowAnimation(Button btn) {
        DropShadow shadow = new DropShadow();
        btn.setEffect(shadow);

        btn.setOnMouseEntered(e -> {
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(shadow.radiusProperty(), 5)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(shadow.radiusProperty(), 12))
            );
            timeline.play();
        });

        btn.setOnMouseExited(e -> {
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(shadow.radiusProperty(), 12)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(shadow.radiusProperty(), 5))
            );
            timeline.play();
        });
    } 
}
