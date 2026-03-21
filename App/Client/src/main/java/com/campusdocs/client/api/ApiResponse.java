/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.campusdocs.client.api;

// Generic wrapper that matches your server's standard response shape.
// Adjust field names to match what your Spring Boot server actually returns.
//
// Example server response:
// { "success": true, "message": "OK", "data": { ... } }
 
 
public class ApiResponse {
 
    private boolean success;
    private String  message;
    private Object  data;
 
    public boolean isSuccess() { return success; }
    public String  getMessage(){ return message; }
    public Object  getData()   { return data; }
}