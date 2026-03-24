package com.campusdocs.server.controllers;

import com.campusdocs.server.models.AgentAdministratif;
import com.campusdocs.server.models.Usager;
import com.campusdocs.server.models.User;
import com.campusdocs.server.repositories.UserRepository;
import com.campusdocs.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // GET tous les users
    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // GET par id
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable int id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST créer un user
    @PostMapping
    public User create(@RequestBody User user) {
        return userRepository.save(user);
    }

    // PUT modifier un user
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable int id, @RequestBody User updated) {
        return userRepository.findById(id).map(user -> {
            user.setNom(updated.getNom());
            user.setPrenom(updated.getPrenom());
            user.setEmail(updated.getEmail());
            user.setRole(updated.getRole());
            user.setActif(updated.isActif());
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE supprimer un user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/agent")
    public ResponseEntity<AgentAdministratif> creerAgent(@RequestBody AgentAdministratif agent) {
        AgentAdministratif nouvelAgent = userService.creerAgent(agent);
        return ResponseEntity.ok(nouvelAgent);
    }
    @GetMapping("/agents")
    public ResponseEntity<List<AgentAdministratif>> getAgents() {
        return ResponseEntity.ok(userService.getAgents());
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<User> toggleActif(@PathVariable int id) {
        try {
            return ResponseEntity.ok(userService.toggleActif(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST créer un usager
    @PostMapping("/usager")
    public ResponseEntity<Usager> creerUsager(@RequestBody Usager usager) {
        try {
            Usager nouvelUsager = userService.creerUsager(usager);
            return ResponseEntity.ok(nouvelUsager);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // GET profil d'un utilisateur
    @GetMapping("/{id}/profil")
    public ResponseEntity<Map<String, Object>> getProfil(@PathVariable int id) {
        try {
            return ResponseEntity.ok(userService.getProfil(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT modifier le profil
    @PutMapping("/{id}/profil")
    public ResponseEntity<User> updateProfil(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {
        try {
            String prenom = body.get("prenom");
            String nom = body.get("nom");
            return ResponseEntity.ok(userService.updateProfil(id, prenom, nom));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT changer le mot de passe
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {
        try {
            String ancienMotDePasse = body.get("ancienMotDePasse");
            String nouveauMotDePasse = body.get("nouveauMotDePasse");
            userService.changePassword(id, ancienMotDePasse, nouveauMotDePasse);
            return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


}
