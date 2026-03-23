package com.campusdocs.server.services;

import com.campusdocs.server.models.*;
import com.campusdocs.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private UsagerRepository usagerRepository;

    @Autowired
    private AdminRepository adminRepository;

    // ── EXISTANTES — NE PAS TOUCHER ──

    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();
        long total = demandeRepository.count();
        stats.put("total", total);
        List<Demande> toutes = demandeRepository.findAll();
        Map<String, Long> parStatut = toutes.stream()
                .collect(Collectors.groupingBy(Demande::getStatut, Collectors.counting()));
        stats.put("parStatut", parStatut);
        long disponibles = acteRepository.count();
        stats.put("disponibles", disponibles);
        return stats;
    }

    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("totalDemandes", demandeRepository.count());
        kpis.put("demandesEnAttente", demandeRepository.findByStatut("EN_ATTENTE").size());
        kpis.put("docsCeMois", acteRepository.count());
        kpis.put("agentsActifs", agentRepository.findByActif(true).size());
        dashboard.put("kpis", kpis);
        List<Map<String, Object>> activity = new ArrayList<>();
        List<AgentAdministratif> agents = agentRepository.findAll();
        if (!agents.isEmpty()) {
            AgentAdministratif dernierAgent = agents.get(agents.size() - 1);
            Map<String, Object> agentActivity = new HashMap<>();
            agentActivity.put("title", "Nouvel agent ajouté");
            agentActivity.put("sub", dernierAgent.getPrenom() + " " + dernierAgent.getNom());
            activity.add(agentActivity);
        }
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

    // ── NOUVELLES ──

    // Stats pour AdminView
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> data = new HashMap<>();

        // ── Utilisateurs ──
        long totalUsers = userRepository.count();
        long totalUsagers = usagerRepository.count();
        long totalAgents = agentRepository.count();
        long totalAdmins = adminRepository.count();
        long agentsActifs = agentRepository.findByActif(true).size();
        long usagersActifs = usagerRepository.findAll().stream().filter(User::isActif).count();
        long inactifs = userRepository.findAll().stream().filter(u -> !u.isActif()).count();

        data.put("statTotalUsers", totalUsers);
        data.put("statStudents", totalUsagers);
        data.put("statStudentsActive", usagersActifs);
        data.put("statAgents", totalAgents);
        data.put("statAgentsActive", agentsActifs);
        data.put("statAdmins", totalAdmins);
        data.put("statInactiveUsers", inactifs);

        // ── Demandes ──
        long totalDemandes = demandeRepository.count();
        long enAttente = demandeRepository.findByStatut("SOUMISE").size();
        long approuvees = demandeRepository.findByStatut("DISPONIBLE").size();
        long rejetees = demandeRepository.findByStatut("REJETEE").size();
        long traitees = approuvees + rejetees;
        double tauxApprobation = traitees > 0 ? Math.round((approuvees * 100.0 / traitees) * 10) / 10.0 : 0;

        data.put("statTotalDemands", totalDemandes);
        data.put("statPendingDemands", enAttente);
        data.put("statApprovedDemands", approuvees);
        data.put("statRejectedDemands", rejetees);
        data.put("statApprovalRate", tauxApprobation + "%");

        // ── Actes ──
        long totalActes = acteRepository.count();
        LocalDateTime debutMois = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMois = debutMois.plusMonths(1);
        long actesCeMois = acteRepository.findByDateCreationBetween(debutMois, finMois).size();

        data.put("statTotalActes", totalActes);
        data.put("statActesThisMonth", actesCeMois);
        data.put("statDownloads", 0); // non implémenté

        // ── Activité récente ──
        List<Map<String, Object>> activity = new ArrayList<>();
        List<AgentAdministratif> agents = agentRepository.findAll();
        if (!agents.isEmpty()) {
            AgentAdministratif dernierAgent = agents.get(agents.size() - 1);
            Map<String, Object> a = new HashMap<>();
            a.put("title", "Nouvel agent ajouté");
            a.put("sub", dernierAgent.getPrenom() + " " + dernierAgent.getNom());
            activity.add(a);
        }
        List<Demande> demandes = demandeRepository.findAll();
        if (!demandes.isEmpty()) {
            Demande derniere = demandes.get(demandes.size() - 1);
            Map<String, Object> d = new HashMap<>();
            d.put("title", "Nouvelle demande créée");
            d.put("sub", "Demande #" + derniere.getId());
            activity.add(d);
        }
        data.put("recentActivity", activity);

        // ── Nouveaux comptes ──
        List<Map<String, Object>> newUsers = new ArrayList<>();
        List<User> tousUsers = userRepository.findAll();
        tousUsers.stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .limit(5)
                .forEach(u -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("nom", u.getNom() + " " + u.getPrenom());
                    userMap.put("role", u.getRole());
                    userMap.put("actif", u.isActif());
                    newUsers.add(userMap);
                });
        data.put("newUsers", newUsers);

        return data;
    }

    // Stats pour AgentView
    public Map<String, Object> getAgentDashboard() {
        Map<String, Object> data = new HashMap<>();

        // ── KPIs ──
        long totalDemandes = demandeRepository.count();
        long enAttente = demandeRepository.findByStatut("SOUMISE").size();
        long totalActes = acteRepository.count();
        long approuvees = demandeRepository.findByStatut("DISPONIBLE").size();
        long rejetees = demandeRepository.findByStatut("REJETEE").size();
        long traitees = approuvees + rejetees;
        double tauxApprobation = traitees > 0 ? Math.round((approuvees * 100.0 / traitees) * 10) / 10.0 : 0;

        data.put("statPendingDemands", enAttente);
        data.put("statTotalDemands", totalDemandes);
        data.put("statActes", totalActes);
        data.put("statApprovalRate", tauxApprobation + "%");

        // ── Stats aujourd'hui ──
        LocalDate today = LocalDate.now();
        long traitéesAujourdhui = demandeRepository.findByDateAndStatut(today, "DISPONIBLE").size()
                + demandeRepository.findByDateAndStatut(today, "DOCUMENT_GENERE").size();
        long rejeteesAujourdhui = demandeRepository.findByDateAndStatut(today, "REJETEE").size();

        data.put("statTodayProcessed", traitéesAujourdhui);
        data.put("statTodayRejected", rejeteesAujourdhui);

        // ── Validées ce mois ──
        LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
        LocalDate finMois = debutMois.plusMonths(1);
        long valideesCeMois = demandeRepository.findByDateBetweenAndStatut(debutMois, finMois, "DISPONIBLE").size();
        data.put("statMonthApproved", valideesCeMois);

        // ── Dernières demandes reçues ──
        List<Demande> touteDemandes = demandeRepository.findAll();
        List<Map<String, Object>> recentDemandes = touteDemandes.stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .limit(5)
                .map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", d.getId());
                    map.put("type", d.getType());
                    map.put("statut", d.getStatut());
                    map.put("date", d.getDate());
                    map.put("userId", d.getUserId());
                    return map;
                })
                .collect(Collectors.toList());
        data.put("recentDemandes", recentDemandes);

        // ── Derniers actes générés ──
        List<ActeAdministratif> tousActes = acteRepository.findAll();
        List<Map<String, Object>> recentActes = tousActes.stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .limit(5)
                .map(a -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", a.getId());
                    map.put("type", a.getType());
                    map.put("numeroDocument", a.getNumeroDocument());
                    map.put("dateCreation", a.getDateCreation());
                    return map;
                })
                .collect(Collectors.toList());
        data.put("recentActes", recentActes);

        return data;
    }

    // Stats pour StatsView
    public Map<String, Object> getStatsView() {
        Map<String, Object> data = new HashMap<>();

        // ── Utilisateurs ──
        data.put("statTotalUsers", userRepository.count());
        data.put("statStudents", usagerRepository.findAll().stream().filter(User::isActif).count());
        data.put("statAgents", agentRepository.findByActif(true).size());
        data.put("statAdmins", adminRepository.count());

        // ── Demandes ──
        long totalDemandes = demandeRepository.count();
        long enAttente = demandeRepository.findByStatut("SOUMISE").size();
        long approuvees = demandeRepository.findByStatut("DISPONIBLE").size();
        long rejetees = demandeRepository.findByStatut("REJETEE").size();
        long traitees = approuvees + rejetees;
        double tauxApprobation = traitees > 0 ? Math.round((approuvees * 100.0 / traitees) * 10) / 10.0 : 0;

        data.put("statTotalDemands", totalDemandes);
        data.put("statPendingDemands", enAttente);
        data.put("statApprovedDemands", approuvees);
        data.put("statTotalActes", acteRepository.count());
        data.put("statApprovalRate", tauxApprobation + "%");

        // ── Journal système ──
        List<Map<String, Object>> logs = new ArrayList<>();
        List<Demande> demandes = demandeRepository.findAll();
        demandes.stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .limit(10)
                .forEach(d -> {
                    Map<String, Object> log = new HashMap<>();
                    log.put("action", "Demande " + d.getStatut());
                    log.put("detail", "Demande #" + d.getId() + " — " + d.getType());
                    log.put("date", d.getDate());
                    logs.add(log);
                });
        data.put("logList", logs);

        return data;
    }

    // Stats pour UsagerView (stats personnelles d'un usager)
    public Map<String, Object> getUsagerStats(int usagerId) {
        Map<String, Object> data = new HashMap<>();

        List<Demande> touteDemandes = demandeRepository.findByUserId(usagerId);

        long enCours = touteDemandes.stream()
                .filter(d -> d.getStatut().equals("SOUMISE") || d.getStatut().equals("EN_TRAITEMENT"))
                .count();

        long disponibles = touteDemandes.stream()
                .filter(d -> d.getStatut().equals("DISPONIBLE"))
                .count();

        long totalDemandes = touteDemandes.size();

        long totalActes = touteDemandes.stream()
                .mapToLong(d -> acteRepository.findByDemandeId(d.getId()).size())
                .sum();

        data.put("statDemandesEnCours", enCours);
        data.put("statActesDisponibles", disponibles);
        data.put("statDemandesTotales", totalDemandes);
        data.put("statActesTotaux", totalActes);

        // Activité récente de l'usager
        List<Map<String, Object>> activity = touteDemandes.stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .limit(5)
                .map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", d.getType());
                    map.put("statut", d.getStatut());
                    map.put("date", d.getDate());
                    return map;
                })
                .collect(Collectors.toList());
        data.put("activityContainer", activity);

        return data;
    }

    // Stats pour RapportsView
    public Map<String, Object> getRapportStats() {
        Map<String, Object> data = new HashMap<>();

        long totalUsers = userRepository.count();
        long totalDemandes = demandeRepository.count();
        long totalActes = acteRepository.count();
        long approuvees = demandeRepository.findByStatut("DISPONIBLE").size();
        long rejetees = demandeRepository.findByStatut("REJETEE").size();
        long traitees = approuvees + rejetees;
        double tauxApprobation = traitees > 0 ? Math.round((approuvees * 100.0 / traitees) * 10) / 10.0 : 0;

        data.put("rptUsers", totalUsers);
        data.put("rptDemands", totalDemandes);
        data.put("rptActes", totalActes);
        data.put("rptRate", tauxApprobation + "%");

        // Journal activités (extrait)
        List<Map<String, Object>> logs = new ArrayList<>();
        demandeRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .limit(5)
                .forEach(d -> {
                    Map<String, Object> log = new HashMap<>();
                    log.put("action", "Demande " + d.getStatut());
                    log.put("detail", "Demande #" + d.getId() + " — " + d.getType());
                    log.put("date", d.getDate());
                    logs.add(log);
                });
        data.put("reportLogContainer", logs);

        return data;
    }
}