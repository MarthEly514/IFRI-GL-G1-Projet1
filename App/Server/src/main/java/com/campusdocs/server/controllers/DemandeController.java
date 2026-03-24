package com.campusdocs.server.controllers;

import com.campusdocs.server.dto.request.DemandeRequest;
import com.campusdocs.server.dto.request.RejectDemandeRequest;
import com.campusdocs.server.dto.request.ValidateDemandeRequest;
import com.campusdocs.server.dto.response.ApiResponse;
import com.campusdocs.server.dto.response.DemandeResponse;
import com.campusdocs.server.security.JwtUtils;
import com.campusdocs.server.services.DemandeService;
import com.campusdocs.server.services.PieceService;
import com.campusdocs.server.models.Piece;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PieceService pieceService;

    // POST /api/demandes - Soumettre une demande
    @PostMapping
    public ResponseEntity<?> soumettre(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DemandeRequest request) {
        try {
            String token = authHeader.substring(7);
            int userId = jwtUtils.getUserIdFromToken(token);
            DemandeResponse response = demandeService.soumettre(request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // GET /api/demandes/me - Mes demandes (étudiant)
    @GetMapping("/me")
    public ResponseEntity<?> getMesDemandes(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            int userId = jwtUtils.getUserIdFromToken(token);
            List<DemandeResponse> demandes = demandeService.getMesDemandes(userId);
            return ResponseEntity.ok(demandes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // GET /api/demandes - Toutes les demandes (agent/admin)
    @GetMapping
    public ResponseEntity<?> getAllDemandes() {
        try {
            List<DemandeResponse> demandes = demandeService.getAllDemandes();
            return ResponseEntity.ok(demandes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // GET /api/demandes/{id} - Détail d'une demande
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            DemandeResponse demande = demandeService.getById(id);
            return ResponseEntity.ok(demande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // PUT /api/demandes/{id}/validate - Valider une demande
    @PutMapping("/{id}/validate")
    public ResponseEntity<?> valider(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ValidateDemandeRequest request) {
        try {
            String token = authHeader.substring(7);
            int agentId = jwtUtils.getUserIdFromToken(token);
            ApiResponse response = demandeService.valider(id, agentId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // PUT /api/demandes/{id}/reject - Rejeter une demande
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejeter(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RejectDemandeRequest request) {
        try {
            String token = authHeader.substring(7);
            int agentId = jwtUtils.getUserIdFromToken(token);
            ApiResponse response = demandeService.rejeter(id, agentId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // POST /api/demandes/{id}/pieces - Uploader les pièces justifictives
    @PostMapping("/{id}/pieces")
    public ResponseEntity<?> uploadPieces(
            @PathVariable int id,
            @RequestParam("fichiers") MultipartFile[] fichiers) {
        try {
            List<Piece> pieces = pieceService.uploadPieces(id, fichiers);
            return ResponseEntity.ok(new ApiResponse(true,
                    pieces.size() + " pièce(s) uploadée(s) avec succès", pieces));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // GET /api/demandes/{id}/pieces - Voir les pièces d'une demande
    @GetMapping("/{id}/pieces")
    public ResponseEntity<?> getPieces(@PathVariable int id) {
        try {
            List<Piece> pieces = pieceService.getPiecesByDemande(id);
            return ResponseEntity.ok(pieces);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}