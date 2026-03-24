package com.campusdocs.server.dto.request;

public class DemandeRequest {

    private String type;
    private String motif;
    private String annee;
    private String details;

    public DemandeRequest() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getAnnee() { return annee; }
    public void setAnnee(String annee) { this.annee = annee; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

}
