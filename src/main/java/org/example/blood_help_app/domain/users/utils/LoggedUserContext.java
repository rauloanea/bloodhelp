package org.example.blood_help_app.domain.users.utils;

import org.example.blood_help_app.domain.users.Admin;
import org.example.blood_help_app.domain.users.Doctor;
import org.example.blood_help_app.domain.users.Donor;

import java.util.Optional;

public class LoggedUserContext {
    private final AppUser user;

    public LoggedUserContext(AppUser user) {
        this.user = user;
    }

    public boolean isDonor() {
        return user instanceof Donor;
    }

    public boolean isDoctor() {
        return user instanceof Doctor;
    }

    public boolean isAdmin() {
        return user instanceof Admin;
    }

    public Optional<Donor> asDonor() {
        return user instanceof Donor d ? Optional.of(d) : Optional.empty();
    }

    public Optional<Doctor> asDoctor() {
        return user instanceof Doctor d ? Optional.of(d) : Optional.empty();
    }

    public Optional<Admin> asAdmin() {
        return user instanceof Admin a ? Optional.of(a) : Optional.empty();
    }

    public AppUser getUser() {
        return user;
    }
}
