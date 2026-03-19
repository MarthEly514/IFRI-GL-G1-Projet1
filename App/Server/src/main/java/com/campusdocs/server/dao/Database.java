/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author asteras
 */
public class Database {

    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "gestion_actes_ifri";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static final String URL_WITHOUT_DB =
            "jdbc:mysql://" + HOST + ":" + PORT +
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    public static void initialize() {
        createDatabaseIfNotExists();
        createTables();
    }

    // Etape 1 : Créer la base de données si elle n'existe pas
    private static void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection(URL_WITHOUT_DB, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS gestion_actes_ifri");
            System.out.println("Base de données '" + DATABASE + "' prête.");

        } catch (SQLException e) {
            System.err.println("Erreur création base : " + e.getMessage());
        }
    }

    // Etape 2 : Créer toutes les tables
    private static void createTables() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Désactiver les clés étrangères le temps de créer les tables
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");

            // ── Table utilisateur (classe mère) ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS utilisateur ("
                + "id BIGINT NOT NULL AUTO_INCREMENT,"
                + "nom VARCHAR(100) NOT NULL,"
                + "prenom VARCHAR(100) NOT NULL,"
                + "email VARCHAR(150) NOT NULL,"
                + "mot_de_passe VARCHAR(255) NOT NULL,"
                + "actif BOOLEAN NOT NULL DEFAULT TRUE,"
                + "type_utilisateur VARCHAR(30) NOT NULL,"
                + "PRIMARY KEY (id),"
                + "UNIQUE KEY uk_email (email)"
                + ") ENGINE=InnoDB"
            );

            // ── Table usager (hérite de utilisateur) ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS usager ("
                + "id BIGINT NOT NULL,"
                + "matricule VARCHAR(50) NOT NULL,"
                + "filiere VARCHAR(100) NOT NULL,"
                + "niveau VARCHAR(20) NOT NULL,"
                + "PRIMARY KEY (id),"
                + "UNIQUE KEY uk_matricule (matricule),"
                + "FOREIGN KEY (id) REFERENCES utilisateur(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table agent_administratif (hérite de utilisateur) ──
            // ERREUR CORRIGÉE : virgule manquante après PRIMARY KEY (id)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS agent_administratif ("
                + "id BIGINT NOT NULL,"
                + "service VARCHAR(100) NOT NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id) REFERENCES utilisateur(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table administrateur (hérite de utilisateur) ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS administrateur ("
                + "id BIGINT NOT NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id) REFERENCES utilisateur(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table demande ──
            // ERREURS CORRIGÉES : "id_agent b BIGINT" → "id_agent BIGINT"
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS demande ("
                + "id BIGINT NOT NULL AUTO_INCREMENT,"
                + "type VARCHAR(100) NOT NULL,"
                + "date DATE NOT NULL,"
                + "statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',"
                + "date_traitement DATE NULL,"
                + "commentaire TEXT NULL,"
                + "id_usager BIGINT NOT NULL,"
                + "id_agent BIGINT NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id_usager) REFERENCES usager(id) ON DELETE RESTRICT ON UPDATE CASCADE,"
                + "FOREIGN KEY (id_agent) REFERENCES agent_administratif(id) ON DELETE SET NULL ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table acte_administratif ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS acte_administratif ("
                + "id BIGINT NOT NULL AUTO_INCREMENT,"
                + "type VARCHAR(100) NOT NULL,"
                + "numero_document VARCHAR(100) NOT NULL,"
                + "date_creation DATE NOT NULL,"
                + "envoye BOOLEAN NOT NULL DEFAULT FALSE,"
                + "id_demande BIGINT NOT NULL,"
                + "id_agent BIGINT NOT NULL,"
                + "PRIMARY KEY (id),"
                + "UNIQUE KEY uk_numero_document (numero_document),"
                + "FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE RESTRICT ON UPDATE CASCADE,"
                + "FOREIGN KEY (id_agent) REFERENCES agent_administratif(id) ON DELETE RESTRICT ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table attestation_de_scolarite ──
            // ERREUR CORRIGÉE : accent sur "scolarité" → "scolarite" (éviter les accents dans les noms de tables)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS attestation_de_scolarite ("
                + "id BIGINT NOT NULL,"
                + "annee_scolaire VARCHAR(20) NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id) REFERENCES acte_administratif(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table attestation_de_reussite ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS attestation_de_reussite ("
                + "id BIGINT NOT NULL,"
                + "mention VARCHAR(50) NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id) REFERENCES acte_administratif(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table releve_de_notes ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS releve_de_notes ("
                + "id BIGINT NOT NULL,"
                + "semestre VARCHAR(20) NULL,"
                + "moyenne DECIMAL(4,2) NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id) REFERENCES acte_administratif(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table autorisation_absence ──
            // ERREUR CORRIGÉE : "autorisationn_absence" → "autorisation_absence"
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS autorisation_absence ("
                + "id BIGINT NOT NULL,"
                + "date_debut DATE NULL,"
                + "date_fin DATE NULL,"
                + "motif TEXT NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id) REFERENCES acte_administratif(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table certificat_de_scolarite ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS certificat_de_scolarite ("
                + "id BIGINT NOT NULL,"
                + "annee_scolaire VARCHAR(20) NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id) REFERENCES acte_administratif(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table diplome ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS diplome ("
                + "id BIGINT NOT NULL,"
                + "intitule VARCHAR(200) NULL,"
                + "annee_obtention YEAR NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id) REFERENCES acte_administratif(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // ── Table piece_justificative ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS piece_justificative ("
                + "id BIGINT NOT NULL AUTO_INCREMENT,"
                + "type VARCHAR(100) NOT NULL,"
                + "nom_fichier VARCHAR(255) NOT NULL,"
                + "chemin_fichier VARCHAR(500) NOT NULL,"
                + "date_upload DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "id_demande BIGINT NOT NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB"
            );

            // Réactiver les clés étrangères
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");

            System.out.println("Toutes les tables ont été créées avec succès !");

        } catch (SQLException e) {
            System.err.println("Erreur création tables : " + e.getMessage());
        }
    }
}