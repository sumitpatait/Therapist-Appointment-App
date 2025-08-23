package com.tapp.therapistappointmentapp.dto;

import java.util.List;

public class TherapistRegistrationRequest {
    private String email;
    private String password;
    private String bio;
    private String experience;
    private String qualifications;
    private List<String> specializationNames; // To register with existing specializations

    public TherapistRegistrationRequest() {
    }

    public TherapistRegistrationRequest(String email, String password, String bio, String experience, String qualifications, List<String> specializationNames) {
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.experience = experience;
        this.qualifications = qualifications;
        this.specializationNames = specializationNames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public List<String> getSpecializationNames() {
        return specializationNames;
    }

    public void setSpecializationNames(List<String> specializationNames) {
        this.specializationNames = specializationNames;
    }
}