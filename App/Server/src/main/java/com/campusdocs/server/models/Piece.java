package com.campusdocs.server.models;

import java.time.LocalDateTime;

public class Piece {
    private int id;
    private String type;
    private String nomFichier;
    private String cheminFichier;
    private LocalDateTime dateUpload ;
    private int demandeId;

    public Piece(){}
    public Piece (int id, String type, String nomFichier, String cheminFichier, LocalDateTime dateUpload, int demandeId) {
        this.id = id;
        this.type = type;
        this.nomFichier = nomFichier;
        this.cheminFichier = cheminFichier;
        this.dateUpload = dateUpload;
        this.demandeId = demandeId;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public String getNomFichier() {return nomFichier;}
    public void setNomFichier(String nomFichier) {this.nomFichier = nomFichier;}
    public String getCheminFichier() {return cheminFichier;}
    public void setCheminFichier(String cheminFichier) {this.cheminFichier = cheminFichier;}
    public LocalDateTime getDateUpload() {return dateUpload;}
    public void setDateUpload(LocalDateTime dateUpload) {this.dateUpload = dateUpload;}
    public int getDemandeId() {return demandeId;}
    public void setDemandeId(int demandeId) {this.demandeId = demandeId;}

}
