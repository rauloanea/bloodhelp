package org.example.blood_help_app.controllers.controller_implementation.donor_pages_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import org.example.blood_help_app.controllers.controller_implementation.generic.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

public class DonorAppointmentPageController extends Controller {
    @FXML
    private Button sendFormButton;
    @FXML
    private Button cancelButton;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Integer> hourDropdown;
    @FXML
    private ComboBox<Integer> minutesDropdown;
    @FXML
    private ComboBox<String> countyDropdown;
    @FXML
    private ComboBox<DonationCenter> centerDropdown;

    private List<DonationCenter> centers;

    @FXML
    private void initialize(){
        this.cancelButton.setOnAction(_ -> {
            ControllerFactory.getInstance().runPage(ControllerType.DONOR_HOME, cancelButton);
        });

        this.sendFormButton.setOnAction(_ -> handleFormSending());

        hourDropdown.getItems().addAll(
                8, 9, 10, 11, 12, 13, 14, 15, 16
        );
        hourDropdown.setValue(8);

        minutesDropdown.getItems().addAll(
                0, 15, 30, 45
        );
        minutesDropdown.setValue(0);

        setCentersData();
    }

    private void handleFormSending() {
        var date = datePicker.getValue();
        var hour = hourDropdown.getValue();
        var minutes = minutesDropdown.getValue();

        var appointmentDateTime = LocalDateTime.of(date, new Time(hour, minutes, 0).toLocalTime());

        var center = centerDropdown.getValue();

        try{
            this.services.makeAppointment(
                    ControllerFactory.getInstance().getLoggedUser().asDonor().get(),
                    center,
                    appointmentDateTime
            );

            ControllerFactory.getInstance().showMessage(Alert.AlertType.CONFIRMATION, null, "Felicitari!",
                    "Programarea la " + center.getName() + " in data de " + date + " la ora " + hour + " a fost efectuata cu succes!");
        } catch (Exception e){
            ControllerFactory.getInstance().showMessage(Alert.AlertType.ERROR, null, "Eroare",
                    "S-a produs o eroare interna: " + e.getMessage());
        }
    }

    private void setCentersData() {
        centers = services.getCenters();

        var counties = centers.stream()
                .map(DonationCenter::getAddress)
                .distinct()
                .toList();
        this.countyDropdown.getItems().addAll(counties);

        centerDropdown.setConverter(new StringConverter<DonationCenter>() {
            @Override
            public String toString(DonationCenter center) {
                if (center == null) {
                    return "";
                }
                return center.getName();
            }

            @Override
            public DonationCenter fromString(String string) {
                return null;
            }
        });
        countyDropdown.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newCounty) -> {
                    if (newCounty != null) {
                        updateCenterDropdown(newCounty);
                    }
                }
        );
    }

    private void updateCenterDropdown(String selectedCounty) {
        List<DonationCenter> centersInCounty = centers.stream()
                .filter(center -> center.getAddress().equals(selectedCounty))
                .toList();

        centerDropdown.getItems().clear();
        centerDropdown.getItems().addAll(centersInCounty);

        if (!centersInCounty.isEmpty()) {
            centerDropdown.getSelectionModel().selectFirst();
        }
    }
}
