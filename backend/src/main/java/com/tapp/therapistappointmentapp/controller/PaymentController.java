package com.tapp.therapistappointmentapp.controller;

import com.razorpay.RazorpayException;
import com.tapp.therapistappointmentapp.dto.RazorpayOrderRequest;
import com.tapp.therapistappointmentapp.dto.RazorpayOrderResponse;
import com.tapp.therapistappointmentapp.dto.RazorpayPaymentVerifyRequest;
import com.tapp.therapistappointmentapp.service.PaymentService;
import com.tapp.therapistappointmentapp.service.PatientService; // To get patient ID
import com.tapp.therapistappointmentapp.model.Patient; // To get patient object

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PatientService patientService; // Needed to get authenticated patient's ID

    public PaymentController(PaymentService paymentService, PatientService patientService) {
        this.paymentService = paymentService;
        this.patientService = patientService;
    }

    @PostMapping("/create_order")
    @PreAuthorize("hasRole('PATIENT')") // Only authenticated patients can create orders
    public ResponseEntity<?> createRazorpayOrder(@RequestBody RazorpayOrderRequest request) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String patientEmail = userDetails.getUsername();

            Patient patient = patientService.findPatientByEmail(patientEmail);

            RazorpayOrderResponse response = paymentService.createOrder(patient.getId(), request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Razorpay order: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/verify_signature")
    @PreAuthorize("hasRole('PATIENT')") // Only authenticated patients can verify their payments
    public ResponseEntity<String> verifyPaymentSignature(@RequestBody RazorpayPaymentVerifyRequest request) {
        try {
            boolean isValid = paymentService.verifyPayment(request);
            if (isValid) {
                return ResponseEntity.ok("Payment successful and verified!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying Razorpay payment: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during verification: " + e.getMessage());
        }
    }
}