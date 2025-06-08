package org.example.blood_help_app.controllers.controller_implementation.doctor_pages_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;

import java.util.List;

public class DoctorBloodRequestController extends Controller {
    @FXML
    private Button donorEligibilityButton;
    @FXML
    private Button exitButton;
    @FXML
    private Label quantityLabel;
    @FXML
    private Button minusButton;
    @FXML
    private Button plusButton;
    @FXML
    private ComboBox<DonationCenter> donationCentersDropdown;
    @FXML
    private ComboBox<String> bloodTypeDropdown;
    @FXML
    private TextArea notesField;
    @FXML
    private Button sendRequestButton;
    @FXML
    private Label labelDoctorName;
    @FXML
    private Label labelInstitutionName;

    private int currentQuantity = 0;

    @FXML
    private void initialize() {
        labelDoctorName.setText("Dr. " + ControllerFactory.getInstance().getLoggedUser().asDoctor().get().getName());
        labelInstitutionName.setText(ControllerFactory.getInstance().getLoggedUser().asDoctor().get().getInstitution());

        exitButton.setOnAction(_ -> ((Stage) exitButton.getScene().getWindow()).close());
        donorEligibilityButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DOCTOR_HOME, donorEligibilityButton));

        quantityLabel.setText("0");

        // Initialize buttons
        minusButton.setVisible(false);
        minusButton.setManaged(false);
        plusButton.setVisible(true);
        plusButton.setManaged(true);

        // Set button actions
        plusButton.setOnAction(_ -> increaseQuantity());
        minusButton.setOnAction(_ -> decreaseQuantity());

        loadDonationCenters();
        loadBloodTypes();
        setLabelListener();

        sendRequestButton.setOnAction(_ -> handleSendBloodRequest());
    }

    private void loadBloodTypes() {
        bloodTypeDropdown.getItems().clear();
        for (BloodTypeEnum bloodType : BloodTypeEnum.values()) {
            bloodTypeDropdown.getItems().add(bloodType.toString());
        }
        bloodTypeDropdown.getSelectionModel().selectFirst();
    }

    private void loadDonationCenters() {
        donationCentersDropdown.getItems().clear();
        List<DonationCenter> centers = services.getDonationCenters();
        donationCentersDropdown.getItems().addAll(centers);

        // Setăm un StringConverter pentru a afișa doar numele centrelor
        donationCentersDropdown.setConverter(new StringConverter<DonationCenter>() {
            @Override
            public String toString(DonationCenter center) {
                return center != null ? center.getName() : "";
            }

            @Override
            public DonationCenter fromString(String string) {
                return null;
            }
        });

        if (!centers.isEmpty()) {
            donationCentersDropdown.getSelectionModel().selectFirst();
        }
    }

    private void handleSendBloodRequest() {
        try {
            // Validare date
            if (currentQuantity == 0) {
                ControllerFactory.getInstance().showMessage(
                        Alert.AlertType.WARNING,
                        "Cantitate invalida",
                        "Selecteaza cantitatea",
                        "Te rugam sa selectezi o cantitate mai mare decat 0"
                );
                return;
            }

            DonationCenter selectedCenter = donationCentersDropdown.getValue();
            if (selectedCenter == null) {
                ControllerFactory.getInstance().showMessage(
                        Alert.AlertType.WARNING,
                        "Centru nespecificat",
                        "Selecteaza un centru",
                        "Te rugam sa selectezi un centru de donare"
                );
                return;
            }

            String selectedBloodType = bloodTypeDropdown.getValue();
            if (selectedBloodType == null || selectedBloodType.isEmpty()) {
                ControllerFactory.getInstance().showMessage(
                        Alert.AlertType.WARNING,
                        "Grupa sanguina nespecificata",
                        "Selecteaza grupa sanguina",
                        "Te rugam sa selectezi o grupa sanguina"
                );
                return;
            }

            // Creare cerere de sange
            BloodTypeEnum bloodType = BloodTypeEnum.valueOf(selectedBloodType);
            String notes = notesField.getText();

            services.createBloodRequest(bloodType, currentQuantity,
                     ControllerFactory.getInstance().getLoggedUser().asDoctor().get(),
                     selectedCenter, notes);

            ControllerFactory.getInstance().showMessage(
                    Alert.AlertType.INFORMATION,
                    "Cerere trimisa",
                    "Succes",
                    "Cererea pentru " + currentQuantity + " unitati de sange grupa " +
                            bloodType + " a fost trimisa la centrul " + selectedCenter.getName()
            );

            // Resetare formular
            resetForm();

        } catch (Exception e) {
            ControllerFactory.getInstance().showMessage(
                    Alert.AlertType.ERROR,
                    "Eroare",
                    "A aparut o eroare",
                    e.getMessage()
            );
        }
    }

    private void setLabelListener() {
        quantityLabel.textProperty().addListener((obs, oldVal, newVal) -> {
            int quantity = Integer.parseInt(newVal);
            minusButton.setVisible(quantity > 0);
            minusButton.setManaged(quantity > 0);
            plusButton.setVisible(quantity < 9);
            plusButton.setManaged(quantity < 9);
        });
    }

    private void increaseQuantity() {
        if (currentQuantity < 9) {
            currentQuantity++;
            quantityLabel.setText(String.valueOf(currentQuantity));
        }
    }

    private void decreaseQuantity() {
        if (currentQuantity > 0) {
            currentQuantity--;
            quantityLabel.setText(String.valueOf(currentQuantity));
        }
    }

    private void resetForm() {
        currentQuantity = 0;
        quantityLabel.setText("0");
        notesField.clear();
        donationCentersDropdown.getSelectionModel().selectFirst();
        bloodTypeDropdown.getSelectionModel().selectFirst();
    }
}