package org.example.blood_help_app.domain.users;

import org.example.blood_help_app.domain.enums.AccessLevelEnum;
import org.example.blood_help_app.domain.enums.UserTypeEnum;

import java.time.LocalDateTime;

public class Admin extends User {
    private AccessLevelEnum accessLevel;

    public Admin(String name, String email, String username, String password, String phoneNumber, LocalDateTime birthdayDate, AccessLevelEnum accessLevel) {
        super(name, email, username, password, phoneNumber, birthdayDate, UserTypeEnum.ADMIN);
        this.accessLevel = accessLevel;
    }

    public AccessLevelEnum getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevelEnum accessLevel) {
        this.accessLevel = accessLevel;
    }
}
