package com.campusdocs.client.model;

public class Demande {

    // Field names must match exactly what the server JSON sends
    private int id;
    private String type;           
    private String date;          
    private String statut;         
    private String dateTraitement;
    private String commentaire;
    private String userId;
    private String ref;            // ex: DEM-2026-001
    

    // ── No-arg constructor required by Gson ──
    public Demande() {}

    // ── Constructor for local creation (before API response) ──
    public Demande(String type, String date, String statut) {
        this.type   = type;
        this.date   = date;
        this.statut = statut;
    }

    // ── Getters ──
    public int getId()             { return id; }
    public String getDocumentType()   { return type; }   
    public String getType()           { return type; }
    public String getDate()           { return date; }
    public String getStatut()         { return statut; }
    public String getDateTraitement() { return dateTraitement; }
    public String getCommentaire()    { return commentaire; }
    public String getUserId()         { return userId; }
    public String getRef()            { return ref; }

    // ── UI helpers ──
    public String getStatusLabel() {
        if ("APPROUVEE".equalsIgnoreCase(statut) || "DISPONIBLE".equalsIgnoreCase(statut))
            return "Approuvée";
        if ("REJETEE".equalsIgnoreCase(statut))
            return "Rejetée";
        return "En cours";
    }

    public String getStatusStyleClass() {
        if ("APPROUVEE".equalsIgnoreCase(statut) || "DISPONIBLE".equalsIgnoreCase(statut))
            return "status-badge-done";
        if ("REJETEE".equalsIgnoreCase(statut))
            return "status-badge-rejected";
        return "status-badge-pending";
    }

    @Override
    public String toString() {
        return "Demande{id=" + id + ", type='" + type + "', statut='" + statut + "'}";
    }
}