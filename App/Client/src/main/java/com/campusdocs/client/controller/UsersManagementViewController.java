/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;
 
import com.campusdocs.client.model.User;
import com.campusdocs.client.util.CssLoader;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
 
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
 
public class UsersManagementViewController implements Initializable {
 
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter, statusFilter;
    @FXML private VBox rootPane, userList, emptyState;
    @FXML private StackPane modalOverlay, toastContainer;
    @FXML private Label toastLabel, modalError;
    @FXML private TextField agentFirstName, agentLastName, agentEmail;
    @FXML private PasswordField agentPassword;
 
    private List<User> allUsers;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //load css
        CssLoader.loadCssFiles(rootPane, "adminshared", "globalStyles", "usersmanagementview");
        
        allUsers = Arrays.asList(
            new User("U001", "Dupont Jean",    "jean@test.com",    User.Role.Usager, "Actif"),
            new User("U002", "Koné Awa",       "awa@test.com",     User.Role.Usager, "Actif"),
            new User("U003", "Agent Martin",   "martin@camp.com",  User.Role.Agent,    "Actif"),
            new User("U004", "Agent Dubois",   "dubois@camp.com",  User.Role.Agent,    "Inactif"),
            new User("U005", "Admin Root",     "admin@camp.com",   User.Role.Admin,    "Actif"),
            new User("U006", "Mbaye Fatou",    "fatou@test.com",   User.Role.Usager, "Actif"),
            new User("U007", "Traoré Moussa",  "moussa@test.com",  User.Role.Usager, "Inactif")
        );
 
        roleFilter.setItems(FXCollections.observableArrayList("Tous les rôles", "Étudiant", "Agent", "Admin"));
        roleFilter.getSelectionModel().selectFirst();
        statusFilter.setItems(FXCollections.observableArrayList("Tous les statuts", "Actif", "Inactif"));
        statusFilter.getSelectionModel().selectFirst();
 
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        roleFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
 
