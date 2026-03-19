module com.campusdocs.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.campusdocs.client.controller to javafx.fxml;
    opens com.campusdocs.client to javafx.fxml;
    exports com.campusdocs.client;
    exports com.campusdocs.client.controller;
}
