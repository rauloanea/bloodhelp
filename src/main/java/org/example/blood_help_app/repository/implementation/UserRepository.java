package org.example.blood_help_app.repository.implementation;

import org.example.blood_help_app.domain.enums.UserTypeEnum;
import org.example.blood_help_app.domain.users.User;
import org.example.blood_help_app.repository.interfaces.AbstractRepository;
import org.example.blood_help_app.repository.interfaces.IUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;

public class UserRepository extends AbstractRepository<Long, User> implements IUserRepository {
    public UserRepository(Properties props) {
        super(props, "users");
    }

    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("phone_number"),
                LocalDateTime.parse(rs.getString("birthday_date")),
                UserTypeEnum.valueOf(rs.getString("user_type"))
        );
        user.setId(rs.getLong("id"));
        return user;
    }

    @Override
    protected void setPreparedStatementForSave(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getUsername());
        ps.setString(4, user.getPassword());
        ps.setString(5, user.getPhoneNumber());
        ps.setString(6, user.getBirthdayDate().toString());
        ps.setString(7, "DONOR"); // Default user type
    }

    @Override
    protected void setPreparedStatementForUpdate(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getUsername());
        ps.setString(4, user.getPassword());
        ps.setString(5, user.getPhoneNumber());
        ps.setString(6, user.getBirthdayDate().toString());
    }

    @Override
    protected String getInsertColumns() {
        return "(name, email, username, password, phone_number, birthday_date, user_type)";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "(?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateColumns() {
        return "name = ?, email = ?, username = ?, password = ?, phone_number = ?, birthday_date = ?";
    }

    @Override
    protected String getSelectOneString(){
        return "SELECT * FROM " + tableName + " WHERE id = ?";
    }

    public Optional<User> findByCredentials(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (var connection = jdbcUtils.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("[USER] Error finding user by email", e);
        }
    }

    @Override
    protected String getUpdateIdentifier() {
        return " WHERE id = ?";
    }
}
