/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.service;
/**
 *
 * @author ely
 */

import com.campusdocs.client.SessionManager;
import com.campusdocs.client.api.ApiClient;
import com.campusdocs.client.api.ApiException;

public class AuthService {

    // ── DTOs ─────────────────────────────────────────────────────────

    public static class LoginRequest {
        String email;
        String password;
        LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class SignupRequest {
        String firstName;
        String lastName;
        String email;
        String password;

        public SignupRequest(String firstName, String lastName, String email, String password) {
            this.firstName = firstName;
            this.lastName  = lastName;
            this.email     = email;
            this.password  = password;
        }
    }

    public static class LoginResponse {
        String token;
        String role;
        String fullName;
        String email;
        String userId;
    }

    public static class SignupResponse {
        String  message;
        boolean success;
    }

    // ── Login ────────────────────────────────────────────────────────

//    public static void login(String email, String password) throws ApiException {
//        LoginRequest body = new LoginRequest(email, password);
//        LoginResponse response = ApiClient.post("/auth/login", body, LoginResponse.class);
//
//        SessionManager session = SessionManager.getInstance();
//        session.setToken(response.token);
//        session.setFirstName(response.firstName);
//        session.setLastName(response.lastName);
////  removed  session.setFullName(response.fullName);
//        session.setFullName(response.firstName+" "+response.lastName);
//        session.setEmail(response.email);response.lastName
//        session.setRole(response.role);
//        session.setUserId(response.userId);
//    }
    
    
    //TESTING_ONLY
    public static void login(String email, String password) throws ApiException {
//        LoginRequest body = new LoginRequest(email, password);
//        LoginResponse response = ApiClient.post("/auth/login", body, LoginResponse.class);

        SessionManager session = SessionManager.getInstance();
        session.setToken("token");
        session.setFirstName("Logan");
        session.setLastName("Sen");
        session.setFullName(session.getFirstName()+" "+session.getLastName());
        session.setEmail(email);
        session.setRole("Admin");
        session.setUserId("0");
    }

    // ── Signup ───────────────────────────────────────────────────────

    public static void signup(String firstName, String lastName,
                              String email, String password) throws ApiException {
        SignupRequest body = new SignupRequest(firstName, lastName, email, password);
        
        ApiClient.post("/auth/signup", body, SignupResponse.class);
    }

    // ── Logout ───────────────────────────────────────────────────────

    public static void logout() {
        try {
            ApiClient.post("/auth/logout", null, Void.class);
        } catch (ApiException e) {
            // Ignore server error — clear session regardless
        } finally {
            SessionManager.getInstance().clear();
        }
    }

    // ── Refresh token ────────────────────────────────────────────────

    public static void refreshToken() throws ApiException {
        LoginResponse response = ApiClient.post("/auth/refresh", null, LoginResponse.class);
        SessionManager.getInstance().setToken(response.token);
    }
}