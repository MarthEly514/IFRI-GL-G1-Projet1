/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client;

import com.campusdocs.client.model.User;

/**
 *
 * @author ely
 */
public class SessionManager {
    

    private static SessionManager instance;

    private String fullName;
    private String email;
    private User.Role role;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }
    public User.Role getRole()     { return role; }

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email)       { this.email = email; }
    public void setRole(User.Role role)         { this.role = role; }

    // Call this on logout
    public void clear() {
        fullName = null;
        email    = null;
        role     = null;
    }
}

