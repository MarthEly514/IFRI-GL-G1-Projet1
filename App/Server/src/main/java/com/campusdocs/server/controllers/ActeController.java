package com.campusdocs.server.controllers;

import com.campusdocs.server.models.Usager;
import com.campusdocs.server.repositories.UserRepository;
import com.campusdocs.server.services.pdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/actes")
@CrossOrigin(origins = "*")
public class ActeController {

    @Autowired
    private pdfService pdfService;

    @Autowired
    private UserRepository userRepository;

    // GET générer bulletin
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

    // GET générer attestation
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
}