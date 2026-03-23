package com.campusdocs.server.controllers;

import com.campusdocs.server.services.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private StatsService statsService;


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        return ResponseEntity.ok(statsService.getStatistiques());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(statsService.getDashboard());
    }

    // ── NOUVELLES ──

    // Stats pour AdminView
    @GetMapping("/stats/admin")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        return ResponseEntity.ok(statsService.getAdminDashboard());
    }

    // Stats pour AgentView
    @GetMapping("/stats/agent")
    public ResponseEntity<Map<String, Object>> getAgentDashboard() {
        return ResponseEntity.ok(statsService.getAgentDashboard());
    }

    // Stats pour StatsView
    @GetMapping("/stats/systeme")
    public ResponseEntity<Map<String, Object>> getStatsView() {
        return ResponseEntity.ok(statsService.getStatsView());
    }

    // Stats pour UsagerView (stats personnelles)
    @GetMapping("/stats/usager/{usagerId}")
    public ResponseEntity<Map<String, Object>> getUsagerStats(@PathVariable int usagerId) {
        return ResponseEntity.ok(statsService.getUsagerStats(usagerId));
    }

    // Stats pour RapportsView
    @GetMapping("/stats/rapport")
    public ResponseEntity<Map<String, Object>> getRapportStats() {
        return ResponseEntity.ok(statsService.getRapportStats());
    }
}
