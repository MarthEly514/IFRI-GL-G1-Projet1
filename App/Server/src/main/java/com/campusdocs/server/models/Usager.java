package com.campusdocs.server.models;

import com.campusdocs.server.models.Utilisateur;

import java.time.LocalDateTime;

public class Usager extends Utilisateur {
    private long matricule;
    private String filiere;
    private String niveau;

    public Usager(String nom, String prenom, String email, String password, String telephone, String sexe, int age, LocalDateTime dateCreation, boolean actif, String role, long matricule, String filiere, String niveau) {
        super(nom, prenom, email, password, telephone, sexe, age, dateCreation, actif, role);
        this.matricule = matricule;
        this.filiere = filiere;
        this.niveau = niveau;
    }

    public long getMatricule() {return matricule;}
    public String getFiliere() {return filiere;}
    public String getNiveau() {return niveau;}

    public void setMatricule(long matricule) {this.matricule = matricule;}
    public void setFiliere(String filiere) {this.filiere = filiere;}
    public void setNiveau(String niveau) {this.niveau = niveau;}
}