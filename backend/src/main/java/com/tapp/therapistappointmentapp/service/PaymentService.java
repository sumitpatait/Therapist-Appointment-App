package com.tapp.therapistappointmentapp.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils; // For signature verification
import com.tapp.therapistappointmentapp.dto.RazorpayOrderRequest;
import com.tapp.therapistappointmentapp.dto.RazorpayOrderResponse;
import com.tapp.therapistappointmentapp.dto.RazorpayPaymentVerifyRequest;
import com.tapp.therapistappointmentapp.model.Appointment;
import com.tapp.therapistappointmentapp.model.AppointmentStatus; // To update appointment status on success
import com.tapp.therapistappointmentapp.model.Payment;
import com.tapp.therapistappointmentapp.model.PaymentStatus;
import com.tapp.therapistappointmentapp.repository.AppointmentRepository;
import com.tapp.therapistappointmentapp.repository.PaymentRepository;
import org.json.JSONObject; // Used by Razorpay SDK
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository; // To link payment to appointment and update status

    @Value("${razorpay.keyId}") // Injected from application.properties
    private String razorpayKeyId;

    @Value("${razorpay.keySecret}") // Injected from application.properties
    private String razorpayKeySecret;

    public PaymentService(RazorpayClient razorpayClient, PaymentRepository paymentRepository, AppointmentRepository appointmentRepository) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public RazorpayOrderResponse createOrder(Long patientId, RazorpayOrderRequest request) throws RazorpayException {
        // 1. Validate Appointment exists and belongs to patient (basic security)
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + request.getAppointmentId()));

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new SecurityException("Unauthorized: This appointment does not belong to the authenticated patient.");
        }
        
        // 2. Ensure appointment is pending and not already paid/confirmed
        if (appointment.getStatus().equals(AppointmentStatus.CONFIRMED) || appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new IllegalArgumentException("Appointment is already confirmed/completed and cannot be paid again.");
        }

        // 3. Create Razorpay Order
        JSONObject orderRequest = new JSONObject();
       
        orderRequest.put("amount", request.getAmount().multiply(new BigDecimal("100")).intValue()); // Convert Rupee to Paisa
        orderRequest.put("currency", request.getCurrency());
        orderRequest.put("receipt", "receipt_appointment_" + request.getAppointmentId()); // Unique receipt ID
        orderRequest.put("payment_capture", 1); // Auto capture payment

        Order order = razorpayClient.orders.create(orderRequest);

        // 4. Save a new Payment record with PENDING status and Razorpay Order ID
        Payment payment = new Payment();
        payment.setAppointment(appointment); // Link to appointment
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setRazorpayOrderId(order.get("id")); // Store Razorpay's generated Order ID

        paymentRepository.save(payment);

        // 5. Prepare response for frontend
        RazorpayOrderResponse response = new RazorpayOrderResponse();
        response.setOrderId(order.get("id"));
        response.setCurrency(order.get("currency"));
        response.setAmount(request.getAmount()); // Return in original currency unit (Rupees)
        response.setKeyId(razorpayKeyId); // Your Razorpay Key ID
        response.setAppointmentId(request.getAppointmentId());

        return response;
    }

    @Transactional
    public boolean verifyPayment(RazorpayPaymentVerifyRequest request) throws RazorpayException {
        // 1. Verify signature
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", request.getRazorpayOrderId());
        options.put("razorpay_payment_id", request.getRazorpayPaymentId());
        options.put("razorpay_signature", request.getRazorpaySignature());

        boolean isSignatureValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

        // 2. Update Payment record status based on verification
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found for Razorpay Order ID: " + request.getRazorpayOrderId()));

        if (isSignatureValid) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            paymentRepository.save(payment);

            // 3. Optionally, update the associated Appointment status (e.g., from PENDING to CONFIRMED)
            // This links payment success to appointment confirmation
            Appointment appointment = payment.getAppointment();
            if (appointment.getStatus().equals(AppointmentStatus.PENDING)) {
                appointment.setStatus(AppointmentStatus.CONFIRMED);
                appointmentRepository.save(appointment);
            }
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }

        return isSignatureValid;
    }
}