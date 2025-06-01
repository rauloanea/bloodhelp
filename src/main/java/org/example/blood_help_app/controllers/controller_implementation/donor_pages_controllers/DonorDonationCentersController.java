package org.example.blood_help_app.controllers.controller_implementation.donor_pages_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;

import java.util.List;

public class DonorDonationCentersController extends Controller {
    @FXML
    private Button homeButton;
    @FXML
    private Button donationHistoryButton;
    @FXML
    private Button donationAppointmentButton;
    @FXML
    private Button profileButton;

    @FXML
    private VBox vboxDonationCenters;

    @FXML
    private void initialize() {
        setupNavigationButtons();
        loadDonationCenters();
    }

    private void setupNavigationButtons() {
        homeButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONOR_HOME, homeButton));
        donationHistoryButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONATION_HISTORY, donationHistoryButton));
        donationAppointmentButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.MAKE_APPOINTMENT_FORM, donationAppointmentButton));
        profileButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONOR_PROFILE_PAGE, profileButton));
    }

    private void loadDonationCenters() {
        List<DonationCenter> centers = services.getDonationCenters();

        if (centers.isEmpty()) {
            Label noCentersLabel = new Label("Nu există centre de donare disponibile momentan.");
            noCentersLabel.getStyleClass().add("label-info");
            vboxDonationCenters.getChildren().add(noCentersLabel);
            return;
        }

        vboxDonationCenters.getChildren().clear();

        centers.forEach(center -> {
            VBox centerCard = createCenterCard(center);
            vboxDonationCenters.getChildren().add(centerCard);
        });
    }

    private VBox createCenterCard(DonationCenter center) {
        // 1. Container principal
        VBox card = new VBox(10);
        card.getStyleClass().add("center-card");
        card.setMaxWidth(Double.MAX_VALUE);

        // 2. Informații centru
        Label nameLabel = new Label(center.getName());
        nameLabel.getStyleClass().add("label-title-center");
        nameLabel.setWrapText(true);

        Label addressLabel = new Label("Adresă: " + center.getAddress());
        addressLabel.getStyleClass().add("label-info-center");
        addressLabel.setWrapText(true);

        Label contactLabel = new Label("Contact: " +
                (center.getContactInfo() != null ? center.getContactInfo() : "N/A"));
        contactLabel.getStyleClass().add("label-info-center");

        // 3. Calculăm numărul total de unități de sânge
        int totalUnits = center.getInventory() != null ? center.getInventory().size() : 0;
        Label inventoryLabel = new Label("Stoc disponibil: " + totalUnits + " unități");
        inventoryLabel.getStyleClass().add("label-highlight-center");

        // 4. Buton programare
        Button scheduleButton = new Button("Programează-te aici");
        scheduleButton.getStyleClass().add("button-primary");
        scheduleButton.setOnAction(e -> handleScheduleAtCenter(center));

        // 5. Adaugă elementele în card
        card.getChildren().addAll(
                nameLabel,
                addressLabel,
                contactLabel,
                inventoryLabel,
                scheduleButton
        );

        return card;
    }

    private void handleScheduleAtCenter(DonationCenter center) {
        // Verifică eligibilitatea înainte de programare
        int eligibility = ControllerFactory.getInstance()
                .getLoggedUser()
                .asDonor()
                .get()
                .getEligibility();

        if (eligibility != 1) {
            ControllerFactory.getInstance().showMessage(
                    Alert.AlertType.WARNING,
                    "Eligibilitate necesară",
                    "Trebuie să fii eligibil pentru donare",
                    "Verifică-ți eligibilitatea înainte de a face o programare."
            );
            return;
        }

        ControllerFactory.getInstance().setSelectedDonationCenter(center);
        ControllerFactory.getInstance().runPage(
                ControllerType.MAKE_APPOINTMENT_FORM,
                homeButton
        );
    }
}