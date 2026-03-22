package com.campusdocs.server.services;

import com.campusdocs.server.models.*;
import com.campusdocs.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}