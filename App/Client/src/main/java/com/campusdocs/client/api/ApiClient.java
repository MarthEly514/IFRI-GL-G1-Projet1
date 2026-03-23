/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.campusdocs.client.api;

import com.campusdocs.client.SessionManager;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    // ── Environment switch ────────────────────────────────────────────
    private static final boolean DEV_MODE = false; 

    private static final String DEV_URL  = "https://campusdocs.free.beeceptor.com";
    private static final String PROD_URL = "http://localhost:8080/api"; 

    private static final String BASE_URL = DEV_MODE ? DEV_URL : PROD_URL;

    // ── HTTP client ───────────────────────────────────────────────────
    private static final HttpClient HTTP = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(DEV_MODE ? 15 : 10))
        .build();

    private static final Gson GSON = new Gson();

    // ─────────────────────────────────────────
    // GET
    // ─────────────────────────────────────────
    public static <T> T get(String endpoint, Class<T> responseType) throws ApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .header("Authorization", bearerToken())
                .GET()
                .build();

            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response, responseType);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw networkError(e);
        }
    }

    // ─────────────────────────────────────────
    // POST
    // ─────────────────────────────────────────
    public static <T> T post(String endpoint, Object body, Class<T> responseType) throws ApiException {
        try {
            String json = body != null ? GSON.toJson(body) : "";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", bearerToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response, responseType);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw networkError(e);
        }
    }

    // ─────────────────────────────────────────
    // PUT
    // ─────────────────────────────────────────
    public static <T> T put(String endpoint, Object body, Class<T> responseType) throws ApiException {
        try {
            String json = body != null ? GSON.toJson(body) : "";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", bearerToken())
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response, responseType);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw networkError(e);
        }
    }

    // ─────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────
    public static void delete(String endpoint) throws ApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .header("Authorization", bearerToken())
                .DELETE()
                .build();

            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            handleResponse(response, Void.class);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw networkError(e);
        }
    }

    // ─────────────────────────────────────────
    // Response handler
    // ─────────────────────────────────────────
    private static <T> T handleResponse(HttpResponse<String> response, Class<T> type) throws ApiException {
        int    status = response.statusCode();
        String body   = response.body();

        if (DEV_MODE) {
            System.out.println("[ApiClient] " + status + " <- " + response.uri());
            System.out.println("[ApiClient] body: " + (body != null ? body.substring(0, Math.min(200, body.length())) : "null"));
        }

        if (status == 200 || status == 201 || status == 204) {
            if (type == Void.class || body == null || body.isBlank()) return null;
            return GSON.fromJson(body, type);
        }

        if (status == 400) {
            throw new ApiException(parseErrorMessage(body, "Requête invalide."), 400);
        }

        if (status == 401) {
            // Token expired — clear session to force re-login
            SessionManager.getInstance().clear();
            throw new ApiException("Session expirée. Veuillez vous reconnecter.", 401);
        }

        if (status == 403) {
            throw new ApiException("Accès refusé. Vous n'avez pas les droits nécessaires.", 403);
        }

        if (status == 404) {
            throw new ApiException("Ressource introuvable.", 404);
        }

        if (status == 409) {
            throw new ApiException(parseErrorMessage(body, "Conflit : cette ressource existe déjà."), 409);
        }

        if (status == 422) {
            throw new ApiException(parseErrorMessage(body, "Données invalides. Vérifiez les champs."), 422);
        }

        if (status == 429) {
            throw new ApiException("Trop de requêtes. Réessayez dans quelques instants.", 429);
        }

        if (status == 500) {
            throw new ApiException("Erreur interne du serveur. Réessayez plus tard.", 500);
        }

        if (status == 502 || status == 503 || status == 504) {
            throw new ApiException("Le serveur est temporairement indisponible.", status);
        }

        // Any other 2xx we haven't listed
        if (status >= 200 && status < 300) {
            if (type == Void.class || body == null || body.isBlank()) return null;
            return GSON.fromJson(body, type);
        }

        // Fallback for any unhandled status
        throw new ApiException(parseErrorMessage(body, "Erreur inattendue (code " + status + ")."), status);
    }

    // ─────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────

    // Try to extract a "message" field from the error response body
    private static String parseErrorMessage(String body, String fallback) {
        try {
            if (body != null && !body.isBlank()) {
                ApiResponse error = GSON.fromJson(body, ApiResponse.class);
                if (error.getMessage() != null && !error.getMessage().isBlank()) {
                    return error.getMessage();
                }
            }
        } catch (Exception ignored) {}
        return fallback;
    }

    // Classify network-level exceptions into user-friendly messages
    private static ApiException networkError(Exception e) {
        String type = e.getClass().getSimpleName();
        if (type.contains("ConnectException") || type.contains("UnknownHost")) {
            return new ApiException("Impossible de joindre le serveur. Vérifiez votre connexion.", 0);
        }
        if (type.contains("Timeout") || type.contains("SocketTimeout")) {
            return new ApiException("Le serveur met trop de temps à répondre. Réessayez.", 0);
        }
        if (type.contains("SSL") || type.contains("Certificate")) {
            return new ApiException("Erreur de sécurité SSL. Contactez l'administrateur.", 0);
        }
        return new ApiException("Erreur réseau : " + e.getMessage(), 0);
    }

    private static String bearerToken() {
        String token = SessionManager.getInstance().getToken();
        return token != null && !token.isBlank() ? "Bearer " + token : "";
    }
}