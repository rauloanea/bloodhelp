package org.example.blood_help_app.domain.users;

import org.example.blood_help_app.domain.Entity;
import org.example.blood_help_app.domain.enums.UserTypeEnum;

import java.time.LocalDateTime;

public class User extends Entity<Long> {
    protected String name;
    protected String email;
    protected String username;
    protected String password;
    protected String phoneNumber;
    protected LocalDateTime birthdayDate;
    protected UserTypeEnum userType;

    public User(String name, String email, String username, String password, String phoneNumber, LocalDateTime birthdayDate, UserTypeEnum userType) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.birthdayDate = birthdayDate;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(LocalDateTime birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public UserTypeEnum getUserType() {
        return userType;
    }

    public void setUserType(UserTypeEnum userType) {
        this.userType = userType;
    }
}
