package org.example.blood_help_app.controllers.controller_implementation.donor_pages_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.domain.donationsdata.Appointment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.enums.AppointmentStatus;
import org.example.blood_help_app.domain.enums.DonationStatusEnum;
import org.example.blood_help_app.domain.users.Donor;

public class DonorHomeController extends Controller {
    // Butoane
    @FXML private Button profileButton;
    @FXML private Button donationHistoryButton;
    @FXML private Button donationAppointmentButton;
    @FXML private Button donationCentersButton;
    @FXML private Button donationAppointmentSecondButton;
    @FXML private Button donationCentersSecondButton;
    @FXML private Button buttonExit;

    // Componente pentru programări
    @FXML private Label appointmentsLabel;
    @FXML private HBox hboxAppointments;

    // Componente pentru donații
    @FXML private Label labelDonations;
    @FXML private HBox hboxPastDonations;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML
    private void initialize() {
        setupButtonActions();
        loadAppointments();
        loadDonations();
    }

    private void setupButtonActions() {
        profileButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONOR_PROFILE_PAGE, profileButton));
        buttonExit.setOnAction(_ -> ((Stage) buttonExit.getScene().getWindow()).close());

        donationAppointmentButton.setOnAction(_ -> handleAppointmentAction());
        donationAppointmentSecondButton.setOnAction(_ -> handleAppointmentAction());
        donationHistoryButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONATION_HISTORY, donationHistoryButton));
        donationCentersButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONATION_CENTERS, donationCentersButton));
        donationCentersSecondButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONATION_CENTERS, donationCentersSecondButton));
    }

    private void handleAppointmentAction() {
        Donor donor = ControllerFactory.getInstance().getLoggedUser()
                .asDonor()
                .orElseThrow(() -> new RuntimeException("Donatorul nu este prezent!"));

        int eligibility = donor.getEligibility();
        String title, message;

        switch (eligibility) {
            case 1:
                ControllerFactory.getInstance().runPage(ControllerType.MAKE_APPOINTMENT_FORM, donationAppointmentButton);
                return;
            case 2:
                title = "Trebuie să aștepți!";
                message = "Un doctor îți va verifica imediat formularul de eligibilitate! Până atunci, trebuie să aștepți.";
                break;
            case -1:
                title = "Eligibilitate neverificată!";
                message = "Înainte de a programa o donare, verifică dacă ești eligibil! Poți face asta din \"Gestionează profil\"";
                break;
            default:
                title = "Momentan nu poți face o programare!";
                message = "Nu ești eligibil pentru donare!";
                break;
        }

        ControllerFactory.getInstance().showMessage(
                Alert.AlertType.WARNING,
                null,
                title,
                message
        );
    }

    private void loadAppointments() {
        Donor donor = ControllerFactory.getInstance().getLoggedUser()
                .asDonor()
                .orElseThrow(() -> new RuntimeException("Donatorul nu este prezent!"));
        List<Appointment> appointments = services.findUserAppointments(donor);

        if (appointments.isEmpty()) {
            showNoAppointments();
            return;
        }

        showAppointments(appointments);
    }

    private void showNoAppointments() {
        appointmentsLabel.setText("Momentan nu ai nicio programare");
        hboxAppointments.setManaged(false);
        hboxAppointments.setVisible(false);
    }

    private void showAppointments(List<Appointment> appointments) {
        appointmentsLabel.setText("Poți vedea mai jos informații despre viitoarele tale programări");
        hboxAppointments.getChildren().clear();

        appointments.stream()
                .limit(3)
                .map(this::createAppointmentCard)
                .forEach(hboxAppointments.getChildren()::add);

        hboxAppointments.setOnMouseClicked(_ ->
                ControllerFactory.getInstance().runPage(ControllerType.DONOR_PROFILE_PAGE, profileButton));
    }

    private void loadDonations() {
        var donor = ControllerFactory.getInstance().getLoggedUser()
                .asDonor()
                .orElseThrow(() -> new RuntimeException("Donatorul nu este prezent!"));
        List<Donation> donations = services.findUserDonations(donor);

        if (donations.isEmpty()) {
            showNoDonations();
            return;
        }

        showDonations(donations);
    }

    private void showNoDonations() {
        labelDonations.setText("Nu ai făcut nicio donație! De ce nu programezi una?");
        hboxPastDonations.setManaged(false);
        hboxPastDonations.setVisible(false);
    }

    private void showDonations(List<Donation> donations) {
        labelDonations.setText("Istoricul ultimelor tale donații");
        hboxPastDonations.getChildren().clear();

        donations.stream()
                .limit(3)
                .map(this::createDonationCard)
                .forEach(hboxPastDonations.getChildren()::add);

//        hboxPastDonations.setOnMouseClicked(_ -> navigateTo(ControllerType.DONATION_HISTORY, donationHistoryButton));
    }

    private VBox createAppointmentCard(Appointment appointment) {
        return createGenericCard(
                "Centru: " + appointment.getDonationCenter().getName(),
                "Data: " + formatDate(appointment.getDate()),
                "Status: " + translateAppointmentStatus(appointment.getStatus()),
                false
        );
    }

    private VBox createDonationCard(Donation donation) {
        DonationCenter center = services.getDonationCenterByID(donation.getDonationCenter());

        return createGenericCard(
                "Centru: " + center.getName() + ", cantitate: " + String.format("%.2f l", donation.getQuantity()),
                "Data: " + formatDate(donation.getDonationDate()),
                "Status: " + translateDonationStatus(donation.getStatus()),
                true
        );
    }

    private VBox createGenericCard(String line1, String line2, String line3, boolean isDonation) {
        VBox card = new VBox(5);
        card.getStyleClass().add(isDonation ? "donation-card" : "appointment-card");
        card.setMaxWidth(Region.USE_PREF_SIZE);

        Label label1 = createLabel(line1, true);
        Label label2 = createLabel(line2, true);
        Label label3 = createLabel(line3, false);

        card.getChildren().addAll(label1, label2, label3);
        return card;
    }

    private Label createLabel(String text, boolean wrapText) {
        Label label = new Label(text);
        label.getStyleClass().add("label-info");
        if (wrapText) {
            label.setWrapText(true);
            label.setMaxWidth(Double.MAX_VALUE);
        }
        return label;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    private String translateAppointmentStatus(AppointmentStatus status) {
        switch (status) {
            case SCHEDULED: return "Programată";
            case CONFIRMED: return "Confirmată";
            case CANCELED: return "Anulată";
            case COMPLETED: return "Efectuată";
            default: return status.toString();
        }
    }

    private String translateDonationStatus(DonationStatusEnum status) {
        switch (status) {
            case PENDING: return "În așteptare";
            case COMPLETED: return "Completă";
            case TESTED: return "Testată";
            case APPROVED: return "Acceptată";
            case REJECTED: return "Respinsă";
            default: return status.toString();
        }
    }
}
