package com.tapp.therapistappointmentapp.dto;

public class SystemStatsResponse {
    private long totalUsers;
    private long totalPatients;
    private long totalTherapists;
    private long totalAppointments;
    private long pendingAppointments;
    private long confirmedAppointments;
    private long completedAppointments;
    private long totalReviews;

    public SystemStatsResponse() {
    }

    public SystemStatsResponse(long totalUsers, long totalPatients, long totalTherapists, long totalAppointments, long pendingAppointments, long confirmedAppointments, long completedAppointments, long totalReviews) {
        this.totalUsers = totalUsers;
        this.totalPatients = totalPatients;
        this.totalTherapists = totalTherapists;
        this.totalAppointments = totalAppointments;
        this.pendingAppointments = pendingAppointments;
        this.confirmedAppointments = confirmedAppointments;
        this.completedAppointments = completedAppointments;
        this.totalReviews = totalReviews;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getTotalTherapists() {
        return totalTherapists;
    }

    public void setTotalTherapists(long totalTherapists) {
        this.totalTherapists = totalTherapists;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getPendingAppointments() {
        return pendingAppointments;
    }

    public void setPendingAppointments(long pendingAppointments) {
        this.pendingAppointments = pendingAppointments;
    }

    public long getConfirmedAppointments() {
        return confirmedAppointments;
    }

    public void setConfirmedAppointments(long confirmedAppointments) {
        this.confirmedAppointments = confirmedAppointments;
    }

    public long getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(long completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }
}