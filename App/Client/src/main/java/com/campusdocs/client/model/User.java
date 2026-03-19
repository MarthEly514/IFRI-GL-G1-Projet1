/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.model;

/**
 *
 * @author ely
 */
public class User {

    
    private String nom;
    private String prénom;
    private String email;
    private Role role;

    public enum Role {
        Admin,
        Usager,
        Agent
    }

    // Constructor
    public User(String nom, String prénom, String email, Role role) {
        this.nom = nom;
        this.prénom = prénom;
        this.email = email;
        this.role = role;
    }

    // Getters and setters
    public String getNom() {
        return nom;
    }
    /**
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return the prenom
     */
    public String getPrenom() {
        return prénom;
    }

    /**
     * @param prénom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prénom = prenom;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }
}
