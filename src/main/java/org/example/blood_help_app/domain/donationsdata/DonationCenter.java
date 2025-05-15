package org.example.blood_help_app.domain.donationsdata;

import jakarta.persistence.*;
import org.example.blood_help_app.domain.Entity;

import java.util.List;

@jakarta.persistence.Entity
@Table(name = "donation_centers")
public class DonationCenter extends Entity<Integer> {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "donationCenter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BloodUnit> inventory;

    @Column(name = "contact_info")
    private String contactInfo;

    public DonationCenter(String name, String address, List<BloodUnit> inventory, String contactInfo) {
        this.name = name;
        this.address = address;
        this.inventory = inventory;
        this.contactInfo = contactInfo;
    }

    public DonationCenter() {}

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
