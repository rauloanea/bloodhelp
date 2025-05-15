package org.example.blood_help_app.domain.donationsdata;

import jakarta.persistence.*;
import org.example.blood_help_app.domain.Entity;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.enums.BloodUnitStatusEnum;

import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "blood_units")
public class BloodUnit extends Entity<Long> {
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type")
    private BloodTypeEnum bloodType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column
    private BloodUnitStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id")
    private Donation donation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_center_id", nullable = false)
    private DonationCenter donationCenter;

    public BloodUnit(BloodTypeEnum bloodType, LocalDateTime expirationDate, BloodUnitStatusEnum status, Donation donation, DonationCenter donationCenter) {
        this.bloodType = bloodType;
        this.expirationDate = expirationDate;
        this.status = status;
        this.donation = donation;
        this.donationCenter = donationCenter;
    }

    public BloodUnit() {}

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BloodUnitStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BloodUnitStatusEnum status) {
        this.status = status;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public DonationCenter getDonationCenter() {
        return donationCenter;
    }

    public void setDonationCenter(DonationCenter donationCenter) {
        this.donationCenter = donationCenter;
    }
}
