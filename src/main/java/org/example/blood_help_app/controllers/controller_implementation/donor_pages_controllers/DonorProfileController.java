package org.example.blood_help_app.controllers.controller_implementation.donor_pages_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.domain.donationsdata.Appointment;
import org.example.blood_help_app.utils.PasswordEncryption;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DonorProfileController extends Controller {
    @FXML
    private Button homeButton;
    @FXML
    private Button donationCentersButton;
    @FXML
    private Button donationAppointmentButton;
    @FXML
    private Button donationHistoryButton;
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
    private VBox vboxAppointments;
    @FXML
    private Label labelAppointments;
    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField newPhoneNumberField;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Button updateInfoButton;
    @FXML
    private Button resetNewInfoButton;

    @FXML
    private void initialize(){
        this.labelName.setText("Salut, " + ControllerFactory.getInstance().getLoggedUser().asDonor().get().getName());

        this.homeButton.setOnAction(_ -> {
            ControllerFactory.getInstance().runPage(ControllerType.DONOR_HOME, homeButton);
        });

        this.donationHistoryButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.DONATION_HISTORY, donationHistoryButton));
        this.donationAppointmentButton.setOnAction(_ -> ControllerFactory.getInstance().runPage(ControllerType.MAKE_APPOINTMENT_FORM, donationAppointmentButton));

        this.negativeEligibilityButton.setOnAction(_ -> handleNegativeEligibiltyAnswer());
        this.affirmativeEligibilityButton.setOnAction(_ ->
                ControllerFactory.getInstance().runPage(ControllerType.ELIGIBILITY_FORM, affirmativeEligibilityButton));
        this.checkDoctorButton.setOnAction(_ -> {
            ControllerFactory.getInstance().showMessage(
                    Alert.AlertType.CONFIRMATION,
                    "Succes",
                    "Felicitari",
                    "Programarea a fost facuta cu succes! Vei primi informatiile privind programarea facuta pe mail!"
            );

            this.services.setDonorEligibility(ControllerFactory.getInstance()
                    .getLoggedUser()
                    .asDonor()
                    .get(), 2);

            this.labelEligibility.setText("Vei fi anuntat de catre medic daca esti eligibil sau nu");
            setVisibilityForEligibilityButtons(false);
        });

        this.checkEligibility();

        this.setAppointmentsInfo();

        this.resetNewInfoButton.setOnAction(_ -> this.resetFields());

        this.updateInfoButton.setOnAction(_ -> this.handleUpdateUserInfo());
    }

    private void checkEligibility() {
        Integer eligibility = ControllerFactory.getInstance().getLoggedUser().asDonor().get().getEligibility();
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

    private void setAppointmentsInfo(){
        var appointments = this.services.findUserAppointments(ControllerFactory.getInstance().getLoggedUser().asDonor().get());

        if(appointments.isEmpty()){
            labelAppointments.setText("Momentan nu ai nicio programare facuta!");
            vboxAppointments.setVisible(false);
            vboxAppointments.setManaged(false);

            return;
        }

        labelAppointments.setText("De aici poti vedea si anula programarile tale viitoare");

        vboxAppointments.setVisible(true);
        vboxAppointments.setManaged(true);
        vboxAppointments.getChildren().clear();

        appointments.forEach(appointment -> {
            VBox appointmentCard = createAppointmentCard(appointment);
            vboxAppointments.getChildren().add(appointmentCard);
        });
    }

    private VBox createAppointmentCard(Appointment appointment) {
        // 1. Container principal
        VBox card = new VBox(10);
        card.getStyleClass().add("vbox-card");
        card.setMaxWidth(Double.MAX_VALUE);

        // 2. Informații programare
        Label centerLabel = new Label("Centru: " + appointment.getDonationCenter().getName());
        centerLabel.getStyleClass().add("label-info");
        centerLabel.setWrapText(true);

        Label dateLabel = new Label("Data: " + formatDateTime(appointment.getDate()));
        dateLabel.getStyleClass().add("label-info");

        Label statusLabel = new Label("Status: " + appointment.getStatus());
        statusLabel.getStyleClass().add("label-info");

        // 3. Buton anulare
        Button cancelButton = new Button("Anulează programarea");
        cancelButton.getStyleClass().add("buttonExit");
        cancelButton.setOnAction(e -> handleCancelAppointment(appointment));

        // 4. Adaugă elementele în card
        card.getChildren().addAll(centerLabel, dateLabel, statusLabel, cancelButton);

        return card;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return dateTime.format(formatter);
    }

    private void handleCancelAppointment(Appointment appointment) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmare anulare");
        confirmation.setHeaderText("Sigur dorești să anulezi programarea?");
        confirmation.setContentText("Programarea la " + appointment.getDonationCenter().getName() +
                " pe " + formatDateTime(appointment.getDate()));

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                services.cancelAppointment(appointment);
                setAppointmentsInfo();

                ControllerFactory.getInstance().showMessage(Alert.AlertType.CONFIRMATION, null, "Succes",
                        "Programarea a fost anulata cu succes!");
            }catch (Exception e){
                ControllerFactory.getInstance().showMessage(Alert.AlertType.ERROR, null, "Eroare", e.getMessage());
            }
        }
    }

    private void resetFields(){
        newUsernameField.setText("");
        newPhoneNumberField.setText("");
        oldPasswordField.setText("");
        newPasswordField.setText("");
    }

    private void handleUpdateUserInfo(){
        System.out.println(PasswordEncryption.decryptPassword(ControllerFactory.getInstance().getLoggedUser().asDonor().get().getPassword()));

        var newUsername = newUsernameField.getText();
        var newPhoneNumber = newPhoneNumberField.getText();
        var oldPassword = oldPasswordField.getText();
        var newPassword = newPasswordField.getText();

        if(oldPassword == null && newPassword == null &&
        newUsername == null && newPhoneNumber == null){
            ControllerFactory.getInstance().showMessage(Alert.AlertType.ERROR, null, "Atentie!", "Nu ai facut nicio modificare! Introdu date pentru a actualiza informatiile!");

            return;
        }

        if(oldPassword != null &&
                !PasswordEncryption.decryptPassword(ControllerFactory.getInstance().getLoggedUser().asDonor().get().getPassword()).equals(oldPassword)){
            ControllerFactory.getInstance().showMessage(Alert.AlertType.ERROR, null, "Atentie!", "Ai gresit parola actuala!");

            return;
        }

        if(oldPassword == null || newPassword == null){
            ControllerFactory.getInstance().showMessage(Alert.AlertType.ERROR, null, "Atentie!", "Pentru a schimba parola trebuie sa introduci atat parola veche, cat si cea noua!");

            return;
        }

        try{
            var user = ControllerFactory.getInstance().getLoggedUser().asDonor().get();
            var updatedUser = this.services.updateUser(user, newUsername, newPhoneNumber, newPassword);
            ControllerFactory.getInstance().setUser(updatedUser);

            ControllerFactory.getInstance().showMessage(Alert.AlertType.CONFIRMATION, null, "Felicitari!",
                    "Datele actualizate au fost salvate cu succes!");

            this.resetFields();
        }catch(Exception e){
            ControllerFactory.getInstance().showMessage(Alert.AlertType.ERROR, null, "Eroare", e.getMessage());
        }
    }
}
