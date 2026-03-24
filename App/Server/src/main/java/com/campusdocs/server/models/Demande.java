package com.campusdocs.server.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande")
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String ref;
    private String type;
    private String motif;
    private String annee;
    private String details;
    private String statut;
    private String rejectReason;
    private String agentNote;
    private LocalDateTime date;
    private LocalDateTime dateTraitement;
    private int userId;
    private int agentId;
    public Demande() {}


    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getAnnee() { return annee; }
    public void setAnnee(String annee) { this.annee = annee; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public String getAgentNote() { return agentNote; }
    public void setAgentNote(String agentNote) { this.agentNote = agentNote; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public LocalDateTime getDateTraitement() { return dateTraitement; }
    public void setDateTraitement(LocalDateTime dateTraitement) { this.dateTraitement = dateTraitement; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getAgentId() { return agentId; }
    public void setAgentId(int agentId) { this.agentId = agentId; }
}