/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.server.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author asteras
 */
public class DatabaseConnection {
    
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "gestion_actes_ifri";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static final String URL = 
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    private static Connection instance = null;
    
    // Constructeur privé : empêche l'instanciation directe
    private DatabaseConnection() {}
    
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à la base de données réussie !");
            }catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL instrouvable :" + e.getMessage());
            }
        }
        return instance;
    }
    
    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.close();
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur fermeture connexion : " + e.getMessage());
            }
        }
    }
}
