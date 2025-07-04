package org.example.blood_help_app.repository.implementation.hibernate;

import org.example.blood_help_app.domain.donationsdata.BloodUnit;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.enums.BloodUnitStatusEnum;
import org.example.blood_help_app.repository.interfaces.IBloodUnitRepository;
import org.example.blood_help_app.utils.repo_utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class BloodUnitMappedRepository implements IBloodUnitRepository {

    @Override
    public Optional<BloodUnit> save(BloodUnit entity) {
        if (entity == null) {
            throw new IllegalArgumentException("BloodUnit cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (entity.getId() != null && session.get(BloodUnit.class, entity.getId()) != null) {
                    transaction.rollback();
                    return Optional.of(entity);
                }
                session.persist(entity);
                transaction.commit();
                return Optional.of(entity);
            } catch (Exception e) {
                transaction.rollback();
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<BloodUnit> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                BloodUnit bloodUnit = session.get(BloodUnit.class, id);
                if (bloodUnit != null) {
                    session.remove(bloodUnit);
                    transaction.commit();
                    return Optional.of(bloodUnit);
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
    public Optional<BloodUnit> update(BloodUnit updatedEntity) {
        if (updatedEntity == null) {
            throw new IllegalArgumentException("Updated BloodUnit cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                BloodUnit existing = session.get(BloodUnit.class, updatedEntity.getId());
                if (existing != null) {
                    session.merge(updatedEntity);
                    transaction.commit();
                    return Optional.of(updatedEntity);
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
    public Optional<BloodUnit> find(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(BloodUnit.class, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<BloodUnit> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<BloodUnit> query = session.createQuery("FROM BloodUnit", BloodUnit.class);
            return query.list();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public void deleteFirstNUnits(BloodTypeEnum bloodType, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Find the first N available units of the specified type
                List<BloodUnit> unitsToUpdate = session.createQuery(
                                "FROM BloodUnit WHERE bloodType = :bloodType AND status = 'AVAILABLE' " +
                                        "ORDER BY expirationDate ASC", BloodUnit.class)
                        .setParameter("bloodType", bloodType)
                        .setMaxResults(quantity)
                        .list();

                if (unitsToUpdate.size() < quantity) {
                    throw new IllegalStateException("Not enough available units of type " + bloodType +
                            ". Requested: " + quantity + ", Available: " + unitsToUpdate.size());
                }

                // Update status to USED instead of deleting
                for (BloodUnit unit : unitsToUpdate) {
                    unit.setStatus(BloodUnitStatusEnum.DISTRIBUTED);
                    session.merge(unit);
                }

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Failed to update blood units", e);
            }
        }
    }
}