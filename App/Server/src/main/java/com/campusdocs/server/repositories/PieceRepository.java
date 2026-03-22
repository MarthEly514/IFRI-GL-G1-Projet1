package com.campusdocs.server.repositories;

import com.campusdocs.server.models.Piece;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PieceRepository extends JpaRepository<Piece, Integer> {
    List<Piece> findByDemandeId(int demandeId);
    List<Piece> findByType(String type);
    List<Piece> findByNomFichier(String nomFichier);
}