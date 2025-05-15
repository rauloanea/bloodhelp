package org.example.blood_help_app.utils;

import org.example.blood_help_app.domain.donationsdata.Appointment;
import org.example.blood_help_app.domain.donationsdata.BloodUnit;
import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.users.Admin;
import org.example.blood_help_app.domain.users.Doctor;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.domain.users.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateUtils {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if ((sessionFactory == null) || (sessionFactory.isClosed())) {
            sessionFactory = createNewSessionFactory();
        }
        return sessionFactory;
    }

    private static SessionFactory createNewSessionFactory() {
        try {
            var inputStream = HibernateUtils.class.getClassLoader()
                    .getResourceAsStream("hibernate.properties");

            Properties properties = new Properties();
            properties.load(inputStream);

            Configuration configuration = new Configuration()
                    .addProperties(properties)
                    .addAnnotatedClass(Appointment.class)
                    .addAnnotatedClass(BloodUnit.class)
                    .addAnnotatedClass(Donation.class)
                    .addAnnotatedClass(DonationCenter.class)
                    .addAnnotatedClass(Admin.class)
                    .addAnnotatedClass(Doctor.class)
                    .addAnnotatedClass(Donor.class)
                    .addAnnotatedClass(User.class);

            return configuration.buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException("Failed to create SessionFactory", e);
        }
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}