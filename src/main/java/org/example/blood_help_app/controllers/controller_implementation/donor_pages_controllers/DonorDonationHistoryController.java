package org.example.blood_help_app.controllers.controller_implementation.donor_pages_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.enums.DonationStatusEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DonorDonationHistoryController extends Controller {
    @FXML
    private Button homeButton;
    @FXML
    private Button donationCentersButton;
    @FXML
    private Button donationAppointmentButton;
    @FXML
    private Button profileButton;

    @FXML
    private Label labelHistoryInfo;
    @FXML
    private VBox vboxDonations;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML
    private void initialize() {
        this.homeButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONOR_HOME, homeButton));
//        this.donationCentersButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType))
        donationAppointmentButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.MAKE_APPOINTMENT_FORM, donationAppointmentButton));
        profileButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONOR_PROFILE_PAGE, profileButton));
        loadDonations();
    }

    private void loadDonations() {
        List<Donation> donations = services.findUserDonations(
                ControllerFactory.getInstance().getLoggedUser().asDonor().get()
        );

        if (donations.isEmpty()) {
            labelHistoryInfo.setText("Momentan nu ai nicio donație făcută!");
            vboxDonations.setVisible(false);
            vboxDonations.setManaged(false);
            return;
        }

        labelHistoryInfo.setText("De aici poți verifica și afla detalii despre donațiile tale anterioare!");
        vboxDonations.getChildren().clear();

        donations.forEach(donation -> {
            VBox donationCard = createDonationCard(donation);
            vboxDonations.getChildren().add(donationCard);
        });
    }

    private VBox createDonationCard(Donation donation) {
        // 1. Container principal
        VBox card = new VBox(10);
        card.getStyleClass().add("donation-card");
        card.setMaxWidth(Double.MAX_VALUE);

        // 2. Obține informații despre centrul de donare
        DonationCenter center = services.getDonationCenterByID(donation.getDonationCenter());

        // 3. Informații donație
        Label centerLabel = new Label("Centru: " + center.getName());
        centerLabel.getStyleClass().add("label-info");
        centerLabel.setWrapText(true);

        Label detailsLabel = new Label(
                "Data: " + formatDate(donation.getDonationDate()) +
                        " | Tip sânge: " + donation.getBloodType() +
                        " | Cantitate: " + String.format("%.2f ml", donation.getQuantity())
        );
        detailsLabel.getStyleClass().add("label-info");

        Label statusLabel = new Label("Status: " + translateStatus(donation.getStatus()));
        statusLabel.getStyleClass().add("label-info");

        // 4. Buton detalii (opțional)
        Button detailsButton = new Button("Vezi detalii complete");
        detailsButton.getStyleClass().add("button2");
        detailsButton.setOnAction(e -> showDonationDetails(donation, center));

        // 5. Adaugă elementele în card
        card.getChildren().addAll(centerLabel, detailsLabel, statusLabel, detailsButton);

        return card;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    private String translateStatus(DonationStatusEnum status) {
        switch (status) {
            case COMPLETED: return "Completă";
            case TESTED: return "Testată";
            case APPROVED: return "Aprobată";
            case REJECTED: return "Respinsă";
            case PENDING: return "În așteptare";
            default: return status.toString();
        }
    }

    private void showDonationDetails(Donation donation, DonationCenter center) {
        Alert detailsDialog = new Alert(Alert.AlertType.INFORMATION);
        detailsDialog.setTitle("Detalii donație");
        detailsDialog.setHeaderText("Informații complete despre donație");

        String content = String.format(
                "Centru: %s\n" +
                        "Data: %s\n" +
                        "Tip sânge: %s\n" +
                        "Cantitate: %.2f ml\n" +
                        "Status: %s\n" +
                        "Doctor responsabil: %s\n" +
                        "Rezultate teste: %s",
                center.getName(),
                formatDate(donation.getDonationDate()),
                donation.getBloodType(),
                donation.getQuantity(),
                translateStatus(donation.getStatus()),
                donation.getDoctor().getName(),
                donation.getTestResults() != null ? donation.getTestResults() : "N/A"
        );

        detailsDialog.setContentText(content);
        detailsDialog.showAndWait();
    }
}
