package com.campusdocs.server.services;

import com.campusdocs.server.config.StorageConfig;
import com.campusdocs.server.models.Piece;
import com.campusdocs.server.repositories.PieceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PieceService {

    @Autowired
    private PieceRepository pieceRepository;

    @Autowired
    private StorageConfig storageConfig;

    // ── Uploader les pièces justificatives ──
    public List<Piece> uploadPieces(int demandeId, MultipartFile[] fichiers) throws IOException {
        List<Piece> pieces = new ArrayList<>();

        for (MultipartFile fichier : fichiers) {
            if (fichier.isEmpty()) continue;

            // Générer un nom unique pour éviter les conflits
            String extension = getExtension(fichier.getOriginalFilename());
            String nomUnique = UUID.randomUUID().toString() + "." + extension;

            // Sauvegarder le fichier dans uploads/pieces/
            Path cheminDossier = Paths.get(storageConfig.getPiecesDir());
            Files.createDirectories(cheminDossier);
            Path cheminFichier = cheminDossier.resolve(nomUnique);
            Files.copy(fichier.getInputStream(), cheminFichier);

            // Enregistrer en base de données
            Piece piece = new Piece();
            piece.setType(fichier.getContentType());
            piece.setNomFichier(fichier.getOriginalFilename());
            piece.setCheminFichier(cheminFichier.toString());
            piece.setDateUpload(LocalDateTime.now());
            piece.setDemandeId(demandeId);

            pieceRepository.save(piece);
            pieces.add(piece);
        }

        return pieces;
    }

    // ── Récupérer les pièces d'une demande ──
    public List<Piece> getPiecesByDemande(int demandeId) {
        return pieceRepository.findByDemandeId(demandeId);
    }

    // ── Extraire l'extension du fichier ──
    private String getExtension(String nomFichier) {
        if (nomFichier == null || !nomFichier.contains(".")) return "bin";
        return nomFichier.substring(nomFichier.lastIndexOf(".") + 1);
    }
}