/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package com.campusdocs.client.controller;

import com.campusdocs.client.SessionManager;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.User;
import com.campusdocs.client.service.UserService;
import com.campusdocs.client.util.CssLoader;
import com.campusdocs.client.util.TaskRunner;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ProfileViewController implements Initializable {

    @FXML private VBox rootPane;
    // ── Avatar ──
    @FXML private Label avatarInitials;

    // ── Info fields ──
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField matriculeField;
    @FXML private HBox academicLevelRow1;
    @FXML private HBox academicLevelRow2;
    @FXML private TextField filiereField;
    @FXML private TextField niveauField;
    @FXML private TextField anneeField;

    // ── Role & status ──
    @FXML private Label roleLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label lastLoginLabel;

    // ── Password form ──
    @FXML private VBox passwordForm;
    @FXML private PasswordField currentPwdField;
    @FXML private PasswordField newPwdField;
    @FXML private PasswordField confirmPwdField;
    @FXML private Label pwdErrorLabel;
    
    // Buttons
    @FXML private Button btnSave;

    // ── Toast ──
    @FXML private StackPane toastContainer;
    @FXML private Label toastLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //load css
        CssLoader.loadCssFiles(rootPane, "profileview", "globalStyles");

        populateFromSession();
    }

    private void populateFromSession() {
        SessionManager session = SessionManager.getInstance();

        String fullName = session.getFullName() != null ? session.getFullName() : "Étudiant Demo";
        String[] parts  = fullName.trim().split(" ", 2);
        String firstName = parts.length > 0 ? parts[0] : "Étudiant";
        String lastName  = parts.length > 1 ? parts[1] : "";

        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        emailField.setText(session.getEmail() != null ? session.getEmail() : "etudiant@campusdocs.com");

        // Initials on avatar
        String initials = (firstName.isEmpty() ? "?" : String.valueOf(firstName.charAt(0)).toUpperCase())
                        + (lastName.isEmpty()  ? ""  : String.valueOf(lastName.charAt(0)).toUpperCase());
        avatarInitials.setText(initials);

        // Academic info — replace with real session fields when available
        matriculeField.setText(session.getMatricule());
        filiereField.setText(session.getFiliere());
        niveauField.setText(session.getNiveau());
        anneeField.setText(session.getAnnee());

        // Role
        String role = session.getRole() != null ? session.getRole() : "Étudiant";
        roleLabel.setText(role);
        applyRoleBadge(role);
        
        //hide academic level if role is not usager/etudiant
        boolean isUsager = !session.getRole().equalsIgnoreCase("ADMIN") && !session.getRole().equalsIgnoreCase("AGENT");
        academicLevelRow1.setVisible(isUsager);
        academicLevelRow1.setManaged(isUsager);
        academicLevelRow2.setVisible(isUsager);
        academicLevelRow2.setManaged(isUsager);

        // Dates
        memberSinceLabel.setText("Octobre 2021");
        lastLoginLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

    private void applyRoleBadge(String role) {
        roleLabel.getStyleClass().removeAll("role-badge-student", "role-badge-admin", "role-badge-staff");
        if (role.equalsIgnoreCase("Admin")) {
            roleLabel.getStyleClass().add("role-badge-admin");
        } else if (role.equalsIgnoreCase("Staff")) {
            roleLabel.getStyleClass().add("role-badge-staff");
        } else {
            roleLabel.getStyleClass().add("role-badge-student");
        }
    }

    // ─────────────────────────────────────────
    // PHOTO
    // ─────────────────────────────────────────
    @FXML
    private void handleUploadPhoto() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir une photo de profil");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fc.showOpenDialog(null);
        if (file != null) {
            // TODO: load image into avatarCircle using ImageView overlay
            showToast("Photo mise à jour !");
        }
    }

    @FXML
    private void handleRemovePhoto() {
        // Reset to initials
        showToast("Photo supprimée.");
    }

    // ─────────────────────────────────────────
    // EMAIL
    // ─────────────────────────────────────────
    @FXML
    private void handleEditEmail() {
        boolean isEditable = !emailField.isEditable();
        emailField.setEditable(isEditable);
        if (isEditable) {
            emailField.getStyleClass().remove("input-readonly");
            emailField.requestFocus();
        } else {
            emailField.getStyleClass().add("input-readonly");
        }
    }

    // ─────────────────────────────────────────
    // PASSWORD
    // ─────────────────────────────────────────
    @FXML
    private void handleChangePassword() {
        boolean visible = !passwordForm.isVisible();
        passwordForm.setVisible(visible);
        passwordForm.setManaged(visible);
        if (visible) {
            currentPwdField.requestFocus();
        } else {
            clearPasswordForm();
        }
    }

    @FXML
    private void handleCancelPassword() {
        passwordForm.setVisible(false);
        passwordForm.setManaged(false);
        clearPasswordForm();
    }

    @FXML
    private void handleSavePassword() {
        String current = currentPwdField.getText().trim();
        String newPwd  = newPwdField.getText().trim();
        String confirm = confirmPwdField.getText().trim();

        // Client-side validation first
        if (current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }
        if (newPwd.length() < 8) {
            showError("Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }
        if (!newPwd.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            return;
        }

        UserService.ChangePasswordRequest request =
            new UserService.ChangePasswordRequest(current, newPwd);

        TaskRunner.run(
            () -> { UserService.changePassword(request); return null; },

            ignored -> {
                passwordForm.setVisible(false);
                passwordForm.setManaged(false);
                clearPasswordForm();
                showToast("Mot de passe mis à jour avec succès !");
            },

            ex -> {
                if (ex instanceof ApiException) {
                    ApiException apiEx = (ApiException) ex;
                    if (apiEx.isUnauthorized()) {
                        showError("Mot de passe actuel incorrect.");
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

    private void showError(String message) {
        pwdErrorLabel.setText(message);
        pwdErrorLabel.setVisible(true);
        pwdErrorLabel.setManaged(true);
    }

    private void clearPasswordForm() {
        currentPwdField.clear();
        newPwdField.clear();
        confirmPwdField.clear();
        pwdErrorLabel.setVisible(false);
        pwdErrorLabel.setManaged(false);
    }

    // ─────────────────────────────────────────
    // SAVE / CANCEL
    // ─────────────────────────────────────────
    @FXML
    
    private void handleSave() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            showToast("Prénom et nom sont obligatoires.");
            return;
        }
        if (!email.contains("@")) {
            showToast("Adresse email invalide.");
            return;
        }

        // Disable button while saving
        btnSave.setText("Enrégistrement en cours..");
        btnSave.setDisable(true);

        UserService.UpdateProfileRequest request =
            new UserService.UpdateProfileRequest(firstName, lastName, email);

        TaskRunner.run(
            () -> { UserService.updateProfile(request); return null; },

            ignored -> {
                // ✅ Update session with new values
                SessionManager.getInstance().setFullName(firstName + " " + lastName);
                SessionManager.getInstance().setEmail(email);

                // ✅ Update avatar initials
                String initials = String.valueOf(firstName.charAt(0)).toUpperCase()
                                + String.valueOf(lastName.charAt(0)).toUpperCase();
                avatarInitials.setText(initials);

                // ✅ Refresh topbar in dashboard
                DashboardViewController dashboard =
                    DashboardViewControllerRegistry.getInstance();
                if (dashboard != null) {
                    dashboard.refreshUserLabels();
                }
                btnSave.setDisable(false);
                btnSave.setText("Enregistrer les modifications");

                showToast("Profil mis à jour avec succès !");
            },

            ex -> {
                if (ex instanceof ApiException) {
                    ApiException apiEx = (ApiException) ex;
                    if (apiEx.getStatusCode() == 409) {
                        showToast("Cette adresse email est déjà utilisée.");
                    } else if (apiEx.isNetworkError()) {
                        showToast("Impossible de joindre le serveur.");
                    } else {
                        showToast("Erreur : " + apiEx.getMessage());
                    }
                } else {
                    showToast("Une erreur inattendue s'est produite.");
                }
                
                btnSave.setDisable(false);
                btnSave.setText("Enregistrer les modifications");
            }
        );
    }

    @FXML
    private void handleCancel() {
        populateFromSession(); // reset to original values
    }

    // ─────────────────────────────────────────
    // TOAST
    // ─────────────────────────────────────────
    private void showToast(String message) {
        toastLabel.setText(message);
        toastContainer.setVisible(true);
        toastContainer.setManaged(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), toastContainer);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        PauseTransition hold = new PauseTransition(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toastContainer);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            toastContainer.setVisible(false);
            toastContainer.setManaged(false);
        });

        new SequentialTransition(fadeIn, hold, fadeOut).play();
    }
}
