package com.tapp.therapistappointmentapp.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "patient")
@EqualsAndHashCode(callSuper = true, exclude = "appointments") // Exclude collection from equals/hashCode
@PrimaryKeyJoinColumn(name = "user_id")
public class Patient extends User {
    
    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "patient_age")
    private Integer patientAge;

    @Column(name = "patient_mobile_number")
    private String patientMobileNumber;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appointments = new HashSet<>();

    public Patient() {
    }

    // Getters and setters for the new fields
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Integer getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(Integer patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientMobileNumber() {
        return patientMobileNumber;
    }

    public void setPatientMobileNumber(String patientMobileNumber) {
        this.patientMobileNumber = patientMobileNumber;
    }

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

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
}
