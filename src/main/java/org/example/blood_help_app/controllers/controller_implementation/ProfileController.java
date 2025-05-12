package org.example.blood_help_app.controllers.controller_implementation;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;

public class ProfileController extends Controller {
    @FXML
    private Button homeButton;
    @FXML
    private Label labelEligibility;
    @FXML
    private Button affirmativeEligibilityButton;
    @FXML
    private Button negativeEligibilityButton;
    @FXML
    private Button checkDoctorButton;
    @FXML
    private Label labelName;

    @FXML
    private void initialize(){
        this.labelName.setText("Salut, " + ControllerFactory.getInstance().getUser().getName());

        this.homeButton.setOnAction(_ -> {
            ControllerFactory.getInstance().runPage(ControllerType.DONOR_HOME, homeButton);
        });

        this.negativeEligibilityButton.setOnAction(_ -> handleNegativeEligibiltyAnswer());
        this.affirmativeEligibilityButton.setOnAction(_ ->
                ControllerFactory.getInstance().runPage(ControllerType.ELIGIBILITY_FORM, affirmativeEligibilityButton));
        this.checkDoctorButton.setOnAction(_ -> {
            //TODO: implement doctor notification, then refactor
            ControllerFactory.getInstance().showMessage(
                    Alert.AlertType.CONFIRMATION,
                    "Succes",
                    "Felicitari",
                    "Programarea a fost facuta cu succes! Vei primi informatiile privind programarea facuta pe mail!"
            );

            this.services.setDonorEligibility(ControllerFactory.getInstance().getUser(), 2);

            this.labelEligibility.setText("Vei fi anuntat de catre medic daca esti eligibil sau nu");
            setVisibilityForEligibilityButtons(false);
        });

        this.checkEligibility();
    }

    private void checkEligibility() {
        Integer eligibility = ControllerFactory.getInstance().getUser().getEligibility();
        switch (eligibility){
            case -1:
                this.labelEligibility.setText("Nu ti-ai verificat eligiblitatea! Vrei sa o faci?");

                setVisibilityForEligibilityButtons(true);
                break;
            case 1:
                this.labelEligibility.setText("Felicitari! Esti eligibil pentru donare!");

                setVisibilityForEligibilityButtons(false);
                break;
            case 0:
                this.labelEligibility.setText("Din pacate nu esti eligibil pentru donare ;(((");
                setVisibilityForEligibilityButtons(false);
                break;
            case 2:
                this.labelEligibility.setText("Vei fi anuntat de catre medic daca esti eligibil sau nu");
                setVisibilityForEligibilityButtons(false);
                break;

        }
    }

    private void setVisibilityForEligibilityButtons(boolean b) {
        this.affirmativeEligibilityButton.setVisible(b);
        this.affirmativeEligibilityButton.setManaged(b);

        this.negativeEligibilityButton.setVisible(b);
        this.negativeEligibilityButton.setManaged(b);

        this.checkDoctorButton.setVisible(b);
        this.checkDoctorButton.setManaged(b);
    }

    private void handleNegativeEligibiltyAnswer() {
        this.labelEligibility.setVisible(false);
        this.labelEligibility.setManaged(false);

        setVisibilityForEligibilityButtons(false);
    }
}
