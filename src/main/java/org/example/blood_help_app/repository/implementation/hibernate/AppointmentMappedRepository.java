package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.donationsdata.Appointment;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.repository.interfaces.IAppointmentRepository;
import org.example.blood_help_app.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AppointmentMappedRepository implements IAppointmentRepository {

    @Override
    public Optional<Appointment> save(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (appointment.getId() != null && session.get(Appointment.class, appointment.getId()) != null) {
                    transaction.rollback();
                    return Optional.of(appointment);
                }
                session.persist(appointment);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(appointment);
            }
        }
    }

    @Override
    public Optional<Appointment> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Appointment appointment = session.get(Appointment.class, id);
                if (appointment != null) {
                    session.remove(appointment);
                    transaction.commit();
                    return Optional.of(appointment);
                }
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Appointment> update(Appointment updatedAppointment) {
        if (updatedAppointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (session.get(Appointment.class, updatedAppointment.getId()) == null) {
                    transaction.rollback();
                    return Optional.of(updatedAppointment);
                }
                session.merge(updatedAppointment);
                transaction.commit();
                return Optional.empty();
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(updatedAppointment);
            }
        }
    }

    @Override
    public Optional<Appointment> findOne(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Appointment.class, id));
        }
    }

    @Override
    public List<Appointment> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Appointment", Appointment.class).getResultList();
        }
    }

    @Override
    public Optional<Appointment> checkDisponibility(Integer centerId, LocalDateTime date) {
        if (centerId == null || date == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Appointment WHERE donationCenter.id = :centerId AND date = :date",
                            Appointment.class)
                    .setParameter("centerId", centerId)
                    .setParameter("date", date)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Appointment> findFutureAppointmentsChronological(Donor donor) {
        if (donor == null) {
            throw new IllegalArgumentException("Donor cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Appointment WHERE donor = :donor AND date >= :now ORDER BY date ASC",
                            Appointment.class)
                    .setParameter("donor", donor)
                    .setParameter("now", LocalDateTime.now())
                    .getResultList();
        }
    }
}