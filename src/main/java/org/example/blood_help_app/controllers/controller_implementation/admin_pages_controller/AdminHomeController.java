package org.example.blood_help_app.controllers.controller_implementation.admin_pages_controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.blood_help_app.controllers.controller_implementation.general.Controller;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.domain.donationsdata.BloodRequest;
import org.example.blood_help_app.domain.donationsdata.BloodUnit;
import org.example.blood_help_app.domain.enums.BloodUnitStatusEnum;
import org.example.blood_help_app.domain.enums.RequestStatus;

import java.util.List;

public class AdminHomeController extends Controller {
    @FXML
    private VBox vboxBloodRequests;
    @FXML
    private Button exitButton;

    @FXML
    private void initialize() {
        this.exitButton.setOnAction(_ -> ((Stage) exitButton.getScene().getWindow()).close());
        loadBloodRequestsCards();
    }

    private void loadBloodRequestsCards() {
        vboxBloodRequests.getChildren().clear();
        vboxBloodRequests.setSpacing(15);

        List<BloodRequest> requests = services.getAllBloodRequests().stream().filter(req -> req.getStatus().equals(RequestStatus.PENDING)).toList();
        List<BloodUnit> allBloodUnits = services.getAllBloodUnits();

        if (requests.isEmpty()) {
            Label noRequestsLabel = new Label("Momentan nu exista cereri de sange");
            noRequestsLabel.getStyleClass().add("empty-list-label");
            vboxBloodRequests.getChildren().add(noRequestsLabel);
            return;
        }

        for (BloodRequest request : requests) {
            VBox requestCard = createRequestCard(request, allBloodUnits);
            vboxBloodRequests.getChildren().add(requestCard);
        }
    }

    private VBox createRequestCard(BloodRequest request, List<BloodUnit> allBloodUnits) {
        VBox card = new VBox(8);
        card.getStyleClass().add("request-card");

        // Blood Type
        Label bloodTypeLabel = new Label("Blood Type: " + request.getBloodType());
        bloodTypeLabel.getStyleClass().add("card-field");

        // Requested Quantity
        Label quantityLabel = new Label("Requested Quantity: " + request.getQuantity() + " units");
        quantityLabel.getStyleClass().add("card-field");

        // Available Quantity
        long availableQuantity = allBloodUnits.stream()
                .filter(unit -> unit.getBloodType() == request.getBloodType())
                .filter(unit -> unit.getStatus() == BloodUnitStatusEnum.AVAILABLE)
                .count();

        Label availableQuantityLabel = new Label("Available Quantity: " + availableQuantity + " units");
        availableQuantityLabel.getStyleClass().add("card-field");

        // Doctor
        Label doctorLabel = new Label("Requested by: Dr. " + request.getRequestingDoctor().getName());
        doctorLabel.getStyleClass().add("card-field");

        // Center
        Label centerLabel = new Label("Center: " + request.getDestinationCenter().getName());
        centerLabel.getStyleClass().add("card-field");

        // Status
        Label statusLabel = new Label("Status: " + request.getStatus());
        statusLabel.getStyleClass().add("card-field");

        // Notes (if available)
        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            Label notesLabel = new Label("Notes: " + request.getNotes());
            notesLabel.getStyleClass().add("card-field");
            notesLabel.setWrapText(true);
            card.getChildren().add(notesLabel);
        }

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("button-container");

        Button rejectButton = new Button("Reject");
        rejectButton.getStyleClass().add("reject-button");

        Button acceptButton = new Button("Accept");
        acceptButton.getStyleClass().add("accept-button");

        // Disable buttons if request is already resolved
        boolean isResolved = request.getStatus() == RequestStatus.APPROVED ||
                request.getStatus() == RequestStatus.REJECTED;

        rejectButton.setDisable(isResolved);
        acceptButton.setDisable(isResolved || availableQuantity < request.getQuantity());

        // Set button actions
        rejectButton.setOnAction(e -> {
            handleRequestAction(request, RequestStatus.REJECTED);
            rejectButton.setDisable(true);
            acceptButton.setDisable(true);
        });

        acceptButton.setOnAction(e -> {
            handleRequestAction(request, RequestStatus.APPROVED);
            rejectButton.setDisable(true);
            acceptButton.setDisable(true);
        });

        buttonBox.getChildren().addAll(rejectButton, acceptButton);

        // Add all elements to the card
        card.getChildren().addAll(
                bloodTypeLabel,
                quantityLabel,
                availableQuantityLabel,
                doctorLabel,
                centerLabel,
                statusLabel,
                buttonBox
        );

        return card;
    }

    private void handleRequestAction(BloodRequest request, RequestStatus newStatus) {
        request.setStatus(newStatus);
        services.updateBloodRequest(request);

        if (newStatus == RequestStatus.APPROVED) {
            services.subtractBloodUnits(request.getBloodType(), request.getQuantity());
            showSuccessAlert("Request approved successfully");
        } else {
            showSuccessAlert("Request rejected successfully");
        }

        // Refresh the list to reflect changes
        loadBloodRequestsCards();
    }

    private void showSuccessAlert(String message) {
        // Implement your alert/show notification method here
        ControllerFactory.getInstance().showMessage(Alert.AlertType.NONE, null, null, message);
    }
}