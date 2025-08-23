package com.tapp.therapistappointmentapp.repository;

import com.tapp.therapistappointmentapp.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Find payment record by Razorpay Order ID
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}