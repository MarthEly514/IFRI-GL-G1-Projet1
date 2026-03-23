package com.campusdocs.client.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class User {

    private int        id;
    private String        nom;
    private String        prenom;
    private String        email;
    private String        role;
    private String        status;   // "ACTIF" | "INACTIF" 
    private String        memberSince;

    // ── Full constructor (used when parsing API response) ────────────
    public User(int id, String nom, String prenom, String email,
                String role, String status, String memberSince) {
        this.id          = id;
        this.nom         = nom;
        this.prenom      = prenom;
        this.email       = email;
        this.role        = role;
        this.status      = status != null ? status.toUpperCase() : "ACTIF";
        this.memberSince = memberSince;
    }

    // ── Minimal constructor (used when creating a new agent) ─────────
    public User(String nom, String prenom, String email, String role) {
        this.nom    = nom;
        this.prenom = prenom;
        this.email  = email;
        this.role   = role;
        this.status = "ACTIF";
        this.memberSince = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH));
    }

    // ── Getters ──────────────────────────────────────────────────────
    public int  getId()          { return id; }
    public String  getNom()         { return nom; }
    public String  getPrenom()      { return prenom; }
    public String  getEmail()       { return email; }
    public String  getRole()        { return role; }
    public String  getStatus()      { return status; }
    public String  getMemberSince() { return memberSince; }

    public String  getFullName()     { return nom + " " + prenom; }

    // ── Derived helpers ──────────────────────────────────────────────

    public boolean isActif() {
        return "ACTIF".equalsIgnoreCase(status);
    }

    // Display label for UI
    public String getStatusLabel() {
        return isActif() ? "Actif" : "Inactif";
    }

    // ── Setters ──────────────────────────────────────────────────────
    public void setNom(String nom)         { this.nom = nom; }
    public void setPrenom(String prenom)   { this.prenom = prenom; }
    public void setEmail(String email)     { this.email = email; }
    public void setRole(String role)       { this.role = role; }
    public void setStatus(String status)   { this.status = status.toUpperCase(); }

    // Toggle convenience — used by UserManagementViewController
    public void toggleStatus() {
        this.status = isActif() ? "INACTIF" : "ACTIF";
    }

    // ── CSS badge helpers ─────────────────────────────────────────────
    public String getRoleBadgeClass() {
        if ("ADMIN".equalsIgnoreCase(role))  return "badge-admin";
        if ("AGENT".equalsIgnoreCase(role))  return "badge-agent";
        return "badge-student";
    }

    public String getStatusBadgeClass() {
        return isActif() ? "badge-active" : "badge-inactive";
    }

    // ── toString ─────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "User{id=" + id
            + ", nom='" + nom + "'"
            + ", prenom='" + prenom + "'"
            + ", email='" + email + "'"
            + ", role='" + role + "'"
            + ", status='" + status + "'"
            + ", memberSince='" + memberSince + "'"
            + "}";
    }
}