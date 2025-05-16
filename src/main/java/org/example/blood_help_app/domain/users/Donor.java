package org.example.blood_help_app.domain.users;

import jakarta.persistence.*;
import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.enums.UserTypeEnum;
import org.example.blood_help_app.domain.users.utils.AppUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@jakarta.persistence.Entity
@Table(name = "donors")
@DiscriminatorValue("DONOR")
public class Donor extends User implements AppUser {
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type")
    private BloodTypeEnum bloodType;

    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Donation> donationHistory = new ArrayList<>();

    @Column(name = "eligible")
    private Integer eligible;

    @Column(name = "contact_info")
    private String contactInfo;;

    public Donor(String name, String email, String username, String password, String phoneNumber, LocalDateTime birthdayDate, BloodTypeEnum bloodType, List<Donation> donationHistory, Integer eligible, String contactInfo) {
        super(name, email, username, password, phoneNumber, birthdayDate, UserTypeEnum.DONOR);
        this.bloodType = bloodType;
        this.donationHistory = donationHistory;
        this.eligible = eligible;
        this.contactInfo = contactInfo;
    }

    public Donor(User user, BloodTypeEnum bloodType, List<Donation> donationHistory, Integer eligible, String contactInfo){
        super(user.name, user.email, user.username, user.password, user.phoneNumber, user.birthdayDate, user.userType);
        id = user.getId();
        this.bloodType = bloodType;
        this.donationHistory = donationHistory;
        this.eligible = eligible;
        this.contactInfo = contactInfo;
    }

    public Donor(){}

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
    }

    public List<Donation> getDonationHistory() {
        return donationHistory;
    }

    public void setDonationHistory(List<Donation> donationHistory) {
        this.donationHistory = donationHistory;
    }

    public Integer getEligibility() {
        return eligible;
    }

    public void setEligibility(Integer eligible) {
        this.eligible = eligible;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
