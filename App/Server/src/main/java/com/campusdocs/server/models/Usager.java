package com.campusdocs.server.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usager")
public class Usager extends User {

    private long matricule;
    private String filiere;
    private String niveau;

    public Usager(String nom, String prenom, String email, String password, LocalDateTime dateCreation, boolean actif, String role, long matricule, String filiere, String niveau) {
        super(nom, prenom, email, password, dateCreation, actif, role);
        this.matricule = matricule; this.filiere = filiere; this.niveau = niveau;
    }

    public Usager(int id, String nom, String prenom, String email, String password, LocalDateTime dateCreation, boolean actif, String role, long matricule, String filiere, String niveau) {
        super(id, nom, prenom, email, password, dateCreation, actif, role);
        this.matricule = matricule; this.filiere = filiere; this.niveau = niveau;
    }

    public long getMatricule() { return matricule; }
    public void setMatricule(long matricule) { this.matricule = matricule; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
}