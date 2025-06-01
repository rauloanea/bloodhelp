package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.donationsdata.BloodRequest;
import org.example.blood_help_app.repository.interfaces.IBloodRequestRepository;
import org.example.blood_help_app.utils.repo_utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class BloodRequestMappedRepository implements IBloodRequestRepository {

    @Override
    public Optional<BloodRequest> save(BloodRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("BloodRequest cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (request.getId() != null && session.get(BloodRequest.class, request.getId()) != null) {
                    transaction.rollback();
                    return Optional.of(request);
                }
                session.persist(request);
                transaction.commit();
                return Optional.of(request);
            } catch (Exception e) {
                transaction.rollback();
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<BloodRequest> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                BloodRequest request = session.get(BloodRequest.class, id);
                if (request != null) {
                    session.remove(request);
                    transaction.commit();
                    return Optional.of(request);
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
    public Optional<BloodRequest> update(BloodRequest updatedRequest) {
        if (updatedRequest == null) {
            throw new IllegalArgumentException("BloodRequest cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (session.get(BloodRequest.class, updatedRequest.getId()) == null) {
                    transaction.rollback();
                    return Optional.of(updatedRequest);
                }
                session.merge(updatedRequest);
                transaction.commit();
                return Optional.of(updatedRequest);
            } catch (Exception e) {
                transaction.rollback();
                return Optional.of(updatedRequest);
            }
        }
    }

    @Override
    public Optional<BloodRequest> find(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(BloodRequest.class, id));
        }
    }

    @Override
    public List<BloodRequest> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM BloodRequest", BloodRequest.class).getResultList();
        }
    }

    @Override
    public List<BloodRequest> findByStatus(String status) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM BloodRequest WHERE status = :status", BloodRequest.class)
                    .setParameter("status", status)
                    .getResultList();
        }
    }

    @Override
    public List<BloodRequest> findByDoctorId(Long doctorId) {
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM BloodRequest WHERE requestingDoctor.id = :doctorId", BloodRequest.class)
                    .setParameter("doctorId", doctorId)
                    .getResultList();
        }
    }

    @Override
    public List<BloodRequest> findByCenterId(Long centerId) {
        if (centerId == null) {
            throw new IllegalArgumentException("Center ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM BloodRequest WHERE destinationCenter.id = :centerId", BloodRequest.class)
                    .setParameter("centerId", centerId)
                    .getResultList();
        }
    }

    @Override
    public List<BloodRequest> findByBloodType(String bloodType) {
        if (bloodType == null || bloodType.isEmpty()) {
            throw new IllegalArgumentException("Blood type cannot be null or empty");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM BloodRequest WHERE bloodType = :bloodType", BloodRequest.class)
                    .setParameter("bloodType", bloodType)
                    .getResultList();
        }
    }
}