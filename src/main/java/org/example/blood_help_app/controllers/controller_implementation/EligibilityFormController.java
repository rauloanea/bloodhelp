package org.example.blood_help_app.controllers.controller_implementation;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.blood_help_app.controllers.factory.ControllerFactory;

import java.time.LocalDateTime;

public class EligibilityFormController extends Controller{
    @FXML
    private TextField textfieldAge;
    @FXML
    private ComboBox<String> dropdownGen;

    @FXML
    private void initialize(){
        this.textfieldAge.setText(
                String.valueOf(LocalDateTime.now().getYear() - ControllerFactory.getInstance().getUser().getBirthdayDate().getYear())
        );
        this.textfieldAge.setDisable(true);

        this.setDropdownData();
    }

    private void setDropdownData(){
        dropdownGen.getItems().addAll("Masculin", "Feminin");
    }
}
