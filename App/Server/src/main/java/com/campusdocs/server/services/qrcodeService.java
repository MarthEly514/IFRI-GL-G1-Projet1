package com.campusdocs.server.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class qrcodeService {

    private static final String UPLOAD_DIR = "uploads";

    public String generate(String data, String reference) throws WriterException, IOException {
        // Créer le dossier uploads s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Chemin du fichier de sortie
        String outputPath = UPLOAD_DIR + "/qr_" + reference + ".png";

        // Générer le QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

        // Sauvegarder en PNG
        Path path = FileSystems.getDefault().getPath(outputPath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return outputPath;
    }
}