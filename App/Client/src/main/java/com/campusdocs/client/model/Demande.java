/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.model;

/**
 *
 * @author ely
 */

public class Demande {
 
    private final String documentType;
    private final String date;
    private final String status; // "EN_COURS", "DISPONIBLE", "REJETEE"
 
    public Demande(String documentType, String date, String status) {
        this.documentType = documentType;
        this.date         = date;
        this.status       = status;
    }
 
    public String getDocumentType() { return documentType; }
    public String getDate()         { return date; }
    public String getStatus()       { return status; }
 
    public String getStatusLabel() {
        switch (status) {
            case "DISPONIBLE": 
                return "Disponible";
            case "REJETEE":
                return "Rejetée";
            default: 
                return "En cours";
        }
    }
 
    public String getStatusStyleClass() {
        switch (status) {
            case "DISPONIBLE":
                return "status-badge-done";
            case "REJETEE":
                return "status-badge-rejected";
            default:
                return "status-badge-pending";
        }
    }
}
