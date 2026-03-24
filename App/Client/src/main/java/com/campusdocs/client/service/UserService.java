/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.campusdocs.client.service;
 
import com.campusdocs.client.api.ApiClient;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.AgentAdministratif;
import com.campusdocs.client.model.Usager;
import com.campusdocs.client.model.User;
 
public class UserService {
 
    // Admin: get all users
    public static User[] getAllUsers() throws ApiException {
        return ApiClient.get("/users", User[].class);
    }
    
    // GET /api/users/{id}
    public static User getUserById(int id) throws ApiException {
        return ApiClient.get("/users/" + id, User.class);
    }
 
    // Admin: create agent account
    public static User createAgent(CreateAgentRequest request) throws ApiException {
        return ApiClient.post("/users/agent", request, User.class);
    }
 
    // Admin: toggle user active/inactive
    public static User toggleUserStatus(int userId) throws ApiException {
        return ApiClient.patch("/users/" + userId + "/toggle", null, User.class);
    }
 
    // Admin: delete user
    public static void deleteUser(int userId) throws ApiException {
        ApiClient.delete("/users/" + userId);
    }
    
    //Agent :get an agent information
    public static AgentAdministratif getAgentInfo() throws ApiException {
        return ApiClient.get("/user/agent", AgentAdministratif.class);
    }
    //Usager :get an usager information
    public static Usager getUsagerInfo() throws ApiException {
        return ApiClient.get("/user/usager", Usager.class);
    }
    // Update profile (name + email)
    public static void updateProfile(UpdateProfileRequest request) throws ApiException {
        ApiClient.put("/users/me", request, Void.class);
    }

    // Update profile (id)
    public static User updateProfile(int userId, UpdateProfileRequest request) throws ApiException {
        return ApiClient.put("/users/" + userId, request, User.class);
    }
    
    // Change password
    public static void changePassword(ChangePasswordRequest request) throws ApiException {
        ApiClient.put("/users/me/password", request, Void.class);
    }
    
    public static void completeProfile(CompleteProfileRequest request) throws ApiException {
        ApiClient.put("/users/me/profile", request, Void.class);
    }
 
    // ── DTOs ──
    public static class CreateAgentRequest {
        public String nom;        
        public String prenom;    
        public String email;
        public String motDePasse;
        public String dateCreation;
        public boolean actif;
        public String role;
    }
    
    public static class UpdateProfileRequest {
        public String nom;
        public String prenom;
        public String email;

        public UpdateProfileRequest(String nom, String prenom, String email) {
            this.nom    = nom;
            this.prenom = prenom;
            this.email  = email;
        }
    }

    public static class ChangePasswordRequest {
        public String newPassword;
        public String currentPassword;

        public ChangePasswordRequest(String currentPassword, String newPassword) {
            this.currentPassword = currentPassword;
            this.newPassword     = newPassword;
        }
    }
    
    public static class CompleteProfileRequest {
        public String matricule;
        public String filiere;
        public String niveau;
        public String annee;

        public CompleteProfileRequest(String matricule, String filiere,
                                       String niveau, String annee) {
            this.matricule = matricule;
            this.filiere   = filiere;
            this.niveau    = niveau;
            this.annee     = annee;
        }
    }
 
    private static class ToggleStatusRequest {
        boolean active;
        ToggleStatusRequest(boolean active) { this.active = active; }
    }

}
