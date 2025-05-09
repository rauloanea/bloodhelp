package org.example.blood_help_app.domain.donationsdata;

import org.example.blood_help_app.domain.Entity;
import org.example.blood_help_app.domain.enums.AppointmentStatus;
import org.example.blood_help_app.domain.users.Donor;

import java.time.LocalDateTime;

public class Appointment extends Entity<Long> {
    private Donor donor;
    private LocalDateTime date;
    private DonationCenter donationCenter;
    private AppointmentStatus status;

    public Appointment(Donor donor, LocalDateTime date, DonationCenter donationCenter, AppointmentStatus status) {
        this.donor = donor;
        this.date = date;
        this.donationCenter = donationCenter;
        this.status = status;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public DonationCenter getDonationCenter() {
        return donationCenter;
    }

    public void setDonationCenter(DonationCenter donationCenter) {
        this.donationCenter = donationCenter;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
