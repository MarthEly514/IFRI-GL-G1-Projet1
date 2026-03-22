package com.campusdocs.server.repositories;

import com.campusdocs.server.models.AgentAdministratif;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgentRepository extends JpaRepository<AgentAdministratif, Integer> {
    List<AgentAdministratif> findByService(String service);
    List<AgentAdministratif> findByActif(boolean actif);
}