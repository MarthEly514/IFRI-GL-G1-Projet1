/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.controller;

/**
 *
 * @author ely
 */
public class DashboardViewControllerRegistry {
 
    private static DashboardViewController instance;
 
    public static void register(DashboardViewController controller) {
        instance = controller;
    }
 
    public static DashboardViewController getInstance() {
        return instance;
    }
}
