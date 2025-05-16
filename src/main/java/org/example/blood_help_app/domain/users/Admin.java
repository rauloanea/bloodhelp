package org.example.blood_help_app.domain.users;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.example.blood_help_app.domain.enums.AccessLevelEnum;
import org.example.blood_help_app.domain.enums.UserTypeEnum;
import org.example.blood_help_app.domain.users.utils.AppUser;

import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name="admins")
public class Admin extends User implements AppUser {
    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, columnDefinition = "TEXT DEFAULT 'STANDARD'")
    private AccessLevelEnum accessLevel;

    public Admin(String name, String email, String username, String password, String phoneNumber, LocalDateTime birthdayDate, AccessLevelEnum accessLevel) {
        super(name, email, username, password, phoneNumber, birthdayDate, UserTypeEnum.ADMIN);
        this.accessLevel = accessLevel;
    }

    public Admin(){}

    public AccessLevelEnum getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevelEnum accessLevel) {
        this.accessLevel = accessLevel;
    }
}
