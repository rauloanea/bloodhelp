package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.users.Doctor;
import org.example.blood_help_app.repository.interfaces.IDoctorRepository;

import java.util.List;
import java.util.Optional;

public class DoctorMappedRepository implements IDoctorRepository {

    @Override
    public Optional<Doctor> save(Doctor entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Doctor> delete(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Optional<Doctor> update(Doctor updatedEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<Doctor> find(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<Doctor> findAll() {
        return List.of();
    }
}
