package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.repository.interfaces.IDonationCenterRepository;
import org.example.blood_help_app.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class DonationCenterMappedRepository implements IDonationCenterRepository {

    @Override
    public Optional<DonationCenter> save(DonationCenter center) {
        if (center == null) {
            throw new IllegalArgumentException("DonationCenter cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (center.getId() != null && session.get(DonationCenter.class, center.getId()) != null) {
                    transaction.rollback();
                    return Optional.of(center);
                }
                session.persist(center);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(center);
            }
        }
    }

    @Override
    public Optional<DonationCenter> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                DonationCenter center = session.get(DonationCenter.class, id);
                if (center != null) {
                    session.remove(center);
                    transaction.commit();
                    return Optional.of(center);
                }
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<DonationCenter> update(DonationCenter updatedCenter) {
        if (updatedCenter == null) {
            throw new IllegalArgumentException("DonationCenter cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (session.get(DonationCenter.class, updatedCenter.getId()) == null) {
                    transaction.rollback();
                    return Optional.of(updatedCenter);
                }

                session.merge(updatedCenter);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(updatedCenter);
            }
        }
    }

    @Override
    public Optional<DonationCenter> find(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(DonationCenter.class, id));
        }
    }

    @Override
    public List<DonationCenter> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM DonationCenter", DonationCenter.class).getResultList();
        }
    }
}
