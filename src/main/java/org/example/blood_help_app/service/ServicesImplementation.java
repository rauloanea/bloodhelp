package org.example.blood_help_app.service;

import org.example.blood_help_app.domain.donationsdata.Appointment;
import org.example.blood_help_app.domain.donationsdata.BloodUnit;
import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.users.Admin;
import org.example.blood_help_app.domain.users.Doctor;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.domain.users.User;
import org.example.blood_help_app.repository.implementation.UserRepository;
import org.example.blood_help_app.repository.interfaces.IRepository;
import org.example.blood_help_app.utils.PasswordEncryption;

public class ServicesImplementation {
    private UserRepository userRepo;
    private IRepository<Long, Admin> adminRepo;
    private IRepository<Long, Donor> donorRepo;
    private IRepository<Long, Doctor> doctorRepo;
    private IRepository<Long, Donation> donationRepo;
    private IRepository<Integer, DonationCenter> donationCenterRepo;
    private IRepository<Long, BloodUnit> bloodUnitRepo;
    private IRepository<Long, Appointment> appointmentRepo;

    public ServicesImplementation(
            UserRepository userRepo,
            IRepository<Long, Admin> adminRepo,
            IRepository<Long, Donor> donorRepo,
            IRepository<Long, Doctor> doctorRepo,
            IRepository<Long, Donation> donationRepo,
            IRepository<Integer, DonationCenter> donationCenterRepo,
            IRepository<Long, BloodUnit> bloodUnitRepo,
            IRepository<Long, Appointment> appointmentRepo
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

    public Donor authenticateAccount(String email, String password){
        var result = this.userRepo.findByCredentials(email, PasswordEncryption.encryptPassword(password));
        if(result.isEmpty())
            throw new RuntimeException("Email sau parola incorecte!");

        var donor = this.donorRepo.findOne(result.get().getId());
        if(donor.isEmpty())
            throw new RuntimeException("Email sau parola incorecte!");
        return donor.get();
    }

    public void setDonorEligibility(Donor user, Integer value){
        user.setEligibility(value);
        this.donorRepo.update(user);
    }
}
