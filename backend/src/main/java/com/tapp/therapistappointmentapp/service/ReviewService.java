package com.tapp.therapistappointmentapp.service;

import com.tapp.therapistappointmentapp.dto.ReviewRequest;
import com.tapp.therapistappointmentapp.dto.ReviewResponse;
import com.tapp.therapistappointmentapp.model.Appointment;
import com.tapp.therapistappointmentapp.model.AppointmentStatus;
import com.tapp.therapistappointmentapp.model.Patient;
import com.tapp.therapistappointmentapp.model.Review;
import com.tapp.therapistappointmentapp.repository.AppointmentRepository;
import com.tapp.therapistappointmentapp.repository.PatientRepository;
import com.tapp.therapistappointmentapp.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public ReviewService(ReviewRepository reviewRepository, AppointmentRepository appointmentRepository, PatientRepository patientRepository) {
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public ReviewResponse submitReview(Long patientId, ReviewRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + request.getAppointmentId()));

        // Change from SecurityException to IllegalArgumentException
        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new IllegalArgumentException("Unauthorized: This appointment does not belong to the authenticated patient."); // <--- CHANGED
        }

        if (!appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new IllegalArgumentException("Appointment must be completed to leave a review.");
        }

        if (reviewRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            throw new IllegalArgumentException("A review for this appointment already exists.");
        }

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Review review = new Review();
        review.setAppointment(appointment);
        review.setPatient(patient);
        review.setTherapist(appointment.getTherapist());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setReviewDate(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        return convertToReviewResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByTherapistId(Long therapistId) {
        return reviewRepository.findByTherapistId(therapistId).stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("Review not found with ID: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponse convertToReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setAppointmentId(review.getAppointment().getId());
        response.setPatientId(review.getPatient().getId());
        response.setPatientEmail(review.getPatient().getEmail());
        response.setTherapistId(review.getTherapist().getId());
        response.setTherapistEmail(review.getTherapist().getEmail());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setReviewDate(review.getReviewDate());
        return response;
    }
}