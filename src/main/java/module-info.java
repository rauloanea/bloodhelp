module org.example.blood_help_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.blood_help_app to javafx.fxml;
    opens org.example.blood_help_app.controllers.controller_implementation to javafx.fxml;
    exports org.example.blood_help_app;
}