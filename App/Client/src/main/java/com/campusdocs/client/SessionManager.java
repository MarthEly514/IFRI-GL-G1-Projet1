package com.campusdocs.client;
 
import com.campusdocs.client.model.User;

public class SessionManager {
 
    private static SessionManager instance;
 
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String token;   
    private String userId; 
 
    private SessionManager() {}
 
    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }
 
    
    //Getters
    public String getFullName() { return fullName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail()    { return email; }
    public String getRole()     { return role; }
    public String getToken()    { return token; }
    public String getUserId()   { return userId; }
    
 
    //Setters
    public void setFullName(String v) { fullName = v; }
    public void setFirstName(String v) { firstName = v; }
    public void setLastName(String v) { lastName = v; }
    public void setEmail(String v)    { email = v; }
    public void setRole(String v)     { role = v; }
    public void setToken(String v)    { token = v; }
    public void setUserId(String v)   { userId = v; }
    
 
    //check if the user is logged-in and his role
    public boolean isLoggedIn()  { return token != null && !token.isEmpty(); }
    public boolean isAdmin()     { return "Admin".equalsIgnoreCase(role); }
    public boolean isAgent()     { return "Agent".equalsIgnoreCase(role); }
    public boolean isStudent()   { return "Étudiant".equalsIgnoreCase(role); }
 
    public void clear() {
        fullName = null; email = null; role = null;
        token = null;    userId = null;
    }
}