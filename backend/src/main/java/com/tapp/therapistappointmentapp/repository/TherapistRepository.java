package com.tapp.therapistappointmentapp.repository;

import com.tapp.therapistappointmentapp.model.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TherapistRepository extends JpaRepository<Therapist, Long> {

    Optional<Therapist> findByEmail(String email);
}