package com.campusdocs.server.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "administrateur")
public class Administrateur extends User {

    public Administrateur() {}

    public Administrateur(String nom, String prenom, String email, String password, LocalDateTime dateCreation, boolean actif, String role) {
        super(nom, prenom, email, password, dateCreation, actif, role);
    }
}