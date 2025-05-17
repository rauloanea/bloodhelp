package org.example.blood_help_app.controllers.controller_implementation.general;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.domain.enums.UserTypeEnum;
import org.example.blood_help_app.domain.users.User;

import java.time.LocalDateTime;

public class CreateAccountController extends Controller {
    @FXML
    private Button backButton;
    @FXML
    private Button createAccButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordCheckField;
    @FXML
    private DatePicker birthdayPicker;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private Label labelError;

    @FXML
    private void initialize(){
        labelError.setVisible(false);
        setPasswordFieldsListener();

        this.backButton.setOnAction(_ -> this.backToLogin());
        createAccButton.setOnAction(_ -> handleCreateAcc());
    }

    private void handleCreateAcc() {
        try{
            validateData();
        }catch (Exception e){
            ControllerFactory.getInstance().showMessage(Alert.AlertType.ERROR, "Eroare", "Date invalide!", e.getMessage());
            return;
        }

        var user = new User(
                nameField.getText(),
                emailField.getText(),
                usernameField.getText(),
                passwordField.getText(),
                phoneNumberField.getText(),
                birthdayPicker.getValue().atStartOfDay(),
                UserTypeEnum.DONOR
        );

        try{
            this.services.createDonorAccount(user);
            ControllerFactory.getInstance().showMessage(Alert.AlertType.INFORMATION, "Succes", "Cont creat", "Acum te poti conecta cu credentialele tale!");
            ControllerFactory.getInstance().runPage(ControllerType.LOGIN, this.createAccButton);
        }catch (Exception e){
            ControllerFactory.getInstance().showMessage(Alert.AlertType.ERROR, "Eroare", "Eroare interna!", e.getMessage());
        }
    }

    private void validateData() throws RuntimeException{
        if(nameField.getText().isEmpty() || nameField.getText().length() < 2){
            throw new RuntimeException("Numele trebuie sa fie valid!");
        }
        if(emailField.getText().isEmpty() || !emailField.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){
            throw new RuntimeException("Email invalid!");
        }
        if(usernameField.getText().isEmpty() || usernameField.getText().length() < 4){
            throw new RuntimeException("Alege un username valid!");
        }
        if((passwordField.getText().isEmpty() || passwordField.getText().length() < 4)
                && !passwordField.getText().equals(passwordCheckField.getText())){
            throw new RuntimeException("Parola invalida! Asigura-te ca parola are cel putin 4 caractere");
        }
        if(birthdayPicker.getValue() == null){
            throw new RuntimeException("Alege o data de nastere!");
        }
        if(LocalDateTime.now().getYear() - birthdayPicker.getValue().getYear() < 18){
            throw new RuntimeException("Trebuie sa ai cel putin 18 ani pentru a putea face un cont!");
        }
        if(phoneNumberField.getText().isEmpty() || phoneNumberField.getText().length() != 10){
            throw new RuntimeException("Numar de telefon invalid!");
        }
    }

    private void setPasswordFieldsListener(){
        this.passwordField.setOnKeyTyped(_ -> {
            if(!passwordField.getText().equals(passwordCheckField.getText())){
                labelError.setVisible(true);
            }else{
                labelError.setVisible(false);
            }
        });
        this.passwordCheckField.setOnKeyTyped(_ -> {
            if(!passwordField.getText().equals(passwordCheckField.getText())){
                labelError.setVisible(true);
            }else {
                labelError.setVisible(false);
            }
        });
    }

    private void backToLogin() {
        ControllerFactory.getInstance().runPage(ControllerType.LOGIN, this.backButton);
    }

}
