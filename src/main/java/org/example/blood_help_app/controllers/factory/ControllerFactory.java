package org.example.blood_help_app.controllers.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.blood_help_app.controllers.controller_implementation.admin_pages_controller.AdminHomeController;
import org.example.blood_help_app.controllers.controller_implementation.doctor_pages_controllers.DoctorAppointmentRequestController;
import org.example.blood_help_app.controllers.controller_implementation.doctor_pages_controllers.DoctorBloodRequestController;
import org.example.blood_help_app.controllers.controller_implementation.donor_pages_controllers.*;
import org.example.blood_help_app.controllers.controller_implementation.general.CreateAccountController;
import org.example.blood_help_app.controllers.controller_implementation.general.LoginController;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.users.utils.AppUser;
import org.example.blood_help_app.domain.users.utils.LoggedUserContext;
import org.example.blood_help_app.service.ServicesImplementation;

public class ControllerFactory {
    private static ControllerFactory instance = null;
    private Stage firstStage;

    private ServicesImplementation services;

    private LoggedUserContext loggedUser;

    private DonationCenter selectedDonationCenter;

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

    public void setUser(AppUser user) {
        this.loggedUser = new LoggedUserContext(user);
    }

    public LoggedUserContext getLoggedUser() {
        return loggedUser;
    }

    public void setFirstStage(final Stage firstStage) {
        this.firstStage = firstStage;
    }

    public void setSelectedDonationCenter(final DonationCenter selectedDonationCenter) {
        this.selectedDonationCenter = selectedDonationCenter;
    }

    public DonationCenter getSelectedDonationCenter() {
        return selectedDonationCenter;
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
            case ADMIN_HOME -> runAdminHomePage();
            case DOCTOR_HOME -> runDoctorHomePage();
            case CREATE_ACCOUNT -> runCreateAccountPage();
            case DONOR_PROFILE_PAGE -> runDonorProfilePage();
            case ELIGIBILITY_FORM -> runEligibilityFormPage();
            case MAKE_APPOINTMENT_FORM -> runAppointmentFormPage();
            case DONATION_HISTORY -> runDonationHistoryPage();
            case DONATION_CENTERS -> runDonationCentersPage();
            case DOCTOR_BLOOD_REQUEST -> runDoctorBloodRequest();
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

//            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.setScene(newScene);
            newStage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/donor_home_page.fxml"));

        DonorHomeController controller = new DonorHomeController();
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

        DonorProfileController controller = new DonorProfileController();
//        controller.setCurrentUser(this.currentUser);
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runEligibilityFormPage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/eligibility_form.fxml"));
        DonorEligibilityFormController controller = new DonorEligibilityFormController();
//        controller.setCurrentUser(this.currentUser);
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runAppointmentFormPage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/make_donation_appointment.fxml"));
        DonorAppointmentPageController controller = new DonorAppointmentPageController();
//        controller.setCurrentUser(this.currentUser);
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runDonationHistoryPage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/donation_history_page.fxml"));
        DonorDonationHistoryController controller = new DonorDonationHistoryController();
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runDonationCentersPage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/donation_centers_page.fxml"));
        DonorDonationCentersController controller = new DonorDonationCentersController();
        controller.setServices(services);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    public void runDoctorHomePage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/doctor_home_page.fxml"));
        DoctorAppointmentRequestController controller = new DoctorAppointmentRequestController();

        controller.setServices(services);
        services.addObserver(controller);

        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runDoctorBloodRequest(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/doctor_bloodrequest_page.fxml"));
        DoctorBloodRequestController controller = new DoctorBloodRequestController();

        controller.setServices(services);

        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runAdminHomePage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/admin_home_page.fxml"));
        AdminHomeController controller = new AdminHomeController();

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
