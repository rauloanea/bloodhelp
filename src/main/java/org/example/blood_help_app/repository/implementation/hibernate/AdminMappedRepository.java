package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.enums.AccessLevelEnum;
import org.example.blood_help_app.domain.users.Admin;
import org.example.blood_help_app.repository.interfaces.IAdminRepository;

import java.util.List;
import java.util.Optional;

public class AdminMappedRepository implements IAdminRepository {
    @Override
    public Optional<Admin> findByAccessLevel(AccessLevelEnum accessLevel) {
        return Optional.empty();
    }

    @Override
    public Optional<Admin> save(Admin entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Admin> delete(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Optional<Admin> update(Admin updatedEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<Admin> findOne(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<Admin> findAll() {
        return List.of();
    }
}
