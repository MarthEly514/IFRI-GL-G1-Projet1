package com.campusdocs.server.repositories;

import com.campusdocs.server.models.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DemandeRepository extends JpaRepository<Demande, Integer> {
    List<Demande> findByUserId(int userId);
    List<Demande> findByStatut(String statut);
    List<Demande> findByType(String type);
    List<Demande> findByUserIdAndStatut(int userId, String statut);
}