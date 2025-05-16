package org.example.blood_help_app.controllers.controller_implementation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.domain.users.Admin;
import org.example.blood_help_app.domain.users.utils.AppUser;
import org.example.blood_help_app.domain.users.Doctor;
import org.example.blood_help_app.domain.users.Donor;

public class LoginController extends Controller{
    @FXML
    private Button newAccButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label labelErrorEmail;
    @FXML
    private Label labelErrorPassword;

    @FXML
    private void initialize(){
        emailField.setOnKeyTyped(_ -> {
            labelErrorEmail.setText("");
        });
        passwordField.setOnKeyTyped(_ -> {
            labelErrorPassword.setText("");
        });

        newAccButton.setOnAction(_ -> handleCreateAcc());
        loginButton.setOnAction(_ -> handleLogin());
    }

    private void handleCreateAcc() {
        ControllerFactory.getInstance().runPage(ControllerType.CREATE_ACCOUNT, newAccButton);
    }

    private void handleLogin() {
        try {
            var email = emailField.textProperty().get();
            var password = passwordField.textProperty().get();

            if(email.isEmpty() || password.isEmpty()){
                ControllerFactory.getInstance().showMessage(Alert.AlertType.WARNING,
                        "Atentie!",
                        "Credentiale invalide!",
                        "Fii sigur ca ai introdus un email si o parola pentru a te conecta!");
                return;
            }

            AppUser user = services.authenticateAccount(email, password);

            ControllerFactory.getInstance().setUser(user);

            switch (user) {
                case Donor donor -> {
                    ControllerFactory.getInstance().setUser(donor);
                    ControllerFactory.getInstance().runPage(ControllerType.DONOR_HOME, loginButton);
                }
                case Doctor doctor -> {
                    ControllerFactory.getInstance().setUser(doctor);
                    ControllerFactory.getInstance().runPage(ControllerType.DOCTOR_HOME, loginButton);
                }
                case Admin admin -> {
                    ControllerFactory.getInstance().setUser(admin);
                    ControllerFactory.getInstance().runPage(ControllerType.ADMIN_HOME, loginButton);
                }
                case null, default -> throw new RuntimeException("Tip de utilizator necunoscut!");
            }


        } catch (Exception e){
            System.err.println(e.getMessage());
            labelErrorEmail.setText(e.getMessage());
            labelErrorPassword.setText(e.getMessage());
        }
    }

}
