package com.tapp.therapistappointmentapp.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "therapist")
@EqualsAndHashCode(callSuper = true, exclude = {"specializations", "schedules", "appointments"})
@PrimaryKeyJoinColumn(name = "user_id")
public class Therapist extends User {
    private String bio;
    private String experience;
    private String qualifications;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "therapist_specialization",
        joinColumns = @JoinColumn(name = "therapist_id"),
        inverseJoinColumns = @JoinColumn(name = "specialization_id")
    )
    private Set<Specialization> specializations = new HashSet<>();

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Schedule> schedules = new HashSet<>();

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appointments = new HashSet<>();

    // Manually added no-arg constructor
    public Therapist() {
    }

    // Custom constructor for setting the specific role during creation and initializing collections
    public Therapist(Long id, String email, String password) {
        super(id, email, password, UserRole.THERAPIST);
        this.specializations = new HashSet<>();
        this.schedules = new HashSet<>();
        this.appointments = new HashSet<>();
    }

    // Inherited field getters/setters (from User) are available via super.get/set
    // Getters and setters for Therapist's own fields
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public Set<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(Set<Specialization> specializations) {
        this.specializations = specializations;
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
}