package com.campusdocs.client;
 
import com.campusdocs.client.model.User;
import java.time.LocalDateTime;
import java.time.Year;

public class SessionManager {
 
    private static SessionManager instance;
 
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String token;   
    private int userId; 
    private String filiere; 
    private String matricule; 
    private String niveau; 
    private String annee; 
    private String service; 
 
    private SessionManager() {
        int currentYear = Year.now().getValue();
        this.annee = currentYear + "-" + (currentYear+1);
                
    }
 
    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }
 
    
    //Getters
    public int getUserId()   { return userId; }
    public String getFullName() { return fullName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail()    { return email; }
    public String getRole()     { return role; }
    public String getToken()    { return token; }
    //usager
    public String getFiliere()   { return filiere; }
    public String getMatricule()   { return matricule; }
    public String getNiveau()   { return niveau; }
    public String getAnnee()   { return annee; }
    //agent
    public String getService()   { return service; }
    
 
    //Setters
    public void setUserId(int v)   { userId = v; }
    public void setFullName(String v) { fullName = v; }
    public void setFirstName(String v) { firstName = v; }
    public void setLastName(String v) { lastName = v; }
    public void setEmail(String v)    { email = v; }
    public void setRole(String v)     { role = v; }
    public void setToken(String v)    { token = v; }
    //usager
    public void setFiliere(String v)   { filiere = v; }
    public void setMatricule(String v)   { matricule = v; }
    public void setNiveau(String v)   { niveau = v; }
    public void setAnnee(String v)   { annee = v; }
    //agent
    public void setService(String v)   { service = v; }
    
 
    //check if the user is logged-in and his role
    public boolean isLoggedIn()  { return token != null && !token.isEmpty(); }
    public boolean isAdmin()     { return "Admin".equalsIgnoreCase(role); }
    public boolean isAgent()     { return "Agent".equalsIgnoreCase(role); }
    public boolean isStudent()   { return "Étudiant".equalsIgnoreCase(role); }
 
    public void clear() {
        fullName = null; email = null; role = null;
        token = null;    userId = 0;
    }
}