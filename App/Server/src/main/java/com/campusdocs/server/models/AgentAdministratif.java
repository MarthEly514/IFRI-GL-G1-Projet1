package com.campusdocs.server.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agentAdministratif")
public class AgentAdministratif extends User {

    private String service;

    public AgentAdministratif() {}

    public AgentAdministratif(String nom, String prenom, String email, String password, LocalDateTime dateCreation, boolean actif, String role, String service) {
        super(nom, prenom, email, password, dateCreation, actif, role);
        this.service = service;
    }

    public AgentAdministratif(int id, String nom, String prenom, String email, String password, LocalDateTime dateCreation, boolean actif, String role, String service) {
        super(id, nom, prenom, email, password, dateCreation, actif, role);
        this.service = service;
    }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
}