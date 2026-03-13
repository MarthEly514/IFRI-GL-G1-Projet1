module com.campusdocs.client {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.campusdocs.client to javafx.fxml;
    exports com.campusdocs.client;
}
