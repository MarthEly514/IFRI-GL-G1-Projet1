/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.campusdocs.client.api;
 
public class ApiException extends Exception {
 
    private final int statusCode;
 
    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
 
    public int getStatusCode() { return statusCode; }
 
    public boolean isUnauthorized() { return statusCode == 401; }
    public boolean isForbidden()    { return statusCode == 403; }
    public boolean isNotFound()     { return statusCode == 404; }
    public boolean isServerError()  { return statusCode >= 500; }
    public boolean isNetworkError() { return statusCode == 0; }
}
