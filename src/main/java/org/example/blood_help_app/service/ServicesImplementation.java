package org.example.blood_help_app.service;

import org.example.blood_help_app.domain.donationsdata.*;
import org.example.blood_help_app.domain.enums.AppointmentStatus;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.enums.RequestStatus;
import org.example.blood_help_app.domain.users.*;
import org.example.blood_help_app.domain.users.utils.AppUser;
import org.example.blood_help_app.repository.interfaces.*;
import org.example.blood_help_app.utils.observer.IObservable;
import org.example.blood_help_app.utils.observer.IObserver;
import org.example.blood_help_app.utils.password_encryption.PasswordEncryption;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ServicesImplementation implements IObservable {
    private final List<IObserver> observers = new ArrayList<>();

    private final IUserRepository userRepo;
    private final IAdminRepository adminRepo;
    private final IDonorRepository donorRepo;
    private final IDoctorRepository doctorRepo;
    private final IDonationRepository donationRepo;
    private final IDonationCenterRepository donationCenterRepo;
    private final IBloodRequestRepository bloodRequestRepo;
    private final IAppointmentRepository appointmentRepo;
    private final IBloodUnitRepository bloodUnitRepo;

    public ServicesImplementation(
            IUserRepository userRepo,
            IAdminRepository adminRepo,
            IDonorRepository donorRepo,
            IDoctorRepository doctorRepo,
            IDonationRepository donationRepo,
            IDonationCenterRepository donationCenterRepo,
            IBloodRequestRepository bloodRequestRepo,
            IAppointmentRepository appointmentRepo,
            IBloodUnitRepository bloodUnitRepo
    ) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
        this.donorRepo = donorRepo;
        this.doctorRepo = doctorRepo;
        this.donationRepo = donationRepo;
        this.donationCenterRepo = donationCenterRepo;
        this.bloodRequestRepo = bloodRequestRepo;
        this.appointmentRepo = appointmentRepo;
        this.bloodUnitRepo = bloodUnitRepo;
    }

    @Override
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(IObserver::update);
    }

    public void createDonorAccount(User user){
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

        if (value == 2) {
            notifyObservers();
        }
    }

    public void checkEligibility(Donor user, Integer age, String gender, Double weight, LocalDateTime lastDonation, String otherInfo) {
        if (lastDonation != null) {
            long monthsBetween = ChronoUnit.MONTHS.between(lastDonation, LocalDateTime.now());
            if (monthsBetween < 3) {
                throw new RuntimeException("Trebuie să așteptați 3 luni între donații!");
            }
        }

        if(weight < 50)
            throw new RuntimeException("Nu poti dona deoarece ai o greutate prea mica!");

        if(age < 18 || age > 65)
            throw new RuntimeException("Nu poti dona deoarece nu indeplinesti criteriie de varsta!");

        String lowerOtherInfo = otherInfo.toLowerCase();
        if (gender.equalsIgnoreCase("Feminin")) {
            if (lowerOtherInfo.contains("sarcina") || lowerOtherInfo.contains("alaptare")) {
                throw new RuntimeException("Femeile gravide sau care alăptează nu pot dona sânge!");
            }
            if (lowerOtherInfo.contains("menstruatie")) {
                throw new RuntimeException("Nu recomandăm donarea în timpul menstruației!");
            }
        }

        // cazuri generale
        if (lowerOtherInfo.contains("hiv") || lowerOtherInfo.contains("hepatita")
                || lowerOtherInfo.contains("boli")) {
            throw new RuntimeException("Nu poți dona din cauza unor probleme de sănătate!");
        }
        if (lowerOtherInfo.contains("tatuaj") || lowerOtherInfo.contains("piercing")) {
            throw new RuntimeException("Trebuie să aștepți 6 luni după tatuaj/piercing!");
        }

        this.setDonorEligibility(user, 1);
    }

    public List<DonationCenter> getDonationCenters(){
        return this.donationCenterRepo.findAll();
    }

    public void makeAppointment(Donor user, DonationCenter center, LocalDateTime appointmentDateTime, AppointmentStatus status) {
        Appointment appointment = new Appointment(user, appointmentDateTime, center, status);

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

    public Donor updateUser(Donor user, String newUsername, String newPhoneNumber, String newPassword){
        try{
            if(newUsername != null && !newUsername.isEmpty())
                user.setUsername(newUsername);

            if(newPhoneNumber != null && !newPhoneNumber.isEmpty())
                user.setContactInfo(newPhoneNumber);

            if(newPassword != null && !newPassword.isEmpty())
                user.setPassword(PasswordEncryption.encryptPassword(newPassword));

            var result = this.userRepo.update(user);
            if(result.isEmpty())
                return user;
        }catch (Exception e){
            throw new RuntimeException("Eroare la salvarea modificarilor!");
        }

        throw new RuntimeException("Eroare la salvarea modificarilor!");
    }

    public List<Donor> getDonorsWithEligibility(int eligibility) {
        return donorRepo.findAll().stream()
                .filter(d -> d.getEligibility() == eligibility)
                .toList();
    }

    public Appointment findEligibilityAppointment(Donor donor){
        return appointmentRepo.findEligibilityAppointment(donor)
                .orElseThrow(() -> new RuntimeException("Eroare la gasirea programarii"));
    }

    public void createBloodRequest(BloodTypeEnum bloodType, int currentQuantity, Doctor doctor, DonationCenter selectedCenter, String notes) {
        var newBloodRequest = new BloodRequest(bloodType, currentQuantity, doctor, selectedCenter, RequestStatus.PENDING, notes);
        bloodRequestRepo.save(newBloodRequest);
    }

    public List<BloodRequest> getAllBloodRequests() {
        return bloodRequestRepo.findAll();
    }

    public List<BloodUnit> getAllBloodUnits(){
        return bloodUnitRepo.findAll();
    }

    public void updateBloodRequest(BloodRequest bloodRequest) {
        bloodRequestRepo.update(bloodRequest);
    }

    public void subtractBloodUnits(BloodTypeEnum bloodType, int quantity){
        bloodUnitRepo.deleteFirstNUnits(bloodType, quantity);
    }
}
