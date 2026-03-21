/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.util;

/**
 *
 * @author ely
 */
import javafx.scene.Parent;
import java.net.URL;

public class CssLoader {
    
    /**
     * Load a CSS file from the /styles directory and add it to a node
     * @param node The node to add the stylesheet to
     * @param cssFileName The name of the CSS file without extension 
     * @return true if CSS was loaded successfully, false otherwise
     */
    public static boolean loadCss(Parent node, String cssFileName) {
        try {
            String cssPath = "/styles/" + cssFileName + ".css";
            URL cssUrl = CssLoader.class.getResource(cssPath);
            
            if (cssUrl != null) {
                node.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("Loaded CSS: " + cssPath);
                return true;
            } else {
                System.err.println("CSS not found: " + cssPath);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error loading CSS " + cssFileName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load multiple CSS files at once
     * @param node The node to add the stylesheets to
     * @param cssFileNames Array of CSS filenames without extension
     */
    public static void loadCssFiles(Parent node, String... cssFileNames) {
        for (String cssFileName : cssFileNames) {
            loadCss(node, cssFileName);
        }
    }
}