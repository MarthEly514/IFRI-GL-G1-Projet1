package com.campusdocs.server.repositories;

import com.campusdocs.server.models.ActeAdministratif;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ActeRepository extends JpaRepository<ActeAdministratif, Integer> {
    Optional<ActeAdministratif> findByNumeroDocument(String numeroDocument);
    List<ActeAdministratif> findByType(String type);
    List<ActeAdministratif> findByEnvoye(boolean envoye);
    List<ActeAdministratif> findByDemandeId(int demandeId);
}