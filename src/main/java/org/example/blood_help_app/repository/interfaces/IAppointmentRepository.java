package org.example.blood_help_app.repository.interfaces;

import org.example.blood_help_app.domain.donationsdata.Appointment;
import org.example.blood_help_app.domain.users.Donor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IAppointmentRepository extends IRepository<Integer, Appointment> {
    Optional<Appointment> checkDisponibility(Integer centerId, LocalDateTime date);

    List<Appointment> findFutureAppointmentsChronological(Donor user);

    Optional<Appointment> findEligibilityAppointment(Donor user);
}