        applyFilters();
    }
 
    private void applyFilters() {
        String q = searchField.getText().trim().toLowerCase();
        String role = roleFilter.getValue();
        String status = statusFilter.getValue();
 
        List<User> filtered = allUsers.stream().filter(u -> {
            boolean mq = u.getName().toLowerCase().contains(q) || u.getEmail().toLowerCase().contains(q);
            boolean mr = role == null || role.equals("Tous les rôles") || u.getRole().toString().equals(role);
            boolean ms = status == null || status.equals("Tous les statuts") || u.getStatus().equals(status);
            return mq && mr && ms;
        }).collect(Collectors.toList());
 
        userList.getChildren().clear();
        if (filtered.isEmpty()) {
            emptyState.setVisible(true); emptyState.setManaged(true);
        } else {
            emptyState.setVisible(false); emptyState.setManaged(false);
            for (User u : filtered) userList.getChildren().add(buildUserRow(u));
        }
    }
 
    private HBox buildUserRow(User u) {
        HBox row = new HBox(0);
        row.getStyleClass().add("table-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
 
        // Avatar + name
        HBox nameCell = new HBox(10);
        nameCell.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        nameCell.setPrefWidth(230);
        StackPane avatar = new StackPane();
        Circle c = new Circle(16); c.getStyleClass().add("stat-card-accent");
        String initials = Arrays.stream(u.getName().split(" ")).map(p -> p.isEmpty() ? "" : String.valueOf(p.charAt(0))).collect(Collectors.joining()).toUpperCase();
        if (initials.length() > 2) initials = initials.substring(0, 2);
        Label ini = new Label(initials); ini.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:white;");
        avatar.getChildren().addAll(c, ini);
        VBox nameBox = new VBox(2);
        Label nm = new Label(u.getName()); nm.getStyleClass().add("table-cell");
        Label em = new Label(u.getEmail()); em.getStyleClass().add("table-cell-muted");
        nameBox.getChildren().addAll(nm, em);
        nameCell.getChildren().addAll(avatar, nameBox);
 
        Label roleBadge = new Label(u.getRole().toString()); roleBadge.getStyleClass().add(u.getRoleBadgeClass()); roleBadge.setPrefWidth(110);
        Label statusBadge = new Label(u.getStatus()); statusBadge.getStyleClass().add(u.getStatusBadgeClass()); statusBadge.setPrefWidth(110);
 
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
 
        // Action buttons — vary by role
        HBox actions = new HBox(6);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
 
        if (!u.getRole().toString().equals("Admin")) {
            Button btnToggle = new Button(u.getStatus().equals("Actif") ? "Désactiver" : "Activer");
            btnToggle.getStyleClass().add(u.getStatus().equals("Actif") ? "btn-warn" : "btn-small");
            btnToggle.setOnAction(e -> toggleUserStatus(u, btnToggle));
            actions.getChildren().add(btnToggle);
 
            if (!u.getRole().toString().equals("Étudiant")) {
                Button btnDelete = new Button("Supprimer");
                btnDelete.getStyleClass().add("btn-danger");
                btnDelete.setOnAction(e -> deleteUser(u));
                actions.getChildren().add(btnDelete);
            }
        }
 
        row.getChildren().addAll(nameCell, roleBadge, statusBadge, spacer, actions);
        return row;
    }
 
    private void toggleUserStatus(User u, Button btn) {
        // API call to toggle status
        boolean isActive = u.getStatus().equals("Actif");
        u.setStatus(isActive ? "Inactif" : "Actif");
        btn.setText(isActive ? "Activer" : "Désactiver");
        showToast("Statut de " + u.getName() + " mis à jour.");
        applyFilters();
    }
 
    private void deleteUser(User u) {
        // API call to delete
        allUsers = allUsers.stream().filter(x -> !x.getId().equals(u.getId())).collect(Collectors.toList());
        showToast("Compte de " + u.getName() + " supprimé.");
        applyFilters();
    }
 
    @FXML private void handleCreateAgent() {
        modalOverlay.setVisible(true); modalOverlay.setManaged(true);
    }
 
    @FXML private void handleCancelModal() {
        modalOverlay.setVisible(false); modalOverlay.setManaged(false);
        clearModal();
    }
 
    @FXML private void handleConfirmCreateAgent() {
        String fn = agentFirstName.getText().trim();
        String ln = agentLastName.getText().trim();
        String em = agentEmail.getText().trim();
        String pw = agentPassword.getText();
 
        if (fn.isEmpty() || ln.isEmpty() || em.isEmpty() || pw.isEmpty()) {
            modalError.setText("Veuillez remplir tous les champs.");
            modalError.setVisible(true); modalError.setManaged(true);
            return;
        }
        // API call: create agent account
        User newAgent = new User("U" + System.currentTimeMillis(), fn + " " + ln, em, User.Role.Agent, "Actif");
        // allUsers would be mutable in real impl
        modalOverlay.setVisible(false); modalOverlay.setManaged(false);
        clearModal();
        showToast("Compte agent créé pour " + fn + " " + ln + " !");
    }
 
    private void clearModal() {
        agentFirstName.clear(); agentLastName.clear(); agentEmail.clear(); agentPassword.clear();
        modalError.setVisible(false); modalError.setManaged(false);
    }
 
    private void showToast(String msg) {
        toastLabel.setText(msg);
        toastContainer.setVisible(true); toastContainer.setManaged(true);
        FadeTransition fi = new FadeTransition(Duration.millis(250), toastContainer); fi.setFromValue(0); fi.setToValue(1);
        PauseTransition hold = new PauseTransition(Duration.seconds(3));
        FadeTransition fo = new FadeTransition(Duration.millis(400), toastContainer); fo.setFromValue(1); fo.setToValue(0);
        fo.setOnFinished(e -> { toastContainer.setVisible(false); toastContainer.setManaged(false); });
        new SequentialTransition(fi, hold, fo).play();
    }
}