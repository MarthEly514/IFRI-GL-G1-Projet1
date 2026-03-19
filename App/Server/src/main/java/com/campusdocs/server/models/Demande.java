package com.campusdocs.server.models;

import java.time.LocalDateTime;

public class Demande {
    private int id;
    private String type;
    private LocalDateTime date;
    private String statut;
    private LocalDateTime dateTraitement;
    private String commentaire;
    private int userId;

    public Demande() {}
    public Demande(int id, String type, LocalDateTime date, String statut,LocalDateTime dateTraitement, String commentaire, int userId) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.statut = statut;
        this.dateTraitement = dateTraitement;
        this.commentaire = commentaire;
        this.userId = userId;

    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public LocalDateTime getDate() {return date;}
    public void setDate(LocalDateTime date) {this.date = date;}
    public String getStatut() {return statut;}
    public void setStatut(String statut) {this.statut = statut;}
    public LocalDateTime getDateTraitement() {return dateTraitement;}
    public  void setDateTraitement (LocalDateTime DateTraitement) {this.dateTraitement = DateTraitement;}
    public String getCommentaire() {return commentaire;}
    public void setCommentaire(String commentaire) {this.commentaire = commentaire;}
    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}

}
