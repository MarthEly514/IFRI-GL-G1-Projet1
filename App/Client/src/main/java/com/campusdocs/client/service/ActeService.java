/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.service;

import com.campusdocs.client.api.ApiClient;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.ActeAdministratif;
import com.campusdocs.client.model.Demande;
import com.campusdocs.client.model.Piece;

import java.util.List;
import java.util.Map;

public class ActeService {

    // ── Génération PDF ──

    /**
     * Generate bulletin PDF for a student
     * @param usagerId Student ID
     * @param semestre Semester number
     * @return PDF bytes
     */
    public static byte[] getBulletin(int usagerId, int semestre) throws ApiException {
        return ApiClient.getBytes("/actes/bulletin/" + usagerId + "/" + semestre);
    }

    /**
     * Generate attestation PDF for a student
     * @param usagerId Student ID
     * @return PDF bytes
     */
    public static byte[] getAttestation(int usagerId) throws ApiException {
        return ApiClient.getBytes("/actes/attestation/" + usagerId);
    }

    // ── Gestion des actes ──

    /**
     * Get all actes (Agent/Admin only)
     * @return List of all actes
     */
    public static ActeAdministratif[] getAllActes() throws ApiException {
        return ApiClient.get("/actes", ActeAdministratif[].class);
    }

    /**
     * Get actes by status
     * @param statut Status (e.g., "EN_ATTENTE", "VALIDE", "REJETE")
     * @return List of actes with given status
     */
    public static ActeAdministratif[] getActesByStatut(String statut) throws ApiException {
        return ApiClient.get("/actes/statut/" + statut, ActeAdministratif[].class);
    }

    /**
     * Get actes for a specific student
     * @param usagerId Student ID
     * @return List of actes for the student
     */
    public static ActeAdministratif[] getActesByUsager(int usagerId) throws ApiException {
        return ApiClient.get("/actes/usager/" + usagerId, ActeAdministratif[].class);
    }

    /**
     * Get actes for the current authenticated student
     * @return List of actes for the current student
     */
    public static ActeAdministratif[] getMyActes() throws ApiException {
        // Assuming the endpoint expects "me" to get current user's actes
        // If not, you might need to use /actes/usager/{id} with the current user's ID
        return ApiClient.get("/actes/me", ActeAdministratif[].class);
    }

    /**
     * Get a single acte by ID
     * @param id Acte ID
     * @return acte object
     */
    public static ActeAdministratif getActeById(int id) throws ApiException {
        return ApiClient.get("/actes/" + id, ActeAdministratif.class);
    }

    /**
     * Submit a new acte (student)
     * @param usagerId Student ID
     * @param typeDocument Type of document (e.g., "BULLETIN", "ATTESTATION")
     * @return The created acte
     */
    public static ActeAdministratif soumettreActe(int usagerId, String typeDocument) throws ApiException {
        Map<String, Object> request = Map.of(
            "usagerId", usagerId,
            "typeDocument", typeDocument
        );
        return ApiClient.post("/actes", request, ActeAdministratif.class);
    }

    /**
     * Advance an acte in the workflow (agent/admin)
     * @param id Acte ID
     * @param action Action to perform (e.g., "VALIDER", "REJETER", "TRANSMETTRE")
     * @return Updated acte
     */
    public static ActeAdministratif avancerActe(int id, String action) throws ApiException {
        Map<String, String> request = Map.of("action", action);
        return ApiClient.patch("/actes/" + id + "/avancer", request, ActeAdministratif.class);
    }

    /**
     * Validate a piece for an acte (agent/admin)
     * @param pieceId Piece ID
     * @param statut Status to set (e.g., "VALIDE", "REJETE")
     * @return Updated Piece
     */
    public static Piece validerPiece(int pieceId, String statut) throws ApiException {
        Map<String, String> request = Map.of("statut", statut);
        return ApiClient.patch("/actes/pieces/" + pieceId + "/valider", request, Piece.class);
    }

    /**
     * Generate PDF for an acte (agent/admin)
     * @param id Acte ID
     * @return Path to the generated PDF
     */
    public static String genererDocument(int id) throws ApiException {
        return ApiClient.post("/actes/" + id + "/generer", null, String.class);
    }

    /**
     * Get statistics for agent dashboard
     * @return Map with statistics (e.g., total, pending, validated, rejected)
     */
    public static Map<String, Object> getStats() throws ApiException {
        return ApiClient.get("/actes/stats", Map.class);
    }
}