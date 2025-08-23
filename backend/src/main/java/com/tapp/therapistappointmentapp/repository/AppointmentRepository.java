package com.tapp.therapistappointmentapp.repository;

import com.tapp.therapistappointmentapp.model.Appointment;
import com.tapp.therapistappointmentapp.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByTherapistId(Long therapistId);
    Optional<Appointment> findByTherapistIdAndAppointmentTime(Long therapistId, LocalDateTime appointmentTime);

    // --- NEW METHODS FOR STATISTICS ---
    long countByStatus(AppointmentStatus status);
    // --- END NEW METHODS ---
}