package com.tapp.therapistappointmentapp.dto;

import java.util.List;

public class TherapistProfileResponse {
    private Long id;
    private String email; // Or could be a display name
    private String bio;
    private String experience;
    private String qualifications;
    private List<String> specializations; // Names of specializations

    public TherapistProfileResponse() {
    }

    public TherapistProfileResponse(Long id, String email, String bio, String experience, String qualifications, List<String> specializations) {
        this.id = id;
        this.email = email;
        this.bio = bio;
        this.experience = experience;
        this.qualifications = qualifications;
        this.specializations = specializations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<String> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations;
    }
}