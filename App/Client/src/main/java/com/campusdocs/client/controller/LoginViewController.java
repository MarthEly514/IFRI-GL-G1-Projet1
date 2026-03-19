/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;

import com.campusdocs.client.SessionManager;
import com.campusdocs.client.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author ely
 */
public class LoginViewController extends BaseViewController{

    @FXML
    private Button loginBtn;
    
    @FXML
    private Hyperlink signupLink;
    
    @FXML
    private AnchorPane leftPane;
    
    @FXML
    private ImageView leftPaneImage;
    

//    @FXML
//    private BorderPane mainBorderPane;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addScaleAnimation(loginBtn);
        addShadowAnimation(loginBtn);
                
        leftPaneImage.fitWidthProperty().bind(leftPane.widthProperty());


        loginBtn.setOnAction(e -> {
            try {
                SessionManager.getInstance().setFullName("Jean Dupont");
                SessionManager.getInstance().setRole(User.Role.Admin);
                navigate("DashboardView", "CampusDocs - Dashboard");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        signupLink.setOnAction(e -> {
            try {
                navigate("SignupView", "CampusDocs - Inscription");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
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
