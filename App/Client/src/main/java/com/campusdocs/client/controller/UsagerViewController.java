/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;

import com.campusdocs.client.SessionManager;
import com.campusdocs.client.util.CssLoader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author ely
 */
public class UsagerViewController implements Initializable {

    @FXML private Label welcomeTitle;
    @FXML private VBox usagerView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //load css
        CssLoader.loadCssFiles(usagerView, "adminshared", "globalStyles", "usagerview");
        // Set initial value from session
        String name = SessionManager.getInstance().getFullName();
        if (name != null) {
            welcomeTitle.setText("Bonjour, " + name + " !");
        }
    }

    // Called by DashboardViewController if it needs to update
    public void setWelcomeTitle(String name) {
        welcomeTitle.setText("Bonjour, " + name + " !");
    }
}
