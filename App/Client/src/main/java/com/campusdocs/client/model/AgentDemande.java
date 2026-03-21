/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.campusdocs.client.model;
 
public class AgentDemande {
    private final String ref, studentName, studentEmail, docType, date;
    private String status;
 
    public AgentDemande(String ref, String studentName, String studentEmail,
                        String docType, String date, String status) {
        this.ref = ref; this.studentName = studentName; this.studentEmail = studentEmail;
        this.docType = docType; this.date = date; this.status = status;
    }
 
    public String getRef()          { return ref; }
    public String getStudentName()  { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getDocType()      { return docType; }
    public String getDate()         { return date; }
    public String getStatus()       { return status; }
    public void   setStatus(String s) { this.status = s; }
 
    public String getStatusLabel() {
        if (status.equals("APPROUVEE")) return "Approuvée";
        if (status.equals("REJETEE"))   return "Rejetée";
        return "En attente";
    }
 
    public String getStatusBadgeClass() {
        if (status.equals("APPROUVEE")) return "badge-approved";
        if (status.equals("REJETEE"))   return "badge-rejected";
        return "badge-pending";
    }
}