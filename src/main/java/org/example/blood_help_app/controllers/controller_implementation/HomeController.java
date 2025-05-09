package org.example.blood_help_app.controllers.controller_implementation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
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
    private Button buttonExit;

    @FXML
    private void initialize() {
        this.profileButton.setOnAction(_ -> {
            ControllerFactory.getInstance().runPage(ControllerType.DONOR_PROFILE_PAGE, profileButton);
        });

        this.buttonExit.setOnAction(_ -> {
            Stage currentStage = (Stage) this.buttonExit.getScene().getWindow();
            currentStage.close();
        });
    }
}
