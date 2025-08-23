package com.tapp.therapistappointmentapp.dto;

public class ReviewRequest {
    private Long appointmentId; // The appointment this review is for
    private Integer rating;    // e.g., 1 to 5
    private String comment;    // Optional comment

    public ReviewRequest() {
    }

    public ReviewRequest(Long appointmentId, Integer rating, String comment) {
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
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
}