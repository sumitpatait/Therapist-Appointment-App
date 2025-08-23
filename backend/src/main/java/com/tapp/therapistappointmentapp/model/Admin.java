package com.tapp.therapistappointmentapp.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "admin_user")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    public Admin() {
    }

    // Getters/setters for inherited fields (from User) via super.get/set
    public String getEmail() {
        return super.getEmail();
    }

    public void setEmail(String email) {
        super.setEmail(email);
    }

    public String getPassword() {
        return super.getPassword();
    }

    public void setPassword(String password) {
        super.setPassword(password);
    }

    public UserRole getRole() {
        return super.getRole();
    }

    public void setRole(UserRole role) {
        super.setRole(role);
    }
}