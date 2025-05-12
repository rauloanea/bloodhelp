package org.example.blood_help_app.repository.implementation;

import org.example.blood_help_app.domain.donationsdata.Appointment;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.enums.AppointmentStatus;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.repository.interfaces.AbstractRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;

public class AppointmentRepository extends AbstractRepository<Long, Appointment> {
    private final DonorRepository donorRepository;
    private final DonationCenterRepository centerRepository;

    public AppointmentRepository(Properties props,
                                 DonorRepository donorRepository,
                                 DonationCenterRepository centerRepository) {
        super(props, "appointments");
        this.donorRepository = donorRepository;
        this.centerRepository = centerRepository;
    }

    @Override
    protected Appointment mapResultSetToEntity(ResultSet rs) throws SQLException {
        Donor donor = donorRepository.findOne(rs.getLong("donor_id")).orElseThrow();
        DonationCenter center = centerRepository.findOne(rs.getInt("donation_center_id")).orElseThrow();

        Appointment appointment = new Appointment(
                donor,
                LocalDateTime.parse(rs.getString("appointment_date")),
                center,
                AppointmentStatus.valueOf(rs.getString("status"))
        );
        appointment.setId(rs.getLong("id"));
        return appointment;
    }

    @Override
    protected void setPreparedStatementForSave(PreparedStatement ps, Appointment appointment) throws SQLException {
        ps.setLong(1, appointment.getDonor().getId());
        ps.setInt(2, appointment.getDonationCenter().getId());
        ps.setString(3, appointment.getDate().toString());
        ps.setString(4, appointment.getStatus().name());
    }

    @Override
    protected void setPreparedStatementForUpdate(PreparedStatement ps, Appointment appointment) throws SQLException {
        ps.setLong(1, appointment.getDonor().getId());
        ps.setInt(2, appointment.getDonationCenter().getId());
        ps.setString(3, appointment.getDate().toString());
        ps.setString(4, appointment.getStatus().name());
    }

    @Override
    protected String getInsertColumns() {
        return "(donor_id, donation_center_id, appointment_date, status)";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "(?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateColumns() {
        return "donor_id = ?, donation_center_id = ?, appointment_date = ?, status = ?";
    }

    @Override
    protected String getSelectOneString(){
        return "SELECT * FROM " + tableName + " WHERE id = ?";
    }

    @Override
    protected String getUpdateIdentifier() {
        return " WHERE id = ?";
    }

    public Optional<Appointment> checkDisponibility(Integer center, LocalDateTime date){
        String sql = "Select * from appointments where donation_center_id = ? and appointment_date = ?";

        try(var connection = jdbcUtils.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, center);
            stmt.setObject(2, date);

            var rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapResultSetToEntity(rs));
            }
            return Optional.empty();
        }catch (SQLException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}