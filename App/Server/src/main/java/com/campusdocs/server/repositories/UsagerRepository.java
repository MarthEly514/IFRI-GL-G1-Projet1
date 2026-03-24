package com.campusdocs.server.repositories;

import com.campusdocs.server.models.Usager;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsagerRepository extends JpaRepository<Usager, Integer> {
    Optional<Usager> findByMatricule(long matricule);
    List<Usager> findByFiliere(String filiere);
    List<Usager> findByNiveau(String niveau);
    List<Usager> findByFiliereAndNiveau(String filiere, String niveau);
    boolean existsByMatricule(long matricule);
}