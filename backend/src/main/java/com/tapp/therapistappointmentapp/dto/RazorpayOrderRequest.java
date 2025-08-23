package com.tapp.therapistappointmentapp.dto;

import java.math.BigDecimal;

public class RazorpayOrderRequest {
    private Long appointmentId; // Link payment to a specific appointment
    private BigDecimal amount; // Amount to be charged
    private String currency; // e.g., "INR"

    public RazorpayOrderRequest() {
    }

    public RazorpayOrderRequest(Long appointmentId, BigDecimal amount, String currency) {
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}