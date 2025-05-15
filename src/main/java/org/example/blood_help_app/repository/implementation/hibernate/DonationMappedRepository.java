package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.repository.interfaces.IDonationRepository;
import org.example.blood_help_app.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class DonationMappedRepository implements IDonationRepository {
    @Override
    public Optional<Donation> save(Donation donation) {
        if (donation == null) {
            throw new IllegalArgumentException("Donation cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Verificăm existența pentru update
                if (donation.getId() != null && session.get(Donation.class, donation.getId()) != null) {
                    transaction.rollback();
                    return Optional.of(donation);
                }

                session.persist(donation);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(donation);
            }
        }
    }

    @Override
    public Optional<Donation> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Donation donation = session.get(Donation.class, id);
                if (donation != null) {
                    session.remove(donation);
                    transaction.commit();
                    return Optional.of(donation);
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
    public Optional<Donation> update(Donation updatedDonation) {
        if (updatedDonation == null) {
            throw new IllegalArgumentException("Donation cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Verificăm existența înainte de actualizare
                if (session.get(Donation.class, updatedDonation.getId()) == null) {
                    transaction.rollback();
                    return Optional.of(updatedDonation);
                }

                session.merge(updatedDonation);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(updatedDonation);
            }
        }
    }

    @Override
    public Optional<Donation> findOne(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Donation.class, id));
        }
    }

    @Override
    public List<Donation> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Donation", Donation.class).getResultList();
        }
    }

    @Override
    public List<Donation> findUserDonations(Donor donor) {
        if (donor == null) {
            throw new IllegalArgumentException("Donor cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Donation WHERE donor = :donor ORDER BY donationDate DESC",
                            Donation.class)
                    .setParameter("donor", donor)
                    .getResultList();
        }
    }
}
