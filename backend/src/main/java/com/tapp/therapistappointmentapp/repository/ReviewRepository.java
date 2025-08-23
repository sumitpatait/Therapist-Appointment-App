package com.tapp.therapistappointmentapp.repository;

import com.tapp.therapistappointmentapp.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Find reviews by therapist ID
    List<Review> findByTherapistId(Long therapistId);

    // Check if a review already exists for a specific appointment
    Optional<Review> findByAppointmentId(Long appointmentId);
}