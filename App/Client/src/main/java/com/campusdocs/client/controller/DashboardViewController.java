package com.campusdocs.client.controller;

import com.campusdocs.client.SessionManager;
import com.campusdocs.client.model.User;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardViewController extends BaseViewController {

    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard;
    @FXML private Button btnDemandes;
    @FXML private Button btnActes;
    @FXML private Button btnProfile;
    @FXML private Button btnStats;
    @FXML private Button btnUsers;
    @FXML private Button btnRapports;
    @FXML private Label pageTitle;
    @FXML private Label portalType;
    @FXML private Label dateLabel;
    @FXML private Label navUserName;
    @FXML private Label topBarUserName;

    // Cache: null means not loaded yet
    private Parent usagerView;
    private Parent demandeView;
    private Parent acteView;
    private Parent profileView;
    private Parent userManagementView;
    private Parent statsView;
    private Parent rapportsView;
    public String userFullName;
    
    //Controllers
    private UsagerViewController userViewController;
    private DemandeViewController demandeViewController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Hides the unaccessible navButtons from basic users
        btnStats.setVisible(false);
        btnStats.setManaged(false);
        btnUsers.setVisible(false);
        btnUsers.setManaged(false);
        btnRapports.setVisible(false);
        btnRapports.setManaged(false);
        
        //Checks user type before loading the view
        checkUserType();
        
        DashboardViewControllerRegistry.register(this); 
        
        //putting user data into the UI
        
        userFullName = SessionManager.getInstance().getFullName();
        navUserName.setText(userFullName);
        topBarUserName.setText(userFullName);
                
        //setting the current date
        setDate();
        
        // Only load the default view at startup
        handleNavDashboard();
    }

    private Parent loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/" + fxml + ".fxml")
            );
            Parent view = loader.load();

            // Store controller reference if needed
            Object controller = loader.getController();
            if (controller instanceof UsagerViewController) {
                userViewController = (UsagerViewController) controller;
            }
            
            if (controller instanceof DemandeViewController) {
                demandeViewController = (DemandeViewController) controller; 
            }

            return view;
        } catch (IOException e) {
            e.printStackTrace();
            return new VBox();
        }
    }
    
    public void loadSubView(String fxml, String title) {
        setActive(
            getNavButtonForFxml(fxml),  
            fxml,
            title
        );
    }
    
    public void loadRawView(Parent view, String title) {
        // Hide all cached views
        contentArea.getChildren().forEach(n -> {
            n.setVisible(false);
            n.setManaged(false);
        });

        // Deactivate all nav buttons
        for (Button b : new Button[]{btnDashboard, btnDemandes, btnActes, btnProfile}) {
            b.getStyleClass().remove("nav-btn-active");
        }
        // Keep Actes button highlighted when viewing detail
        btnActes.getStyleClass().add("nav-btn-active");

        // Add the raw view if not already present
        if (!contentArea.getChildren().contains(view)) {
            contentArea.getChildren().add(view);
        }

        view.setVisible(true);
        view.setManaged(true);
        pageTitle.setText(title);
    }
    
    public void refreshUserLabels() {
        String name = SessionManager.getInstance().getFullName();
        if (navUserName != null)    navUserName.setText(name);
        if (topBarUserName != null) topBarUserName.setText(name);
    }
    
    public DemandeViewController getDemandeViewController() {
        return demandeViewController;
    }
    
    private Button getNavButtonForFxml(String fxml) {
        switch (fxml) {
            case "DemandeView":
            case "DemandeFormView":
                return btnDemandes;
            case "ActeView":
                return btnActes;
            case "ProfileView":
                return btnProfile;
            default:
                return btnDashboard;
        }
    }
    
    public void checkUserType(){
        
        portalType.setText("Portail "+SessionManager.getInstance().getRole());
        
        if(SessionManager.getInstance().getRole() == User.Role.Admin){
            
            btnStats.setVisible(true);
            btnStats.setManaged(true);
            btnUsers.setVisible(true);
            btnUsers.setManaged(true);
            btnRapports.setVisible(true);
            btnRapports.setManaged(true);
            
        }else if(SessionManager.getInstance().getRole() == User.Role.Agent){
            
            btnRapports.setVisible(true);
            
        }
    }

    private void setActive(Button btn, String fxml, String title) {
        // Lazy load: check cache first
        Parent view = getCached(fxml);
        if (view == null) {
            view = loadView(fxml);         // load for the first time
            setCached(fxml, view);         // save to cache
            contentArea.getChildren().add(view); // add to StackPane once
        }

        // Hide all currently loaded views
        contentArea.getChildren().forEach(n -> {
            n.setVisible(false);
            n.setManaged(false);
        });

        // Deactivate all buttons
        for (Button b : new Button[]{btnDashboard, btnDemandes, btnActes, btnProfile, btnStats, btnUsers, btnRapports}) {
            b.getStyleClass().remove("nav-btn-active");
        }

        // Show selected view
        view.setVisible(true);
        view.setManaged(true);
        btn.getStyleClass().add("nav-btn-active");
        pageTitle.setText(title);
    }

    // ── Cache helpers ─────────────────────────────
    
    private final Map<String, Parent> viewCache = new HashMap<>();

    private Parent getCached(String fxml) {
        return viewCache.get(fxml); // returns null if not loaded yet
    }

    private void setCached(String fxml, Parent view) {
        viewCache.put(fxml, view);
    }

    // ── Nav handlers ──────────────────────────────
    @FXML public void handleNavDashboard() { 
        setActive(btnDashboard, "UsagerView", "Dashboard"); 
        // Pass data to UserView's controller
        if (userViewController != null) {
            userViewController.setWelcomeTitle(userFullName);
        }
    }
    
    private void setDate(){
        String dateFormat = "EEEE dd MMMM yyyy";
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern(dateFormat, Locale.FRENCH));
        String finalDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
        dateLabel.setText(finalDate);
    }
    
    @FXML public void handleNavDemandes()  { setActive(btnDemandes,"DemandeView","Demandes Administratives"); }
    @FXML public void handleNavActes()     { setActive(btnActes,"ActeView","Actes Administratifs"); }
    @FXML public void handleNavRapports()   { setActive(btnRapports,"RapportsView", "Rapports"); }
    @FXML public void handleNavStats()   { setActive(btnStats,"StatsView","Statistiques"); }
    @FXML public void handleNavUsers()   { setActive(btnUsers,"UsersManagementView","Gestion des utilisateurs"); }
    @FXML public void handleNavProfile()   { setActive(btnProfile,"ProfileView","Mon profil"); }

    @FXML
    private void handleLogout() {
        try {
            navigate("LoginView", "CampusDocs - Connexion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}