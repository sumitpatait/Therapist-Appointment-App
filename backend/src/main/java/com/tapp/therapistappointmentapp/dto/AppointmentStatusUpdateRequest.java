package com.tapp.therapistappointmentapp.dto;

import com.tapp.therapistappointmentapp.model.AppointmentStatus;

public class AppointmentStatusUpdateRequest {
    private AppointmentStatus status; // The new status for the appointment

    public AppointmentStatusUpdateRequest() {
    }

    public AppointmentStatusUpdateRequest(AppointmentStatus status) {
        this.status = status;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}