package org.example.blood_help_app.repository.interfaces;

import org.example.blood_help_app.domain.donationsdata.Donation;
import org.example.blood_help_app.domain.users.Donor;

import java.util.List;

public interface IDonationRepository extends IRepository<Long, Donation> {
    List<Donation> findUserDonations(Donor user);
}
