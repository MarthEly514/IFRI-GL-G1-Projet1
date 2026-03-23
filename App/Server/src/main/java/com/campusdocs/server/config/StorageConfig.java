package com.campusdocs.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;

@Configuration
public class StorageConfig {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.pdfs}")
    private String pdfsDir;

    @Value("${app.upload.qrcodes}")
    private String qrcodesDir;

    @Value("${app.upload.pieces}")
    private String piecesDir;

    // Créé automatiquement au démarrage de l'application
    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
        Files.createDirectories(Paths.get(pdfsDir));
        Files.createDirectories(Paths.get(qrcodesDir));
        Files.createDirectories(Paths.get(piecesDir));
        System.out.println("Dossiers uploads créés : " + uploadDir);
    }

    public String getPdfsDir() { return pdfsDir; }
    public String getQrcodesDir() { return qrcodesDir; }
    public String getUploadDir() { return uploadDir; }
    public String getPiecesDir() { return piecesDir; }
}