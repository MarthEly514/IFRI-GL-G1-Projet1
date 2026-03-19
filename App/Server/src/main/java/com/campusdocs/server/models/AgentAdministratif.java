package com.campusdocs.server.models;
import com.campusdocs.server.models.Utilisateur;

import java.time.LocalDateTime;

public class AgentAdministratif extends Utilisateur {
    private String service;
    public AgentAdministratif() {}
    public AgentAdministratif(String nom, String prenom, String email, String password, String telephone, String sexe, int age, LocalDateTime dateCreation, boolean actif, String role, String service) {
        super(nom, prenom, email, password, telephone, sexe, age, dateCreation, actif, role);
        this.service = service;
    }
    public String getService() {return service;}
    public void setService(String service) {this.service = service;}
}
