package org.example.blood_help_app.repository.implementation.db;

import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.enums.DonationStatusEnum;
import org.example.blood_help_app.domain.users.Donor;
import org.example.blood_help_app.domain.users.Doctor;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.repository.interfaces.IDoctorRepository;
import org.example.blood_help_app.repository.interfaces.IDonationCenterRepository;
import org.example.blood_help_app.repository.interfaces.IDonationRepository;
import org.example.blood_help_app.repository.interfaces.IDonorRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.time.LocalDateTime;

public class DonationRepository extends AbstractRepository<Integer, Donation> implements IDonationRepository {
    private final IDonorRepository donorRepository;
    private final IDoctorRepository doctorRepository;
    private final IDonationCenterRepository centerRepository;

    public DonationRepository(Properties props,
                              IDonorRepository donorRepository,
                              IDoctorRepository doctorRepository,
                              IDonationCenterRepository centerRepository) {
        super(props, "donations");
        this.donorRepository = donorRepository;
        this.doctorRepository = doctorRepository;
        this.centerRepository = centerRepository;
    }

    @Override
    protected Donation mapResultSetToEntity(ResultSet rs) throws SQLException {
        Donor donor = donorRepository.find(rs.getInt("donor_id")).orElseThrow();
        Doctor doctor = doctorRepository.find(rs.getInt("doctor_id")).orElseThrow();
        DonationCenter center = centerRepository.find(rs.getInt("donation_center_id")).orElseThrow();

        Donation donation = new Donation(
                donor,
                LocalDateTime.parse(rs.getString("donation_date")),
                BloodTypeEnum.valueOf(rs.getString("blood_type")),
                rs.getDouble("quantity"),
                center.getId(),
                DonationStatusEnum.valueOf(rs.getString("status")),
                rs.getString("test_results"),
                doctor
        );
        donation.setId(rs.getInt("id"));
        return donation;
    }

    @Override
    protected void setPreparedStatementForSave(PreparedStatement ps, Donation donation) throws SQLException {
        ps.setLong(1, donation.getDonor().getId());
        ps.setLong(2, donation.getDoctor().getId());
        ps.setInt(3, donation.getDonationCenter());
        ps.setString(4, donation.getDonationDate().toString());
        ps.setString(5, donation.getBloodType().name());
        ps.setDouble(6, donation.getQuantity());
        ps.setString(7, donation.getStatus().name());
        ps.setString(8, donation.getTestResults());
    }

    @Override
    protected void setPreparedStatementForUpdate(PreparedStatement ps, Donation donation) throws SQLException {
        ps.setLong(1, donation.getDonor().getId());
        ps.setLong(2, donation.getDoctor().getId());
        ps.setInt(3, donation.getDonationCenter());
        ps.setString(4, donation.getDonationDate().toString());
        ps.setString(5, donation.getBloodType().name());
        ps.setDouble(6, donation.getQuantity());
        ps.setString(7, donation.getStatus().name());
        ps.setString(8, donation.getTestResults());
    }


    @Override
    protected String getInsertColumns() {
        return "(donor_id, doctor_id, donation_center_id, donation_date, blood_type, quantity, status, test_results)";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "(?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateColumns() {
        return "donor_id = ?, doctor_id = ?, donation_center_id = ?, donation_date = ?, blood_type = ?, quantity = ?, status = ?, test_results = ?";
    }

    @Override
    protected String getSelectOneString(){
        return "SELECT * FROM " + tableName + " WHERE id = ?";
    }

    @Override
    protected String getUpdateIdentifier() {
        return " WHERE id = ?";
    }

    public List<Donation> findUserDonations(Donor user){
        String sql = "SELECT * from donations where donor_id = ? order by donation_date desc";

        try(var conn = this.jdbcUtils.getConnection();
        var stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, user.getId());

            var rs = stmt.executeQuery();
            var lst = new ArrayList<Donation>();
            while(rs.next()){
                lst.add(mapResultSetToEntity(rs));
            }

            return lst;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
