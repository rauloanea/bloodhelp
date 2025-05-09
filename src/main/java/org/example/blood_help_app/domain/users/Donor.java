package org.example.blood_help_app.domain.users;

import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.enums.UserTypeEnum;

import java.time.LocalDateTime;
import java.util.List;

public class Donor extends User {
    private BloodTypeEnum bloodType;
    private List<Donation> donationHistory;
    private Boolean eligible;
    private String contactInfo;

    public Donor(String name, String email, String username, String password, String phoneNumber, LocalDateTime birthdayDate, BloodTypeEnum bloodType, List<Donation> donationHistory, Boolean eligible, String contactInfo) {
        super(name, email, username, password, phoneNumber, birthdayDate, UserTypeEnum.DONOR);
        this.bloodType = bloodType;
        this.donationHistory = donationHistory;
        this.eligible = eligible;
        this.contactInfo = contactInfo;
    }

    public Donor(User user, BloodTypeEnum bloodType, List<Donation> donationHistory, Boolean eligible, String contactInfo){
        super(user.name, user.email, user.username, user.password, user.phoneNumber, user.birthdayDate, user.userType);
        id = user.getId();
        this.bloodType = bloodType;
        this.donationHistory = donationHistory;
        this.eligible = eligible;
        this.contactInfo = contactInfo;
    }

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

    public Boolean getEligibility() {
        return eligible;
    }

    public void setEligibility(Boolean eligible) {
        this.eligible = eligible;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
