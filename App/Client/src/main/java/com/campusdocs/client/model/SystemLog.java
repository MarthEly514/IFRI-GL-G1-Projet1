/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.model;
 
public class SystemLog {
    private final String action, user, detail, time, type;
 
    public SystemLog(String action, String user, String detail, String time, String type) {
        this.action = action; this.user = user; this.detail = detail;
        this.time = time;     this.type = type;
    }
    public String getAction() { return action; }
    public String getUser()   { return user; }
    public String getDetail() { return detail; }
    public String getTime()   { return time; }
    public String getType()   { return type; } // demand | approve | reject | create | login
}