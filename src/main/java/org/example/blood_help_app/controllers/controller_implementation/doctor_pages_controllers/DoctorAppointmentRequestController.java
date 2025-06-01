package org.example.blood_help_app.controllers.controller_implementation.doctor_pages_controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.utils.observer.IObserver;

import java.util.List;

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
    private void initialize(){
        var doctor = ControllerFactory.getInstance().getLoggedUser().asDoctor().get();
        this.labelDoctorName.setText("Dr. " + doctor.getName());
        this.labelInstitutionName.setText(doctor.getInstitution());

        initializeDonorsApp();
    }

    @Override
    public void update() {
        Platform.runLater(this::initializeDonorsApp);
    }

    private void initializeDonorsApp() {
        List<Donor> donorsNeedingConsultation = services.getDonorsWithEligibility(2);
        vboxAppointmentsRequests.getChildren().clear();

        donorsNeedingConsultation.forEach(donor -> {
            VBox donorCard = createDonorCard(donor);
            vboxAppointmentsRequests.getChildren().add(donorCard);
        });
    }

    private VBox createDonorCard(Donor donor) {
        VBox card = new VBox(10);
        card.getStyleClass().add("vbox-card");

        Label nameLabel = new Label("Donor: " + donor.getName());
        Label bloodTypeLabel = new Label("Grupa sanguină: " + donor.getBloodType());
        Label contactLabel = new Label("Contact: " + donor.getContactInfo());

        Button acceptButton = new Button("Acceptă consultația");
        acceptButton.setOnAction(e -> handleAcceptConsultation(donor));

        card.getChildren().addAll(nameLabel, bloodTypeLabel, contactLabel, acceptButton);
        return card;
    }

    private void handleAcceptConsultation(Donor donor) {
        // Implementează logica pentru programarea consultației
        donor.setEligibility(1); // Sau altă valoare după consult
        services.setDonorEligibility(donor, donor.getEligibility());
    }
}
