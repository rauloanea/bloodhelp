package org.example.blood_help_app.repository.interfaces;

import org.example.blood_help_app.domain.Entity;
import org.example.blood_help_app.utils.JDBCUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Optional;

public abstract class AbstractRepository<ID, E extends Entity<ID>> implements IRepository<ID, E> {
    protected final JDBCUtils jdbcUtils;
    protected final String tableName;

    public AbstractRepository(Properties props, String tableName) {
        this.jdbcUtils = new JDBCUtils(props);
        this.tableName = tableName;
    }

    protected abstract E mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract void setPreparedStatementForSave(PreparedStatement ps, E entity) throws SQLException;
    protected abstract void setPreparedStatementForUpdate(PreparedStatement ps, E entity) throws SQLException;

    @Override
    public Optional<E> save(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        String sql = "INSERT INTO " + tableName + getInsertColumns() + " VALUES " + getInsertPlaceholders();

        try (var connection = jdbcUtils.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPreparedStatementForSave(ps, entity);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return Optional.of(entity);
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ID id = (ID) Long.valueOf(generatedKeys.getLong(1));
                    entity.setId(id);
                }
            }
            return Optional.of(entity);
        } catch (SQLException e) {
            // Handle unique constraint violations (e.g., duplicate email)
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return Optional.empty();
            }
            throw new RuntimeException("Error saving entity", e);
        }
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<E> entity = findOne(id);
        if (entity.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM " + tableName + " WHERE id = ?";

        try (var connection = jdbcUtils.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, (Long) id);
            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting entity", e);
        }
    }

    @Override
    public Optional<E> update(E updatedEntity) {
        if (updatedEntity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        if (findOne(updatedEntity.getId()).isEmpty()) {
            return Optional.of(updatedEntity);
        }

        String sql = "UPDATE " + tableName + " SET " + getUpdateColumns() + getUpdateIdentifier();

        try (var connection = jdbcUtils.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            setPreparedStatementForUpdate(ps, updatedEntity);
            ps.setLong(getUpdateColumns().split(",").length + 1, (Long) updatedEntity.getId());
            ps.executeUpdate();
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating entity", e);
        }
    }

    protected abstract String getUpdateIdentifier();

    @Override
    public Optional<E> findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        String sql = getSelectOneString();

        try (var connection = jdbcUtils.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, (Long) id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding entity", e);
        }
    }

    @Override
    public List<E> findAll() {
        String sql = "SELECT * FROM " + tableName;
        List<E> entities = new ArrayList<>();

        try (var connection = jdbcUtils.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }
            return entities;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all entities", e);
        }
    }

    protected abstract String getInsertColumns();
    protected abstract String getInsertPlaceholders();
    protected abstract String getUpdateColumns();
    protected abstract String getSelectOneString();
}
