package org.example.blood_help_app.domain.users;

import jakarta.persistence.*;
import org.example.blood_help_app.domain.donationsdata.Appointment;
import org.example.blood_help_app.domain.enums.SpecializationEnum;
import org.example.blood_help_app.domain.enums.UserTypeEnum;

import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "doctors")
@DiscriminatorValue("DOCTOR")
public class Doctor extends User implements AppUser {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "TEXT DEFAULT 'GENERAL'")
    private SpecializationEnum specialization;

    @Column
    private String institution;

    public Doctor(String name, String email, String username, String password, String phoneNumber, LocalDateTime birthdayDate, SpecializationEnum specialization, String institution) {
        super(name, email, username, password, phoneNumber, birthdayDate, UserTypeEnum.DOCTOR);
        this.specialization = specialization;
        this.institution = institution;
    }

    public Doctor() {}

    public SpecializationEnum getSpecialization() {
        return specialization;
    }

    public void setSpecialization(SpecializationEnum specialization) {
        this.specialization = specialization;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }
}
