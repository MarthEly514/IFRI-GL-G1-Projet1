package com.campusdocs.server.controllers;

import com.campusdocs.server.models.ActeAdministratif;
import com.campusdocs.server.models.Demande;
import com.campusdocs.server.models.Piece;
import com.campusdocs.server.models.Usager;
import com.campusdocs.server.repositories.ActeRepository;
import com.campusdocs.server.repositories.DemandeRepository;
import com.campusdocs.server.repositories.UserRepository;
import com.campusdocs.server.security.JwtUtils;
import com.campusdocs.server.services.ActeService;
import com.campusdocs.server.services.pdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/actes")
@CrossOrigin(origins = "*")
public class ActeController {

    @Autowired
    private pdfService pdfService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActeService acteService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ActeRepository acteRepository;

    @Autowired
    private DemandeRepository demandeRepository;

    // ── Génération PDF directe ──

    @GetMapping("/bulletin/{usagerId}/{semestre}")
    public ResponseEntity<byte[]> getBulletin(
            @PathVariable int usagerId,
            @PathVariable int semestre) {
        try {
            Usager usager = (Usager) userRepository.findById(usagerId)
                    .orElseThrow(() -> new RuntimeException("Usager introuvable"));

            String reference = "REF-" + usagerId + "-S" + semestre + "-" + System.currentTimeMillis();

            String path = pdfService.generateBulletin(usager, reference, semestre,
                    "Nom Directeur", "Nom Directeur Adjoint", "Charge des affaires academiques");

            byte[] pdfBytes = Files.readAllBytes(Paths.get(path));

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=" + reference + ".pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/attestation/{usagerId}")
    public ResponseEntity<byte[]> getAttestation(@PathVariable int usagerId) {
        try {
            Usager usager = (Usager) userRepository.findById(usagerId)
                    .orElseThrow(() -> new RuntimeException("Usager introuvable"));

            int annee = java.time.LocalDate.now().getYear();
            String anneeAcademique = (annee - 1) + "-" + annee;
            String reference = "ATTEST-" + usagerId + "-" + System.currentTimeMillis();

            String path = pdfService.generateAttestationInscription(usager, reference, anneeAcademique);

            byte[] pdfBytes = Files.readAllBytes(Paths.get(path));

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=" + reference + ".pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── Gestion des demandes ──

    // GET toutes les demandes
    @GetMapping("/demandes")
    public ResponseEntity<List<Demande>> getDemandes() {
        return ResponseEntity.ok(acteService.getDemandes());
    }

    // GET demandes par statut
    @GetMapping("/demandes/statut/{statut}")
    public ResponseEntity<List<Demande>> getDemandesByStatut(@PathVariable String statut) {
        return ResponseEntity.ok(acteService.getDemandesByStatut(statut));
    }

    // GET demandes d'un usager
    @GetMapping("/demandes/usager/{usagerId}")
    public ResponseEntity<List<Demande>> getDemandesByUsager(@PathVariable int usagerId) {
        return ResponseEntity.ok(acteService.getDemandesByUsager(usagerId));
    }

    // GET une demande par id
    @GetMapping("/demandes/{id}")
    public ResponseEntity<Demande> getDemandeById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(acteService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST soumettre une demande
    @PostMapping("/demandes/soumettre")
    public ResponseEntity<Demande> soumettre(@RequestBody Map<String, Object> body) {
        try {
            int usagerId = (int) body.get("usagerId");
            String typeDocument = (String) body.get("typeDocument");
            return ResponseEntity.ok(acteService.soumettre(usagerId, typeDocument));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PATCH avancer une demande dans le workflow
    @PatchMapping("/demandes/{id}/avancer")
    public ResponseEntity<Demande> avancer(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {
        try {
            String action = body.get("action");
            return ResponseEntity.ok(acteService.avancer(id, action));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PATCH valider une pièce
    @PatchMapping("/demandes/pieces/{pieceId}/valider")
    public ResponseEntity<Piece> validerPiece(
            @PathVariable int pieceId,
            @RequestBody Map<String, String> body) {
        try {
            String statut = body.get("statut");
            return ResponseEntity.ok(acteService.validerPiece(pieceId, statut));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST générer le PDF d'une demande → enregistré dans acteAdministratif
    @PostMapping("/demandes/{id}/generer")
    public ResponseEntity<String> genererDocument(@PathVariable int id) {
        try {
            String pdfPath = acteService.genererDocument(id);
            return ResponseEntity.ok(pdfPath);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // ── Gestion des actes ──

    // GET tous les actes
    @GetMapping
    public ResponseEntity<List<ActeAdministratif>> getActes() {
        return ResponseEntity.ok(acteService.getAllActes());
    }

    // GET un acte par id
    @GetMapping("/acte/{acteId}")
    public ResponseEntity<ActeAdministratif> getActeById(@PathVariable int acteId) {
        try {
            return ResponseEntity.ok(acteService.getActeById(acteId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET actes par type
    @GetMapping("/acte/type/{type}")
    public ResponseEntity<List<ActeAdministratif>> getActesByType(@PathVariable String type) {
        return ResponseEntity.ok(acteService.getActesByType(type));
    }

    // GET actes d'une demande
    @GetMapping("/acte/demande/{demandeId}")
    public ResponseEntity<List<ActeAdministratif>> getActesByDemande(@PathVariable int demandeId) {
        return ResponseEntity.ok(acteService.getActesByDemande(demandeId));
    }

    // GET actes d'un usager
    @GetMapping("/acte/usager/{usagerId}")
    public ResponseEntity<List<ActeAdministratif>> getActesByUsager(@PathVariable int usagerId) {
        return ResponseEntity.ok(acteService.getActesByUsager(usagerId));
    }

    // PATCH marquer un acte comme envoyé
    @PatchMapping("/acte/{acteId}/envoye")
    public ResponseEntity<ActeAdministratif> marquerEnvoye(@PathVariable int acteId) {
        try {
            return ResponseEntity.ok(acteService.marquerEnvoye(acteId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET stats de l'agent
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(acteService.getStats());
    }

    // GET actes de l'étudiant connecté
    @GetMapping("/me")
    public ResponseEntity<?> getMesActes(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            int userId = jwtUtils.getUserIdFromToken(token);
            List<ActeAdministratif> actes = acteRepository.findAll()
                    .stream()
                    .filter(a -> demandeRepository.findById(a.getDemandeId())
                            .map(d -> d.getUserId() == userId)
                            .orElse(false))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(actes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

