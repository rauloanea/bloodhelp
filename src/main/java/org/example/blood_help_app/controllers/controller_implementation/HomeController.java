package org.example.blood_help_app.controllers.controller_implementation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;

public class HomeController extends Controller {
    @FXML
    private Button profileButton;
    @FXML
    private Button donationHistoryButton;
    @FXML
    private Button donationAppointmentButton;
    @FXML
    private Button donationCentersButton;
    @FXML
    private Button donationAppointmentSecondButton;
    @FXML
    private Button donationCentersSecondButton;

    @FXML
    private void initialize() {
        this.profileButton.setOnAction(_ -> {
            ControllerFactory.getInstance().runPage(ControllerType.PROFILE_PAGE, profileButton);
        });
    }
}
