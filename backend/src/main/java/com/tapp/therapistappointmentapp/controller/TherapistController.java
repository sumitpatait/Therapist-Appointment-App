package com.tapp.therapistappointmentapp.controller;

import com.tapp.therapistappointmentapp.dto.ScheduleRequest;
import com.tapp.therapistappointmentapp.dto.ScheduleResponse;
import com.tapp.therapistappointmentapp.dto.TherapistRegistrationRequest;
import com.tapp.therapistappointmentapp.model.Therapist;
import com.tapp.therapistappointmentapp.service.ScheduleService;
import com.tapp.therapistappointmentapp.service.TherapistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

import com.tapp.therapistappointmentapp.dto.AppointmentResponse;
import com.tapp.therapistappointmentapp.service.AppointmentService;

import com.tapp.therapistappointmentapp.dto.AppointmentStatusUpdateRequest;

@RestController
@RequestMapping("/api/therapists")
public class TherapistController {

    private final TherapistService therapistService;
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;

    public TherapistController(TherapistService therapistService, ScheduleService scheduleService, AppointmentService appointmentService) {
        this.therapistService = therapistService;
        this.scheduleService = scheduleService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerTherapist(@RequestBody TherapistRegistrationRequest request) {
        try {
            Therapist registeredTherapist = therapistService.registerTherapist(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Therapist registered successfully with email: " + registeredTherapist.getEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during therapist registration: " + e.getMessage());
        }
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleRequest request) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String therapistEmail = userDetails.getUsername();

            Therapist therapist = therapistService.findTherapistByEmail(therapistEmail)
                                        .orElseThrow(() -> new IllegalArgumentException("Authenticated therapist not found."));

            ScheduleResponse response = scheduleService.addTherapistSchedule(therapist.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding schedule: " + e.getMessage());
        }
    }

    @GetMapping("/{therapistId}/schedule") // Public endpoint to view a therapist's schedule by ID
    public ResponseEntity<?> getTherapistSchedule(@PathVariable Long therapistId) {
        try {
            List<ScheduleResponse> schedules = scheduleService.getTherapistSchedules(therapistId);
            return ResponseEntity.ok(schedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving schedule: " + e.getMessage());
        }
    }

    @DeleteMapping("/schedule/{scheduleId}")
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long scheduleId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String therapistEmail = userDetails.getUsername();

            Therapist therapist = therapistService.findTherapistByEmail(therapistEmail)
                                        .orElseThrow(() -> new IllegalArgumentException("Authenticated therapist not found."));

            scheduleService.deleteSchedule(scheduleId, therapist.getId()); // <--- THIS LINE WAS INCOMPLETE IN YOUR SNIPPET
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting schedule: " + e.getMessage());
        }
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<?> getTherapistAppointments() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String therapistEmail = userDetails.getUsername();

            Therapist therapist = therapistService.findTherapistByEmail(therapistEmail)
                                        .orElseThrow(() -> new IllegalArgumentException("Authenticated therapist not found."));

            List<AppointmentResponse> appointments = appointmentService.getAppointmentsByTherapistId(therapist.getId());
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving therapist appointments: " + e.getMessage());
        }
    }

    @PutMapping("/appointments/{appointmentId}/status")
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long appointmentId, @RequestBody AppointmentStatusUpdateRequest request) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String therapistEmail = userDetails.getUsername();

            Therapist therapist = therapistService.findTherapistByEmail(therapistEmail)
                                        .orElseThrow(() -> new IllegalArgumentException("Authenticated therapist not found."));

            AppointmentResponse updatedAppointment = appointmentService.updateAppointmentStatus(appointmentId, therapist.getId(), request);
            return ResponseEntity.ok(updatedAppointment);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating appointment status: " + e.getMessage());
        }
    }

    // NEW ENDPOINT: GET OWN THERAPIST SCHEDULE (Secured)
    @GetMapping("/schedule") // Get schedules for the authenticated therapist
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<?> getOwnTherapistSchedule() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String therapistEmail = userDetails.getUsername();

            Therapist therapist = therapistService.findTherapistByEmail(therapistEmail)
                                        .orElseThrow(() -> new IllegalArgumentException("Authenticated therapist not found."));

            List<ScheduleResponse> schedules = scheduleService.getTherapistSchedules(therapist.getId());
            return ResponseEntity.ok(schedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving own therapist schedules: " + e.getMessage());
        }
    }
}