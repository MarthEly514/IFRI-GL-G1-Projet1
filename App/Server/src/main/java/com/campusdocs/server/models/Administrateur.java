package com.campusdocs.server.models;
import com.campusdocs.server.models.Utilisateur;

import java.time.LocalDateTime;

public class Administrateur  extends Utilisateur{
    public Administrateur(String nom, String prenom, String email, String password, String telephone, String sexe, int age, LocalDateTime dateCreation, boolean actif, String role, long matricule, String filiere, String niveau) {
        super(nom, prenom, email, password, telephone, sexe, age, dateCreation, actif, role);
    }
}
