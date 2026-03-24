package com.campusdocs.server.services;

import com.campusdocs.server.models.ActeAdministratif;
import com.campusdocs.server.models.Demande;
import com.campusdocs.server.models.Piece;
import com.campusdocs.server.models.Usager;
import com.campusdocs.server.repositories.ActeRepository;
import com.campusdocs.server.repositories.DemandeRepository;
import com.campusdocs.server.repositories.PieceRepository;
import com.campusdocs.server.repositories.UsagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActeService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private PieceRepository pieceRepository;

    @Autowired
    private UsagerRepository usagerRepository;

    @Autowired
    private pdfService pdfService;

    @Autowired
    private ActeRepository acteRepository;

    // ── Soumettre une demande ──
    public Demande soumettre(int usagerId, String typeDocument) {
        usagerRepository.findById(usagerId)
                .orElseThrow(() -> new RuntimeException("Usager introuvable"));

        Demande demande = new Demande();
        demande.setUserId(usagerId);
        demande.setType(typeDocument);
        demande.setDate(LocalDate.now().atStartOfDay());
        demande.setStatut("SOUMISE");

        return demandeRepository.save(demande);
    }

    // ── Récupérer toutes les demandes ──
    public List<Demande> getDemandes() {
        return demandeRepository.findAll();
    }

    // ── Récupérer tous les actes ──
    public List<ActeAdministratif> getAllActes() {
        return acteRepository.findAll();
    }

    // ── Récupérer un acte par id ──
    public ActeAdministratif getActeById(int acteId) {
        return acteRepository.findById(acteId)
                .orElseThrow(() -> new RuntimeException("Acte introuvable"));
    }

    // ── Récupérer les actes par type ──
    public List<ActeAdministratif> getActesByType(String type) {
        return acteRepository.findByType(type);
    }

    // ── Récupérer les actes d'une demande ──
    public List<ActeAdministratif> getActesByDemande(int demandeId) {
        return acteRepository.findByDemandeId(demandeId);
    }

    // ── Récupérer les actes envoyés ou non ──
    public List<ActeAdministratif> getActesByEnvoye(boolean envoye) {
        return acteRepository.findByEnvoye(envoye);
    }

    // ── Récupérer les actes d'un usager ──
    public List<ActeAdministratif> getActesByUsager(int usagerId) {
        // Récupérer toutes les demandes de l'usager
        List<Demande> demandes = demandeRepository.findByUserId(usagerId);

        // Pour chaque demande récupérer les actes
        return demandes.stream()
                .flatMap(d -> acteRepository.findByDemandeId(d.getId()).stream())
                .collect(java.util.stream.Collectors.toList());
    }

    // ── Marquer un acte comme envoyé ──
    public ActeAdministratif marquerEnvoye(int acteId) {
        ActeAdministratif acte = getActeById(acteId);
        acte.setEnvoye(true);
        return acteRepository.save(acte);
    }

    // ── Récupérer les demandes par statut ──
    public List<Demande> getDemandesByStatut(String statut) {
        return demandeRepository.findByStatut(statut);
    }

    // ── Récupérer les demandes d'un usager ──
    public List<Demande> getDemandesByUsager(int usagerId) {
        return demandeRepository.findByUserId(usagerId);
    }

    // ── Récupérer une demande par id ──
    public Demande getById(int demandeId) {
        return demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
    }

    // ── Avancer une demande dans le workflow ──
    public Demande avancer(int demandeId, String action) {
        Demande demande = getById(demandeId);

        String prochainStatut = switch (action) {
            case "PRENDRE_EN_CHARGE" -> {
                if (!demande.getStatut().equals("SOUMISE"))
                    throw new RuntimeException("La demande doit être SOUMISE pour être prise en charge");
                yield "EN_TRAITEMENT";
            }
            case "GENERER_DOCUMENT" -> {
                if (!demande.getStatut().equals("EN_TRAITEMENT"))
                    throw new RuntimeException("La demande doit être EN_TRAITEMENT pour générer un document");
                yield "DOCUMENT_GENERE";
            }
            case "RENDRE_DISPONIBLE" -> {
                if (!demande.getStatut().equals("DOCUMENT_GENERE"))
                    throw new RuntimeException("Le document doit être généré avant d'être disponible");
                yield "DISPONIBLE";
            }
            case "REJETER" -> "REJETEE";
            default -> throw new RuntimeException("Action invalide : " + action);
        };

        demande.setStatut(prochainStatut);
        return demandeRepository.save(demande);
    }

    // ── Valider ou rejeter une pièce ──
    public Piece validerPiece(int pieceId, String statut) {
        Piece piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable"));

        if (!statut.equals("VALIDEE") && !statut.equals("REJETEE")) {
            throw new RuntimeException("Statut invalide : attendu VALIDEE ou REJETEE");
        }

        piece.setType(statut);
        return pieceRepository.save(piece);
    }

    // ── Générer le document PDF et enregistrer dans acteAdministratif ──
    public String genererDocument(int demandeId) throws Exception {
        // 1ère requête → récupérer la demande
        Demande demande = getById(demandeId);

        if (!demande.getStatut().equals("EN_TRAITEMENT")) {
            throw new RuntimeException("La demande doit être EN_TRAITEMENT pour générer un document");
        }

        // 2ème requête → récupérer l'usager
        Usager usager = usagerRepository.findById(demande.getUserId())
                .orElseThrow(() -> new RuntimeException("Usager introuvable"));

        int annee = LocalDate.now().getYear();
        String reference = "ETD-" + annee + "-" + demande.getType() + "-" + demandeId + "-" + System.currentTimeMillis();

        String pdfPath;
        if (demande.getType().equals("ATTESTATION_INSCRIPTION")) {
            String anneeAcademique = (annee - 1) + "-" + annee;
            pdfPath = pdfService.generateAttestationInscription(usager, reference, anneeAcademique);
        } else {
            pdfPath = pdfService.generateBulletin(usager, reference, 1,
                    "Le Directeur", "Le Directeur Adjoint", "Charge des affaires academiques");
        }

        ActeAdministratif acte = new ActeAdministratif();
        acte.setType(demande.getType());
        acte.setNumeroDocument(reference);
        acte.setDateCreation(LocalDateTime.now());
        acte.setEnvoye(false);
        acte.setDemandeId(demandeId);
        acteRepository.save(acte);
        demande.setStatut("DOCUMENT_GENERE");
        demandeRepository.save(demande);

        return pdfPath;
    }

    // ── Stats de l'agent ──
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("aTraiter", demandeRepository.findByStatut("SOUMISE").size());
        stats.put("enTraitement", demandeRepository.findByStatut("EN_TRAITEMENT").size());
        stats.put("documentGenere", demandeRepository.findByStatut("DOCUMENT_GENERE").size());
        stats.put("disponibles", demandeRepository.findByStatut("DISPONIBLE").size());
        stats.put("rejetees", demandeRepository.findByStatut("REJETEE").size());
        stats.put("totalActes", acteRepository.count());

        return stats;
    }
}