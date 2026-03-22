package com.campusdocs.server.services;

import com.campusdocs.server.models.*;
import com.campusdocs.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private ActeRepository acteRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Statistiques globales ──
    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();

        // Total demandes
        long total = demandeRepository.count();
        stats.put("total", total);

        // Par statut
        List<Demande> toutes = demandeRepository.findAll();
        Map<String, Long> parStatut = toutes.stream()
                .collect(Collectors.groupingBy(Demande::getStatut, Collectors.counting()));
        stats.put("parStatut", parStatut);

        // Documents disponibles
        long disponibles = acteRepository.count();
        stats.put("disponibles", disponibles);

        return stats;
    }

    // ── Dashboard ──
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // KPIs
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("totalDemandes", demandeRepository.count());
        kpis.put("demandesEnAttente", demandeRepository.findByStatut("EN_ATTENTE").size());
        kpis.put("docsCeMois", acteRepository.count());
        kpis.put("agentsActifs", agentRepository.findByActif(true).size());
        dashboard.put("kpis", kpis);

        // Activité récente
        List<Map<String, Object>> activity = new java.util.ArrayList<>();

        // Dernier agent créé
        List<AgentAdministratif> agents = agentRepository.findAll();
        if (!agents.isEmpty()) {
            AgentAdministratif dernierAgent = agents.get(agents.size() - 1);
            Map<String, Object> agentActivity = new HashMap<>();
            agentActivity.put("title", "Nouvel agent ajouté");
            agentActivity.put("sub", dernierAgent.getPrenom() + " " + dernierAgent.getNom());
            activity.add(agentActivity);
        }

        // Dernière demande créée
        List<Demande> demandes = demandeRepository.findAll();
        if (!demandes.isEmpty()) {
            Demande derniereDemande = demandes.get(demandes.size() - 1);
            Map<String, Object> demandeActivity = new HashMap<>();
            demandeActivity.put("title", "Nouvelle demande créée");
            demandeActivity.put("sub", "Demande #" + derniereDemande.getId());
            activity.add(demandeActivity);
        }

        dashboard.put("activity", activity);

        return dashboard;
    }
}