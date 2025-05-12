package org.example.blood_help_app.controllers.controller_implementation;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

        this.donationAppointmentButton.setOnAction(_ ->
                handleAppointment(donationAppointmentButton)
        );
        this.donationAppointmentSecondButton.setOnAction(_ ->
                handleAppointment(donationAppointmentSecondButton));
    }

    private void handleAppointment(Button triggerButton){
        if(ControllerFactory.getInstance().getUser().getEligibility() == 1)
            ControllerFactory.getInstance().runPage(ControllerType.MAKE_APPOINTMENT_FORM, triggerButton);
        if(ControllerFactory.getInstance().getUser().getEligibility() == 2)
            ControllerFactory.getInstance().showMessage(Alert.AlertType.WARNING, null, "Trebuie sa astepti!",
                    "Un doctor iti va verifica imediat formularul de eligibilitate! Pana atunci, trebuie sa astepti.");
        if(ControllerFactory.getInstance().getUser().getEligibility() == -1)
            ControllerFactory.getInstance().showMessage(Alert.AlertType.WARNING, null, "Eligibilitate neverificata!",
                    "Inainte de a programa o donare, verifica daca esti eligibil! Poti face asta din \"Gestioneaza profil\"");
        if(ControllerFactory.getInstance().getUser().getEligibility() == 0)
            ControllerFactory.getInstance().showMessage(Alert.AlertType.WARNING, null, "Momentan nu poti face o programare!",
                    "Nu esti eligibil pentru donare!");
    }
}
