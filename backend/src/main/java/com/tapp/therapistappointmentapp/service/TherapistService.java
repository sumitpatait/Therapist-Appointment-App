package com.tapp.therapistappointmentapp.service;

import com.tapp.therapistappointmentapp.dto.TherapistRegistrationRequest;
import com.tapp.therapistappointmentapp.model.Specialization;
import com.tapp.therapistappointmentapp.model.Therapist;
import com.tapp.therapistappointmentapp.model.UserRole;
import com.tapp.therapistappointmentapp.repository.TherapistRepository;
import com.tapp.therapistappointmentapp.repository.SpecializationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors; // <--- ADD THIS IMPORT

import com.tapp.therapistappointmentapp.dto.TherapistProfileResponse; // <--- ADD THIS IMPORT

@Service
public class TherapistService {

    private final TherapistRepository therapistRepository;
    private final SpecializationRepository specializationRepository;
    private final PasswordEncoder passwordEncoder;

    public TherapistService(TherapistRepository therapistRepository, SpecializationRepository specializationRepository, PasswordEncoder passwordEncoder) {
        this.therapistRepository = therapistRepository;
        this.specializationRepository = specializationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Therapist registerTherapist(TherapistRegistrationRequest request) {
        // ... (existing registerTherapist method remains unchanged) ...
        if (therapistRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        Therapist therapist = new Therapist();
        therapist.setEmail(request.getEmail());
        therapist.setPassword(passwordEncoder.encode(request.getPassword()));
        therapist.setRole(UserRole.THERAPIST);

        therapist.setBio(request.getBio());
        therapist.setExperience(request.getExperience());
        therapist.setQualifications(request.getQualifications());

        Set<Specialization> specializations = new HashSet<>();
        if (request.getSpecializationNames() != null) {
            for (String name : request.getSpecializationNames()) {
                Specialization specialization = specializationRepository.findByName(name)
                    .orElseGet(() -> {
                        Specialization newSpec = new Specialization();
                        newSpec.setName(name); // Assumes setName() is present (Lombok or manual)
                        return specializationRepository.save(newSpec);
                    });
                specializations.add(specialization);
            }
        }
        therapist.setSpecializations(specializations);
        return therapistRepository.save(therapist);
    }

    public Optional<Therapist> findTherapistByEmail(String email) {
        return therapistRepository.findByEmail(email);
    }

    public Optional<Therapist> findTherapistById(Long id) {
        return therapistRepository.findById(id);
    }

    // --- NEW METHODS FOR PROFILE VIEWING ---

    @Transactional(readOnly = true)
    public List<TherapistProfileResponse> getAllTherapistProfiles() {
        List<Therapist> therapists = therapistRepository.findAll();
        return therapists.stream()
                .map(this::convertToProfileResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TherapistProfileResponse getTherapistProfileById(Long id) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Therapist not found with ID: " + id));
        return convertToProfileResponse(therapist);
    }

    private TherapistProfileResponse convertToProfileResponse(Therapist therapist) {
        TherapistProfileResponse response = new TherapistProfileResponse();
        response.setId(therapist.getId());
        response.setEmail(therapist.getEmail());
        response.setBio(therapist.getBio());
        response.setExperience(therapist.getExperience());
        response.setQualifications(therapist.getQualifications());

        // Convert Specialization entities to a list of names
        if (therapist.getSpecializations() != null) {
            response.setSpecializations(
                therapist.getSpecializations().stream()
                    .map(Specialization::getName) // Assumes getName() is present (Lombok or manual)
                    .collect(Collectors.toList())
            );
        }
        return response;
    }
    // --- END NEW METHODS ---
}