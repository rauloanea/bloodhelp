package org.example.blood_help_app.repository.interfaces;

import org.example.blood_help_app.domain.donationsdata.BloodUnit;
import org.example.blood_help_app.domain.enums.BloodTypeEnum;

public interface IBloodUnitRepository extends IRepository<Integer, BloodUnit> {
    void deleteFirstNUnits(BloodTypeEnum bloodType, int quantity);
}
