package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.repository.interfaces.IDonorRepository;
import org.example.blood_help_app.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class DonorMappedRepository implements IDonorRepository {
    @Override
    public Optional<Donor> save(Donor donor) {
        if (donor == null) {
            throw new IllegalArgumentException("Donor cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Verificăm dacă există deja (pentru update)
                if (donor.getId() != null && session.get(Donor.class, donor.getId()) != null) {
                    transaction.rollback();
                    return Optional.of(donor);
                }

                session.persist(donor);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(donor);
            }
        }
    }

    @Override
    public Optional<Donor> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Donor donor = session.get(Donor.class, id);
                if (donor != null) {
                    session.remove(donor);
                    transaction.commit();
                    return Optional.of(donor);
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
    public Optional<Donor> update(Donor updatedDonor) {
        if (updatedDonor == null) {
            throw new IllegalArgumentException("Donor cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Verificăm existența înainte de actualizare
                if (session.get(Donor.class, updatedDonor.getId()) == null) {
                    transaction.rollback();
                    return Optional.of(updatedDonor);
                }

                Donor mergedDonor = session.merge(updatedDonor);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(updatedDonor);
            }
        }
    }

    @Override
    public Optional<Donor> findOne(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Donor.class, id));
        }
    }

    @Override
    public List<Donor> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Donor", Donor.class).getResultList();
        }
    }

}