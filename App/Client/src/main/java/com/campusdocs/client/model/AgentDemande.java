/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.campusdocs.client.model;
 
public class AgentDemande {
    private final String ref, nomEtudiant, prenomEtudiant, type, date;
    private String statut;
    private final int id;
 
    public AgentDemande(int id, String ref, String nomEtudiant, String prenomEtudiant,
                        String type, String date, String statut) {
        this.id = id; this.ref = ref; this.nomEtudiant = nomEtudiant; this.prenomEtudiant = prenomEtudiant;
        this.type = type; this.date = date; this.statut = statut;
    }
    
//    id": 3,
//    "ref": "DEM-2026-003",
//    "type": "Autorisation d'absence",
//    "motif": "Hee hee",
//    "annee": "",
//    "details": "Non spécifié",
//    "statut": "EN_ATTENTE",
//    "rejectReason": null,
//    "agentNote": null,
//    "date": "2026-03-23T21:07:45",
//    "dateTraitement": null,
//    "userId": 1,
//    "nomEtudiant": "Raven",
//    "prenomEtudiant": "Lanna",
//    "agentId": 0
 
    public int getId()          { return id; }
    public String getRef()          { return ref; }
    public String getStudentName()  { return nomEtudiant; }
    public String getStudentEmail() { return prenomEtudiant; }
    public String getDocType()      { return type; }
    public String getDate()         { return date; }
    public String getStatus()       { return statut; }
    public void   setStatus(String s) { this.statut = s; }
 
    public String getStatusLabel() {
        if (statut.equals("APPROUVEE")) return "Approuvée";
        if (statut.equals("REJETEE"))   return "Rejetée";
        return "En attente";
    }
 
    public String getStatusBadgeClass() {
        if (statut.equals("APPROUVEE")) return "badge-approved";
        if (statut.equals("REJETEE"))   return "badge-rejected";
        return "badge-pending";
    }
}