package org.example.blood_help_app.domain.donationsdata;

import jakarta.persistence.*;
import org.example.blood_help_app.domain.Entity;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.enums.DonationStatusEnum;
import org.example.blood_help_app.domain.users.Doctor;
import org.example.blood_help_app.domain.users.Donor;

import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "donations")
public class Donation extends Entity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id")
    private Donor donor;

    @Column(name = "donation_date")
    private LocalDateTime donationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type")
    private BloodTypeEnum bloodType;

    @Column
    private double quantity;

    @Column(name = "donation_center_id")
    private int donationCenterID;

    @Enumerated(EnumType.STRING)
    @Column
    private DonationStatusEnum status;

    @Column(name = "test_results")
    private String testResults;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    public Donation() {}

    public Donation(Donor donor, LocalDateTime donationDate, BloodTypeEnum bloodType, double quantity, int donationCenterID, DonationStatusEnum status, String testResults, Doctor doctor) {
        this.donor = donor;
        this.donationDate = donationDate;
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.donationCenterID = donationCenterID;
        this.status = status;
        this.testResults = testResults;
        this.doctor = doctor;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public LocalDateTime getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDateTime donationDate) {
        this.donationDate = donationDate;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getDonationCenter() {
        return donationCenterID;
    }

    public void setDonationCenter(int donationCenterID) {
        this.donationCenterID = donationCenterID;
    }

    public DonationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DonationStatusEnum status) {
        this.status = status;
    }

    public String getTestResults() {
        return testResults;
    }

    public void setTestResults(String testResults) {
        this.testResults = testResults;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
