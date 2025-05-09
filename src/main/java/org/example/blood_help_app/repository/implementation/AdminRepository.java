package org.example.blood_help_app.repository.implementation;

import org.example.blood_help_app.domain.enums.AccessLevelEnum;
import org.example.blood_help_app.domain.users.Admin;
import org.example.blood_help_app.domain.users.User;
import org.example.blood_help_app.repository.interfaces.AbstractRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

public class AdminRepository extends AbstractRepository<Long, Admin> {
    private final UserRepository userRepository;

    public AdminRepository(Properties props, UserRepository userRepository) {
        super(props, "admins");
        this.userRepository = userRepository;
    }

    @Override
    protected Admin mapResultSetToEntity(ResultSet rs) throws SQLException {
        User user = userRepository.findOne(rs.getLong("user_id")).orElseThrow();

        Admin admin = new Admin(
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getBirthdayDate(),
                AccessLevelEnum.valueOf(rs.getString("access_level"))
        );
        admin.setId(user.getId());
        return admin;
    }

    @Override
    protected void setPreparedStatementForSave(PreparedStatement ps, Admin admin) throws SQLException {
        ps.setLong(1, admin.getId());
        ps.setString(2, admin.getAccessLevel().name());
    }

    @Override
    protected void setPreparedStatementForUpdate(PreparedStatement ps, Admin admin) throws SQLException {
        ps.setString(1, admin.getAccessLevel().name());
    }

    @Override
    protected String getInsertColumns() {
        return "(user_id, access_level)";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "(?, ?)";
    }

    @Override
    protected String getUpdateColumns() {
        return "access_level = ?";
    }

    @Override
    protected String getUpdateIdentifier() {
        return " WHERE user_id = ?";
    }

    // Additional admin-specific methods could be added here
    public Optional<Admin> findByAccessLevel(AccessLevelEnum accessLevel) {
        String sql = "SELECT a.* FROM admins a WHERE a.access_level = ?";

        try (var connection = jdbcUtils.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, accessLevel.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding admin by access level", e);
        }
    }

    @Override
    protected String getSelectOneString(){
        return "SELECT * FROM " + tableName + " WHERE user_id = ?";
    }
}
