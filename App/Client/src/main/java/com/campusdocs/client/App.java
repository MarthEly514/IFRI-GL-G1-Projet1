package com.campusdocs.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage; 


    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("LoginView"));
        stage.setScene(scene);
        
        stage.setTitle("CampusDocs - Connexion");
        stage.setMinWidth(1080.0);
        stage.setMinHeight(720.0);
        
        // Set restore size and position BEFORE maximizing
        stage.setWidth(1080.0);
        stage.setHeight(720.0);
        stage.centerOnScreen(); // Centrer on screen
        
        stage.setMaximized(true);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    
    public static void setTitle(String title) {
        primaryStage.setTitle(title);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
