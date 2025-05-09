package org.example.blood_help_app.domain.users;

import org.example.blood_help_app.domain.enums.SpecializationEnum;
import org.example.blood_help_app.domain.enums.UserTypeEnum;

import java.time.LocalDateTime;

public class Doctor extends User{
    private SpecializationEnum specialization;
    private String institution;

    public Doctor(String name, String email, String username, String password, String phoneNumber, LocalDateTime birthdayDate, SpecializationEnum specialization, String institution) {
        super(name, email, username, password, phoneNumber, birthdayDate, UserTypeEnum.DOCTOR);
        this.specialization = specialization;
        this.institution = institution;
    }

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
