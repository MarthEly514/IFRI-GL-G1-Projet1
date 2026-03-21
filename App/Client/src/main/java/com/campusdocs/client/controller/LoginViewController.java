/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import com.campusdocs.client.SessionManager;
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
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
 
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.AnchorPane;
 
public class LoginViewController extends BaseViewController {
 
    @FXML private AnchorPane rootPane;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Hyperlink signupLink;
    @FXML private Label errorLabel;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            
        CssLoader.loadCssFiles(rootPane, "loginview", "globalStyles");
        
        addHoverAnimation(loginBtn);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
 
        loginBtn.setOnAction(e -> handleLogin());
 
        signupLink.setOnAction(e -> {
            try {
                navigate("SignupView", "CampusDocs - Inscription");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
 
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
 
        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }
        
        //TEST_MODE To delete on prod
        
//        navigateToDashboard();
        
 
        setLoading(true);
 
        TaskRunner.run(
            // Background thread — call API
            () -> { 
                AuthService.login(email, password);       
                return null; 
            },
 
            // Success — back on JavaFX thread
            ignored -> {
                setLoading(false);
                navigateToDashboard();
            },
 
            // Error — back on JavaFX thread
            ex -> {
                setLoading(false);
                if (ex instanceof ApiException) {
                    ApiException apiEx = (ApiException) ex;
                    if (apiEx.isUnauthorized()) {
                        showError("Email ou mot de passe incorrect.");
                    } else if (apiEx.isNetworkError()) {
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
 
    private void navigateToDashboard() {
        try {
            String role = SessionManager.getInstance().getRole();
            System.out.println(role);
            String title;
            if ("Admin".equalsIgnoreCase(role)) {
                title = "CampusDocs - Administration";
            } else if ("Agent".equalsIgnoreCase(role)) {
                title = "CampusDocs - Agent";
            } else {
                title = "CampusDocs - Espace étudiant";
            }
            navigate("DashboardView", title);
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Erreur de navigation.");
        }
    }
 
    private void setLoading(boolean loading) {
        loginBtn.setDisable(loading);
        loginBtn.setText(loading ? "Connexion en cours..." : "Se connecter");
    }
 
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
 
    private void addHoverAnimation(Button btn) {
        ScaleTransition scaleIn  = new ScaleTransition(Duration.millis(200), btn);
        scaleIn.setToX(1.02); scaleIn.setToY(1.02);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), btn);
        scaleOut.setToX(1.0); scaleOut.setToY(1.0);
        btn.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.play(); });
        btn.setOnMouseExited(e ->  { scaleIn.stop();  scaleOut.play(); });
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
