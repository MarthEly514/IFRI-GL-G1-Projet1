package com.campusdocs.server;

import com.campusdocs.server.dao.DatabaseConnection;
import com.campusdocs.server.dao.Database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        try {
            // Crée la BDD et les tables si elles n'exitent pas
            Database.initialize();
            
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("✅ Connexion réussie !");
            System.out.println("✅ Base de données : " + conn.getCatalog());
            ResultSet rs = conn.getMetaData().getTables("gestion_actes_ifri", null, "%", new String[]{"TABLE"});
            System.out.println("✅ Tables trouvées :");
            while (rs.next()) {
                System.out.println("   - " + rs.getString("TABLE_NAME"));
            }
            conn.close();
            System.out.println("✅ Tous les tests sont passés !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }
}