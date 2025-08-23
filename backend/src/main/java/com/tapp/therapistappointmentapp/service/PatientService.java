package com.tapp.therapistappointmentapp.service;

import com.tapp.therapistappointmentapp.dto.PatientRegistrationRequest;
import com.tapp.therapistappointmentapp.model.Patient;
import com.tapp.therapistappointmentapp.model.UserRole;
import com.tapp.therapistappointmentapp.repository.PatientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Patient registerPatient(PatientRegistrationRequest request) {
        if (patientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }


        Patient patient = new Patient();
        patient.setEmail(request.getEmail());
        patient.setPassword(passwordEncoder.encode(request.getPassword()));
        patient.setRole(UserRole.PATIENT); 


        return patientRepository.save(patient);
    }

    public Patient findPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with email: " + email));
    }
}