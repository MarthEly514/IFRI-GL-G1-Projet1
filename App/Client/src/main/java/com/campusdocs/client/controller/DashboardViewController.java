package com.campusdocs.client.controller;

import com.campusdocs.client.App;
import com.campusdocs.client.SessionManager;
import com.campusdocs.client.model.User;
import com.campusdocs.client.util.CssLoader;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardViewController extends BaseViewController {

    @FXML private BorderPane rootPane;
    @FXML private StackPane contentArea;
    @FXML private ImageView logoImage;
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
    
    //icons
    @FXML private ImageView iconDashboard;
    @FXML private ImageView iconDemandes;
    @FXML private ImageView iconActes;
    @FXML private ImageView iconProfile;
    @FXML private ImageView iconStats;
    @FXML private ImageView iconUsers;
    @FXML private ImageView iconRapports;

    private Map<Button, Image[]> buttonIcons;

    // Cache: null means not loaded yet
    private Parent usagerView;
    private Parent demandeView;
    private Parent acteView;
    private Parent profileView;
    private Parent userManagementView;
    private Parent statsView;
    private Parent rapportsView;
    public String userFullName;
    public SessionManager currentUser;
    
    //Controllers
    private UsagerViewController userViewController;
    private DemandeViewController demandeViewController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //loading css file
        CssLoader.loadCssFiles(rootPane, "dashboardview", "globalStyles");
        
        //Hides the unaccessible navButtons from basic users
        btnStats.setVisible(false);
        btnStats.setManaged(false);
        btnUsers.setVisible(false);
        btnUsers.setManaged(false);
        btnRapports.setVisible(false);
        btnRapports.setManaged(false);
        currentUser = SessionManager.getInstance();
        
        //loading icons and images
        Image logo = new Image(getClass().getResourceAsStream("/icons/logo.png"));

        logoImage.setImage(logo);
        
        //Checks user type before loading the view
        checkUserType();
        
        DashboardViewControllerRegistry.register(this); 
        
        //putting user data into the UI
        
        userFullName = currentUser.getFullName();
        navUserName.setText(userFullName);
        topBarUserName.setText(userFullName);
                
        //setting the current date
        setDate();
        
        setupButtonIcons();
        setupButtonIconViews();  
        
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
    
    private void setupButtonIcons() {
        buttonIcons = new HashMap<>();

        buttonIcons.put(btnDashboard, new Image[]{
            loadImage("/icons/dashboard.png"),
            loadImage("/icons/dashboard_black.png")
        });
        buttonIcons.put(btnDemandes, new Image[]{
            loadImage("/icons/demandes.png"),
            loadImage("/icons/demandes_black.png")
        });
        buttonIcons.put(btnActes, new Image[]{
            loadImage("/icons/actes.png"),
            loadImage("/icons/actes_black.png")
        });
        buttonIcons.put(btnProfile, new Image[]{
            loadImage("/icons/profile.png"),
            loadImage("/icons/profile_black.png")
        });
        buttonIcons.put(btnStats, new Image[]{
            loadImage("/icons/stats.png"),
            loadImage("/icons/stats_black.png")
        });
        buttonIcons.put(btnUsers, new Image[]{
            loadImage("/icons/users.png"),
            loadImage("/icons/users_black.png")
        });
        buttonIcons.put(btnRapports, new Image[]{
            loadImage("/icons/rapport.png"),
            loadImage("/icons/rapport_black.png")
        });
        
        
    }

    // Maps each button to its ImageView
    private Map<Button, ImageView> buttonIconViews;

    private void setupButtonIconViews() {
        buttonIconViews = new HashMap<>();
        buttonIconViews.put(btnDashboard, iconDashboard);
        buttonIconViews.put(btnDemandes,  iconDemandes);
        buttonIconViews.put(btnActes,     iconActes);
        buttonIconViews.put(btnProfile,   iconProfile);
        buttonIconViews.put(btnStats,     iconStats);
        buttonIconViews.put(btnUsers,     iconUsers);
        buttonIconViews.put(btnRapports,  iconRapports);
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.err.println("Image not found: " + path);
            return null;
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
        String name = currentUser.getFullName();
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
        
        if(SessionManager.getInstance().getRole().equalsIgnoreCase("Admin")){
            
            btnStats.setVisible(true);
            btnStats.setManaged(true);
            btnUsers.setVisible(true);
            btnUsers.setManaged(true);
            btnRapports.setVisible(true);
            btnRapports.setManaged(true);
            
        }
    }

    private void setActive(Button btn, String fxml, String title) {
        Parent view = getCached(fxml);
        if (view == null) {
            view = loadView(fxml);
            setCached(fxml, view);
            contentArea.getChildren().add(view);
        }

        contentArea.getChildren().forEach(n -> {
            n.setVisible(false);
            n.setManaged(false);
        });

        //Deactivate all buttons + reset their icons
        Button[] allBtns = {btnDashboard, btnDemandes, btnActes,
                            btnProfile, btnStats, btnUsers, btnRapports};

        for (Button b : allBtns) {
            b.getStyleClass().remove("nav-btn-active");

            // Set inactive icon
            ImageView iconView = buttonIconViews.get(b);
            Image[]   icons    = buttonIcons.get(b);
            if (iconView != null && icons != null && icons[1] != null) {
                iconView.setImage(icons[1]); // index 1 = inactive (black)
            }
        }

        view.setVisible(true);
        view.setManaged(true);
        btn.getStyleClass().add("nav-btn-active");
        pageTitle.setText(title);

        ImageView activeIconView = buttonIconViews.get(btn);
        Image[]   activeIcons    = buttonIcons.get(btn);
        if (activeIconView != null && activeIcons != null && activeIcons[0] != null) {
            activeIconView.setImage(activeIcons[0]); 
        }
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
    @FXML 
    public void handleNavDashboard() { 
        if (null == currentUser.getRole()) {
            // Session not set — redirect back to login
            try {
                App.setRoot("LoginView");
                App.setTitle("CampusDocs - Connexion");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
        else switch (currentUser.getRole().toUpperCase()) {
            case "ADMIN":
                setActive(btnDashboard, "AdminView", "Dashboard");
                // Pass data to UserView's controller
                if (userViewController != null) {
                    userViewController.setWelcomeTitle(userFullName);
                }   break;
            case "AGENT":
                setActive(btnDashboard, "AgentView", "Dashboard");
                // Pass data to UserView's controller
                if (userViewController != null) {
                    userViewController.setWelcomeTitle(userFullName);
                }   break;
            default:
                setActive(btnDashboard, "UsagerView", "Dashboard");
                // Pass data to UserView's controller
                if (userViewController != null) {
                    userViewController.setWelcomeTitle(userFullName);
                }   break;
        }
    }
    
    private void setDate(){
        String dateFormat = "EEEE dd MMMM yyyy";
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern(dateFormat, Locale.FRENCH));
        String finalDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
        dateLabel.setText(finalDate);
    }
    
    @FXML public void handleNavDemandes(){ 
        switch (currentUser.getRole().toUpperCase()) {
            case "ADMIN":
                setActive(btnDemandes,"AgentDemandeView","Demandes Administratives"); 
                break;
            case "AGENT":
                setActive(btnDemandes,"AgentDemandeView","Demandes Administratives"); 
                break;
            default:
                setActive(btnDemandes,"DemandeView","Demandes Administratives"); 
                break;
        }
    }
    @FXML public void handleNavActes(){ 
        switch (currentUser.getRole().toUpperCase()) {
            case "ADMIN":
                setActive(btnActes,"AgentActeView","Actes Administratifs"); 
                break;
            case "AGENT":
                setActive(btnActes,"AgentActeView","Actes Administratifs"); 
                break;
            default:
                setActive(btnActes,"ActeView","Actes Administratifs"); 
                break;
        }
    }
    @FXML public void handleNavRapports(){
        setActive(btnRapports,"RapportsView", "Rapports"); 
    }
    @FXML public void handleNavStats(){ 
        setActive(btnStats,"StatsView","Statistiques"); 
    }
    @FXML public void handleNavUsers(){ 
        setActive(btnUsers,"UsersManagementView","Gestion des utilisateurs"); 
    }
    @FXML public void handleNavProfile(){
        setActive(btnProfile,"ProfileView","Mon profil"); 
    }

    @FXML
    private void handleLogout() {
        try {
            navigate("LoginView", "CampusDocs - Connexion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}