package com.tapp.therapistappointmentapp.dto;

import com.tapp.therapistappointmentapp.model.AppointmentStatus;
import java.time.LocalDateTime;

public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientEmail;
    private Long therapistId;
    private String therapistEmail;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;

    public AppointmentResponse() {
    }

    public AppointmentResponse(Long id, Long patientId, String patientEmail, Long therapistId, String therapistEmail, LocalDateTime appointmentTime, AppointmentStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.patientEmail = patientEmail;
        this.therapistId = therapistId;
        this.therapistEmail = therapistEmail;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public Long getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(Long therapistId) {
        this.therapistId = therapistId;
    }

    public String getTherapistEmail() {
        return therapistEmail;
    }

    public void setTherapistEmail(String therapistEmail) {
        this.therapistEmail = therapistEmail;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}