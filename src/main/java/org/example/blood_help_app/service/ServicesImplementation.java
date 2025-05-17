package org.example.blood_help_app.service;

import org.example.blood_help_app.domain.donationsdata.Appointment;
import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.enums.AppointmentStatus;
import org.example.blood_help_app.domain.users.*;
import org.example.blood_help_app.domain.users.utils.AppUser;
import org.example.blood_help_app.repository.interfaces.*;
import org.example.blood_help_app.utils.PasswordEncryption;

import java.time.LocalDateTime;
import java.util.List;

public class ServicesImplementation {
    private final IUserRepository userRepo;
    private final IAdminRepository adminRepo;
    private final IDonorRepository donorRepo;
    private final IDoctorRepository doctorRepo;
    private final IDonationRepository donationRepo;
    private final IDonationCenterRepository donationCenterRepo;
    private final IBloodUnitRepository bloodUnitRepo;
    private final IAppointmentRepository appointmentRepo;

    public ServicesImplementation(
            IUserRepository userRepo,
            IAdminRepository adminRepo,
            IDonorRepository donorRepo,
            IDoctorRepository doctorRepo,
            IDonationRepository donationRepo,
            IDonationCenterRepository donationCenterRepo,
            IBloodUnitRepository bloodUnitRepo,
            IAppointmentRepository appointmentRepo
    ) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
        this.donorRepo = donorRepo;
        this.doctorRepo = doctorRepo;
        this.donationRepo = donationRepo;
        this.donationCenterRepo = donationCenterRepo;
        this.bloodUnitRepo = bloodUnitRepo;
        this.appointmentRepo = appointmentRepo;
    }

    public Donor createDonorAccount(User user){
        user.setPassword(PasswordEncryption.encryptPassword(user.getPassword()));
        var result = this.userRepo.save(user);
        if(result.isEmpty())
            throw new RuntimeException("Eroare la creearea noului utilizator!");

        var donor = this.donorRepo.save(new Donor(
                user,
                null, null, -1, null
        ));
        if(donor.isEmpty())
            throw new RuntimeException("Eroare la creearea noului donator!");
        return donor.get();
    }

    public AppUser authenticateAccount(String email, String password) {
        var result = this.userRepo.findByCredentials(email, PasswordEncryption.encryptPassword(password));
        if (result.isEmpty())
            throw new RuntimeException("Email sau parola incorecte!");

        User user = result.get();

        switch (user.getUserType()) {
            case DONOR -> {
                var donor = this.donorRepo.find(user.getId());
                return donor.orElseThrow(() -> new RuntimeException("Eroare la preluarea informatiilor despre donator!"));
            }
            case DOCTOR -> {
                var doctor = this.doctorRepo.find(user.getId());
                return doctor.orElseThrow(() -> new RuntimeException("Eroare la preluarea informatiilor despre doctor!"));
            }
            case ADMIN -> {
                var admin = this.adminRepo.find(user.getId());
                return admin.orElseThrow(() -> new RuntimeException("Eroare la preluarea informatiilor despre admin!"));
            }
            default -> throw new RuntimeException("Tip de utilizator necunoscut!");
        }
    }


    public void setDonorEligibility(Donor user, Integer value){
        user.setEligibility(value);
        this.donorRepo.update(user);
    }

    public void checkEligibility(Donor user, Integer age, String gender, Double weight, LocalDateTime lastDonation, String otherInfo) {
        if(lastDonation != null && lastDonation.getYear() == LocalDateTime.now().getYear()
                && LocalDateTime.now().getMonth().getValue() - lastDonation.getMonth().getValue() < 4)
            throw new RuntimeException("Nu poti dona deoarece ai facut o donatie in ultimele 3 luni!");
    }

    public List<DonationCenter> getCenters(){
        return this.donationCenterRepo.findAll();
    }

    public void makeAppointment(Donor user, DonationCenter center, LocalDateTime appointmentDateTime) {
        Appointment appointment = new Appointment(user, appointmentDateTime, center, AppointmentStatus.SCHEDULED);

        if(this.appointmentRepo.checkDisponibility(center.getId(), appointmentDateTime).isPresent())
            throw new RuntimeException("Exista deja o programare la centrul " + center.getName() + " la data si ora selectata!");

        var result = this.appointmentRepo.save(appointment);
        if(result.isPresent()){
            throw new RuntimeException("Problema la salvarea programarii! Reincearca!");
        }
    }

    public List<Appointment> findUserAppointments(Donor user){
        return this.appointmentRepo.findFutureAppointmentsChronological(user);
    }

    public void cancelAppointment(Appointment appointment){
        try {
            this.appointmentRepo.delete(appointment.getId());
        }catch (Exception e){
            throw new RuntimeException("Eroare la stergerea programarii din baza de date!");
        }
    }

    public List<Donation> findUserDonations(Donor user){
        return this.donationRepo.findUserDonations(user);
    }

    public DonationCenter getDonationCenterByID(Integer id){
        var result = this.donationCenterRepo.find(id);
        return result.orElse(null);
    }
}
