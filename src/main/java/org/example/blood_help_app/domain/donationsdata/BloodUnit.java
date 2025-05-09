package org.example.blood_help_app.domain.donationsdata;

import org.example.blood_help_app.domain.Entity;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.enums.BloodUnitStatusEnum;

import java.time.LocalDateTime;

public class BloodUnit extends Entity<Long> {
    private BloodTypeEnum bloodType;
    private LocalDateTime expirationDate;
    private BloodUnitStatusEnum status;
    private Donation donation;

    public BloodUnit(BloodTypeEnum bloodType, LocalDateTime expirationDate, BloodUnitStatusEnum status, Donation donation) {
        this.bloodType = bloodType;
        this.expirationDate = expirationDate;
        this.status = status;
        this.donation = donation;
    }

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
}
