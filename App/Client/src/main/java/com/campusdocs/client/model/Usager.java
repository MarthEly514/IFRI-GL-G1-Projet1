/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.model;

public class Usager extends User {
    private long matricule;
    private String filiere;
    private String niveau;

    public Usager(String nom, String prenom, String email,String role, long matricule, String filiere, String niveau) {
        super(nom, prenom, email, role);
        this.matricule = matricule;
        this.filiere = filiere;
        this.niveau = niveau;
    }
    
    public Usager(String id, String nom, String prenom, String email,String role, String status, String memberSince, long matricule, String filiere, String niveau) {
        super(id, nom, prenom, email, role, status, memberSince);
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