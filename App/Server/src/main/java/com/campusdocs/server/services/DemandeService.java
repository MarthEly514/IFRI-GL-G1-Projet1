package com.campusdocs.server.services;

import com.campusdocs.server.dto.request.DemandeRequest;
import com.campusdocs.server.dto.request.RejectDemandeRequest;
import com.campusdocs.server.dto.request.ValidateDemandeRequest;
import com.campusdocs.server.dto.response.ApiResponse;
import com.campusdocs.server.dto.response.DemandeResponse;
import com.campusdocs.server.models.Demande;
import com.campusdocs.server.models.User;
import com.campusdocs.server.models.Usager;
import com.campusdocs.server.repositories.DemandeRepository;
import com.campusdocs.server.repositories.UserRepository;
import com.campusdocs.server.repositories.UsagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DemandeService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsagerRepository usagerRepository;

    @Autowired
    private pdfService pdfService;

    // ── Générer une référence unique ──
    private String generateRef() {
        long count = demandeRepository.count() + 1;
        return String.format("DEM-%d-%03d", LocalDateTime.now().getYear(), count);
    }

    // ── Convertir Demande en DemandeResponse ──
    private DemandeResponse toResponse(Demande demande) {
        User user = userRepository.findById(demande.getUserId()).orElse(null);
        return new DemandeResponse(
                demande.getId(),
                demande.getRef(),
                demande.getType(),
                demande.getMotif(),
                demande.getAnnee(),
                demande.getDetails(),
                demande.getStatut(),
                demande.getRejectReason(),
                demande.getAgentNote(),
                demande.getDate(),
                demande.getDateTraitement(),
                demande.getUserId(),
                user != null ? user.getNom() : "",
                user != null ? user.getPrenom() : "",
                demande.getAgentId()
        );
    }

    // ── Soumettre une demande ──
    public DemandeResponse soumettre(DemandeRequest request, int userId) {
        Demande demande = new Demande();
        demande.setRef(generateRef());
        demande.setType(request.getType());
        demande.setMotif(request.getMotif());
        demande.setAnnee(request.getAnnee());
        demande.setDetails(request.getDetails());
        demande.setStatut("EN_ATTENTE");
        demande.setDate(LocalDateTime.now());
        demande.setUserId(userId);

        demandeRepository.save(demande);
        return toResponse(demande);
    }

    // ── Mes demandes (étudiant connecté) ──
    public List<DemandeResponse> getMesDemandes(int userId) {
        return demandeRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Toutes les demandes (agent/admin) ──
    public List<DemandeResponse> getAllDemandes() {
        return demandeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Détail d'une demande ──
    public DemandeResponse getById(int id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
        return toResponse(demande);
    }

    // ── Valider une demande ──
    public ApiResponse valider(int demandeId, int agentId, ValidateDemandeRequest request) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        if (!"EN_ATTENTE".equals(demande.getStatut())) {
            throw new RuntimeException("Cette demande a déjà été traitée");
        }

        demande.setStatut("APPROUVEE");
        demande.setAgentId(agentId);
        demande.setAgentNote(request.getAgentNote());
        demande.setDateTraitement(LocalDateTime.now());
        demandeRepository.save(demande);

        // Générer le PDF selon le type de demande
        try {
            Usager usager = usagerRepository.findById(demande.getUserId()).orElse(null);
            if (usager != null) {
                String reference = "ACT-" + demande.getId() + "-" + System.currentTimeMillis();
                if ("BULLETIN".equals(demande.getType())) {
                    int semestre = demande.getDetails() != null ?
                            Integer.parseInt(demande.getDetails()) : 1;
                    pdfService.generateBulletin(usager, reference, semestre,
                            null, null, null);
                } else if ("ATTESTATION_INSCRIPTION".equals(demande.getType())) {
                    pdfService.generateAttestationInscription(usager, reference,
                            demande.getAnnee());
                } else if ("RELEVE_NOTES".equals(demande.getType())) {
                    pdfService.generateReleveDeNotes(usager, null, reference);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur génération PDF : " + e.getMessage());
        }

        return new ApiResponse(true, "Demande validée avec succès");
    }

    // ── Rejeter une demande ──
    public ApiResponse rejeter(int demandeId, int agentId, RejectDemandeRequest request) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        if (!"EN_ATTENTE".equals(demande.getStatut())) {
            throw new RuntimeException("Cette demande a déjà été traitée");
        }

        demande.setStatut("REJETEE");
        demande.setAgentId(agentId);
        demande.setRejectReason(request.getRejectReason());
        demande.setDateTraitement(LocalDateTime.now());
        demandeRepository.save(demande);

        return new ApiResponse(true, "Demande rejetée");
    }
}