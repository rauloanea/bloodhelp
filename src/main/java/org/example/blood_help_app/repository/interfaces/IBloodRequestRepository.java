package org.example.blood_help_app.repository.interfaces;

import org.example.blood_help_app.domain.donationsdata.BloodRequest;

import java.util.List;

public interface IBloodRequestRepository extends IRepository<Integer, BloodRequest> {
    List<BloodRequest> findByStatus(String status);
    List<BloodRequest> findByDoctorId(Long doctorId);
    List<BloodRequest> findByCenterId(Long centerId);
    List<BloodRequest> findByBloodType(String bloodType);
}
