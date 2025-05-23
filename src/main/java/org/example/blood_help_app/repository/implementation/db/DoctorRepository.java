package org.example.blood_help_app.repository.implementation.db;

import org.example.blood_help_app.domain.enums.SpecializationEnum;
import org.example.blood_help_app.domain.users.Doctor;
import org.example.blood_help_app.domain.users.User;
import org.example.blood_help_app.repository.interfaces.IDoctorRepository;
import org.example.blood_help_app.repository.interfaces.IUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DoctorRepository extends AbstractRepository<Integer, Doctor> implements IDoctorRepository {
    private final IUserRepository userRepository;

    public DoctorRepository(Properties props, IUserRepository userRepository) {
        super(props, "doctors");
        this.userRepository = userRepository;
    }

    @Override
    protected Doctor mapResultSetToEntity(ResultSet rs) throws SQLException {
        User user = userRepository.find(rs.getInt("user_id")).orElseThrow();

        Doctor doctor = new Doctor(
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getBirthdayDate(),
                SpecializationEnum.valueOf(rs.getString("specialization")),
                rs.getString("institution")
        );
        doctor.setId(user.getId());
        return doctor;
    }

    @Override
    protected void setPreparedStatementForSave(PreparedStatement ps, Doctor doctor) throws SQLException {
        ps.setLong(1, doctor.getId());
        ps.setString(2, doctor.getSpecialization().name());
        ps.setString(3, doctor.getInstitution());
    }

    @Override
    protected void setPreparedStatementForUpdate(PreparedStatement ps, Doctor doctor) throws SQLException {
        ps.setString(1, doctor.getSpecialization().name());
        ps.setString(2, doctor.getInstitution());
    }

    @Override
    protected String getInsertColumns() {
        return "(user_id, specialization, institution)";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "(?, ?, ?)";
    }

    @Override
    protected String getUpdateColumns() {
        return "specialization = ?, institution = ?";
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
