/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.service;
 
import com.campusdocs.client.api.ApiClient;
import com.campusdocs.client.api.ApiException;
import com.campusdocs.client.model.ActeAdministratif;
 
public class ActeService {
 
    // Student: get own actes
    public static ActeAdministratif[] getMyActes() throws ApiException {
        return ApiClient.get("/actes/me", ActeAdministratif[].class);
    }
 
    // Agent/Admin: get all actes
    public static ActeAdministratif[] getAllActes() throws ApiException {
        return ApiClient.get("/actes", ActeAdministratif[].class);
    }
 
    // Get one acte by id
    public static ActeAdministratif getActeById(String id) throws ApiException {
        return ApiClient.get("/actes/" + id, ActeAdministratif.class);
    }
}
 
