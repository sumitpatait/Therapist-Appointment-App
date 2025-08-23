package com.tapp.therapistappointmentapp.dto;

import com.tapp.therapistappointmentapp.model.UserRole; // Don't forget this import!

public class UserUpdateRequest {
    private String email;    // Optional: for finding user if not by ID, or for verification
    private UserRole role;   // Allow admin to change role
    // private Boolean active; // Could add a field for blocking/unblocking users later

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String email, UserRole role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}