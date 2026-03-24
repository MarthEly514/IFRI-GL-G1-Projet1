/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.model;
 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author ely
 */
public class ActeAdministratif {
    
    private final String ref;
    private final String name;
    private final String icon;
    private final String date;   // stored as "yyyy-MM-dd" for sorting
    private final String type;
    private final String status;
 
    public ActeAdministratif(String ref, String name, String icon, String date, String type, String status) {
        this.ref    = ref;
        this.name   = name;
        this.icon   = icon;
        this.date   = date;
        this.type   = type;
        this.status = status;
    }
 
    public String getRef()    { return ref != null ? ref : ""; }
    public String getName()   { return name != null ? name : "";  }
    public String getIcon()   { return icon != null ? icon : "";  }
    public String getDate()   { return date != null ? date : "";  }
    public String getType()   { return type != null ? type : "";  }
    public String getStatus() { return status != null ? status : "";  }
 
    public String getFormattedDate() {
        try {
            LocalDate d = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return d.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return date;
        }
    }
}