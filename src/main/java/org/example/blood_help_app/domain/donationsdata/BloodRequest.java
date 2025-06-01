package org.example.blood_help_app.domain.donationsdata;

import jakarta.persistence.*;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.enums.RequestStatus;
import org.example.blood_help_app.domain.generic.Entity;
import org.example.blood_help_app.domain.users.Doctor;

@jakarta.persistence.Entity
@Table(name = "blood_requests")
public class BloodRequest extends Entity<Integer> {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "blood_type")
    private BloodTypeEnum bloodType;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor requestingDoctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "center_id", nullable = false)
    private DonationCenter destinationCenter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(length = 500)
    private String notes;

    public BloodRequest() {}

    public BloodRequest(final BloodTypeEnum bloodType, final Integer quantity, final Doctor requestingDoctor, final DonationCenter destinationCenter, final RequestStatus status, final String notes) {
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.requestingDoctor = requestingDoctor;
        this.destinationCenter = destinationCenter;
        this.status = status;
        this.notes = notes;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Doctor getRequestingDoctor() {
        return requestingDoctor;
    }

    public void setRequestingDoctor(Doctor requestingDoctor) {
        this.requestingDoctor = requestingDoctor;
    }

    public DonationCenter getDestinationCenter() {
        return destinationCenter;
    }

    public void setDestinationCenter(DonationCenter destinationCenter) {
        this.destinationCenter = destinationCenter;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
