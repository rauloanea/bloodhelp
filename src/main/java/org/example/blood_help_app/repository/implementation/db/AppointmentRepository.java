package org.example.blood_help_app.repository.implementation.db;

import org.example.blood_help_app.domain.donationsdata.Appointment;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.enums.AppointmentStatus;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.repository.interfaces.IAppointmentRepository;
import org.example.blood_help_app.repository.interfaces.IDonationCenterRepository;
import org.example.blood_help_app.repository.interfaces.IDonorRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class AppointmentRepository extends AbstractRepository<Integer, Appointment> implements IAppointmentRepository {
    private final IDonorRepository donorRepository;
    private final IDonationCenterRepository centerRepository;

    public AppointmentRepository(Properties props,
                                 IDonorRepository donorRepository,
                                 IDonationCenterRepository centerRepository) {
        super(props, "appointments");
        this.donorRepository = donorRepository;
        this.centerRepository = centerRepository;
    }

    @Override
    protected Appointment mapResultSetToEntity(ResultSet rs) throws SQLException {
        Donor donor = donorRepository.find(rs.getInt("donor_id")).orElseThrow();
        DonationCenter center = centerRepository.find(rs.getInt("donation_center_id")).orElseThrow();

        Appointment appointment = new Appointment(
                donor,
                LocalDateTime.parse(rs.getString("appointment_date")),
                center,
                AppointmentStatus.valueOf(rs.getString("status"))
        );
        appointment.setId(rs.getInt("id"));
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

    public Optional<Appointment> checkDisponibility(Integer centerId, LocalDateTime date){
        String sql = "Select * from appointments where donation_center_id = ? and appointment_date = ?";

        try(var connection = jdbcUtils.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, centerId);
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

    public List<Appointment> findFutureAppointmentsChronological(Donor user){
        String sql = "Select * from appointments where donor_id = ? order by appointment_date";

        try(var conn = this.jdbcUtils.getConnection();
        var stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, user.getId());

            var rs = stmt.executeQuery();
            var list = new ArrayList<Appointment>();
            while(rs.next()){
                list.add(mapResultSetToEntity(rs));
            }

            return list;
        }catch (SQLException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}