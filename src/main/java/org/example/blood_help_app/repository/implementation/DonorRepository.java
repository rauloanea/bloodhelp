package org.example.blood_help_app.repository.implementation;

import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.domain.users.User;
import org.example.blood_help_app.repository.interfaces.AbstractRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Properties;

public class DonorRepository extends AbstractRepository<Long, Donor> {
    private final UserRepository userRepository;

    public DonorRepository(Properties properties, UserRepository userRepository) {
        super(properties, "donors");
        this.userRepository = userRepository;
    }

    @Override
    protected Donor mapResultSetToEntity(ResultSet rs) throws SQLException {
        User user = userRepository.findOne(rs.getLong("user_id")).orElseThrow();

        Donor donor = new Donor(
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getBirthdayDate(),
                rs.getString("blood_type") != null ? BloodTypeEnum.valueOf(rs.getString("blood_type")) : null,
                new ArrayList<>(), // Will be populated separately
                rs.getInt("eligible"),
                rs.getString("contact_info")
        );
        donor.setId(user.getId());
        return donor;
    }

    @Override
    protected void setPreparedStatementForSave(PreparedStatement ps, Donor donor) throws SQLException {
        ps.setLong(1, donor.getId());

        if (donor.getBloodType() != null) {
            ps.setString(2, donor.getBloodType().name());
        } else {
            ps.setNull(2, Types.VARCHAR);
        }

        if (donor.getEligibility() != null) {
            ps.setInt(3, donor.getEligibility());
        } else {
            ps.setInt(3, -1);
        }

        ps.setString(4, donor.getContactInfo());
    }

    @Override
    protected void setPreparedStatementForUpdate(PreparedStatement ps, Donor donor) throws SQLException {
        if(donor.getBloodType() != null)
            ps.setString(1, donor.getBloodType().name());
        else
            ps.setNull(1, Types.VARCHAR);
        ps.setInt(2, donor.getEligibility() != null ? donor.getEligibility() : -1);

        if(donor.getContactInfo() != null)
            ps.setString(3, donor.getContactInfo());
        else
            ps.setNull(3, Types.VARCHAR);
    }

    @Override
    protected String getInsertColumns() {
        return "(user_id, blood_type, eligible, contact_info)";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "(?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateColumns() {
        return "blood_type = ?, eligible = ?, contact_info = ?";
    }

    @Override
    protected String getSelectOneString(){
        return "SELECT * FROM " + tableName + " WHERE user_id = ?";
    }

    @Override
    protected String getUpdateIdentifier() {
        return " WHERE user_id = ?";
    }
}
