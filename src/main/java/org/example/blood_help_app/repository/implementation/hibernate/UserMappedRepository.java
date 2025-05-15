package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.users.User;
import org.example.blood_help_app.repository.interfaces.IUserRepository;
import org.example.blood_help_app.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserMappedRepository implements IUserRepository {

    @Override
    public Optional<User> save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        try (var session = HibernateUtils.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            try {
                if (entity.getId() != null && session.get(User.class, entity.getId()) != null) {
                    transaction.rollback();
                    return Optional.of(entity);
                }

                session.persist(entity);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(entity);
            }
        }
    }

    @Override
    public Optional<User> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (var session = HibernateUtils.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                if (user != null) {
                    session.remove(user);
                    transaction.commit();
                    return Optional.of(user);
                }
                transaction.rollback();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<User> update(User updatedEntity) {
        if (updatedEntity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        try (var session = HibernateUtils.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            try {
                // Verificăm dacă entitatea există
                if (session.get(User.class, updatedEntity.getId()) == null) {
                    transaction.rollback();
                    return Optional.of(updatedEntity);
                }

                session.merge(updatedEntity);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(updatedEntity);
            }
        }
    }

    @Override
    public Optional<User> findOne(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (var session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        }
    }

    @Override
    public List<User> findAll() {
        try (var session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();
        }
    }

    @Override
    public Optional<User> findByCredentials(String email, String encryptedPassword) {
        try (var session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM User WHERE email = :email AND password = :password", User.class)
                    .setParameter("email", email)
                    .setParameter("password", encryptedPassword)
                    .uniqueResultOptional();
        }
    }
}
