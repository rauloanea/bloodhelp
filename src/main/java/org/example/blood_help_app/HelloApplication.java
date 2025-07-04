package org.example.blood_help_app;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.repository.implementation.hibernate.*;
import org.example.blood_help_app.service.ServicesImplementation;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Properties props = new Properties();
        try (FileReader reader = new FileReader("bd.config")) {
            props.load(reader);
        }catch (IOException e) {
            System.out.println("Error reading config file: " + e.getMessage());
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
            return;
        }

        System.out.println("Found properties, starting app...");

        var service = getService(props);

        ControllerFactory.getInstance().setServices(service);
        ControllerFactory.getInstance().setFirstStage(stage);
        ControllerFactory.getInstance().runPage(ControllerType.LOGIN, null);
    }

    private static ServicesImplementation getService(Properties props) {
        var userRepo = new UserMappedRepository();
        var adminRepo = new AdminMappedRepository();
        var doctorRepo = new DoctorMappedRepository();
        var donorRepo = new DonorMappedRepository();
        var donationCenterRepo = new DonationCenterMappedRepository();
        var donationRepo = new DonationMappedRepository();
        var bloodRequestRepo = new BloodRequestMappedRepository();
        var appointmentRepo = new AppointmentMappedRepository();
        var bloodUnitRepo = new BloodUnitMappedRepository();

        return new ServicesImplementation(userRepo, adminRepo, donorRepo, doctorRepo,
                donationRepo, donationCenterRepo, bloodRequestRepo, appointmentRepo, bloodUnitRepo);
    }

    public static void main(String[] args) {
        launch();
    }
}