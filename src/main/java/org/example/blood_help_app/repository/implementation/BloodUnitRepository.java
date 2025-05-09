package org.example.blood_help_app.repository.implementation;

import org.example.blood_help_app.domain.donationsdata.BloodUnit;
import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;
import org.example.blood_help_app.domain.enums.BloodUnitStatusEnum;
import org.example.blood_help_app.repository.interfaces.AbstractRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;

public class BloodUnitRepository extends AbstractRepository<Long, BloodUnit> {
    private final DonationRepository donationRepository;
    private final DonationCenterRepository centerRepository;

    public BloodUnitRepository(Properties props,
                               DonationRepository donationRepository,
                               DonationCenterRepository centerRepository) {
        super(props, "blood_units");
        this.donationRepository = donationRepository;
        this.centerRepository = centerRepository;
    }

    @Override
    protected BloodUnit mapResultSetToEntity(ResultSet rs) throws SQLException {
        Donation donation = donationRepository.findOne(rs.getLong("donation_id")).orElseThrow();
        DonationCenter center = centerRepository.findOne(rs.getInt("donation_center_id")).orElseThrow();

        BloodUnit unit = new BloodUnit(
                BloodTypeEnum.valueOf(rs.getString("blood_type")),
                LocalDateTime.parse(rs.getString("expiration_date")),
                BloodUnitStatusEnum.valueOf(rs.getString("status")),
                donation
        );
        unit.setId(rs.getLong("id"));
        return unit;
    }

    @Override
    protected void setPreparedStatementForSave(PreparedStatement ps, BloodUnit unit) throws SQLException {
        ps.setLong(1, unit.getDonation().getId());
        ps.setString(2, unit.getBloodType().name());
        ps.setString(3, unit.getExpirationDate().toString());
        ps.setString(4, unit.getStatus().name());
        ps.setInt(5, unit.getDonation().getDonationCenter());
    }

    @Override
    protected void setPreparedStatementForUpdate(PreparedStatement ps, BloodUnit unit) throws SQLException {
        ps.setLong(1, unit.getDonation().getId());
        ps.setString(2, unit.getBloodType().name());
        ps.setString(3, unit.getExpirationDate().toString());
        ps.setString(4, unit.getStatus().name());
        ps.setInt(5, unit.getDonation().getDonationCenter());
    }

    @Override
    protected String getInsertColumns() {
        return "(donation_id, blood_type, expiration_date, status, donation_center_id)";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "(?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateColumns() {
        return "donation_id = ?, blood_type = ?, expiration_date = ?, status = ?, donation_center_id = ?";
    }

    @Override
    protected String getSelectOneString(){
        return "SELECT * FROM " + tableName + " WHERE id = ?";
    }
}
