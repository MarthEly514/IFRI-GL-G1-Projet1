module com.campusdocs.client {
    
    
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    
    requires java.net.http;          
    requires com.google.gson;        
    requires java.sql;

    opens com.campusdocs.client to javafx.fxml;
    opens com.campusdocs.client.controller to javafx.fxml;
    opens com.campusdocs.client.model to com.google.gson;
    opens com.campusdocs.client.api to com.google.gson;
    opens com.campusdocs.client.service to com.google.gson;
    
    exports com.campusdocs.client;
    exports com.campusdocs.client.controller;
}
