package com.tapp.therapistappointmentapp.service;

import com.tapp.therapistappointmentapp.dto.AppointmentRequest;
import com.tapp.therapistappointmentapp.dto.AppointmentResponse;
import com.tapp.therapistappointmentapp.model.Appointment;
import com.tapp.therapistappointmentapp.model.AppointmentStatus;
import com.tapp.therapistappointmentapp.model.Patient;
import com.tapp.therapistappointmentapp.model.Schedule;
import com.tapp.therapistappointmentapp.model.Therapist;
import com.tapp.therapistappointmentapp.repository.AppointmentRepository;
import com.tapp.therapistappointmentapp.repository.PatientRepository;
import com.tapp.therapistappointmentapp.repository.ScheduleRepository;
import com.tapp.therapistappointmentapp.repository.TherapistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tapp.therapistappointmentapp.dto.AppointmentStatusUpdateRequest; // <--- ADD THIS IMPORT

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final TherapistRepository therapistRepository;
    private final ScheduleRepository scheduleRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
                              TherapistRepository therapistRepository, ScheduleRepository scheduleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.therapistRepository = therapistRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public AppointmentResponse bookAppointment(Long patientId, AppointmentRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));

        Therapist therapist = therapistRepository.findById(request.getTherapistId())
                .orElseThrow(() -> new IllegalArgumentException("Therapist not found with ID: " + request.getTherapistId()));

        LocalDateTime desiredTime = request.getAppointmentTime();

        // 1. Validate against Therapist's General Schedule (DayOfWeek & Time Range)
        DayOfWeek dayOfWeek = desiredTime.getDayOfWeek();
        LocalTime requestedTime = desiredTime.toLocalTime();

        List<Schedule> therapistSchedules = scheduleRepository.findByTherapistId(therapist.getId());
        
        boolean isTimeAvailableInGeneralSchedule = therapistSchedules.stream()
            .anyMatch(schedule -> schedule.getDayOfWeek().equals(dayOfWeek) &&
                                  !requestedTime.isBefore(schedule.getStartTime()) &&
                                  !requestedTime.isAfter(schedule.getEndTime().minusMinutes(1)));

        if (!isTimeAvailableInGeneralSchedule) {
            throw new IllegalArgumentException("Therapist is not available at the requested date and time based on their general schedule.");
        }

        // 2. Validate against existing appointments (Prevent double-booking)
        if (appointmentRepository.findByTherapistIdAndAppointmentTime(therapist.getId(), desiredTime).isPresent()) {
            throw new IllegalArgumentException("This appointment slot is already booked for the therapist.");
        }

        // 3. Create the Appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setTherapist(therapist);
        appointment.setAppointmentTime(desiredTime);
        appointment.setStatus(AppointmentStatus.PENDING); // Initial status can be PENDING, CONFIRMED, etc.

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToAppointmentResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatientId(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByTherapistId(Long therapistId) {
        if (!therapistRepository.existsById(therapistId)) {
            throw new IllegalArgumentException("Therapist not found with ID: " + therapistId);
        }
        return appointmentRepository.findByTherapistId(therapistId).stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }

    // --- NEW METHOD: UPDATE APPOINTMENT STATUS ---
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, Long therapistId, AppointmentStatusUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        // Security check: Ensure the appointment belongs to the authenticated therapist
        if (!appointment.getTherapist().getId().equals(therapistId)) {
            throw new SecurityException("Unauthorized: Appointment does not belong to this therapist.");
        }

        appointment.setStatus(request.getStatus());
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToAppointmentResponse(updatedAppointment);
    }
    // --- END NEW METHOD ---

    // Helper method to convert Appointment entity to AppointmentResponse DTO
    private AppointmentResponse convertToAppointmentResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setPatientId(appointment.getPatient().getId());
        response.setPatientEmail(appointment.getPatient().getEmail());
        response.setTherapistId(appointment.getTherapist().getId());
        response.setTherapistEmail(appointment.getTherapist().getEmail());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus());
        return response;
    }
}