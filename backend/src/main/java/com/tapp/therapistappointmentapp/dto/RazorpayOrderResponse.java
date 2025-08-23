package com.tapp.therapistappointmentapp.dto;

import java.math.BigDecimal;

public class RazorpayOrderResponse {
    private String orderId; // Razorpay Order ID
    private String currency;
    private BigDecimal amount; // Amount in rupees, not paisa
    private String keyId;      // Your Razorpay Key ID
    private Long appointmentId; // Original appointment ID

    public RazorpayOrderResponse() {
    }

    public RazorpayOrderResponse(String orderId, String currency, BigDecimal amount, String keyId, Long appointmentId) {
        this.orderId = orderId;
        this.currency = currency;
        this.amount = amount;
        this.keyId = keyId;
        this.appointmentId = appointmentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
}