package com.tapp.therapistappointmentapp.service;

import com.tapp.therapistappointmentapp.dto.AppointmentResponse;
import com.tapp.therapistappointmentapp.dto.SystemStatsResponse; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.dto.UserResponse;
import com.tapp.therapistappointmentapp.dto.UserUpdateRequest;
import com.tapp.therapistappointmentapp.model.Appointment;
import com.tapp.therapistappointmentapp.model.AppointmentStatus;
import com.tapp.therapistappointmentapp.model.Patient; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.model.Review; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.model.Therapist; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.model.User;
import com.tapp.therapistappointmentapp.model.UserRole;
import com.tapp.therapistappointmentapp.repository.AppointmentRepository;
import com.tapp.therapistappointmentapp.repository.PatientRepository; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.repository.ReviewRepository; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.repository.TherapistRepository; // <--- ADD THIS IMPORT
import com.tapp.therapistappointmentapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository; // <--- ADD PATIENT REPO
    private final TherapistRepository therapistRepository; // <--- ADD THERAPIST REPO
    private final ReviewRepository reviewRepository; // <--- ADD REVIEW REPO


    public AdminService(UserRepository userRepository, AppointmentRepository appointmentRepository,
                        PatientRepository patientRepository, TherapistRepository therapistRepository, ReviewRepository reviewRepository) { // <--- UPDATE CONSTRUCTOR
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.therapistRepository = therapistRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        return convertToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateAppointmentStatusByAdmin(Long appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        appointment.setStatus(newStatus);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToAppointmentResponse(updatedAppointment);
    }

    // --- NEW METHOD: GET SYSTEM STATISTICS ---
    @Transactional(readOnly = true)
    public SystemStatsResponse getSystemStatistics() {
        long totalUsers = userRepository.count();
        long totalPatients = patientRepository.count();
        long totalTherapists = therapistRepository.count();
        long totalReviews = reviewRepository.count();

        long totalAppointments = appointmentRepository.count();
        long pendingAppointments = appointmentRepository.countByStatus(AppointmentStatus.PENDING);
        long confirmedAppointments = appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED);
        long completedAppointments = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);

        return new SystemStatsResponse(
            totalUsers,
            totalPatients,
            totalTherapists,
            totalAppointments,
            pendingAppointments,
            confirmedAppointments,
            completedAppointments,
            totalReviews
        );
    }
    // --- END NEW METHOD ---

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }

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