package com.campusdocs.server.controllers;

import com.campusdocs.server.models.Demande;
import com.campusdocs.server.models.Piece;
import com.campusdocs.server.models.Usager;
import com.campusdocs.server.repositories.UserRepository;
import com.campusdocs.server.services.ActeService;
import com.campusdocs.server.services.pdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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

    // ── Génération PDF ──

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

    // ── Gestion des actes ──

    // GET tous les actes
    @GetMapping
    public ResponseEntity<List<Demande>> getActes() {
        return ResponseEntity.ok(acteService.getDemandes());
    }

    // GET actes par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Demande>> getByStatut(@PathVariable String statut) {
        return ResponseEntity.ok(acteService.getDemandesByStatut(statut));
    }

    // GET actes d'un usager
    @GetMapping("/usager/{usagerId}")
    public ResponseEntity<List<Demande>> getByUsager(@PathVariable int usagerId) {
        return ResponseEntity.ok(acteService.getDemandesByUsager(usagerId));
    }

    // GET un acte par id
    @GetMapping("/{id}")
    public ResponseEntity<Demande> getById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(acteService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST soumettre un acte
    @PostMapping
    public ResponseEntity<Demande> soumettre(@RequestBody Map<String, Object> body) {
        try {
            int usagerId = (int) body.get("usagerId");
            String typeDocument = (String) body.get("typeDocument");
            return ResponseEntity.ok(acteService.soumettre(usagerId, typeDocument));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PATCH avancer un acte dans le workflow
    @PatchMapping("/{id}/avancer")
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

    // PATCH valider une pièce d'un acte
    @PatchMapping("/pieces/{pieceId}/valider")
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

    // POST générer le PDF d'un acte
    @PostMapping("/{id}/generer")
    public ResponseEntity<String> genererDocument(@PathVariable int id) {
        try {
            String pdfPath = acteService.genererDocument(id);
            return ResponseEntity.ok(pdfPath);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // GET stats de l'agent
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(acteService.getStats());
    }
}
