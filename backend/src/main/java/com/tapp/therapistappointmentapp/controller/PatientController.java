package com.tapp.therapistappointmentapp.controller;

import com.tapp.therapistappointmentapp.dto.PatientRegistrationRequest;
import com.tapp.therapistappointmentapp.model.Patient;
import com.tapp.therapistappointmentapp.service.AuthService;
import com.tapp.therapistappointmentapp.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

import com.tapp.therapistappointmentapp.dto.TherapistProfileResponse;
import com.tapp.therapistappointmentapp.service.TherapistService;

import com.tapp.therapistappointmentapp.dto.AppointmentRequest;
import com.tapp.therapistappointmentapp.dto.AppointmentResponse;
import com.tapp.therapistappointmentapp.service.AppointmentService;

import com.tapp.therapistappointmentapp.dto.ReviewRequest; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.dto.ReviewResponse; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.service.ReviewService; // <--- ADD THIS IMPORT

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final TherapistService therapistService;
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;
    private final AuthService authService; // New dependency

    public PatientController(PatientService patientService, TherapistService therapistService, AppointmentService appointmentService, ReviewService reviewService, AuthService authService) {
        this.patientService = patientService;
        this.therapistService = therapistService;
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
        this.authService = authService; // Assign the new dependency
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@RequestBody PatientRegistrationRequest request) {
        try {
            // Updated to call the correct service
            Patient registeredPatient = patientService.registerPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Patient registered successfully with email: " + registeredPatient.getEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during registration.");
        }
    }

    @GetMapping("/profile/{username}")
    @PreAuthorize("hasRole('PATIENT') and authentication.principal.username == #username")
    public ResponseEntity<?> getPatientProfile(@PathVariable String username) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticatedUsername = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        if (!authenticatedUsername.equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied to this patient's profile.");
        }
        return ResponseEntity.ok("Welcome, " + authenticatedUsername + "! You are logged in as " + role + ".");
    }

    @GetMapping("/therapists")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<TherapistProfileResponse>> getAllTherapists() {
        List<TherapistProfileResponse> therapists = therapistService.getAllTherapistProfiles();
        return ResponseEntity.ok(therapists);
    }

    @GetMapping("/therapists/{therapistId}/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getTherapistPublicProfile(@PathVariable Long therapistId) {
        try {
            TherapistProfileResponse therapist = therapistService.getTherapistProfileById(therapistId);
            return ResponseEntity.ok(therapist);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving therapist profile: " + e.getMessage());
        }
    }

    @PostMapping("/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String patientEmail = userDetails.getUsername();

            Patient patient = patientService.findPatientByEmail(patientEmail);

            AppointmentResponse response = appointmentService.bookAppointment(patient.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error booking appointment: " + e.getMessage());
        }
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getPatientAppointments() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String patientEmail = userDetails.getUsername();

            Patient patient = patientService.findPatientByEmail(patientEmail);

            List<AppointmentResponse> appointments = appointmentService.getAppointmentsByPatientId(patient.getId());
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving patient appointments: " + e.getMessage());
        }
    }

    // --- NEW ENDPOINT: SUBMIT REVIEW ---
    @PostMapping("/reviews") // Patient submits a review
   
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequest request) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String patientEmail = userDetails.getUsername();

            Patient patient = patientService.findPatientByEmail(patientEmail);

            ReviewResponse response = reviewService.submitReview(patient.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting review: " + e.getMessage());
        }
    }
    // --- END NEW ENDPOINT ---
}
