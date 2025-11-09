package models;

import java.time.LocalDateTime;

/**
 * Agency model - represents different administrative agencies/branches
 */
public class Agency {
    private int id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private int totalCounters; // Number of guichets
    private LocalDateTime createdAt;

    // Constructors
    public Agency() {
    }

    public Agency(int id, String name, String address, String city, String phone, int totalCounters) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.totalCounters = totalCounters;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTotalCounters() {
        return totalCounters;
    }

    public void setTotalCounters(int totalCounters) {
        this.totalCounters = totalCounters;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
