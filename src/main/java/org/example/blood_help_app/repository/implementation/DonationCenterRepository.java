package org.example.blood_help_app.repository.implementation;

import org.example.blood_help_app.domain.donationsdata.DonationCenter;
import org.example.blood_help_app.repository.interfaces.AbstractRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

public class DonationCenterRepository extends AbstractRepository<Integer, DonationCenter> {
    public DonationCenterRepository(Properties props) {
        super(props, "donation_centers");
    }

    @Override
    protected DonationCenter mapResultSetToEntity(ResultSet rs) throws SQLException {
        DonationCenter center = new DonationCenter(
                rs.getString("name"),
                rs.getString("address"),
                new ArrayList<>(), // Inventory will be populated separately
                rs.getString("contact_info")
        );
        center.setId(rs.getInt("id"));
        return center;
    }

    @Override
    protected void setPreparedStatementForSave(PreparedStatement ps, DonationCenter center) throws SQLException {
        ps.setString(1, center.getName());
        ps.setString(2, center.getAddress());
        ps.setString(3, center.getContactInfo());
    }

    @Override
    protected void setPreparedStatementForUpdate(PreparedStatement ps, DonationCenter center) throws SQLException {
        ps.setString(1, center.getName());
        ps.setString(2, center.getAddress());
        ps.setString(3, center.getContactInfo());
    }

    @Override
    protected String getInsertColumns() {
        return "(name, address, contact_info)";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "(?, ?, ?)";
    }

    @Override
    protected String getUpdateColumns() {
        return "name = ?, address = ?, contact_info = ?";
    }

    @Override
    protected String getSelectOneString(){
        return "SELECT * FROM " + tableName + " WHERE id = ?";
    }
}
