/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.service;
 
import com.campusdocs.client.api.ApiClient;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.AgentDemande;
import com.campusdocs.client.model.Demande;
import com.campusdocs.client.model.Piece;
 
public class DemandeService {
    
    public static Piece[] getPieces(String demandeId) throws ApiException {
        return ApiClient.get("/demandes/" + demandeId + "/pieces", Piece[].class);
    }
 
    // Student: get own demandes
    public static Demande[] getMyDemandes() throws ApiException {
        return ApiClient.get("/demandes/me", Demande[].class);
    }
 
    // Student: submit a new demande
    public static Demande submitDemande(DemandeRequest request) throws ApiException {
        return ApiClient.post("/demandes", request, Demande.class);
    }
 
    // Agent: get all pending demandes
    public static AgentDemande[] getAllDemandes() throws ApiException {
        return ApiClient.get("/demandes", AgentDemande[].class);
    }
 
    // Agent: get one demande by id
    public static AgentDemande getDemandeById(String id) throws ApiException {
        return ApiClient.get("/demandes/" + id, AgentDemande.class);
    }
 
    // Agent: validate a demande → triggers acte generation on server
    public static void validateDemande(String id, String note) throws ApiException {
        ValidateRequest body = new ValidateRequest(note);
        ApiClient.put("/demandes/" + id + "/validate", body, Void.class);
    }
 
    // Agent: reject a demande
    public static void rejectDemande(String id, String reason) throws ApiException {
        RejectRequest body = new RejectRequest(reason);
        ApiClient.put("/demandes/" + id + "/reject", body, Void.class);
    }
 
    // ── DTOs ──
    public static class DemandeRequest {
        public String documentType;
        public String motif;
        public String annee;
        // Add fields matching your server's expected body
    }
 
    private static class ValidateRequest {
        String note;
        ValidateRequest(String note) { this.note = note; }
    }
 
    private static class RejectRequest {
        String reason;
        RejectRequest(String reason) { this.reason = reason; }
    }
}
