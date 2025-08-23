package com.tapp.therapistappointmentapp.dto;

public class PatientRegistrationRequest {
    private String patientName;
    private Integer patientAge;
    private String patientMobileNumber;
    private String email;
    private String password;

    // CONSTRUCTOR WITH ALL FIELDS
    public PatientRegistrationRequest(String patientName, Integer patientAge, String patientMobileNumber, String email, String password) {
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientMobileNumber = patientMobileNumber;
        this.email = email;
        this.password = password;
    }

    // DEFAULT CONSTRUCTOR
    public PatientRegistrationRequest() {
    }

    // GETTERS AND SETTERS
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
}
