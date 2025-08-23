package com.tapp.therapistappointmentapp.dto;

import java.time.LocalDateTime;

public class ReviewResponse {
    private Long id;
    private Long appointmentId;
    private Long patientId;
    private String patientEmail;
    private Long therapistId;
    private String therapistEmail;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;

    public ReviewResponse() {
    }

    public ReviewResponse(Long id, Long appointmentId, Long patientId, String patientEmail, Long therapistId, String therapistEmail, Integer rating, String comment, LocalDateTime reviewDate) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientEmail = patientEmail;
        this.therapistId = therapistId;
        this.therapistEmail = therapistEmail;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
}