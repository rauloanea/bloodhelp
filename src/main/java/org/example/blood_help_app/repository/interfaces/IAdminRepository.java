package org.example.blood_help_app.repository.interfaces;

import org.example.blood_help_app.domain.enums.AccessLevelEnum;
import org.example.blood_help_app.domain.users.Admin;

import java.util.Optional;

public interface IAdminRepository extends IRepository<Integer, Admin> {
    Optional<Admin> findByAccessLevel(AccessLevelEnum accessLevel);
}
