package models;

import java.time.LocalDateTime;

/**
 * Service model - represents different services (e.g., ID Card, Passport, etc.)
 */
public class Service {
    private int id;
    private String name;
    private String description;
    private int estimatedTime; // in minutes
    private boolean active;
    private LocalDateTime createdAt;

    // Constructors
    public Service() {
    }

    public Service(int id, String name, String description, int estimatedTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.estimatedTime = estimatedTime;
        this.active = true;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
