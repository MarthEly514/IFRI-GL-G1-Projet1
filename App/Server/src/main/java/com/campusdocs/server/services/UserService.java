package com.campusdocs.server.services;

import com.campusdocs.server.models.*;
import com.campusdocs.server.security.PasswordUtils;
import com.campusdocs.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AgentRepository userRepository;

    @Autowired
    private UsagerRepository usagerRepository;

    @Autowired
    private AdminRepository adminRepository;

    //Créer un agent
    public AgentAdministratif creerAgent(AgentAdministratif agent) {
        // 1. Date de création
        agent.setDateCreation(LocalDateTime.now());

        // 2. Génerer le sel
        String salt = PasswordUtils.generateSalt();

        // 3. Hacher le mot de passe avec le sel
        String hashedPassword = PasswordUtils.hashPassword(agent.getPassword(), salt);

        // 4. Stocker le hash et le sel (jamais le mot de passe en clair)
        agent.setPassword(hashedPassword);
        agent.setPasswordSalt(salt);

        // 5. Mettre le compte sur actif
        agent.setActif(true);

        // Sauvegarder
        return agentRepository.save(agent);
    }

    //Récupérer tous les agents
    public List<AgentAdministratif> getAgents() {
        return agentRepository.findAll();
    }

    // Activer ou désactiver un compte
    public User toggleActif(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setActif(!user.isActif());
        if (user instanceof AgentAdministratif) {
            return agentRepository.save((AgentAdministratif) user);
        } else if (user instanceof Usager) {
            return usagerRepository.save((Usager) user);
        } else if (user instanceof Administrateur) {
            return adminRepository.save((Administrateur) user);
        }
        throw new RuntimeException("Type utilisateur inconnu");
    }

    // Créer un usager
    public Usager creerUsager(Usager usager) {
        return usagerRepository.save(usager);
    }

    // Récupérer le profil complet d'un utilisateur
    public Map<String, Object> getProfil(int userId) {
        User user = userRepository.findById(userId)

                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Map<String, Object> profil = new HashMap<>();

        // Infos communes
        profil.put("id", user.getId());
        profil.put("prenom", user.getPrenom());
        profil.put("nom", user.getNom());
        profil.put("email", user.getEmail());
        profil.put("role", user.getRole());
        profil.put("actif", user.isActif());
        profil.put("memberSince", user.getDateCreation());

        // Infos spécifiques si c'est un usager
        if (user instanceof Usager) {
            Usager usager = (Usager) user;
            profil.put("matricule", usager.getMatricule());
            profil.put("filiere", usager.getFiliere());
            profil.put("niveau", usager.getNiveau());
        }

        // Infos spécifiques si c'est un agent
        if (user instanceof AgentAdministratif) {
            AgentAdministratif agent = (AgentAdministratif) user;
            profil.put("service", agent.getService());
        }

        return profil;
    }

    // Modifier les infos profil (prénom, nom)
    public User updateProfil(int userId, String prenom, String nom) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        user.setPrenom(prenom);
        user.setNom(nom);

        if (user instanceof AgentAdministratif) {
            return agentRepository.save((AgentAdministratif) user);
        } else if (user instanceof Usager) {
            return usagerRepository.save((Usager) user);
        } else if (user instanceof Administrateur) {
            return adminRepository.save((Administrateur) user);
        }

        throw new RuntimeException("Type utilisateur inconnu");
    }

    // Changer le mot de passe
    public void changePassword(int userId, String ancienMotDePasse, String nouveauMotDePasse) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérifier l'ancien mot de passe
        boolean passwordOk = PasswordUtils.checkPassword(
                ancienMotDePasse,
                user.getPassword(),
                user.getPasswordSalt()
        );

        if (!passwordOk) {
            throw new RuntimeException("Mot de passe actuel incorrect");
        }

        // Hacher le nouveau mot de passe
        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(nouveauMotDePasse, salt);

        user.setPassword(hashedPassword);
        user.setPasswordSalt(salt);

        if (user instanceof AgentAdministratif) {
            agentRepository.save((AgentAdministratif) user);
        } else if (user instanceof Usager) {
            usagerRepository.save((Usager) user);
        } else if (user instanceof Administrateur) {
            adminRepository.save((Administrateur) user);
        }
    }
}
