package com.campusdocs.server.repositories;

import com.campusdocs.server.models.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface DemandeRepository extends JpaRepository<Demande, Integer> {
    List<Demande> findByUserId(int userId);
    List<Demande> findByStatut(String statut);
    List<Demande> findByType(String type);
    List<Demande> findByUserIdAndStatut(int userId, String statut);

    // Demandes traitées aujourd'hui
    @Query("SELECT d FROM Demande d WHERE d.date = :today")
    List<Demande> findByDate(@Param("today") LocalDate today);

    // Demandes traitées aujourd'hui par statut
    @Query("SELECT d FROM Demande d WHERE d.date = :today AND d.statut = :statut")
    List<Demande> findByDateAndStatut(
            @Param("today") LocalDate today,
            @Param("statut") String statut
    );

    // Demandes ce mois par statut
    @Query("SELECT d FROM Demande d WHERE d.date >= :debut AND d.date < :fin AND d.statut = :statut")
    List<Demande> findByDateBetweenAndStatut(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin,
            @Param("statut") String statut
    );
}