package org.example.blood_help_app;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.blood_help_app.controllers.factory.ControllerFactory;
import org.example.blood_help_app.controllers.factory.ControllerType;
import org.example.blood_help_app.repository.implementation.*;
import org.example.blood_help_app.service.ServicesImplementation;

import java.io.Console;
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
        var userRepo = new UserRepository(props);
        var adminRepo = new AdminRepository(props, userRepo);
        var doctorRepo = new DoctorRepository(props, userRepo);
        var donorRepo = new DonorRepository(props, userRepo);
        var donationCenterRepo = new DonationCenterRepository(props);
        var donationRepo = new DonationRepository(props, donorRepo, doctorRepo, donationCenterRepo);
        var bloodUnitRepo = new BloodUnitRepository(props, donationRepo, donationCenterRepo);
        var appointmentRepo = new AppointmentRepository(props, donorRepo, donationCenterRepo);

        return new ServicesImplementation(userRepo, adminRepo, donorRepo, doctorRepo,
                donationRepo, donationCenterRepo, bloodUnitRepo, appointmentRepo);
    }

    public static void main(String[] args) {
        launch();
    }
}