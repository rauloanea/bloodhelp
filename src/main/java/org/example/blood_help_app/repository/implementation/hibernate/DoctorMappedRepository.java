package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.users.Doctor;
import org.example.blood_help_app.repository.interfaces.IDoctorRepository;
import org.example.blood_help_app.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class DoctorMappedRepository implements IDoctorRepository {

    @Override
    public Optional<Doctor> save(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (doctor.getId() != null && session.get(Doctor.class, doctor.getId()) != null) {
                    transaction.rollback();
                    return Optional.of(doctor); // doctor deja există
                }
                session.persist(doctor);
                transaction.commit();
                return Optional.empty(); // success
            } catch (Exception e) {
                transaction.rollback();
                e.printStackTrace();
                return Optional.of(doctor); // failed
            }
        }
    }

    @Override
    public Optional<Doctor> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Doctor doctor = session.get(Doctor.class, id);
                if (doctor != null) {
                    session.remove(doctor);
                    transaction.commit();
                    return Optional.of(doctor);
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
    public Optional<Doctor> update(Doctor updatedDoctor) {
        if (updatedDoctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (session.get(Doctor.class, updatedDoctor.getId()) == null) {
                    transaction.rollback();
                    return Optional.of(updatedDoctor); // doctor nu există
                }
                session.merge(updatedDoctor);
                transaction.commit();
                return Optional.empty(); // update reușit
            } catch (Exception e) {
                transaction.rollback();
                e.printStackTrace();
                return Optional.of(updatedDoctor); // eroare la update
            }
        }
    }

    @Override
    public Optional<Doctor> find(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Doctor.class, id));
        }
    }

    @Override
    public List<Doctor> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Doctor", Doctor.class).getResultList();
        }
    }
}
