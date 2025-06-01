package org.example.blood_help_app.controllers.controller_implementation.admin_pages_controller;



import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;

import java.awt.*;

public class AdminHomeController extends Controller {
    @FXML
    private VBox vboxBloodRequests;
    @FXML
    private Button exitButton;

    @FXML
    private void initialize(){
        this.exitButton.setOnAction(_ -> ((Stage) exitButton.getScene().getWindow()).close());

        loadBloodRequestsCards();
    }

    private void loadBloodRequestsCards() {
        //TODO load blood requests as cards
    }
}
