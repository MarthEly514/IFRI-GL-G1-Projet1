package com.campusdocs.server.repositories;

import com.campusdocs.server.models.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Administrateur, Integer> {
    Optional<Administrateur> findByEmail(String email);
}