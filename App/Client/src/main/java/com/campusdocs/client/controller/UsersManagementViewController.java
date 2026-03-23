/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.campusdocs.client.controller;
 
import com.campusdocs.client.model.User;
import com.campusdocs.client.service.UserService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
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
        
//        allUsers = Arrays.asList(
//            new User("U001", "Dupont Jean",    "jean@test.com",    "Usager", "Actif"),
//            new User("U002", "Koné Awa",       "awa@test.com",     "Usager", "Actif"),
//            new User("U003", "Agent Martin",   "martin@camp.com",  "Usager",    "Actif"),
//            new User("U004", "Agent Dubois",   "dubois@camp.com",  "Usager",    "Inactif"),
//            new User("U005", "Admin Root",     "admin@camp.com",   "Admin",    "Actif"),
//            new User("U006", "Mbaye Fatou",    "fatou@test.com",   "Agent", "Actif"),
//            new User("U007", "Traoré Moussa",  "moussa@test.com",  "Agent", "Inactif")
//        );

        roleFilter.setItems(FXCollections.observableArrayList(
            "Tous les rôles", "Étudiant", "Agent", "Admin"));
        roleFilter.getSelectionModel().selectFirst();
        statusFilter.setItems(FXCollections.observableArrayList(
            "Tous les statuts", "Actif", "Inactif"));
        statusFilter.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        roleFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, o, n) -> applyFilters());

        loadUsers();
    }
 
    private void applyFilters() {
        String q = searchField.getText().trim().toLowerCase();
        String role = roleFilter.getValue();
        String status = statusFilter.getValue();
 
        List<User> filtered = allUsers.stream().filter(u -> {
            boolean mq = u.getNom().toLowerCase().contains(q) || u.getEmail().toLowerCase().contains(q);
            boolean mr = role == null || role.equals("Tous les rôles") || u.getRole().equals(role);
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
    
    private void loadUsers() {
    // Show loading state while fetching
    userList.getChildren().clear();
    Label loading = new Label("Chargement des utilisateurs...");
    loading.getStyleClass().add("table-cell-muted");
    userList.getChildren().add(loading);

    TaskRunner.run(
        () -> UserService.getAllUsers(),
        users -> {
            allUsers = Arrays.asList(users);
            applyFilters();
        },
        ex -> {
            userList.getChildren().clear();
            Label error = new Label("Erreur : " + ex.getMessage());
            error.getStyleClass().add("badge-rejected");
            userList.getChildren().add(error);
        }
    );
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
        String initials = Arrays.stream(u.getNom().split(" ")).map(p -> p.isEmpty() ? "" : String.valueOf(p.charAt(0))).collect(Collectors.joining()).toUpperCase();
        if (initials.length() > 2) initials = initials.substring(0, 2);
        Label ini = new Label(initials); ini.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:white;");
        avatar.getChildren().addAll(c, ini);
        VBox nameBox = new VBox(2);
        Label nm = new Label(u.getNom()); nm.getStyleClass().add("table-cell");
        Label em = new Label(u.getEmail()); em.getStyleClass().add("table-cell-muted");
        nameBox.getChildren().addAll(nm, em);
        nameCell.getChildren().addAll(avatar, nameBox);
 
        Label roleBadge = new Label(u.getRole()); roleBadge.getStyleClass().add(u.getRoleBadgeClass()); roleBadge.setPrefWidth(110);
        Label statusBadge = new Label(u.getStatus()); statusBadge.getStyleClass().add(u.getStatusBadgeClass()); statusBadge.setPrefWidth(110);
 
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
 
        // Action buttons — vary by role
        HBox actions = new HBox(6);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
 
        if (!u.getRole().equals("Admin")) {
            Button btnToggle = new Button(u.getStatus().equals("Actif") ? "Désactiver" : "Activer");
            btnToggle.getStyleClass().add(u.getStatus().equals("Actif") ? "btn-warn" : "btn-small");
            btnToggle.setOnAction(e -> toggleUserStatus(u, btnToggle));
            actions.getChildren().add(btnToggle);
 
            if (!u.getRole().equals("Usager")) {
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
        String newStatus = u.isActif() ? "INACTIF" : "ACTIF";

        TaskRunner.run(
            () -> { UserService.toggleUserStatus(u.getId(), !u.isActif()); return null; },
            ignored -> {
                u.toggleStatus();
                btn.setText(u.isActif() ? "Désactiver" : "Activer");
                btn.getStyleClass().removeAll("btn-warn", "btn-small");
                btn.getStyleClass().add(u.isActif() ? "btn-warn" : "btn-small");
                showToast("Statut de " + u.getNom() + " mis à jour.");
                applyFilters();
            },
            ex -> showToast("Erreur : " + ex.getMessage())
        );
    }

    private void deleteUser(User u) {
        TaskRunner.run(
            () -> { UserService.deleteUser(u.getId()); return null; },
            ignored -> {
                allUsers = allUsers.stream()
                    .filter(x -> !x.getId().equals(u.getId()))
                    .collect(Collectors.toList());
                showToast("Compte de " + u.getNom() + " supprimé.");
                applyFilters();
            },
            ex -> showToast("Erreur : " + ex.getMessage())
        );
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

        UserService.CreateAgentRequest request = new UserService.CreateAgentRequest();
        request.firstName = fn;
        request.lastName  = ln;
        request.email     = em;
        request.password  = pw;

        TaskRunner.run(
            () -> UserService.createAgent(request),
            newUser -> {
                modalOverlay.setVisible(false); modalOverlay.setManaged(false);
                clearModal();
                showToast("Compte agent créé pour " + fn + " " + ln + " !");
                loadUsers(); // refresh the list
            },
            ex -> {
                modalError.setText("Erreur : " + ex.getMessage());
                modalError.setVisible(true); modalError.setManaged(true);
            }
        );
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