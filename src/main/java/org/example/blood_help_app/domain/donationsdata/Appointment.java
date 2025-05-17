package org.example.blood_help_app.domain.donationsdata;

import jakarta.persistence.*;
import org.example.blood_help_app.domain.generic.Entity;
import org.example.blood_help_app.domain.enums.AppointmentStatus;
import org.example.blood_help_app.domain.users.Donor;

import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "appointments")
public class Appointment extends Entity<Integer> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donation_center_id", nullable = false)
    private DonationCenter donationCenter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    public Appointment(Donor donor, LocalDateTime date, DonationCenter donationCenter, AppointmentStatus status) {
        this.donor = donor;
        this.date = date;
        this.donationCenter = donationCenter;
        this.status = status;
    }

    public Appointment(){}

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
