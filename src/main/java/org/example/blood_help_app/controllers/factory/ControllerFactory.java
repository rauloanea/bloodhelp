package org.example.blood_help_app.controllers.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.blood_help_app.controllers.controller_implementation.*;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.service.ServicesImplementation;

public class ControllerFactory {
    private static ControllerFactory instance = null;
    private Stage firstStage;

    private ServicesImplementation services;

    private Donor user;

    private ControllerFactory() {}

    public static ControllerFactory getInstance() {
        if (instance == null) {
            instance = new ControllerFactory();
        }
        return instance;
    }

    public void setServices(final ServicesImplementation services) {
        this.services = services;
    }

    public void setUser(final Donor user) {
        this.user = user;
    }

    public Donor getUser(){
        return user;
    }

    public void setFirstStage(final Stage firstStage) {
        this.firstStage = firstStage;
    }

    public void runPage(ControllerType controllerType, Button buttonTrigger){
        if(this.firstStage != null){
            runLoginPage();
            this.firstStage = null;
            return;
        }

        switch(controllerType){
            case LOGIN -> runLoginPage();
            case DONOR_HOME -> runDonorHomePage();
            case ADMIN_HOME -> throw new RuntimeException("Not implemented!");
            case DOCTOR_HOME -> throw new RuntimeException("Not implemented!");
            case CREATE_ACCOUNT -> runCreateAccountPage();
            case DONOR_PROFILE_PAGE -> runDonorProfilePage();
            case ELIGIBILITY_FORM -> runEligibilityFormPage();
            case MAKE_APPOINTMENT_FORM -> runAppointmentFormPage();
        }

        if(buttonTrigger != null){
            Stage currentStage = (Stage) buttonTrigger.getScene().getWindow();
            currentStage.close();
        }
    }

    private void runGenericPage(FXMLLoader loader) {
        try {
            Parent newWindowRoot = loader.load();

            Stage newStage = new Stage();
            Scene newScene = new Scene(newWindowRoot);

            newScene.getStylesheets().add(getClass().getResource("/files/style.css").toExternalForm());

            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.setScene(newScene);
            newStage.show();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void runLoginPage(){
        System.out.println("loading login page...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/login_page.fxml"));

        LoginController loginPageController = new LoginController();
        loginPageController.setServices(services);
        loader.setController(loginPageController);

        if(this.firstStage != null) {
            try {
                Scene scene = new Scene(loader.load(), 600, 400);
                this.firstStage.initStyle(StageStyle.UNDECORATED);
                this.firstStage.setScene(scene);
                this.firstStage.show();
            }
            catch(Exception e) {
                System.out.println("Found error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            this.runGenericPage(loader);
    }

    private void runDonorHomePage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/home_page.fxml"));

        HomeController controller = new HomeController();
//        controller.setCurrentUser(this.currentUser);
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runCreateAccountPage(){
        System.out.println("Running new account page...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/create_account.fxml"));

        CreateAccountController controller = new CreateAccountController();
//        controller.setCurrentUser(this.currentUser);
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runDonorProfilePage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/profile_page.fxml"));

        ProfileController controller = new ProfileController();
//        controller.setCurrentUser(this.currentUser);
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runEligibilityFormPage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/eligibility_form.fxml"));
        EligibilityFormController controller = new EligibilityFormController();
//        controller.setCurrentUser(this.currentUser);
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runAppointmentFormPage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/make_donation_appointment.fxml"));
        AppointmentPageController controller = new AppointmentPageController();
//        controller.setCurrentUser(this.currentUser);
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    public void showMessage(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
