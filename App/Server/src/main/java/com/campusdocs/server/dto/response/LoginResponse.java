package com.campusdocs.server.dto.response;

public class LoginResponse {
    private String token;
    private String role;
    private String nom;
    private String prenom;
    private String email;
    private int id;

    public LoginResponse() {}

    public LoginResponse(String token, String role, String nom, String prenom, String email, int id) {
        this.token = token;
        this.role = role;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.id = id;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
