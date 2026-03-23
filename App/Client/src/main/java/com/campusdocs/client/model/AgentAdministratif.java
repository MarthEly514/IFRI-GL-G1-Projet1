/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.model;

/**
 *
 * @author ely
 */
public class AgentAdministratif extends User{
    private String service;
    
    public AgentAdministratif(String nom, String prenom, String email, String role, String service) {
        super(nom, prenom, email, role);
        this.service = service;
    }
    
    // with id
    public AgentAdministratif(String id, String nom, String prenom, String email,String role, String status, String memberSince, String service) {
        super(id, nom, prenom, email, role, status, memberSince);
        this.service = service;
    }
    public String getService() {return service;}
    public void setService(String service) {this.service = service;}
}