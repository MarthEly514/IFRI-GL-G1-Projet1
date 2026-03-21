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

    private String id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private String status;

    public User(String id, String name, String email, Role role, String status) {
        this.id = id; this.nom = name; this.email = email;
        this.role = role; this.status = status;
    }
 
    public String getId()          { return id; }
    public String getName()        { return nom; }
    public String getEmail()       { return email; }
    public Role getRole()        { return role; }
    public String getStatus()      { return status; }
    public void   setStatus(String s) { this.status = s; }
 
    public String getRoleBadgeClass() {
        if (role.toString().equals("Admin"))  return "badge-admin";
        if (role.toString().equals("Agent"))  return "badge-agent";
        return "badge-student";
    }
 
    public String getStatusBadgeClass() {
        return status.equals("Actif") ? "badge-active" : "badge-inactive";
    }



    public enum Role {
        Admin,
        Usager,
        Agent
    }

    // Constructor
    public User(String nom, String prenom, String email, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    // Getters and setters
    
    public String getFullName() {
        return nom + " " + prenom;
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
        return prenom;
    }

    /**
     * @param prénom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * @param role the role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }
}
