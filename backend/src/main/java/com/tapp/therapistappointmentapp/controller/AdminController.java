package com.tapp.therapistappointmentapp.controller;

import com.tapp.therapistappointmentapp.dto.AppointmentResponse;
import com.tapp.therapistappointmentapp.dto.AppointmentStatusUpdateRequest;
import com.tapp.therapistappointmentapp.dto.SystemStatsResponse; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.dto.UserResponse;
import com.tapp.therapistappointmentapp.dto.UserUpdateRequest;
import com.tapp.therapistappointmentapp.model.AppointmentStatus;
import com.tapp.therapistappointmentapp.service.AdminService;
import com.tapp.therapistappointmentapp.service.ReviewService; // <--- ADD THIS IMPORT
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.tapp.therapistappointmentapp.dto.ReviewResponse; // <--- ADD THIS IMPORT


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // All methods in this controller require ADMIN role
public class AdminController {

    private final AdminService adminService;
    private final ReviewService reviewService; // <--- ADD REVIEW SERVICE

    public AdminController(AdminService adminService, ReviewService reviewService) { // <--- UPDATE CONSTRUCTOR
        this.adminService = adminService;
        this.reviewService = reviewService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserResponse user = adminService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving user: " + e.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        try {
            UserResponse updatedUser = adminService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<AppointmentResponse> appointments = adminService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long id, @RequestBody AppointmentStatusUpdateRequest request) {
        try {
            AppointmentResponse updatedAppointment = adminService.updateAppointmentStatusByAdmin(id, request.getStatus());
            return ResponseEntity.ok(updatedAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating appointment status: " + e.getMessage());
        }
    }

    // --- NEW ENDPOINT: GET ALL REVIEWS (FOR ADMIN) ---
    @GetMapping("/reviews") // Get all reviews in the system
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        List<ReviewResponse> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }
    // --- END NEW ENDPOINT ---

    // --- NEW ENDPOINT: DELETE REVIEW (FOR ADMIN) ---
    @DeleteMapping("/reviews/{id}") // Delete a review by ID
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting review: " + e.getMessage());
        }
    }
    // --- END NEW ENDPOINT ---

    // --- NEW ENDPOINT: GET SYSTEM STATISTICS ---
    @GetMapping("/stats") // Get overall system statistics
    public ResponseEntity<SystemStatsResponse> getSystemStats() {
        SystemStatsResponse stats = adminService.getSystemStatistics();
        return ResponseEntity.ok(stats);
    }
    // --- END NEW ENDPOINT ---
}