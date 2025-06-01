package org.example.blood_help_app.controllers.controller_implementation.doctor_pages_controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.enums.AppointmentStatus;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.utils.observer.IObserver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class DoctorAppointmentRequestController extends Controller implements IObserver {
    @FXML
    private Label labelDoctorName;
    @FXML
    private Label labelInstitutionName;
    @FXML
    private Label labelInfo;
    @FXML
    private VBox vboxAppointmentsRequests;
    @FXML
    private Button exitButton;
    @FXML
    private Button sendBloodRequestButton;

    private List<DonationCenter> allCenters;

    @FXML
    private void initialize(){
        var doctor = ControllerFactory.getInstance().getLoggedUser().asDoctor().get();
        this.labelDoctorName.setText("Dr. " + doctor.getName());
        this.labelInstitutionName.setText(doctor.getInstitution());

        // Încarcă toate centrele de donare o singură dată
        allCenters = services.getDonationCenters();
        initializeDonorsApp();

        this.exitButton.setOnAction(_ -> ((Stage) exitButton.getScene().getWindow()).close());
        this.sendBloodRequestButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DOCTOR_BLOOD_REQUEST, sendBloodRequestButton));
    }

    @Override
    public void update() {
        Platform.runLater(this::initializeDonorsApp);
    }

    private void initializeDonorsApp() {
        List<Donor> donorsNeedingConsultation = services.getDonorsWithEligibility(2);
        vboxAppointmentsRequests.getChildren().clear();
        vboxAppointmentsRequests.setSpacing(15);

        donorsNeedingConsultation.forEach(donor -> {
            VBox donorCard = createDonorCard(donor);
            vboxAppointmentsRequests.getChildren().add(donorCard);
        });
    }

    private VBox createDonorCard(Donor donor) {
        // 1. Container principal
        VBox card = new VBox(10);
        card.getStyleClass().add("vbox-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(10));

        // 2. Informații donator
        Label nameLabel = new Label("Donator: " + donor.getName());
        nameLabel.getStyleClass().add("label");
        nameLabel.setWrapText(true);

        Label bloodTypeLabel = new Label("Grupa sanguină: " +
                (donor.getBloodType() != null ? donor.getBloodType() : "Nespecificat"));
        bloodTypeLabel.getStyleClass().add("label-info");

        Label contactLabel = new Label("Contact: " +
                (donor.getContactInfo() != null ? donor.getContactInfo() : "Nespecificat"));
        contactLabel.getStyleClass().add("label-info");

        // 3. Selector pentru centrul de donare
        ComboBox<DonationCenter> centerComboBox = new ComboBox<>();
        centerComboBox.getItems().addAll(allCenters);
        centerComboBox.setConverter(new StringConverter<DonationCenter>() {
            @Override
            public String toString(DonationCenter center) {
                return center != null ? center.getName() + " - " + center.getAddress() : "";
            }

            @Override
            public DonationCenter fromString(String string) {
                return null;
            }
        });
        centerComboBox.setPromptText("Selectează centrul");

        // 4. Selector pentru data și ora consultației
        HBox dateTimePicker = createDateTimePicker();

        // 5. Butoane acțiuni
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button acceptButton = new Button("Programează consultația");
        acceptButton.getStyleClass().add("button-primary");
        acceptButton.setOnAction(e -> handleScheduleConsultation(donor, centerComboBox, dateTimePicker));

        Button rejectButton = new Button("Respinge");
        rejectButton.getStyleClass().add("buttonExit");
        rejectButton.setOnAction(e -> handleRejectDonor(donor));

        buttonsBox.getChildren().addAll(rejectButton, acceptButton);

        var centerLabel = new Label("Selectează centrul:");
        centerLabel.getStyleClass().add("label-info");

        var dataTimeLabel = new Label("Selectează data și ora consultației:");
        dataTimeLabel.getStyleClass().add("label-info");

        // 6. Adaugă elementele în card
        card.getChildren().addAll(
                nameLabel,
                bloodTypeLabel,
                contactLabel,
                centerLabel,
                centerComboBox,
                dataTimeLabel,
                dateTimePicker,
                buttonsBox
        );

        return card;
    }

    private HBox createDateTimePicker() {
        HBox container = new HBox(5);
        container.setAlignment(Pos.CENTER_LEFT);

        // DatePicker pentru data
        DatePicker datePicker = new DatePicker();
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(LocalDate.now())); // Dezactivează datele trecute
            }
        });

        // ComboBox pentru ore (8-15)
        ComboBox<String> hourCombo = new ComboBox<>();
        hourCombo.getItems().addAll("08", "09", "10", "11", "12", "13", "14", "15");
        hourCombo.setPromptText("Ora");

        // ComboBox pentru minute (00, 20, 40)
        ComboBox<String> minuteCombo = new ComboBox<>();
        minuteCombo.getItems().addAll("00", "20", "40");
        minuteCombo.setPromptText("Minute");

        container.getChildren().addAll(
                new Label("Data:"), datePicker,
                new Label("Ora:"), hourCombo,
                new Label(":"), minuteCombo
        );

        return container;
    }

    private void handleScheduleConsultation(Donor donor, ComboBox<DonationCenter> centerCombo, HBox dateTimeContainer) {
        try {
            // Validare centru selectat
            if (centerCombo.getValue() == null) {
                ControllerFactory.getInstance().showMessage(
                        Alert.AlertType.WARNING,
                        "Centru nespecificat",
                        "Selectează un centru",
                        "Te rugăm să selectezi un centru de donare pentru programare"
                );
                return;
            }

            // Extrage componentele pentru dată și oră
            DatePicker datePicker = (DatePicker) dateTimeContainer.getChildren().get(1);
            ComboBox<String> hourCombo = (ComboBox<String>) dateTimeContainer.getChildren().get(3);
            ComboBox<String> minuteCombo = (ComboBox<String>) dateTimeContainer.getChildren().get(5);

            // Validare dată și oră
            if (datePicker.getValue() == null || hourCombo.getValue() == null || minuteCombo.getValue() == null) {
                ControllerFactory.getInstance().showMessage(
                        Alert.AlertType.WARNING,
                        "Date incomplete",
                        "Selectează data și ora complet",
                        "Te rugăm să completezi toate câmpurile pentru programare"
                );
                return;
            }

            // Construiește LocalDateTime
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                    datePicker.getValue(),
                    LocalTime.of(
                            Integer.parseInt(hourCombo.getValue()),
                            Integer.parseInt(minuteCombo.getValue())
                    )
            );

            // Verifică dacă data este în viitor
            if (appointmentDateTime.isBefore(LocalDateTime.now())) {
                ControllerFactory.getInstance().showMessage(
                        Alert.AlertType.WARNING,
                        "Dată invalidă",
                        "Data trecută",
                        "Te rugăm să selectezi o dată și oră viitoare"
                );
                return;
            }

            // Actualizează eligibilitatea și creează programarea
            donor.setEligibility(3); // Marchează ca eligibil după consult
            services.setDonorEligibility(donor, donor.getEligibility());

            // Obține centrul selectat
            DonationCenter selectedCenter = centerCombo.getValue();

            // Creează programarea
            services.makeAppointment(
                    donor,
                    selectedCenter,
                    appointmentDateTime,
                    AppointmentStatus.ELIGIBILITY_CHECK
            );

            ControllerFactory.getInstance().showMessage(
                    Alert.AlertType.INFORMATION,
                    "Programare creată",
                    "Succes",
                    "Consultația pentru " + donor.getName() +
                            " a fost programată la " + selectedCenter.getName() +
                            " pe data de " + formatDateTime(appointmentDateTime)
            );

            // Reîmprospătează lista
            initializeDonorsApp();

        } catch (Exception e) {
            ControllerFactory.getInstance().showMessage(
                    Alert.AlertType.ERROR,
                    "Eroare",
                    "A apărut o eroare",
                    e.getMessage()
            );
        }
    }

    private void handleRejectDonor(Donor donor) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmă respingerea");
        confirmation.setHeaderText("Respinge cererea donatorului");
        confirmation.setContentText("Ești sigur că vrei să respingi cererea lui " + donor.getName() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            donor.setEligibility(0); // Marchează ca neeligibil
            services.setDonorEligibility(donor, donor.getEligibility());
            initializeDonorsApp(); // Reîmprospătează lista
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return dateTime.format(formatter);
    }
}