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

    // GET statistiques globales
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        return ResponseEntity.ok(statsService.getStatistiques());
    }

    // GET dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(statsService.getDashboard());
    }
}
