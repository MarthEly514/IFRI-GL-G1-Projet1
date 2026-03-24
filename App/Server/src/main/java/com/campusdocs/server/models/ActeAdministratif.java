package com.campusdocs.server.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "acteAdministratif")
@Inheritance(strategy = InheritanceType.JOINED)
public class ActeAdministratif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String type;
    public String numeroDocument;
    public LocalDateTime dateCreation;
    public boolean envoye;
    public int demandeId;

    public ActeAdministratif() {}

    public ActeAdministratif(int id, String type, String numeroDocument, LocalDateTime dateCreation, boolean envoye, int demandeId) {
        this.id = id; this.type = type; this.numeroDocument = numeroDocument;
        this.dateCreation = dateCreation; this.envoye = envoye; this.demandeId = demandeId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNumeroDocument() { return numeroDocument; }
    public void setNumeroDocument(String numeroDocument) { this.numeroDocument = numeroDocument; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public boolean isEnvoye() { return envoye; }
    public void setEnvoye(boolean envoye) { this.envoye = envoye; }
    public int getDemandeId() { return demandeId; }
    public void setDemandeId(int demandeId) { this.demandeId = demandeId; }
}