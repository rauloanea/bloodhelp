package org.example.blood_help_app.controllers.controller_implementation.donor_pages_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.DoubleStringConverter;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;

import java.time.LocalDateTime;

public class DonorEligibilityFormController extends Controller {
    @FXML
    private TextField textfieldAge;
    @FXML
    private ComboBox<String> dropdownGen;
    @FXML
    private Spinner<Double> spinnerWeight;
    @FXML
    private DatePicker lastDonationDatePicker;
    @FXML
    private TextArea otherInfoTextArea;

    @FXML
    private Button cancelButton;
    @FXML
    private Button sendFormButton;

    @FXML
    private void initialize(){
        this.textfieldAge.setText(
                String.valueOf(LocalDateTime.now().getYear() - ControllerFactory.getInstance()
                        .getLoggedUser().asDonor().get()
                        .getBirthdayDate().getYear())
        );
        this.textfieldAge.setDisable(true);

        setDropdownData();

        setWeightSpinnerData();

        cancelButton.setOnAction(_ -> {
            ControllerFactory.getInstance().runPage(ControllerType.DONOR_PROFILE_PAGE, cancelButton);
        });

        sendFormButton.setOnAction(_ -> handleFormSending());
    }

    private void setWeightSpinnerData() {
        SpinnerValueFactory.DoubleSpinnerValueFactory weightFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(
                        40,
                        160,
                        70.0,
                        0.1
                );

        weightFactory.setConverter(new DoubleStringConverter() {
            @Override
            public String toString(Double value) {
                return String.format("%.1f kg", value);
            }

            @Override
            public Double fromString(String string) {
                try {
                    String numberStr = string.replaceAll(" kg", "");
                    return Double.parseDouble(numberStr);
                } catch (NumberFormatException e) {
                    return 0.1;
                }
            }
        });

        this.spinnerWeight.setValueFactory(weightFactory);
    }

    private void setDropdownData(){
        dropdownGen.getItems().addAll("Masculin", "Feminin");
    }

    private void handleFormSending(){
        var age = Integer.valueOf(textfieldAge.getText());

        var gender = dropdownGen.getValue();
        if(gender == null){
            ControllerFactory.getInstance().showMessage(Alert.AlertType.WARNING, null, "Atentie",
                    "Alege genul tau!");
        }

        var weight = spinnerWeight.getValue();
        if(weight == null){
            ControllerFactory.getInstance().showMessage(Alert.AlertType.WARNING, null, "Atentie",
                    "Introdu greutatea corporala actuala!");
        }

        LocalDateTime lastDonation = null;
        if(lastDonationDatePicker.getValue() != null){
            lastDonation = lastDonationDatePicker.getValue().atStartOfDay();
        }

        var otherInfo = otherInfoTextArea.getText();

        try {
            this.services.checkEligibility(ControllerFactory.getInstance().getLoggedUser().asDonor().get(),
                    age, gender, weight, lastDonation, otherInfo);

            ControllerFactory.getInstance().showMessage(
                    Alert.AlertType.CONFIRMATION,
                    "Succes",
                    "Felicitari",
                    "Formularul a fost trimis cu succes! Un medic iti va vizualiza raspunsurile in cel mai scurt timp"
            );

            this.services.setDonorEligibility(ControllerFactory.getInstance().getLoggedUser().asDonor().get(), 2);

            ControllerFactory.getInstance().runPage(ControllerType.DONOR_PROFILE_PAGE, sendFormButton);
        } catch (Exception e) {
            ControllerFactory.getInstance().showMessage(Alert.AlertType.ERROR, "Eroare", "Atentie!", e.getMessage());
            ControllerFactory.getInstance().runPage(ControllerType.DONOR_PROFILE_PAGE, sendFormButton);
        }
    }
}
