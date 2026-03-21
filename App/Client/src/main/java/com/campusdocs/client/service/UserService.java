/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.campusdocs.client.service;
 
import com.campusdocs.client.api.ApiClient;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.User;
 
public class UserService {
 
    // Admin: get all users
    public static User[] getAllUsers() throws ApiException {
        return ApiClient.get("/users", User[].class);
    }
 
    // Admin: create agent account
    public static User createAgent(CreateAgentRequest request) throws ApiException {
        return ApiClient.post("/users/agent", request, User.class);
    }
 
    // Admin: toggle user active/inactive
    public static void toggleUserStatus(String userId, boolean active) throws ApiException {
        ToggleStatusRequest body = new ToggleStatusRequest(active);
        ApiClient.put("/users/" + userId + "/status", body, Void.class);
    }
 
    // Admin: delete user
    public static void deleteUser(String userId) throws ApiException {
        ApiClient.delete("/users/" + userId);
    }
 
    // ── DTOs ──
    public static class CreateAgentRequest {
        public String firstName;
        public String lastName;
        public String email;
        public String password;
    }
 
    private static class ToggleStatusRequest {
        boolean active;
        ToggleStatusRequest(boolean active) { this.active = active; }
    }
}
