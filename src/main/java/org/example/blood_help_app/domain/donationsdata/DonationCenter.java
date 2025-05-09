package org.example.blood_help_app.domain.donationsdata;

import org.example.blood_help_app.domain.Entity;

import java.util.List;

public class DonationCenter extends Entity<Integer> {
    private String name;
    private String address;
    private List<BloodUnit> inventory;
    private String contactInfo;

    public DonationCenter(String name, String address, List<BloodUnit> inventory, String contactInfo) {
        this.name = name;
        this.address = address;
        this.inventory = inventory;
        this.contactInfo = contactInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<BloodUnit> getInventory() {
        return inventory;
    }

    public void setInventory(List<BloodUnit> inventory) {
        this.inventory = inventory;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
