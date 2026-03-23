package com.campusdocs.server.dto.response;

import java.time.LocalDateTime;

public class DemandeResponse {

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
    private String nomEtudiant;
    private String prenomEtudiant;
    private int agentId;

    public DemandeResponse() {}

    public DemandeResponse(int id, String ref, String type, String motif, String annee,
                           String details, String statut, String rejectReason, String agentNote,
                           LocalDateTime date, LocalDateTime dateTraitement,
                           int userId, String nomEtudiant, String prenomEtudiant, int agentId) {
        this.id = id;
        this.ref = ref;
        this.type = type;
        this.motif = motif;
        this.annee = annee;
        this.details = details;
        this.statut = statut;
        this.rejectReason = rejectReason;
        this.agentNote = agentNote;
        this.date = date;
        this.dateTraitement = dateTraitement;
        this.userId = userId;
        this.nomEtudiant = nomEtudiant;
        this.prenomEtudiant = prenomEtudiant;
        this.agentId = agentId;
    }

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
    public String getNomEtudiant() { return nomEtudiant; }
    public void setNomEtudiant(String nomEtudiant) { this.nomEtudiant = nomEtudiant; }
    public String getPrenomEtudiant() { return prenomEtudiant; }
    public void setPrenomEtudiant(String prenomEtudiant) { this.prenomEtudiant = prenomEtudiant; }
    public int getAgentId() { return agentId; }
    public void setAgentId(int agentId) { this.agentId = agentId; }
}