package com.campusdocs.server.dto.request;

public class SignupRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;

    public SignupRequest() {};

    public String getNom() {return nom;}
    public void setNom(String nom) {this.nom = nom;}
    public String getPrenom() {return prenom;}
    public void setPrenom(String prenom) { this.prenom = prenom;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
}
