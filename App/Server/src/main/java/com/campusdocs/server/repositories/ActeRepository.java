package com.campusdocs.server.repositories;

import com.campusdocs.server.models.ActeAdministratif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActeRepository extends JpaRepository<ActeAdministratif, Integer> {
    Optional<ActeAdministratif> findByNumeroDocument(String numeroDocument);
    List<ActeAdministratif> findByType(String type);
    List<ActeAdministratif> findByEnvoye(boolean envoye);
    List<ActeAdministratif> findByDemandeId(int demandeId);

    // Actes ce mois
    @Query("SELECT a FROM ActeAdministratif a WHERE a.dateCreation >= :debut AND a.dateCreation < :fin")
    List<ActeAdministratif> findByDateCreationBetween(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );
}