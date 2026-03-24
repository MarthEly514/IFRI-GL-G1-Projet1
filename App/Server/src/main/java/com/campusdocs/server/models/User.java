package com.campusdocs.server.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    protected String nom;
    protected String prenom;

    @Column(unique = true, nullable = false)
    protected String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String passwordSalt;

    protected String role;
    protected boolean actif;
    protected LocalDateTime dateCreation;


    public User() {}

    public User(String nom, String prenom, String email, String password, String role, boolean actif) {
        this.nom = nom; this.prenom = prenom; this.email = email;
        this.password = password; this.role = role; this.actif = actif;
    }

    public User(int id, String nom, String prenom, String email, String password, String role, boolean actif) {
        this.id = id; this.nom = nom; this.prenom = prenom; this.email = email;
        this.password = password; this.role = role; this.actif = actif;
    }

    public User(String nom, String prenom, String email, String password, LocalDateTime dateCreation, boolean actif, String role) {
        this.nom = nom; this.prenom = prenom; this.email = email;
        this.password = password; this.actif = actif; this.role = role;
        this.dateCreation = dateCreation;
    }

    public User(int id, String nom, String prenom, String email, String password, LocalDateTime dateCreation, boolean actif, String role) {
        this.id = id; this.nom = nom; this.prenom = prenom; this.email = email;
        this.password = password; this.actif = actif; this.role = role;
        this.dateCreation = dateCreation;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPasswordSalt() {return passwordSalt;}
    public void setPasswordSalt(String passwordSalt) { this.passwordSalt = passwordSalt;}
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + id + ", nom='" + nom + "', prenom='" + prenom + "', email='" + email + "', createdAt=" + dateCreation + '}';
    }

    public String getFullName() { return prenom + " " + nom; }
}