package com.campusdocs.server.models;

import java.time.LocalDateTime;

public class Utilisateur {
    protected int id;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String password;
    protected String telephone;
    protected String sexe;
    protected int age;
    protected String role;
    protected boolean actif;
    protected LocalDateTime dateCreation; //

    // Constructeur vide
    public Utilisateur() {}

    // Constructeur avec paramètres
    public Utilisateur(String nom, String prenom, String email, String password, String telephone, String sexe, int age, String role, boolean actif) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.telephone = telephone;
        this.sexe = sexe;
        this.age = age;
        this.role = role;
        this.actif = actif;

    }
    public Utilisateur(String nom, String prenom, String email, String password, String telephone, String sexe, int age, LocalDateTime dateCreation, boolean actif, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.telephone = telephone;
        this.sexe = sexe;
        this.age = age;
        this.actif = actif;
        this.role = role;
        this.dateCreation = dateCreation;
    }

    // ========== GETTERS ==========

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getSexe() {
        return sexe;
    }

    public int getAge() {
        return age;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    public boolean isActif() {
        return actif;
    }
    public String getRole() {
        return role;
    }

    // ========== SETTERS ==========

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    public void setActif(boolean actif) {this.actif = actif;}
    public void setRole(String role) {this.role = role;}

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", sexe='" + sexe + '\'' +
                ", age=" + age +
                ", createdAt=" + dateCreation+
                '}';
    }

    public String getFullName() {
        return prenom + " " + nom;
    }
}

