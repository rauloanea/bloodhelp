package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.users.Admin;
import org.example.blood_help_app.domain.enums.AccessLevelEnum;
import org.example.blood_help_app.repository.interfaces.IAdminRepository;
import org.example.blood_help_app.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class AdminMappedRepository implements IAdminRepository {
    @Override
    public Optional<Admin> save(Admin entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (entity.getId() != null && session.get(Admin.class, entity.getId()) != null) {
                    transaction.rollback();
                    return Optional.of(entity); // deja există
                }
                session.persist(entity);
                transaction.commit();
                return Optional.empty(); // succes
            } catch (Exception e) {
                transaction.rollback();
                e.printStackTrace();
                return Optional.of(entity); // eșec
            }
        }
    }

    @Override
    public Optional<Admin> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Admin admin = session.get(Admin.class, id);
                if (admin != null) {
                    session.remove(admin);
                    transaction.commit();
                    return Optional.of(admin);
                }
                transaction.rollback();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                e.printStackTrace();
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Admin> update(Admin updatedEntity) {
        if (updatedEntity == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (session.get(Admin.class, updatedEntity.getId()) == null) {
                    transaction.rollback();
                    return Optional.of(updatedEntity); // nu există
                }
                session.merge(updatedEntity);
                transaction.commit();
                return Optional.empty(); // succes
            } catch (Exception e) {
                transaction.rollback();
                e.printStackTrace();
                return Optional.of(updatedEntity); // eșec
            }
        }
    }

    @Override
    public Optional<Admin> find(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Admin.class, id));
        }
    }

    @Override
    public List<Admin> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Admin", Admin.class).getResultList();
        }
    }

    @Override
    public Optional<Admin> findByAccessLevel(AccessLevelEnum accessLevel) {
        if (accessLevel == null) {
            throw new IllegalArgumentException("Access level cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Admin WHERE accessLevel = :accessLevel", Admin.class)
                    .setParameter("accessLevel", accessLevel)
                    .uniqueResultOptional();
        }
    }
}
