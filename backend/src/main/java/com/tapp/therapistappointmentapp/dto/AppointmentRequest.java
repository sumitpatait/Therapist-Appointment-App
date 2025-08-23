package com.tapp.therapistappointmentapp.dto;

import java.time.LocalDateTime;

public class AppointmentRequest {
    private Long therapistId;
    private LocalDateTime appointmentTime; // The exact date and time the patient wants to book

    public AppointmentRequest() {
    }

    public AppointmentRequest(Long therapistId, LocalDateTime appointmentTime) {
        this.therapistId = therapistId;
        this.appointmentTime = appointmentTime;
    }

    public Long getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(Long therapistId) {
        this.therapistId = therapistId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) { // Corrected method name from 'void setAppointmentTime'
        this.appointmentTime = appointmentTime;
    }
}