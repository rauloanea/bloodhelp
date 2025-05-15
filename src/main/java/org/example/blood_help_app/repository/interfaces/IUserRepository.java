package org.example.blood_help_app.repository.interfaces;

import org.example.blood_help_app.domain.users.User;

import java.util.Optional;

public interface IUserRepository extends IRepository<Long, User> {
    Optional<User> findByCredentials(String email, String encryptedPassword);
}
