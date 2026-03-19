module com.campusdocs.server {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.campusdocs.server;
    opens com.campusdocs.server to javafx.fxml;
}